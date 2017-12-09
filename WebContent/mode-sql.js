ace.define("ace/mode/sql_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;

var SqlHighlightRules = function() {

    var keywords = (
        "not|rr|default|rml|ql|xsd|rrx|rrxf|ogc|schema|geof|map|rdf|rdfs|strdf|vocab|transit|wgs84_pos"
    );

    var builtinConstants = (
        "true|prefix|base"
    );

    var builtinFunctions = (
        "predicate|objectMap|reference|predicateMap|datatype|type|subClassOf|range|domain|subPropertyOf|subjectMap|constant|class|referenceFormulation|source|iterator|template|tableName|column|SQLQuery"
    );

    var dataTypes = (
        "logicalTable|predicateObjectMap|logicalSource"
    );

    var keywordMapper = this.createKeywordMapper({
        "support.function": builtinFunctions,
        "keyword": keywords,
        "constant.language": builtinConstants,
        "storage.type": dataTypes
    }, "identifier", true);

    this.$rules = {
        "start" : [ {
            token : "comment",
            regex : "#.*$"
        },  {
            token : "string",           // " string
            regex : '".*?"'
        }, {
            token : "string",           // ' string
            regex : "'.*?'"
        },  {
            token : "string",           // " string
            regex : '<.*?>'
        },  {
            token : keywordMapper,
            regex : "[a-zA-Z_$][a-zA-Z0-9_$]*\\b"
        },  {
            token : "paren.lparen",
            regex : "[\\(]"
        }, {
            token : "paren.rparen",
            regex : "[\\)]"
        } ]
    };
    this.normalizeRules();
};

oop.inherits(SqlHighlightRules, TextHighlightRules);

exports.SqlHighlightRules = SqlHighlightRules;
});

ace.define("ace/mode/sql",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/sql_highlight_rules","ace/range"], function(require, exports, module) {
"use strict";

var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var SqlHighlightRules = require("./sql_highlight_rules").SqlHighlightRules;
var Range = require("../range").Range;

var Mode = function() {
    this.HighlightRules = SqlHighlightRules;
};
oop.inherits(Mode, TextMode);

(function() {

    this.lineCommentStart = "#";

    this.$id = "ace/mode/sql";
}).call(Mode.prototype);

exports.Mode = Mode;

});
