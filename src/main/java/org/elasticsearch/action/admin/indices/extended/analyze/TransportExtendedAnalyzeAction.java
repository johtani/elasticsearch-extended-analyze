/*
 * Copyright 2013 Jun Ohtani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.elasticsearch.action.admin.indices.extended.analyze;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import org.apache.lucene.util.BytesRef;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.action.support.single.custom.TransportSingleCustomOperationAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.routing.ShardsIterator;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.lucene.Lucene;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.*;
import org.elasticsearch.index.mapper.FieldMapper;
import org.elasticsearch.index.mapper.internal.AllFieldMapper;
import org.elasticsearch.index.service.IndexService;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 */
public class TransportExtendedAnalyzeAction extends TransportSingleCustomOperationAction<ExtendedAnalyzeRequest, ExtendedAnalyzeResponse> {

    private final IndicesService indicesService;
    private final IndicesAnalysisService indicesAnalysisService;

    @Inject
    public TransportExtendedAnalyzeAction(Settings settings, ThreadPool threadPool, ClusterService clusterService, TransportService transportService,
                                          IndicesService indicesService, IndicesAnalysisService indicesAnalysisService) {
        super(settings, threadPool, clusterService, transportService);
        this.indicesService = indicesService;
        this.indicesAnalysisService = indicesAnalysisService;
    }

    @Override
    protected String executor() {
        return ThreadPool.Names.INDEX;
    }

    @Override
    protected ExtendedAnalyzeRequest newRequest() {
        return new ExtendedAnalyzeRequest();
    }

    @Override
    protected ExtendedAnalyzeResponse newResponse() {
        return new ExtendedAnalyzeResponse();
    }

    @Override
    protected String transportAction() {
        return ExtendedAnalyzeAction.NAME;
    }

    @Override
    protected ClusterBlockException checkGlobalBlock(ClusterState state, ExtendedAnalyzeRequest request) {
        return state.blocks().globalBlockedException(ClusterBlockLevel.READ);
    }

    @Override
    protected ClusterBlockException checkRequestBlock(ClusterState state, ExtendedAnalyzeRequest request) {
        if (request.index() != null) {
            request.index(state.metaData().concreteIndex(request.index()));
            return state.blocks().indexBlockedException(ClusterBlockLevel.READ, request.index());
        }
        return null;
    }

    @Override
    protected ShardsIterator shards(ClusterState state, ExtendedAnalyzeRequest request) {
        if (request.index() == null) {
            // just execute locally....
            return null;
        }
        return state.routingTable().index(request.index()).randomAllActiveShardsIt();
    }

    @Override
    protected ExtendedAnalyzeResponse shardOperation(ExtendedAnalyzeRequest request, int shardId) throws ElasticSearchException {
        IndexService indexService = null;
        if (request.index() != null) {
            indexService = indicesService.indexServiceSafe(request.index());
        }
        Analyzer analyzer = null;
        boolean closeAnalyzer = false;
        String field = null;
        if (request.field() != null) {
            if (indexService == null) {
                throw new ElasticSearchIllegalArgumentException("No index provided, and trying to analyzer based on a specific field which requires the index parameter");
            }
            FieldMapper<?> fieldMapper = indexService.mapperService().smartNameFieldMapper(request.field());
            if (fieldMapper != null) {
                if (fieldMapper.isNumeric()) {
                    throw new ElasticSearchIllegalArgumentException("Can't process field [" + request.field() + "], Analysis requests are not supported on numeric fields");
                }
                analyzer = fieldMapper.indexAnalyzer();
                field = fieldMapper.names().indexName();

            }
        }
        if (field == null) {
            if (indexService != null) {
                field = indexService.queryParserService().defaultField();
            } else {
                field = AllFieldMapper.NAME;
            }
        }
        if (analyzer == null && request.analyzer() != null) {
            if (indexService == null) {
                analyzer = indicesAnalysisService.analyzer(request.analyzer());
            } else {
                analyzer = indexService.analysisService().analyzer(request.analyzer());
            }
            if (analyzer == null) {
                throw new ElasticSearchIllegalArgumentException("failed to find analyzer [" + request.analyzer() + "]");
            }
        } else if (request.tokenizer() != null) {
            TokenizerFactory tokenizerFactory;
            if (indexService == null) {
                TokenizerFactoryFactory tokenizerFactoryFactory = indicesAnalysisService.tokenizerFactoryFactory(request.tokenizer());
                if (tokenizerFactoryFactory == null) {
                    throw new ElasticSearchIllegalArgumentException("failed to find global tokenizer under [" + request.tokenizer() + "]");
                }
                tokenizerFactory = tokenizerFactoryFactory.create(request.tokenizer(), ImmutableSettings.Builder.EMPTY_SETTINGS);
            } else {
                tokenizerFactory = indexService.analysisService().tokenizer(request.tokenizer());
                if (tokenizerFactory == null) {
                    throw new ElasticSearchIllegalArgumentException("failed to find tokenizer under [" + request.tokenizer() + "]");
                }
            }
            TokenFilterFactory[] tokenFilterFactories = new TokenFilterFactory[0];
            if (request.tokenFilters() != null && request.tokenFilters().length > 0) {
                tokenFilterFactories = new TokenFilterFactory[request.tokenFilters().length];
                for (int i = 0; i < request.tokenFilters().length; i++) {
                    String tokenFilterName = request.tokenFilters()[i];
                    if (indexService == null) {
                        TokenFilterFactoryFactory tokenFilterFactoryFactory = indicesAnalysisService.tokenFilterFactoryFactory(tokenFilterName);
                        if (tokenFilterFactoryFactory == null) {
                            throw new ElasticSearchIllegalArgumentException("failed to find global token filter under [" + request.tokenizer() + "]");
                        }
                        tokenFilterFactories[i] = tokenFilterFactoryFactory.create(tokenFilterName, ImmutableSettings.Builder.EMPTY_SETTINGS);
                    } else {
                        tokenFilterFactories[i] = indexService.analysisService().tokenFilter(tokenFilterName);
                        if (tokenFilterFactories[i] == null) {
                            throw new ElasticSearchIllegalArgumentException("failed to find token filter under [" + request.tokenizer() + "]");
                        }
                    }
                    if (tokenFilterFactories[i] == null) {
                        throw new ElasticSearchIllegalArgumentException("failed to find token filter under [" + request.tokenizer() + "]");
                    }
                }
            }
            analyzer = new CustomAnalyzer(tokenizerFactory, new CharFilterFactory[0], tokenFilterFactories);
            closeAnalyzer = true;
        } else if (analyzer == null) {
            if (indexService == null) {
                analyzer = Lucene.STANDARD_ANALYZER;
            } else {
                analyzer = indexService.analysisService().defaultIndexAnalyzer();
            }
        }
        if (analyzer == null) {
            throw new ElasticSearchIllegalArgumentException("failed to find analyzer");
        }

        List<ExtendedAnalyzeResponse.ExtendedAnalyzeToken> tokens = Lists.newArrayList();
        TokenStream stream = null;
        try {
            stream = analyzer.tokenStream(field, request.text());
            stream.reset();
            CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
            PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
            OffsetAttribute offset = stream.addAttribute(OffsetAttribute.class);
            TypeAttribute type = stream.addAttribute(TypeAttribute.class);

            int position = 0;
            while (stream.incrementToken()) {
                int increment = posIncr.getPositionIncrement();
                if (increment > 0) {
                    position = position + increment;
                }

                tokens.add(new ExtendedAnalyzeResponse.ExtendedAnalyzeToken(term.toString(), position, offset.startOffset(), offset.endOffset(), type.type(), extractExtendedAttributes(stream)));
            }
            stream.end();
        } catch (IOException e) {
            throw new ElasticSearchException("failed to analyze", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (closeAnalyzer) {
                analyzer.close();
            }
        }

        return new ExtendedAnalyzeResponse(tokens);
    }

    /**
     * other attribute extract object
     *
     * @param stream
     * @return
     */
    private List<Object> extractExtendedAttributes(TokenStream stream) {
        final List<Object> extendedAttributes = Lists.newArrayList();

        stream.reflectWith(new AttributeReflector() {
            @Override
            public void reflect(Class<? extends Attribute> attClass, String key, Object value) {
                if (CharTermAttribute.class.isAssignableFrom(attClass))
                    return;
                if (PositionIncrementAttribute.class.isAssignableFrom(attClass))
                    return;
                if (OffsetAttribute.class.isAssignableFrom(attClass))
                    return;
                if (TypeAttribute.class.isAssignableFrom(attClass))
                    return;

                String k = attClass.getName() + '#' + key;

                if (value instanceof BytesRef) {
                    final BytesRef p = (BytesRef) value;
                    value = p.toString();
                }
                Map<String, Object> attObj = Maps.newHashMap();
                attObj.put(k, value);
                extendedAttributes.add(attObj);
            }
        });

        return extendedAttributes;
    }
}

