package org.elasticsearch.action.admin.indices.extended.analyze;

import org.elasticsearch.action.admin.indices.IndicesAction;
import org.elasticsearch.client.IndicesAdminClient;

/**
 * Created with IntelliJ IDEA.
 * User: johtani
 * Date: 2013/10/25
 * Time: 0:39
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedAnalyzeAction extends IndicesAction<ExtendedAnalyzeRequest, ExtendedAnalyzeResponse, ExtendedAnalyzeRequestBuilder> {

    public static final ExtendedAnalyzeAction INSTANCE = new ExtendedAnalyzeAction();
    public static final String NAME = "indices/extended_analyze";

    public ExtendedAnalyzeAction() {
        super(NAME);
    }

    @Override
    public ExtendedAnalyzeResponse newResponse() {
        return new ExtendedAnalyzeResponse();
    }

    @Override
    public ExtendedAnalyzeRequestBuilder newRequestBuilder(IndicesAdminClient client) {
        return new ExtendedAnalyzeRequestBuilder(client);
    }
}
