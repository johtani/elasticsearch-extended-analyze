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

package info.johtani.elasticsearch.indices.extended.analyze;


import info.johtani.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeRequestBuilder;
import info.johtani.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.node.Node;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.elasticsearch.common.settings.ImmutableSettings.*;
import static org.elasticsearch.node.NodeBuilder.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * TODO : currently, simple test only.
 */
public class ExtendedAnalyzeActionTests {

    private Node node;

    @Before
    public void setupServer() {
        node = nodeBuilder().local(true).settings(settingsBuilder()
            .put("path.data", "target/data")
            .put("cluster.name", "test-cluster-extended-analyze-" + NetworkUtils.getLocalAddress())
            .put("gateway.type", "none")).node();
    }

    @After
    public void closeServer() {
        node.close();
    }

    @Test
    public void analyzeUsingAnalyzerWithNoIndex() throws Exception {

        ExtendedAnalyzeResponse analyzeResponse = prepareAnalyze(node.client().admin().indices(), "THIS IS A TEST").setAnalyzer("simple").execute().actionGet();
        assertThat(analyzeResponse.tokenizer(), IsNull.nullValue());
        assertThat(analyzeResponse.tokenfilters(), IsNull.nullValue());
        //FIxME charfilter is null test
        assertThat(analyzeResponse.analyzer().getName(), equalTo("simple"));
        assertThat(analyzeResponse.analyzer().getTokens().size(), equalTo(4));

    }

    @Test
    public void analyzeUsingCustomAnalyzerWithNoIndex() throws Exception {
        ExtendedAnalyzeResponse analyzeResponse = prepareAnalyze(node.client().admin().indices(), "THIS IS A TEST").setTokenizer("keyword").setTokenFilters("lowercase").execute().actionGet();
        assertThat(analyzeResponse.analyzer(), IsNull.nullValue());
        //FIXME charfilter test
        //tokenizer
        assertThat(analyzeResponse.tokenizer().getName(), equalTo("keyword"));
        assertThat(analyzeResponse.tokenizer().getTokens().size(), equalTo(1));
        assertThat(analyzeResponse.tokenizer().getTokens().get(0).getTerm(), equalTo("THIS IS A TEST"));
        //tokenfilters
        assertThat(analyzeResponse.tokenfilters().size(), equalTo(1));
        assertThat(analyzeResponse.tokenfilters().get(0).getName(), equalTo("lowercase"));
        assertThat(analyzeResponse.tokenfilters().get(0).getTokens().size(), equalTo(1));
        assertThat(analyzeResponse.tokenfilters().get(0).getTokens().get(0).getTerm(), equalTo("this is a test"));


        //check other attributes
        analyzeResponse = prepareAnalyze(node.client().admin().indices(), "This is troubled").setTokenizer("standard").setTokenFilters("snowball").execute().actionGet();

        assertThat(analyzeResponse.tokenfilters().size(), equalTo(1));
        assertThat(analyzeResponse.tokenfilters().get(0).getName(), equalTo("snowball"));
        assertThat(analyzeResponse.tokenfilters().get(0).getTokens().size(), equalTo(3));
        assertThat(analyzeResponse.tokenfilters().get(0).getTokens().get(2).getTerm(), equalTo("troubl"));
        assertThat(analyzeResponse.tokenfilters().get(0).getTokens().get(2).getExtendedAttrbutes().size(), equalTo(2));
        String[] expectedAttributesKey = {
            "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute#bytes",
            "org.apache.lucene.analysis.tokenattributes.KeywordAttribute#keyword"};
        Map<String, Object> extendedAttribute = null;

        for (int i = 0; i < expectedAttributesKey.length; i++) {
            String attClassName = expectedAttributesKey[i].substring(0, expectedAttributesKey[i].indexOf("#"));
            String key = expectedAttributesKey[i].substring(expectedAttributesKey[i].indexOf("#") + 1);
            extendedAttribute = (Map<String, Object>) analyzeResponse.tokenfilters().get(0).getTokens().get(2).getExtendedAttrbutes().get(attClassName);
            assertThat(extendedAttribute.size(), equalTo(1));
            assertThat(extendedAttribute.containsKey(key), equalTo(true));
        }

    }

    private ExtendedAnalyzeRequestBuilder prepareAnalyze(IndicesAdminClient client, String text) {
        return new ExtendedAnalyzeRequestBuilder(client, null, text);
    }
}
