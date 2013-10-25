package org.elasticsearch.rest.action.admin.indices.analyze;

import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeAction;
import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeRequest;
import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.*;

import java.io.IOException;

import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestRequest.Method.POST;
import static org.elasticsearch.rest.RestStatus.OK;
import static org.elasticsearch.rest.action.support.RestXContentBuilder.restContentBuilder;

/**
 * Extended _analyze for REST endpoint
 */
public class RestExtendedAnalyzeAction extends BaseRestHandler {

    @Inject
    public RestExtendedAnalyzeAction(Settings settings, Client client, RestController controller) {
        super(settings, client);
        controller.registerHandler(GET, "/_extended_analyze", this);
        controller.registerHandler(GET, "/{index}/_extended_analyze", this);
        controller.registerHandler(POST, "/_extended_analyze", this);
        controller.registerHandler(POST, "/{index}/_extended_analyze", this);
    }

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel) {
        String text = request.param("text");
        if (text == null && request.hasContent()) {
            text = request.content().toUtf8();
        }
        if (text == null) {
            try {
                channel.sendResponse(new XContentThrowableRestResponse(request, new ElasticSearchIllegalArgumentException("text is missing")));
            } catch (IOException e1) {
                logger.warn("Failed to send response", e1);
            }
            return;
        }

        ExtendedAnalyzeRequest analyzeRequest = new ExtendedAnalyzeRequest(request.param("index"), text);
        analyzeRequest.listenerThreaded(false);
        analyzeRequest.preferLocal(request.paramAsBoolean("prefer_local", analyzeRequest.preferLocalShard()));
        analyzeRequest.analyzer(request.param("analyzer"));
        analyzeRequest.field(request.param("field"));
        analyzeRequest.tokenizer(request.param("tokenizer"));
        analyzeRequest.tokenFilters(request.paramAsStringArray("token_filters", request.paramAsStringArray("filters", null)));
        client.admin().indices().execute(ExtendedAnalyzeAction.INSTANCE, analyzeRequest, new ActionListener<ExtendedAnalyzeResponse>() {
            @Override
            public void onResponse(ExtendedAnalyzeResponse response) {
                try {
                    XContentBuilder builder = restContentBuilder(request, null);
                    builder.startObject();
                    response.toXContent(builder, request);
                    builder.endObject();
                    channel.sendResponse(new XContentRestResponse(request, OK, builder));
                } catch (Throwable e) {
                    onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable e) {
                try {
                    channel.sendResponse(new XContentThrowableRestResponse(request, e));
                } catch (IOException e1) {
                    logger.error("Failed to send failure response", e1);
                }
            }
        });
    }
}
