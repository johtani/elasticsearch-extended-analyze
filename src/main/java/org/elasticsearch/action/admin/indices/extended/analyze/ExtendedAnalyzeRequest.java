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
package org.elasticsearch.action.admin.indices.extended.analyze;

import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.support.single.custom.SingleCustomOperationRequest;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;

import java.io.IOException;

import static org.elasticsearch.action.ValidateActions.*;

/**
 * TODO: extends AnalyzeRequest? this implement is not smart...
 */
public class ExtendedAnalyzeRequest extends SingleCustomOperationRequest<ExtendedAnalyzeRequest> {

    private String index;
    private String text;
    private String analyzer;
    private String tokenizer;
    private String[] tokenFilters;
    private String field;
    /**
     * specified output attribute names
     */
    private String[] attributes;
    /**
     * output all tokenChain tokens
     */
    private boolean tokenChain = false;

    ExtendedAnalyzeRequest() {

    }

    /**
     * Constructs a new analyzer request for the provided text.
     *
     * @param text The text to analyze
     */
    public ExtendedAnalyzeRequest(String text) {
        this.text = text;
    }

    /**
     * Constructs a new analyzer request for the provided index and text.
     *
     * @param index The index name
     * @param text  The text to analyze
     */
    public ExtendedAnalyzeRequest(@Nullable String index, String text) {
        this.index = index;
        this.text = text;
    }

    public String text() {
        return this.text;
    }

    public ExtendedAnalyzeRequest index(String index) {
        this.index = index;
        return this;
    }

    public String index() {
        return this.index;
    }

    public ExtendedAnalyzeRequest analyzer(String analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public String analyzer() {
        return this.analyzer;
    }

    public ExtendedAnalyzeRequest tokenizer(String tokenizer) {
        this.tokenizer = tokenizer;
        return this;
    }

    public String tokenizer() {
        return this.tokenizer;
    }

    public ExtendedAnalyzeRequest tokenFilters(String... tokenFilters) {
        this.tokenFilters = tokenFilters;
        return this;
    }

    public String[] tokenFilters() {
        return this.tokenFilters;
    }

    public ExtendedAnalyzeRequest attributes(String... attributes) {
        this.attributes = attributes;
        return this;
    }

    public String[] attributes() {
        return this.attributes;
    }

    public ExtendedAnalyzeRequest field(String field) {
        this.field = field;
        return this;
    }

    public String field() {
        return this.field;
    }

    public boolean tokenChain() {
        return this.tokenChain;
    }

    public ExtendedAnalyzeRequest tokenChain(boolean tokenChain) {
        this.tokenChain = tokenChain;
        return this;
    }

    @Override
    public ActionRequestValidationException validate() {
        ActionRequestValidationException validationException = super.validate();
        if (text == null) {
            validationException = addValidationError("text is missing", validationException);
        }
        return validationException;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        index = in.readOptionalString();
        text = in.readString();
        analyzer = in.readOptionalString();
        tokenizer = in.readOptionalString();
        int size = in.readVInt();
        if (size > 0) {
            tokenFilters = new String[size];
            for (int i = 0; i < size; i++) {
                tokenFilters[i] = in.readString();
            }
        }
        field = in.readOptionalString();
        tokenChain = in.readBoolean();
        int attSize = in.readVInt();
        if (size > 0) {
            attributes = new String[attSize];
            for (int i = 0; i < attSize; i++) {
                attributes[i] = in.readString();
            }
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeOptionalString(index);
        out.writeString(text);
        out.writeOptionalString(analyzer);
        out.writeOptionalString(tokenizer);
        if (tokenFilters == null) {
            out.writeVInt(0);
        } else {
            out.writeVInt(tokenFilters.length);
            for (String tokenFilter : tokenFilters) {
                out.writeString(tokenFilter);
            }
        }
        out.writeOptionalString(field);
        out.writeBoolean(tokenChain);
        if (attributes == null) {
            out.writeVInt(0);
        } else {
            out.writeVInt(attributes.length);
            for (String attribute : attributes) {
                out.writeString(attribute);
            }
        }
    }
}
