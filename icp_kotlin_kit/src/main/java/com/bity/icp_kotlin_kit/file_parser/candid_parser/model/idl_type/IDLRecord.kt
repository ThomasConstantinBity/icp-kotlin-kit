package com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_type

import com.bity.icp_kotlin_kit.file_parser.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.file_parser.candid_parser.model.idl_comment.IDLComment
import com.bity.icp_kotlin_kit.file_parser.file_generator.helper.IDLTypeHelper
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

// TODO rename
internal data class IDLRecord(
    override val comment: IDLComment? = null,
    override val id: String? = null,
    override val isOptional: Boolean = false,
    val recordName: String? = null,
    val types: List<IDLType> = emptyList(),
) : IDLType(
    comment = comment,
    id = id,
    isOptional = isOptional
) {
    companion object : ParserNodeDeclaration<IDLRecord> by reflective()

    override fun typeVariable(className: String?): String {
        requireNotNull(className)
        return className
    }

    override fun getKotlinClassDefinition(): KotlinClassDefinition {
        requireNotNull(recordName)
        val kotlinClassDefinition = KotlinClassDefinition.Class(
            className = recordName
        )
        val params = types.map { idlType ->
            var className: String? = null
            val innerClassToDeclare = IDLTypeHelper.getInnerTypeToDeclare(idlType)
            if(innerClassToDeclare != null) {
                className = "$recordName${idlType.id?.replaceFirstChar { it.uppercase() } ?: ""}"
                kotlinClassDefinition.innerClasses.add(
                    IDLTypeHelper.kotlinDefinition(
                        className = className,
                        idlType = innerClassToDeclare
                    )
                )
            }
            idlType.getKotlinClassParameter(className)
        }
        kotlinClassDefinition.params.addAll(params)
        return kotlinClassDefinition
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as IDLRecord

        if (recordName != other.recordName) return false
        if (types != other.types) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (recordName?.hashCode() ?: 0)
        result = 31 * result + types.hashCode()
        return result
    }
}