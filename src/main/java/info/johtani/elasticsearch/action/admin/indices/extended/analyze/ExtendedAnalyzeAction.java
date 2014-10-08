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

import org.elasticsearch.action.admin.indices.IndicesAction;
import org.elasticsearch.client.IndicesAdminClient;

/**
 */
public class ExtendedAnalyzeAction extends IndicesAction<ExtendedAnalyzeRequest, ExtendedAnalyzeResponse, ExtendedAnalyzeRequestBuilder> {

    public static final ExtendedAnalyzeAction INSTANCE = new ExtendedAnalyzeAction();
    public static final String NAME = "indices:admin/extended_analyze";

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
