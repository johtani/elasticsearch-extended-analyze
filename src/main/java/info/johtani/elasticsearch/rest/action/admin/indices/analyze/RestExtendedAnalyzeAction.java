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
package info.johtani.elasticsearch.rest.action.admin.indices.analyze;

import info.johtani.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeAction;
import info.johtani.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeRequest;
import info.johtani.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeResponse;
import org.elasticsearch.ElasticsearchIllegalArgumentException;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.support.RestActions;
import org.elasticsearch.rest.action.support.RestToXContentListener;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.*;

/**
 * Extended _analyze for REST endpoint
 */
public class RestExtendedAnalyzeAction extends BaseRestHandler {

    @Inject
    public RestExtendedAnalyzeAction(Settings settings, Client client, RestController controller) {
        super(settings, controller, client);
        controller.registerHandler(GET, "/_extended_analyze", this);
        controller.registerHandler(GET, "/{index}/_extended_analyze", this);
        controller.registerHandler(POST, "/_extended_analyze", this);
        controller.registerHandler(POST, "/{index}/_extended_analyze", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
        String text = request.param("text");

        ExtendedAnalyzeRequest analyzeRequest = new ExtendedAnalyzeRequest(request.param("index"));
        analyzeRequest.text(text);
        analyzeRequest.listenerThreaded(false);
        analyzeRequest.preferLocal(request.paramAsBoolean("prefer_local", analyzeRequest.preferLocalShard()));
        analyzeRequest.analyzer(request.param("analyzer"));
        analyzeRequest.field(request.param("field"));
        analyzeRequest.tokenizer(request.param("tokenizer"));
        analyzeRequest.tokenFilters(request.paramAsStringArray("token_filters", request.paramAsStringArray("filters", analyzeRequest.tokenFilters())));
        analyzeRequest.charFilters(request.paramAsStringArray("char_filters", analyzeRequest.charFilters()));
        analyzeRequest.attributes(request.paramAsStringArray("attributes", null));
        analyzeRequest.shortAttributeName(request.paramAsBoolean("use_short_attr", false));

        if (request.hasContent() || request.hasParam("source")) {
            XContentType type = guessBodyContentType(request);
            if (type == null) {
                if (Strings.isEmpty(text)) {
                    text = RestActions.getRestContent(request).toUtf8();
                    analyzeRequest.text(text);
                }
            } else {
                buildFromContent(RestActions.getRestContent(request), analyzeRequest);
            }
        }

        client.admin().indices().execute(ExtendedAnalyzeAction.INSTANCE, analyzeRequest, new RestToXContentListener<ExtendedAnalyzeResponse>(channel));
    }

    public static XContentType guessBodyContentType(final RestRequest request) {
        final BytesReference restContent = RestActions.getRestContent(request);
        if (restContent == null) {
            return null;
        }
        return XContentFactory.xContentType(restContent);
    }


    public static void buildFromContent(BytesReference content, ExtendedAnalyzeRequest analyzeRequest) throws ElasticsearchIllegalArgumentException {
        try (XContentParser parser = XContentHelper.createParser(content)) {
            if (parser.nextToken() != XContentParser.Token.START_OBJECT) {
                throw new ElasticsearchIllegalArgumentException("Malforrmed content, must start with an object");
            } else {
                XContentParser.Token token;
                String currentFieldName = null;
                while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                    if (token == XContentParser.Token.FIELD_NAME) {
                        currentFieldName = parser.currentName();
                    } else if ("prefer_local".equals(currentFieldName) && token == XContentParser.Token.VALUE_BOOLEAN) {
                        analyzeRequest.preferLocal(parser.booleanValue());
                    } else if ("text".equals(currentFieldName) && token == XContentParser.Token.VALUE_STRING) {
                        analyzeRequest.text(parser.text());
                    } else if ("analyzer".equals(currentFieldName) && token == XContentParser.Token.VALUE_STRING) {
                        analyzeRequest.analyzer(parser.text());
                    } else if ("field".equals(currentFieldName) && token == XContentParser.Token.VALUE_STRING) {
                        analyzeRequest.field(parser.text());
                    } else if ("tokenizer".equals(currentFieldName) && token == XContentParser.Token.VALUE_STRING) {
                        analyzeRequest.tokenizer(parser.text());
                    } else if (("token_filters".equals(currentFieldName) || "filters".equals(currentFieldName)) && token == XContentParser.Token.START_ARRAY) {
                        List<String> filters = Lists.newArrayList();
                        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                            if (token.isValue() == false) {
                                throw new ElasticsearchIllegalArgumentException(currentFieldName + " array element should only contain token filter's name");
                            }
                            filters.add(parser.text());
                        }
                        analyzeRequest.tokenFilters(filters.toArray(Strings.EMPTY_ARRAY));
                    } else if ("char_filters".equals(currentFieldName) && token == XContentParser.Token.START_ARRAY) {
                        List<String> charFilters = Lists.newArrayList();
                        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                            if (token.isValue() == false) {
                                throw new ElasticsearchIllegalArgumentException(currentFieldName + " array element should only contain char filter's name");
                            }
                            charFilters.add(parser.text());
                        }
                        analyzeRequest.tokenFilters(charFilters.toArray(Strings.EMPTY_ARRAY));
                    } else if ("attributes".equals(currentFieldName) && token == XContentParser.Token.START_ARRAY){
                        List<String> attributes = Lists.newArrayList();
                        while ((token = parser.nextToken()) != XContentParser.Token.END_ARRAY) {
                            if (token.isValue() == false) {
                                throw new ElasticsearchIllegalArgumentException(currentFieldName + " array element should only contain attribute name");
                            }
                            attributes.add(parser.text());
                        }
                        analyzeRequest.attributes(attributes.toArray(Strings.EMPTY_ARRAY));
                    } else if ("use_short_attr".equals(currentFieldName) && token == XContentParser.Token.VALUE_BOOLEAN) {
                        analyzeRequest.shortAttributeName(parser.booleanValue());
                    } else {
                        throw new ElasticsearchIllegalArgumentException("Unknown parameter [" + currentFieldName + "] in request body or parameter is of the wrong type[" + token + "] ");
                    }
                }
            }
        } catch (IOException e) {
            throw new ElasticsearchIllegalArgumentException("Failed to parse request body", e);
        }
    }
}
