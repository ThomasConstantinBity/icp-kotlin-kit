package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeFuncDeclaration
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.classNameFromVariableName
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal class IDLFileServiceConverter(
    private val fileName: String,
    private val services: List<IDLService>,
) {

    /**
     * To avoid multiple classes with the same parameter we keep track of the generated classes
     * using an hash map where the key is the record definition (ex: record { text; Value })
     * and the value is the generated class
     */
    private val classForRecordDefinition = hashMapOf<String, KotlinClassDefinitionType>()

    fun getKotlinServiceDefinition(): KotlinClassDefinitionType {
        val serviceClass = KotlinClassDefinitionType.Class(
            className = "${fileName}Service"
        )
        val params = mutableListOf(
            KotlinClassParameter(
                id = "canister",
                typeVariable = "ICPPrincipal",
                isOptional = false,
            )
        )

        // TODO Support Service init args
        serviceClass.params.addAll(params)

        val classFunctions = services.map {
            idlServiceToKotlinClass(it)
        }
        serviceClass.innerClasses.addAll(classFunctions)
        return serviceClass
    }

    private fun idlServiceToKotlinClass(idlService: IDLService): KotlinClassDefinitionType {
        val icpQuery = KotlinClassDefinitionType.ICPQuery(
            comment = idlService.comment,
            queryName = idlService.id,
            inputParamsDeclaration = idlService.inputParamsDeclaration,
            outputParamsDeclaration = idlService.outputParamsDeclaration
        )

        val innerClasses = mutableListOf<KotlinClassDefinitionType>()
        val inputArgs = CandidServiceParamParser
            .parseServiceParam(idlService.inputParamsDeclaration)
            .params
            .map {
                kotlinClassParam(it, null)
            }
        icpQuery.inputArgs.addAll(inputArgs)

        val outputParams = CandidServiceParamParser
            .parseServiceParam(idlService.outputParamsDeclaration)
            .params

        val outputArgs = outputParams.map { param ->
            var className: String? = null
            val outputResponseClasses = innerClassesToDeclare(param).map {
                className = "${idlService.id.classNameFromVariableName()}Response"
                val generatedClass = generateKotlinClassDefinitionType(
                    idlType = it,
                    className = className!!
                )
                generatedClass
            }
            innerClasses.addAll(outputResponseClasses)
            kotlinClassParam(param, className)
        }

        icpQuery.outputArgs.addAll(outputArgs)
        icpQuery.innerClasses.addAll(innerClasses)
        return icpQuery
    }

    private fun kotlinClassParam(
        idlType: IDLType,
        className: String?
    ): KotlinClassParameter {
        val typeVariable = IDLTypeHelper.kotlinTypeVariable(idlType, className)
        return KotlinClassParameter(
            comment = null,
            id = idlType.id ?: typeVariable.kotlinVariableName(),
            isOptional = idlType.isOptional,
            typeVariable = typeVariable
        )
    }

    private fun innerClassesToDeclare(idlType: IDLType): List<IDLType> =
        when(idlType) {
            is IDLTypeInt,
            is IDLTypeNat,
            is IDLTypeNat64,
            is IDLTypeBlob,
            is IDLTypePrincipal,
            is IDLTypeBoolean,
            is IDLTypeText,
            is IDLTypeCustom -> emptyList()
            is IDLTypeRecord -> listOf(idlType)

            is IDLFun -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeNull -> TODO()

            is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(idlType.vecDeclaration)
                innerClassesToDeclare(idlVec.type)
            }
        }

    private fun generateKotlinClassDefinitionType(
        idlType: IDLType,
        className: String
    ): KotlinClassDefinitionType {
        return when(idlType) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> TODO()
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeFuncDeclaration -> TODO()
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNat64 -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLTypeRecord -> {
                if(classForRecordDefinition.containsKey(idlType.recordDeclaration))
                    return classForRecordDefinition[idlType.recordDeclaration]!!
                val recordDeclaration = CandidRecordParser.parseRecord(idlType.recordDeclaration)
                val kotlinClass = KotlinClassDefinitionType.Class(
                    className = className
                )
                val params = recordDeclaration.records.map {
                    val typeVariable = IDLTypeHelper.kotlinTypeVariable(it.type)
                    KotlinClassParameter(
                        comment = KotlinCommentGenerator.getNullableKotlinComment(it.comment),
                        id = it.id ?: typeVariable.kotlinVariableName(),
                        isOptional = it.isOptional,
                        typeVariable = typeVariable
                    )
                }
                kotlinClass.params.addAll(params)
                kotlinClass
            }
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()
        }
    }
}