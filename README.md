Extend _analyze API for Elasticsearch
=====================================

This plugin output tokens that like `_analyze` outputs.
But the plugin output more detail with all token attributes.
And the plugin output tokens step by step.

Similar functionality to Solr admin UI analysis page.

|Plugin   |Elasticsearch      |Release date|
|---------|-------------------|------------|
|1.6.0    | >1.6.0            | 2015-06-12 |
|1.5.2    | 1.5.2             | 2015-05-10 |
|1.4.3    | 1.4.3             | 2015-02-19 |
|1.3.0    | 1.3.0             | 2014-07-24 |
|1.2.0    | 1.2.0             | 2014-05-26 |
|1.1.0    | 1.1.0             | 2014-03-29 |
|1.0.0    | 1.0.0             | 2014-02-13 |
|1.0.0.RC2| 1.0.0.RC2         | 2014-02-04 |
|1.0.0.RC1| 1.0.0.RC1         | 2014-01-19 |
|0.7.0    | 0.90.7->0.90      | 2013-11-28 |
|0.6.0    | 0.90.7->0.90      | 2013-11-19 |
|0.5      | 0.90.7->0.90      | 2013-11-14 |

### Feature

1. Output tokens with all attributes. *Implemented.*
2. Output each tokens tokenizer chain. *Implemented.*
    * <strike>Not implemented CharFilter output text.</strike> *Implemented*
    * `attributes` request parameter specify only attributes that include response (over 0.6.0)
3. View on browser token changes. *Not implemented*
4. Support JSON request body and use_short_attr

### Install

This plugin is installed using following command.

```
bin/plugin -i info.johtani/elasticsearch-extended-analyze/1.5.2
```

### example

1. Request example to specify `standard` tokenizer and `lowercase` tokenfilter and `stop` filter.
```json
curl -XPOST 'localhost:9200/_extended_analyze?pretty' -d '
{
  "tokenizer" : "standard",
  "filters" : ["lowercase","stop"],
  "text" : "THIS IS A PEN"
}'
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
      "position" : 0,
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
      "position" : 1,
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
      "position" : 2,
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
      "position" : 3,
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
      "position" : 0,
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
      "position" : 1,
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
      "position" : 2,
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
      "position" : 3,
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
      "position" : 3,
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
And set `true` to `use_short_attr` to display attribute class name only.
And filter the output token attributes using `attributes` parameter.   

```
curl -XPOST 'localhost:9200/_extended_analyze?pretty' -d '
{
  "tokenizer" : "kuromoji_tokenizer",
  "filters" : ["kuromoji_baseform"],
  "text" : "寿司が美味しかった",
  "use_short_attr" : true,
  "attributes" : ["BaseFormAttribute", "PartOfSpearchAttribute", "ReadingAttribute"]
}'
```

response example
```json
{
   "custom_analyzer": true,
   "tokenizer": {
      "kuromoji_tokenizer": [
         {
            "token": "寿司",
            "start_offset": 0,
            "end_offset": 2,
            "type": "word",
            "position": 0,
            "extended_attributes": {
               "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                  "baseForm": null
               },
               "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                  "partOfSpeech": "名詞-一般",
                  "partOfSpeech (en)": "noun-common"
               },
               "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                  "reading (en)": "sushi",
                  "pronunciation": "スシ",
                  "reading": "スシ",
                  "pronunciation (en)": "sushi"
               }
            }
         },
         {
            "token": "が",
            "start_offset": 2,
            "end_offset": 3,
            "type": "word",
            "position": 1,
            "extended_attributes": {
               "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                  "baseForm": null
               },
               "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                  "partOfSpeech": "助詞-格助詞-一般",
                  "partOfSpeech (en)": "particle-case-misc"
               },
               "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                  "reading (en)": "ga",
                  "pronunciation": "ガ",
                  "reading": "ガ",
                  "pronunciation (en)": "ga"
               }
            }
         },
         {
            "token": "美味しかっ",
            "start_offset": 3,
            "end_offset": 8,
            "type": "word",
            "position": 2,
            "extended_attributes": {
               "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                  "baseForm": "美味しい"
               },
               "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                  "partOfSpeech": "形容詞-自立",
                  "partOfSpeech (en)": "adjective-main"
               },
               "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                  "reading (en)": "oishika",
                  "pronunciation": "オイシカッ",
                  "reading": "オイシカッ",
                  "pronunciation (en)": "oishika"
               }
            }
         },
         {
            "token": "た",
            "start_offset": 8,
            "end_offset": 9,
            "type": "word",
            "position": 3,
            "extended_attributes": {
               "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                  "baseForm": null
               },
               "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                  "partOfSpeech": "助動詞",
                  "partOfSpeech (en)": "auxiliary-verb"
               },
               "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                  "reading (en)": "ta",
                  "pronunciation": "タ",
                  "reading": "タ",
                  "pronunciation (en)": "ta"
               }
            }
         }
      ]
   },
   "tokenfilters": [
      {
         "kuromoji_baseform": [
            {
               "token": "寿司",
               "start_offset": 0,
               "end_offset": 2,
               "type": "word",
               "position": 0,
               "extended_attributes": {
                  "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                     "baseForm": null
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                     "partOfSpeech": "名詞-一般",
                     "partOfSpeech (en)": "noun-common"
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                     "reading (en)": "sushi",
                     "pronunciation": "スシ",
                     "reading": "スシ",
                     "pronunciation (en)": "sushi"
                  }
               }
            },
            {
               "token": "が",
               "start_offset": 2,
               "end_offset": 3,
               "type": "word",
               "position": 1,
               "extended_attributes": {
                  "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                     "baseForm": null
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                     "partOfSpeech": "助詞-格助詞-一般",
                     "partOfSpeech (en)": "particle-case-misc"
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                     "reading (en)": "ga",
                     "pronunciation": "ガ",
                     "reading": "ガ",
                     "pronunciation (en)": "ga"
                  }
               }
            },
            {
               "token": "美味しい",
               "start_offset": 3,
               "end_offset": 8,
               "type": "word",
               "position": 2,
               "extended_attributes": {
                  "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                     "baseForm": "美味しい"
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                     "partOfSpeech": "形容詞-自立",
                     "partOfSpeech (en)": "adjective-main"
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                     "reading (en)": "oishika",
                     "pronunciation": "オイシカッ",
                     "reading": "オイシカッ",
                     "pronunciation (en)": "oishika"
                  }
               }
            },
            {
               "token": "た",
               "start_offset": 8,
               "end_offset": 9,
               "type": "word",
               "position": 3,
               "extended_attributes": {
                  "org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute": {
                     "baseForm": null
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute": {
                     "partOfSpeech": "助動詞",
                     "partOfSpeech (en)": "auxiliary-verb"
                  },
                  "org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute": {
                     "reading (en)": "ta",
                     "pronunciation": "タ",
                     "reading": "タ",
                     "pronunciation (en)": "ta"
                  }
               }
            }
         ]
      }
   ]
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
```json
curl -XPOST 'localhost:9200/extended_test/_extended_analyze?pretty' -d '
{
  "analyzer" : "my_analyzer",
  "text" : "THIS IS A phen",
  "use_short_attr" : true
}'
```

response example

```json
{
   "custom_analyzer": true,
   "charfilters": [
      {
         "name": "my_char_filter",
         "filtered_text": "THIS IS A fen"
      }
   ],
   "tokenizer": {
      "kuromoji_tokenizer": [
         {
            "token": "THIS",
            "start_offset": 0,
            "end_offset": 4,
            "type": "word",
            "position": 0,
            "extended_attributes": {
               "BaseFormAttribute": {
                  "baseForm": null
               },
               "InflectionAttribute": {
                  "inflectionForm (en)": null
               },
               "PartOfSpeechAttribute": {
                  "partOfSpeech (en)": "noun-proper-organization"
               },
               "PositionLengthAttribute": {
                  "positionLength": 1
               },
               "ReadingAttribute": {
                  "pronunciation (en)": null
               },
               "TermToBytesRefAttribute": {
                  "bytes": "[54 48 49 53]"
               }
            }
         },
         {
            "token": "IS",
            "start_offset": 5,
            "end_offset": 7,
            "type": "word",
            "position": 1,
            "extended_attributes": {
               "BaseFormAttribute": {
                  "baseForm": null
               },
               "InflectionAttribute": {
                  "inflectionForm (en)": null
               },
               "PartOfSpeechAttribute": {
                  "partOfSpeech (en)": "noun-proper-organization"
               },
               "PositionLengthAttribute": {
                  "positionLength": 1
               },
               "ReadingAttribute": {
                  "pronunciation (en)": null
               },
               "TermToBytesRefAttribute": {
                  "bytes": "[49 53]"
               }
            }
         },
         {
            "token": "A",
            "start_offset": 8,
            "end_offset": 9,
            "type": "word",
            "position": 2,
            "extended_attributes": {
               "BaseFormAttribute": {
                  "baseForm": null
               },
               "InflectionAttribute": {
                  "inflectionForm (en)": null
               },
               "PartOfSpeechAttribute": {
                  "partOfSpeech (en)": "noun-proper-organization"
               },
               "PositionLengthAttribute": {
                  "positionLength": 1
               },
               "ReadingAttribute": {
                  "pronunciation (en)": null
               },
               "TermToBytesRefAttribute": {
                  "bytes": "[41]"
               }
            }
         },
         {
            "token": "fen",
            "start_offset": 10,
            "end_offset": 14,
            "type": "word",
            "position": 3,
            "extended_attributes": {
               "BaseFormAttribute": {
                  "baseForm": null
               },
               "InflectionAttribute": {
                  "inflectionForm (en)": null
               },
               "PartOfSpeechAttribute": {
                  "partOfSpeech (en)": "noun-proper-organization"
               },
               "PositionLengthAttribute": {
                  "positionLength": 1
               },
               "ReadingAttribute": {
                  "pronunciation (en)": null
               },
               "TermToBytesRefAttribute": {
                  "bytes": "[66 65 6e]"
               }
            }
         }
      ]
   },
   "tokenfilters": [
      {
         "kuromoji_baseform": [
            {
               "token": "THIS",
               "start_offset": 0,
               "end_offset": 4,
               "type": "word",
               "position": 0,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[54 48 49 53]"
                  }
               }
            },
            {
               "token": "IS",
               "start_offset": 5,
               "end_offset": 7,
               "type": "word",
               "position": 1,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[49 53]"
                  }
               }
            },
            {
               "token": "A",
               "start_offset": 8,
               "end_offset": 9,
               "type": "word",
               "position": 2,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[41]"
                  }
               }
            },
            {
               "token": "fen",
               "start_offset": 10,
               "end_offset": 14,
               "type": "word",
               "position": 3,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[66 65 6e]"
                  }
               }
            }
         ]
      },
      {
         "kuromoji_readingform": [
            {
               "token": "THIS",
               "start_offset": 0,
               "end_offset": 4,
               "type": "word",
               "position": 0,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[54 48 49 53]"
                  }
               }
            },
            {
               "token": "IS",
               "start_offset": 5,
               "end_offset": 7,
               "type": "word",
               "position": 1,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[49 53]"
                  }
               }
            },
            {
               "token": "A",
               "start_offset": 8,
               "end_offset": 9,
               "type": "word",
               "position": 2,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[41]"
                  }
               }
            },
            {
               "token": "fen",
               "start_offset": 10,
               "end_offset": 14,
               "type": "word",
               "position": 3,
               "extended_attributes": {
                  "BaseFormAttribute": {
                     "baseForm": null
                  },
                  "InflectionAttribute": {
                     "inflectionForm (en)": null
                  },
                  "KeywordAttribute": {
                     "keyword": false
                  },
                  "PartOfSpeechAttribute": {
                     "partOfSpeech (en)": "noun-proper-organization"
                  },
                  "PositionLengthAttribute": {
                     "positionLength": 1
                  },
                  "ReadingAttribute": {
                     "pronunciation (en)": null
                  },
                  "TermToBytesRefAttribute": {
                     "bytes": "[66 65 6e]"
                  }
               }
            }
         ]
      }
   ]
}
```

License
-------

See NOTECE and LICENSE.txt
