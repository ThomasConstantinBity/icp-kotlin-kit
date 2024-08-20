package com.bity.icp_kotlin_kit.plugin.candid_parser.util

import com.bity.icp_kotlin_kit.plugin.candid_parser.Token
import guru.zoroark.tegral.niwen.lexer.matchers.matches
import guru.zoroark.tegral.niwen.lexer.niwenLexer

internal val lexer = niwenLexer {
    state {
        matches("//.*") isToken Token.SingleLineComment
        "/*" isToken Token.StartComment
        "=" isToken Token.Equals
        "(" isToken Token.LParen
        ")" isToken Token.RParen
        "{" isToken Token.LBrace
        "}" isToken Token.RBrace
        ";" isToken Token.Semi
        "," isToken Token.Comma
        "." isToken Token.Dot
        ":" isToken Token.Colon
        "->" isToken Token.Arrow
        "null" isToken Token.Null
        "text" isToken Token.Text

        matches("vec record \\{[^}]+\\}") isToken Token.VecRecord
        matches ("vec [^;]+") isToken Token.Vec
        matches("record \\{[^}]+\\}") isToken Token.Record
        matches("variant\\s*\\{[^{}]*+(?:\\{[^{}]*+}[^{}]*+)*}") isToken Token.Variant
        matches("func \\([^}]+\\)( query)?") isToken Token.Func

        "service" isToken Token.Service
        "oneway" isToken Token.Oneway
        "query" isToken Token.Query
        "composite_query" isToken Token.CompositeQuery
        "blob" isToken Token.Blob
        "type" isToken Token.Type
        "import" isToken Token.Import
        "opt" isToken Token.Opt
        "==" isToken Token.TestEqual
        "!=" isToken Token.NotEqual
        "!:" isToken Token.NotDecode
        "principal" isToken Token.Principal

        "int" isToken Token.Int

        "nat64" isToken Token.Nat64
        "nat" isToken Token.Nat

        matches("true|false") isToken Token.Boolean
        matches("[+-]") isToken Token.Sign


        matches("0[xX][0-9a-fA-F][_0-9a-fA-F]*") isToken Token.Hex
        matches("[0-9][_0-9]*") isToken Token.Decimal
        matches("[0-9]*\\.[0-9]*") isToken Token.Float
        matches("[0-9]+(\\.[0-9]*)?[eE][+-]?[0-9]+") isToken Token.Float
        matches("[a-zA-Z_][a-zA-Z0-9_]*") isToken Token.Id

        // matches("\".*? ?\"") isToken Token.String
        matches("[ \t\r\n]+").ignore
        matches("//[^\n]*").ignore
    }
}