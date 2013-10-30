package org.elasticsearch.action.admin.indices.extended.analyze;


import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: johtani
 * Date: 2013/10/25
 * Time: 0:44
 * To change this template use File | Settings | File Templates.
 */
public class ExtendedAnalyzeResponse extends ActionResponse implements Iterable<ExtendedAnalyzeResponse.ExtendedAnalyzeToken>, ToXContent {

    public static class ExtendedAnalyzeToken implements Streamable {
        private String term;
        private int startOffset;
        private int endOffset;
        private int position;
        private String type;
        private Map<String, Map<String,Object>> extendedAttributes;

        ExtendedAnalyzeToken() {
        }

        public ExtendedAnalyzeToken(String term, int position, int startOffset, int endOffset, String type, Map<String, Map<String, Object>> extendedAttributes) {
            this.term = term;
            this.position = position;
            this.startOffset = startOffset;
            this.endOffset = endOffset;
            this.type = type;
            this.extendedAttributes = extendedAttributes;
        }

        public String getTerm() {
            return this.term;
        }

        public int getStartOffset() {
            return this.startOffset;
        }

        public int getEndOffset() {
            return this.endOffset;
        }

        public int getPosition() {
            return this.position;
        }

        public String getType() {
            return this.type;
        }

        public Map<String, Map<String, Object>> getExtendedAttrbutes() {
            return this.extendedAttributes;
        }

        public static ExtendedAnalyzeToken readExtendedAnalyzeToken(StreamInput in) throws IOException {
            ExtendedAnalyzeToken analyzeToken = new ExtendedAnalyzeToken();
            analyzeToken.readFrom(in);
            return analyzeToken;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            term = in.readString();
            startOffset = in.readInt();
            endOffset = in.readInt();
            position = in.readVInt();
            type = in.readOptionalString();
            extendedAttributes = (Map<String, Map<String, Object>>) in.readGenericValue();
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(term);
            out.writeInt(startOffset);
            out.writeInt(endOffset);
            out.writeVInt(position);
            out.writeOptionalString(type);
            out.writeGenericValue(extendedAttributes);
        }
    }


    private List<ExtendedAnalyzeToken> tokens;

    ExtendedAnalyzeResponse() {
    }

    public ExtendedAnalyzeResponse(List<ExtendedAnalyzeToken> tokens) {
        this.tokens = tokens;
    }

    public List<ExtendedAnalyzeToken> getTokens() {
        return this.tokens;
    }

    @Override
    public Iterator<ExtendedAnalyzeToken> iterator() {
        return tokens.iterator();
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startArray("tokens");
        for (ExtendedAnalyzeToken token : tokens) {
            builder.startObject();
            builder.field("token", token.getTerm());
            builder.field("start_offset", token.getStartOffset());
            builder.field("end_offset", token.getEndOffset());
            builder.field("type", token.getType());
            builder.field("position", token.getPosition());
            builder.field("extended_attributes", token.getExtendedAttrbutes());
            builder.endObject();
        }
        builder.endArray();
        return builder;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        int size = in.readVInt();
        tokens = new ArrayList<ExtendedAnalyzeToken>(size);
        for (int i = 0; i < size; i++) {
            tokens.add(ExtendedAnalyzeToken.readExtendedAnalyzeToken(in));
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeVInt(tokens.size());
        for (ExtendedAnalyzeToken token : tokens) {
            token.writeTo(out);
        }
    }
}
