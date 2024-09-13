package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_service.IDLService
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLFun
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLRecord
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBlob
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeBoolean
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeCustom
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeInt
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNat64
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeNull
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypePrincipal
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeText
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVariant
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_type.IDLTypeVec
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.CandidServiceParamParser
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
    private val classForRecordDefinition = hashMapOf<String, KotlinClassDefinition>()
    private var unnamedClassIndex = 1

    fun getKotlinServiceDefinition(): KotlinClassDefinition {
        val serviceClass = KotlinClassDefinition.Class(
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

    private fun idlServiceToKotlinClass(idlService: IDLService): KotlinClassDefinition {
        TODO()
        /*val icpQuery = KotlinClassDefinition.ICPQuery(
            comment = idlService.comment,
            queryName = idlService.id,
            inputParamsDeclaration = idlService.inputParamsDeclaration,
            outputParamsDeclaration = idlService.outputParamsDeclaration
        )

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
            innerClassesToDeclare(param).map {
                className = generateKotlinClassDefinitionType(it)
                val generatedClass = classForRecordDefinition[className]!!
                generatedClass
            }
            kotlinClassParam(param, className)
        }

        icpQuery.outputArgs.addAll(outputArgs)
        return icpQuery*/
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

            is IDLRecord -> listOf(idlType)

            is IDLFun -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeNull -> TODO()

            is IDLTypeVec -> {
                val idlVec = TODO() //  CandidVecParser.parseVec(idlType.vecDeclaration)
                // innerClassesToDeclare(idlVec.type)
                TODO()
            }
        }

    private fun generateKotlinClassDefinitionType(idlType: IDLType): String {
        return when(idlType) {
            is IDLFun -> TODO()
            is IDLTypeBlob -> TODO()
            is IDLTypeBoolean -> TODO()
            is IDLTypeCustom -> TODO()
            is IDLTypeInt -> TODO()
            is IDLTypeNat -> TODO()
            is IDLTypeNat64 -> TODO()
            is IDLTypeNull -> TODO()
            is IDLTypePrincipal -> TODO()
            is IDLRecord -> {
                TODO()
                /*if(classForRecordDefinition.containsKey(idlType.recordDeclaration))
                    return classForRecordDefinition[idlType.recordDeclaration]!!.name
                val className = "UnnamedClass${unnamedClassIndex}"
                val recordDeclaration = CandidRecordParser.parseRecord(idlType.recordDeclaration)
                val kotlinClass = KotlinClassDefinition.Class(
                    className = className
                )
                val params = recordDeclaration.types.map { type ->
                    innerClassesToDeclare(type).map {
                        generateKotlinClassDefinitionType(it)
                    }
                    val typeVariable = IDLTypeHelper.kotlinTypeVariable(type)
                    KotlinClassParameter(
                        comment = KotlinCommentGenerator.getNullableKotlinComment(type.comment),
                        id = type.id ?: typeVariable.kotlinVariableName(),
                        isOptional = type.isOptional,
                        typeVariable = typeVariable
                    )
                }
                kotlinClass.params.addAll(params)
                classForRecordDefinition[idlType.recordDeclaration] = kotlinClass
                unnamedClassIndex++
                classForRecordDefinition
                className*/
            }
            is IDLTypeText -> TODO()
            is IDLTypeVariant -> TODO()
            is IDLTypeVec -> TODO()
        }
    }
}