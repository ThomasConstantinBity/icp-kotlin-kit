package com.bity.icp_kotlin_kit.plugin.candid_parser

import guru.zoroark.tegral.niwen.lexer.TokenType

internal enum class Token: TokenType {
    SingleLineComment,
    Equals,
    LParen,
    RParen,
    LBrace,
    RBrace,
    Semi,
    Comma,
    Colon,
    Arrow,
    Null,
    Vec,
    Record,
    Variant,
    Func,
    ServiceArgs,
    Service,
    Oneway,
    Query,
    CompositeQuery,
    Blob,
    Type,
    Import,
    Opt,
    Principal,
    Id,
    Text,
    Boolean,

    Float,
    Float64,

    Int,
    Int64,

    Nat,
    Nat8,
    Nat64
}
