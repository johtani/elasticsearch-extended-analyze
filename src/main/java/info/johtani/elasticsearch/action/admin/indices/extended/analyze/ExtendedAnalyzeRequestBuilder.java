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

package info.johtani.elasticsearch.action.admin.indices.extended.analyze;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequestBuilder;
import org.elasticsearch.client.IndicesAdminClient;

/**
 */
public class ExtendedAnalyzeRequestBuilder extends SingleCustomOperationRequestBuilder<ExtendedAnalyzeRequest, ExtendedAnalyzeResponse, ExtendedAnalyzeRequestBuilder> {

    public ExtendedAnalyzeRequestBuilder(IndicesAdminClient indicesClient) {
        super(indicesClient, new ExtendedAnalyzeRequest());
    }

    public ExtendedAnalyzeRequestBuilder(IndicesAdminClient indicesClient, String index) {
        super(indicesClient, new ExtendedAnalyzeRequest(index));
    }

    public ExtendedAnalyzeRequestBuilder(IndicesAdminClient indicesClient, String index, String text) {
        super(indicesClient, new ExtendedAnalyzeRequest(index).text(text));
    }

    public ExtendedAnalyzeRequestBuilder setText(String... text) {
        request.text(text);
        return this;
    }

    /**
     * Sets the index to use to analyzer the text (for example, if it holds specific analyzers
     * registered).
     */
    public ExtendedAnalyzeRequestBuilder setIndex(String index) {
        request.index(index);
        return this;
    }

    /**
     * Sets the analyzer name to use in order to analyze the text.
     *
     * @param analyzer The analyzer name.
     */
    public ExtendedAnalyzeRequestBuilder setAnalyzer(String analyzer) {
        request.analyzer(analyzer);
        return this;
    }

    /**
     * Sets the field that its analyzer will be used to analyze the text. Note, requires an index
     * to be set.
     */
    public ExtendedAnalyzeRequestBuilder setField(String field) {
        request.field(field);
        return this;
    }

    /**
     * Instead of setting the analyzer, sets the tokenizer that will be used as part of a custom
     * analyzer.
     */
    public ExtendedAnalyzeRequestBuilder setTokenizer(String tokenizer) {
        request.tokenizer(tokenizer);
        return this;
    }

    /**
     * Sets token filters that will be used on top of a tokenizer provided.
     */
    public ExtendedAnalyzeRequestBuilder setTokenFilters(String... tokenFilters) {
        request.tokenFilters(tokenFilters);
        return this;
    }

    /**
     * Sets char filters that will be used previous a tokenizer provided.
     */
    public ExtendedAnalyzeRequestBuilder setCharFilters(String... charFilters){
        request.charFilters(charFilters);
        return this;
    }

    /**
     * Sets attributes that will include results
     */
    public ExtendedAnalyzeRequestBuilder setAttributes(String attributes){
        request.attributes(attributes);
        return this;
    }

    /**
     * Sets shortAttributeName
     */
    public ExtendedAnalyzeRequestBuilder setShortAttributeName(boolean shortAttributeName) {
        request.shortAttributeName(shortAttributeName);
        return this;
    }

    @Override
    protected void doExecute(ActionListener<ExtendedAnalyzeResponse> listener) {
        client.execute(ExtendedAnalyzeAction.INSTANCE, request, listener);
    }
}
