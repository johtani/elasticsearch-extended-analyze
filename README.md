Extend _analyze API for ElasticSearch
=====================================

This plugin output tokens that like `_analyze` outputs.
But the plugin output more detail with all token attributes.
And the plugin output tokens step by step.

Similar functionality to Solr admin UI analysis page.

|Plugin   |Elasticsearch      |Release date|
|---------|-------------------|------------|
|1.0.0.RC1| 1.0.0.RC1->master | 2014-01-19 |
|0.7.0    | 0.90.7->0.90      | 2013-11-28 |
|0.6.0    | 0.90.7->master    | 2013-11-19 |
|0.5      | 0.90.7->master    | 2013-11-14 |

### Feature

1. Output tokens with all attributes. *Implemented.*
2. Output each tokens tokenizer chain. *Implemented.*
    * <strike>Not implemented CharFilter output text.</strike> *Implemented*
    * `attributes` request parameter specify only attributes that include response (over 0.6.0)
3. View on browser token changes. *Not implemented*

### Install

This plugin is installed using following command.

```
/bin/plugin -i info.johtani/elasticsearch-extended-analyze/1.0.0.RC1
```

__Currently, This plugin is not released Maven Repository.__

### example

1. Request example to specify `standard` tokenizer and `lowercase` tokenfilter and `stop` filter.
```
curl -XPOST 'localhost:9200/_extended_analyze?tokenizer=standard&filters=lowercase,stop&pretty' -d 'THIS IS A PEN'
```

Response example.
```json
{
  "custom_analyzer" : true,
  "tokenizer" : {
    "standard" : [ {
      "token" : "THIS",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "<ALPHANUM>",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[54 48 49 53]"
        }
      }
    }, {
      "token" : "IS",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "<ALPHANUM>",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[49 53]"
        }
      }
    }, {
      "token" : "A",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "<ALPHANUM>",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[41]"
        }
      }
    }, {
      "token" : "PEN",
      "start_offset" : 10,
      "end_offset" : 13,
      "type" : "<ALPHANUM>",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[50 45 4e]"
        }
      }
    } ]
  },
  "tokenfilters" : [ {
    "lowercase" : [ {
      "token" : "this",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "<ALPHANUM>",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[74 68 69 73]"
        }
      }
    }, {
      "token" : "is",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "<ALPHANUM>",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[69 73]"
        }
      }
    }, {
      "token" : "a",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "<ALPHANUM>",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[61]"
        }
      }
    }, {
      "token" : "pen",
      "start_offset" : 10,
      "end_offset" : 13,
      "type" : "<ALPHANUM>",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[70 65 6e]"
        }
      }
    } ]
  }, {
    "stop" : [ {
      "token" : "pen",
      "start_offset" : 10,
      "end_offset" : 13,
      "type" : "<ALPHANUM>",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[70 65 6e]"
        }
      }
    } ]
  } ]
}
```

2. Request example to use elasticsearch-analysis-kuromoji

```
curl -XPOST 'localhost:9200/_extended_analyze?tokenizer=kuromoji_tokenizer&filters=kuromoji_baseform&pretty' -d '寿司が美味しかった'
```

response example
```json
{
  "custom_analyzer" : true,
  "tokenizer" : {
    "kuromoji_tokenizer" : [ {
      "token" : "寿司",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "word",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-common",
          "partOfSpeech" : "名詞-一般"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "sushi",
          "reading" : "スシ",
          "pronunciation (en)" : "sushi",
          "pronunciation" : "スシ"
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e5 af bf e5 8f b8]"
        }
      }
    }, {
      "token" : "が",
      "start_offset" : 2,
      "end_offset" : 3,
      "type" : "word",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "particle-case-misc",
          "partOfSpeech" : "助詞-格助詞-一般"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "ga",
          "reading" : "ガ",
          "pronunciation (en)" : "ga",
          "pronunciation" : "ガ"
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e3 81 8c]"
        }
      }
    }, {
      "token" : "美味しかっ",
      "start_offset" : 3,
      "end_offset" : 8,
      "type" : "word",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : "美味しい"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : "adj-group-i",
          "inflectionType" : "形容詞・イ段",
          "inflectionForm (en)" : "conjunctive-ta-connection",
          "inflectionForm" : "連用タ接続"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "adjective-main",
          "partOfSpeech" : "形容詞-自立"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "oishika",
          "reading" : "オイシカッ",
          "pronunciation (en)" : "oishika",
          "pronunciation" : "オイシカッ"
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e7 be 8e e5 91 b3 e3 81 97 e3 81 8b e3 81 a3]"
        }
      }
    }, {
      "token" : "た",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "word",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : "special-da",
          "inflectionType" : "特殊・タ",
          "inflectionForm (en)" : "base",
          "inflectionForm" : "基本形"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "auxiliary-verb",
          "partOfSpeech" : "助動詞"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "ta",
          "reading" : "タ",
          "pronunciation (en)" : "ta",
          "pronunciation" : "タ"
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e3 81 9f]"
        }
      }
    } ]
  },
  "tokenfilters" : [ {
    "kuromoji_baseform" : [ {
      "token" : "寿司",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "word",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-common",
          "partOfSpeech" : "名詞-一般"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "sushi",
          "reading" : "スシ",
          "pronunciation (en)" : "sushi",
          "pronunciation" : "スシ"
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e5 af bf e5 8f b8]"
        }
      }
    }, {
      "token" : "が",
      "start_offset" : 2,
      "end_offset" : 3,
      "type" : "word",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "particle-case-misc",
          "partOfSpeech" : "助詞-格助詞-一般"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "ga",
          "reading" : "ガ",
          "pronunciation (en)" : "ga",
          "pronunciation" : "ガ"
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e3 81 8c]"
        }
      }
    }, {
      "token" : "美味しい",
      "start_offset" : 3,
      "end_offset" : 8,
      "type" : "word",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : "美味しい"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : "adj-group-i",
          "inflectionType" : "形容詞・イ段",
          "inflectionForm (en)" : "conjunctive-ta-connection",
          "inflectionForm" : "連用タ接続"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "adjective-main",
          "partOfSpeech" : "形容詞-自立"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "oishika",
          "reading" : "オイシカッ",
          "pronunciation (en)" : "oishika",
          "pronunciation" : "オイシカッ"
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e7 be 8e e5 91 b3 e3 81 97 e3 81 84]"
        }
      }
    }, {
      "token" : "た",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "word",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : "special-da",
          "inflectionType" : "特殊・タ",
          "inflectionForm (en)" : "base",
          "inflectionForm" : "基本形"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "auxiliary-verb",
          "partOfSpeech" : "助動詞"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : "ta",
          "reading" : "タ",
          "pronunciation (en)" : "ta",
          "pronunciation" : "タ"
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[e3 81 9f]"
        }
      }
    } ]
  } ]
}
```

3. Analyze Custom Analyzer that defined analyzer in index.
create index command
```
curl -XPUT 'http://localhost:9200/extended_test/' -d'
{
    "index":{
        "analysis":{
             "char_filter" : {
                 "my_char_filter" : {
                     "type" : "mapping",
                     "mappings" : ["ph=>f","qu=>q"]
                  }
            },
            "analyzer" : {
                "my_analyzer" : {
                    "type" : "custom",
                    "tokenizer" : "kuromoji_tokenizer",
                    "filter" : ["kuromoji_baseform", "kuromoji_readingform"],
                    "char_filter" : ["my_char_filter"]
                }
            }
        }
    }
}'
```

request and response.
```
curl -XPOST 'localhost:9200/extended_test/_extended_analyze?analyzer=my_analyzer&pretty' -d 'THIS IS A phen'
{
  "custom_analyzer" : true,
  "charfilters" : [ {
    "name" : "my_char_filter",
    "filterd_text" : "THIS IS A fen"
  } ],
  "tokenizer" : {
    "kuromoji_tokenizer" : [ {
      "token" : "THIS",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "word",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[54 48 49 53]"
        }
      }
    }, {
      "token" : "IS",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "word",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[49 53]"
        }
      }
    }, {
      "token" : "A",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "word",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[41]"
        }
      }
    }, {
      "token" : "fen",
      "start_offset" : 10,
      "end_offset" : 13,
      "type" : "word",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[66 65 6e]"
        }
      }
    } ]
  },
  "tokenfilters" : [ {
    "kuromoji_baseform" : [ {
      "token" : "THIS",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "word",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[54 48 49 53]"
        }
      }
    }, {
      "token" : "IS",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "word",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[49 53]"
        }
      }
    }, {
      "token" : "A",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "word",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[41]"
        }
      }
    }, {
      "token" : "fen",
      "start_offset" : 10,
      "end_offset" : 13,
      "type" : "word",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[66 65 6e]"
        }
      }
    } ]
  }, {
    "kuromoji_readingform" : [ {
      "token" : "THIS",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "word",
      "position" : 1,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[54 48 49 53]"
        }
      }
    }, {
      "token" : "IS",
      "start_offset" : 5,
      "end_offset" : 7,
      "type" : "word",
      "position" : 2,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[49 53]"
        }
      }
    }, {
      "token" : "A",
      "start_offset" : 8,
      "end_offset" : 9,
      "type" : "word",
      "position" : 3,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[41]"
        }
      }
    }, {
      "token" : "fen",
      "start_offset" : 10,
      "end_offset" : 13,
      "type" : "word",
      "position" : 4,
      "extended_attributes" : {
        "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute" : {
          "baseForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute" : {
          "inflectionType (en)" : null,
          "inflectionType" : null,
          "inflectionForm (en)" : null,
          "inflectionForm" : null
        },
        "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute" : {
          "partOfSpeech (en)" : "noun-proper-organization",
          "partOfSpeech" : "名詞-固有名詞-組織"
        },
        "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute" : {
          "reading (en)" : null,
          "reading" : null,
          "pronunciation (en)" : null,
          "pronunciation" : null
        },
        "org.apache.lucene.analysis.tokenattributes.KeywordAttribute" : {
          "keyword" : false
        },
        "org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute" : {
          "positionLength" : 1
        },
        "org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute" : {
          "bytes" : "[66 65 6e]"
        }
      }
    } ]
  } ]
}
```

License
-------

See NOTECE and LICENSE.txt
