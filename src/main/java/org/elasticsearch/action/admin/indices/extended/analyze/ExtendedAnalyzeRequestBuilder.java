package org.elasticsearch.action.admin.indices.extended.analyze;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.internal.InternalIndicesAdminClient;

/**
 * Created with IntelliJ IDEA.
 * User: johtani
 * Date: 2013/10/25
 * Time: 0:44
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedAnalyzeRequestBuilder extends SingleCustomOperationRequestBuilder<ExtendedAnalyzeRequest, ExtendedAnalyzeResponse, ExtendedAnalyzeRequestBuilder> {

    public ExtendedAnalyzeRequestBuilder(IndicesAdminClient indicesClient) {
        super((InternalIndicesAdminClient) indicesClient, new ExtendedAnalyzeRequest());
    }

    public ExtendedAnalyzeRequestBuilder(IndicesAdminClient indicesClient, String index, String text) {
        super((InternalIndicesAdminClient) indicesClient, new ExtendedAnalyzeRequest(index, text));
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

    @Override
    protected void doExecute(ActionListener<ExtendedAnalyzeResponse> listener) {
        ((IndicesAdminClient) client).execute(ExtendedAnalyzeAction.INSTANCE, request, listener);
    }
}
