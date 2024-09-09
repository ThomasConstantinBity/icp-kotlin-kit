package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidFuncParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinTypeDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
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
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.classNameFromVariableName
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper

internal class IDLTypeDeclarationConverter(
    private val fileName: String,
    private val types: List<IDLFileType>
) {

    val generatedClasses = hashMapOf<String, KotlinClassDefinitionType>()

    fun convertTypes(): List<KotlinTypeDefinition> {
        return types.map {
            getKotlinTypeDefinition(
                input = it.typeDefinition,
                fileName = fileName
            )
        }
    }

    private fun getKotlinTypeDefinition(
        input: String,
        fileName: String,
    ): KotlinTypeDefinition {

        val idlTypeDeclaration = CandidTypeParser.parseType(input)
        val comment = KotlinCommentGenerator.getNullableKotlinComment(idlTypeDeclaration.comment)

        val classDefinitionType = getClassDefinition(
            className = idlTypeDeclaration.id,
            parentClassName = fileName,
            type = idlTypeDeclaration.type,
        )
        return KotlinTypeDefinition(
            className = idlTypeDeclaration.id,
            comment = comment,
            candidDefinition = input,
            classDefinitionType = classDefinitionType
        )
    }

    private fun getClassDefinition(
        className: String,
        parentClassName: String?,
        type: IDLType,
    ): KotlinClassDefinitionType {
        val kotlinClassDefinitionType = when(type) {
            is IDLFun -> TODO()

            is IDLTypeFuncDeclaration -> convertIDLTypeFuncDeclaration(
                functionName = className,
                iDLTypeFuncDeclaration = type
            )

            is IDLTypeVec -> convertIDLTypeVec(
                className = className,
                parentClassName = parentClassName,
                idlType = type
            )

            is IDLTypeText,
            is IDLTypeInt,
            is IDLTypeNat,
            is IDLTypeNat64,
            is IDLTypeBoolean,
            is IDLTypeBlob -> KotlinClassDefinitionType.TypeAlias(
                typeAliasId = className,
                className = parentClassName,
                type = type
            )

            is IDLTypeCustom,
            is IDLTypeNull -> KotlinClassDefinitionType.Object(
                objectName = className
            )
            is IDLTypePrincipal -> TODO()

            is IDLTypeRecord -> convertIDLTypeRecord(
                idlType = type,
                className = className,
            )
            is IDLTypeVariant -> convertIDLTypeVariant(
                sealedClassName = className,
                idlTypeVariant = type,
            )
        }
        return kotlinClassDefinitionType
    }

    private fun convertIDLTypeRecord(
        idlType: IDLTypeRecord,
        className: String,
    ): KotlinClassDefinitionType {
        val idlRecordDeclaration = CandidRecordParser.parseRecord(idlType.recordDeclaration)

        val kotlinClass = KotlinClassDefinitionType.Class(className)
        generatedClasses[className] = kotlinClass

        val classParameters = idlRecordDeclaration.records.map {

            val innerClass = when(val type = it.type) {

                is IDLTypeVec -> {
                    // Need to create new class name only if a new record is declared
                    val idlVec = CandidVecParser.parseVec(type.vecDeclaration)
                    if(idlVec.type is IDLTypeCustom) null else {
                        getClassDefinition(
                            // TODO, remove !!
                            className = it.id!!.classNameFromVariableName(),
                            parentClassName = className,
                            type = type
                        )
                    }
                }

                // Class param is a record declaration, need to declare e new class
                is IDLTypeRecord -> TODO()

                else -> { null }
            }

            innerClass?.let { clazz ->
                kotlinClass.innerClasses.add(clazz)
            }

            KotlinClassParameter(
                comment = KotlinCommentGenerator.getNullableKotlinComment(it.comment),
                id = it.id,
                type = it.type,
                isOptional = it.isOptional,
                kotlinClassType = innerClass
                // TODO, class could be declared later on
                // generatedClasses = generatedClasses
            )
        }
        kotlinClass.params.addAll(classParameters)
        return kotlinClass
    }

    private fun getClassToGenerate(idlType: IDLType): List<IDLType> {
        return when(idlType) {

            is IDLTypeCustom ->
                if(generatedClasses.containsKey(idlType.typeDef)) emptyList() else
                    listOf(idlType)

            is IDLTypeRecord,
            is IDLTypeVariant -> { listOf(idlType) }

            is IDLTypeVec -> {
                val idlVec = CandidVecParser.parseVec(idlType.vecDeclaration)
                getClassToGenerate(idlVec.type)
            }
            else -> { emptyList() }
        }
    }

    private fun convertIDLTypeVariant(
        sealedClassName: String,
        idlTypeVariant: IDLTypeVariant,
    ): KotlinClassDefinitionType {
        val idlVariantDeclaration = CandidVariantParser.parseVariant(idlTypeVariant.variantDeclaration)
        val sealedClass = KotlinClassDefinitionType.SealedClass(sealedClassName)
        val classes = idlVariantDeclaration.variants.map {
            val clasDefinition = getClassDefinition(
                parentClassName = null,
                className = it.id ?: IDLTypeHelper.kotlinTypeVariable(it.type),
                type = it.type,
            )
            when(clasDefinition) {
                is KotlinClassDefinitionType.Class -> clasDefinition.inheritedClass = sealedClass
                is KotlinClassDefinitionType.Object -> clasDefinition.inheritedClass = sealedClass
                is KotlinClassDefinitionType.SealedClass -> clasDefinition.inheritedClass = sealedClass
                else -> { }
            }
            clasDefinition
        }
        sealedClass.innerClasses.addAll(classes)
        generatedClasses[sealedClassName] = sealedClass
        return sealedClass
    }

    private fun convertIDLTypeFuncDeclaration(
        functionName: String,
        iDLTypeFuncDeclaration: IDLTypeFuncDeclaration
    ): KotlinClassDefinitionType {
        val idlFun = CandidFuncParser.parseFunc(iDLTypeFuncDeclaration.funcDeclaration)

        val inputArgs = idlFun.inputParams.map {
            (it.idlType as? IDLTypeCustom)?.let { typeCustom ->
                generatedClasses[typeCustom.typeDef]
            } ?: TODO()
        }

        val outputArgs = idlFun.outputParams.map {
            (it.idlType as? IDLTypeCustom)?.let { typeCustom ->
                generatedClasses[typeCustom.typeDef]
            } ?: TODO()
        }

        return KotlinClassDefinitionType.Function(
            functionName = functionName,
            inputArgs = inputArgs,
            outputArgs = outputArgs
        )
    }

    private fun convertIDLTypeVec(
        className: String,
        parentClassName: String?,
        idlType: IDLTypeVec
    ): KotlinClassDefinitionType {

        val idlVec = CandidVecParser.parseVec(idlType.vecDeclaration)

        val classToGenerate = getClassToGenerate(idlVec.type)

        return if (classToGenerate.isNotEmpty())
            getClassDefinition(
                className = className,
                parentClassName = parentClassName,
                type = classToGenerate.first()
            ) else KotlinClassDefinitionType.TypeAlias(
                typeAliasId = className,
                className = parentClassName,
                type = idlVec.type
            )
    }
}