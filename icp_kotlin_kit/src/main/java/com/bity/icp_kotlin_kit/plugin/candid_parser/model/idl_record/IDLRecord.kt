package com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_record

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import guru.zoroark.tegral.niwen.parser.ParserNodeDeclaration
import guru.zoroark.tegral.niwen.parser.reflective

/**
 * We assume that only one type of comment is present in the record declaration:
 *
 * - Option 1: comment at the end of the line
 * ```candid
 * record {
 *     from_subaccount: opt blob; // The subaccount to transfer the token from
 *     to : Account;
 *     token_id : nat;
 *     memo : opt blob;
 *     created_at_time : opt nat64;
 * }
 * ```
 *
 * - Option 2: comment o top of the variable
 * ```candid
 * record {
 *     // The subaccount to transfer the token from
 *     from_subaccount: opt blob;
 *     to : Account;
 *     token_id : nat;
 *     memo : opt blob;
 *     created_at_time : opt nat64;
 * }
 * ```
 */
internal data class IDLRecord(
    val comment: IDLComment? = null,
    val id: String,
    val isOptional: Boolean = false,
    val type: IDLType,
) {
    companion object : ParserNodeDeclaration<IDLRecord> by reflective()
}