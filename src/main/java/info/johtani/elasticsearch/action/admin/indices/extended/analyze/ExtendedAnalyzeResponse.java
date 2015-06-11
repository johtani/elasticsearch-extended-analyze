package info.johtani.elasticsearch.action.admin.indices.extended.analyze;


import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Streamable;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 */
public class ExtendedAnalyzeResponse extends ActionResponse implements ToXContent {

    private ExtendedAnalyzeTokenList analyzer;
    private List<CharFilteredText> charfilters;
    private ExtendedAnalyzeTokenList tokenizer;
    private List<ExtendedAnalyzeTokenList> tokenfilters;
    private boolean customAnalyzer = false;
    private boolean shortAttributeName = false;

    ExtendedAnalyzeResponse() {
    }

    public ExtendedAnalyzeResponse(boolean customAnalyzer, ExtendedAnalyzeTokenList analyzer, ExtendedAnalyzeTokenList tokenizer, List<ExtendedAnalyzeTokenList> tokenfilters, List<CharFilteredText> charfilters) {
        this.analyzer = analyzer;
        this.tokenizer = tokenizer;
        this.tokenfilters = tokenfilters;
        this.customAnalyzer = customAnalyzer;
        this.charfilters = charfilters;
    }

    public ExtendedAnalyzeTokenList analyzer() {
        return this.analyzer;
    }

    public ExtendedAnalyzeResponse analyzer(ExtendedAnalyzeTokenList analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public ExtendedAnalyzeTokenList tokenizer() {
        return this.tokenizer;
    }

    public ExtendedAnalyzeResponse tokenizer(ExtendedAnalyzeTokenList tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public List<ExtendedAnalyzeTokenList> tokenfilters() {
        return this.tokenfilters;
    }

    public ExtendedAnalyzeResponse addTokenfilter(ExtendedAnalyzeTokenList tokenfilter) {
        if (tokenfilters == null) {
            tokenfilters = Lists.newArrayList(tokenfilter);
        } else {
            tokenfilters.add(tokenfilter);
        }
        return this;
    }

    public boolean customAnalyzer() {
        return this.customAnalyzer;
    }

    public ExtendedAnalyzeResponse customAnalyzer(boolean customAnalyzer) {
        this.customAnalyzer = customAnalyzer;
        return this;
    }

    public List<CharFilteredText> charfilters() {
        return this.charfilters;
    }

    public ExtendedAnalyzeResponse addCharfilter(CharFilteredText charfilter) {
        if (charfilters == null) {
            charfilters = Lists.newArrayList(charfilter);
        } else {
            this.charfilters.add(charfilter);
        }
        return this;
    }

    private XContentBuilder toXContentExtendedAnalyzeTokenList(XContentBuilder builder, ExtendedAnalyzeTokenList list) throws IOException {
        builder.startArray(list.name);
        for (ExtendedAnalyzeToken token : list.getTokens()) {
            builder.startObject();
            builder.field("token", token.getTerm());
            builder.field("start_offset", token.getStartOffset());
            builder.field("end_offset", token.getEndOffset());
            builder.field("type", token.getType());
            builder.field("position", token.getPosition());
            builder.field("extended_attributes", token.getExtendedAttributes());
            builder.endObject();
        }
        builder.endArray();
        return builder;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.field("custom_analyzer", this.customAnalyzer);

        if (analyzer != null) {
            builder.startObject("analyzer");
            toXContentExtendedAnalyzeTokenList(builder, analyzer);
            builder.endObject();
        }

        if (charfilters != null && !charfilters.isEmpty()) {
            builder.startArray("charfilters");
            for (CharFilteredText charfilter : charfilters) {
                builder.startObject();
                builder.field("name", charfilter.getName());
                builder.field("filtered_text", charfilter.getTexts());
                builder.endObject();
            }
            builder.endArray();
        }

        if (tokenizer != null) {
            builder.startObject("tokenizer");
            toXContentExtendedAnalyzeTokenList(builder, tokenizer);
            builder.endObject();
        }

        if (tokenfilters != null && !tokenfilters.isEmpty()) {
            builder.startArray("tokenfilters");
            for (ExtendedAnalyzeTokenList tokenfilter : tokenfilters) {
                builder.startObject();
                toXContentExtendedAnalyzeTokenList(builder, tokenfilter);
                builder.endObject();
            }
            builder.endArray();
        }

        return builder;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        customAnalyzer = in.readBoolean();
        if (!customAnalyzer) {
            analyzer = ExtendedAnalyzeTokenList.readExtendedAnalyzeTokenList(in);
        } else {
            tokenizer = ExtendedAnalyzeTokenList.readExtendedAnalyzeTokenList(in);
            int size = in.readVInt();
            if (size > 0) {
                tokenfilters = Lists.newArrayListWithCapacity(size);
                for (int i = 0; i < size; i++) {
                    tokenfilters.add(ExtendedAnalyzeTokenList.readExtendedAnalyzeTokenList(in));
                }
            }
            size = in.readVInt();
            if (size > 0) {
                charfilters = Lists.newArrayListWithCapacity(size);
                for (int i = 0; i < size; i++) {
                    charfilters.add(CharFilteredText.readCharFilteredText(in));
                }
            }
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeBoolean(customAnalyzer);
        if (!customAnalyzer) {
            analyzer.writeTo(out);
        } else {
            tokenizer.writeTo(out);
            if (tokenfilters != null) {
                out.writeVInt(tokenfilters.size());
                for (ExtendedAnalyzeTokenList tokenList : tokenfilters) {
                    tokenList.writeTo(out);
                }
            } else {
                out.writeVInt(0);
            }
            if (charfilters != null) {
                out.writeVInt(charfilters.size());
                for (CharFilteredText charfilter : charfilters) {
                    charfilter.writeTo(out);
                }
            } else {
                out.writeVInt(0);
            }
        }
    }

    public static class ExtendedAnalyzeTokenList implements Streamable {
        private List<ExtendedAnalyzeToken> tokens;
        private String name;

        ExtendedAnalyzeTokenList() {
        }

        public ExtendedAnalyzeTokenList(String name, List<ExtendedAnalyzeToken> tokens) {
            this.name = name;
            this.tokens = tokens;
        }

        public static ExtendedAnalyzeTokenList readExtendedAnalyzeTokenList(StreamInput in) throws IOException {
            ExtendedAnalyzeTokenList list = new ExtendedAnalyzeTokenList();
            list.readFrom(in);
            return list;
        }

        public String getName() {
            return this.name;
        }

        public List<ExtendedAnalyzeToken> getTokens() {
            return tokens;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            name = in.readString();
            int size = in.readVInt();
            tokens = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                tokens.add(ExtendedAnalyzeToken.readExtendedAnalyzeToken(in));
            }
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(name);
            out.writeVInt(tokens.size());
            for (ExtendedAnalyzeToken token : tokens) {
                token.writeTo(out);
            }
        }
    }

    public static class ExtendedAnalyzeToken implements Streamable {
        private String term;
        private int startOffset;
        private int endOffset;
        private int position;
        private String type;
        private Map<String, Map<String, Object>> extendedAttributes;

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

        public static ExtendedAnalyzeToken readExtendedAnalyzeToken(StreamInput in) throws IOException {
            ExtendedAnalyzeToken analyzeToken = new ExtendedAnalyzeToken();
            analyzeToken.readFrom(in);
            return analyzeToken;
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

        public Map<String, Map<String, Object>> getExtendedAttributes() {
            return this.extendedAttributes;
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

    public static class CharFilteredText implements Streamable {
        private String name;
        private List<String> texts;

        CharFilteredText() {
        }

        public CharFilteredText(String name, List<String> texts) {
            this.name = name;
            this.texts = texts;
        }

        public String getName() {
            return name;
        }

        public List<String> getTexts() {
            return texts;
        }

        public static CharFilteredText readCharFilteredText(StreamInput in) throws IOException {
            CharFilteredText text = new CharFilteredText();
            text.readFrom(in);
            return text;
        }

        @Override
        public void readFrom(StreamInput in) throws IOException {
            name = in.readString();
            texts = Lists.newArrayList(in.readStringArray());
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            out.writeString(name);
            out.writeStringArray(texts.toArray(Strings.EMPTY_ARRAY));
        }
    }
}
