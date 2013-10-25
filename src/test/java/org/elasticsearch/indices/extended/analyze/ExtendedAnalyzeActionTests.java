package org.elasticsearch.indices.extended.analyze;

import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.extended.analyze.ExtendedAnalyzeResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.network.NetworkUtils;
import org.elasticsearch.node.Node;
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
    public void analyzeWithNoIndex() throws Exception {

        ExtendedAnalyzeResponse analyzeResponse = prepareAnalyze(node.client().admin().indices(), "THIS IS A TEST").setAnalyzer("simple").execute().actionGet();
        assertThat(analyzeResponse.getTokens().size(), equalTo(4));

        analyzeResponse = prepareAnalyze(node.client().admin().indices(), "THIS IS A TEST").setTokenizer("keyword").setTokenFilters("lowercase").execute().actionGet();
        assertThat(analyzeResponse.getTokens().size(), equalTo(1));
        assertThat(analyzeResponse.getTokens().get(0).getTerm(), equalTo("this is a test"));

        analyzeResponse = prepareAnalyze(node.client().admin().indices(), "This is troubled").setTokenizer("standard").setTokenFilters("snowball").execute().actionGet();
        assertThat(analyzeResponse.getTokens().size(), equalTo(3));
        assertThat(analyzeResponse.getTokens().get(2).getTerm(), equalTo("troubl"));
        assertThat(analyzeResponse.getTokens().get(2).getExtendedAttrbutes().size(), equalTo(2));
        String[] expectedAttributesKey = {
            "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute#bytes",
            "org.apache.lucene.analysis.tokenattributes.KeywordAttribute#keyword"};
        Map<String, Object> extendedAttribute = null;
        for(int i=0;i<expectedAttributesKey.length;i++){
            extendedAttribute = (Map<String, Object>)analyzeResponse.getTokens().get(2).getExtendedAttrbutes().get(i);
            assertThat(extendedAttribute.size(), equalTo(1));
            assertThat(extendedAttribute.containsKey(expectedAttributesKey[i]), equalTo(true));
        }
    }

    private ExtendedAnalyzeRequestBuilder prepareAnalyze(IndicesAdminClient client, String text) {
        return new ExtendedAnalyzeRequestBuilder(client, null, text);
    }
}
