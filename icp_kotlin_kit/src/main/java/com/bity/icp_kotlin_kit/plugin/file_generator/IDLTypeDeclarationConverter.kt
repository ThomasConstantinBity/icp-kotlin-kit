package com.bity.icp_kotlin_kit.plugin.file_generator

import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidRecordParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidTypeParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVariantParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.CandidVecParser
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinTypeDefinition
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassDefinitionType
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.file_generator.KotlinClassParameter
import com.bity.icp_kotlin_kit.plugin.candid_parser.model.idl_file.IDLFileType
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
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.classNameFromVariableName
import com.bity.icp_kotlin_kit.plugin.candid_parser.util.ext_fun.kotlinVariableName
import com.bity.icp_kotlin_kit.plugin.file_generator.helper.IDLTypeHelper
import java.lang.RuntimeException

internal class IDLTypeDeclarationConverter(
    private val fileName: String,
    private val types: List<IDLFileType>
) {

    private val generatedClasses = hashMapOf<String, KotlinClassDefinitionType>()

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
            is IDLTypeBlob -> {
                val typeAlias = KotlinClassDefinitionType.TypeAlias(
                    typeAliasId = className,
                    className = parentClassName,
                    type = type
                )
                generatedClasses[className] = typeAlias
                typeAlias
            }

            is IDLTypeCustom,
            is IDLTypeNull -> KotlinClassDefinitionType.Object(
                objectName = className
            )

            is IDLTypePrincipal -> TODO()

            is IDLTypeVariant -> convertIDLTypeVariant(
                sealedClassName = className,
                idlTypeVariant = type,
            )

            is IDLRecord -> TODO()
        }
        return kotlinClassDefinitionType
    }

    private fun kotlinClassWithParameter(
        className: String,
        paramId: String,
        type: IDLType,
    ): KotlinClassDefinitionType {
        val kotlinClass = KotlinClassDefinitionType.Class(
            className = className
        )
        val kotlinClassParameter = KotlinClassParameter(
            id = paramId,
            // TODO, check is correct
            isOptional = type.isOptional,
            typeVariable = IDLTypeHelper.kotlinTypeVariable(type, className)
        )
        kotlinClass.params.add(kotlinClassParameter)
        return kotlinClass
    }

    private fun getClassDefinitionForSealedClass(
        variantId: String?,
        type: IDLType,
    ): KotlinClassDefinitionType {

        return when(type) {
            is IDLFun -> TODO()

            is IDLTypeInt,
            is IDLTypeNat,
            is IDLTypeNat64,
            is IDLTypeBlob,
            is IDLTypeText,
            is IDLTypeBoolean -> kotlinClassWithParameter(
                className = variantId ?: IDLTypeHelper.kotlinTypeVariable(type),
                paramId = IDLTypeHelper.kotlinGenericVariableName(type),
                type = type
            )
            is IDLTypeCustom -> {
                TODO()
                /*if(variantId != null) {
                    val kotlinClass = KotlinClassDefinitionType.Class(
                        className = variantId
                    )
                    val kotlinClassParameter = KotlinClassParameter(
                        id = type.typeDef.kotlinVariableName(),
                        isOptional = type.isOptional,
                        typeVariable = IDLTypeHelper.kotlinTypeVariable(type)
                    )
                    kotlinClass.params.add(kotlinClassParameter)
                    return kotlinClass
                } else {
                    return KotlinClassDefinitionType.Object(
                        objectName = type.typeDef
                    )
                }*/
            }

            is IDLTypeNull -> KotlinClassDefinitionType.Object(
                variantId ?: throw RuntimeException("Missing object name")
            )
            is IDLTypePrincipal -> TODO()
            /*is IDLTypeRecord -> {
                val className = variantId ?: IDLTypeHelper.kotlinTypeVariable(type)
                val record = getClassDefinition(
                    className = "${className}Record",
                    parentClassName = null,
                    type = type
                )
                val kotlinClass = KotlinClassDefinitionType.Class(
                    className = className
                )
                kotlinClass.innerClasses.add(record)
                kotlinClass.params.add(
                    KotlinClassParameter(
                        id = className.kotlinVariableName(),
                        isOptional = type.isOptional,
                        typeVariable = "${className}Record"
                    )
                )
                kotlinClass
            }*/
            is IDLTypeVariant -> TODO()

            // TODO, handle vec record?
            is IDLTypeVec -> {
                convertIDLTypeVec(
                    className = variantId ?: IDLTypeHelper.kotlinTypeVariable(type),
                    parentClassName = null,
                    idlType = type
                )
            }

            is IDLRecord -> TODO()
        }
        // TODO, add to hashMap?
    }

    /*private fun convertIDLTypeRecord(
        idlType: IDLTypeRecord,
        className: String,
    ): KotlinClassDefinitionType {
        val idlRecordDeclaration = CandidRecordParser.parseRecord(idlType.recordDeclaration)
        val kotlinClass = KotlinClassDefinitionType.Class(className)
        generatedClasses[className] = kotlinClass

        val classParameters = idlRecordDeclaration.types.map { idlType ->
            val innerClasses = getClassToGenerate(idlType).map {
                getClassDefinition(
                    className = idlType.id?.classNameFromVariableName() ?: TODO(),
                    parentClassName = null,
                    type = it
                )
            }
            kotlinClass.innerClasses.addAll(innerClasses)

            KotlinClassParameter(
                comment = KotlinCommentGenerator.getNullableKotlinComment(idlType.comment),
                id = idlType.id ?: IDLTypeHelper.kotlinTypeVariable(idlType).kotlinVariableName(),
                isOptional = idlType.isOptional,
                typeVariable = IDLTypeHelper.kotlinTypeVariable(idlType, innerClasses.firstOrNull()?.name)
            )
        }
        kotlinClass.params.addAll(classParameters)
        return kotlinClass
    }*/

    private fun getClassToGenerate(idlType: IDLType): List<IDLType> {
        return when(idlType) {

            is IDLTypeCustom ->
                if(generatedClasses.containsKey(idlType.typeDef)) emptyList() else
                    listOf(idlType)

            is IDLRecord,
            is IDLTypeVariant -> { listOf(idlType) }

            is IDLTypeVec -> {
                TODO()
                /*val idlVec = CandidVecParser.parseVec(idlType.vecDeclaration)
                getClassToGenerate(idlVec.type)*/
            }
            else -> { emptyList() }
        }
    }

    private fun convertIDLTypeVariant(
        sealedClassName: String,
        idlTypeVariant: IDLTypeVariant,
    ): KotlinClassDefinitionType {
        val idlVariantDeclaration = TODO() // CandidVariantParser.parseVariant(idlTypeVariant.variantDeclaration)
        /*val sealedClass = KotlinClassDefinitionType.SealedClass(sealedClassName)
        generatedClasses[sealedClassName] = sealedClass
        val classes = idlVariantDeclaration.variants.map {
            val clasDefinition = getClassDefinitionForSealedClass(
                variantId = it.id,
                type = it.type,
            )
            clasDefinition.inheritedClass = sealedClass
            clasDefinition
        }
        generatedClasses[sealedClassName]?.innerClasses?.addAll(classes)
        return sealedClass*/
    }

    private fun convertIDLTypeVec(
        className: String,
        parentClassName: String?,
        idlType: IDLTypeVec
    ): KotlinClassDefinitionType {

        val idlVec = TODO()// CandidVecParser.parseVec(idlType.vecDeclaration)

        /*val classToGenerate = getClassToGenerate(idlVec.type)
        val kotlinArray = KotlinClassDefinitionType.Array(
            arrayName = className,
            parentClassName = parentClassName,
            type = idlVec.type
        )
        classToGenerate.forEach {
            val arrayClassDefinition = when(it) {
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
                is IDLRecord -> getClassDefinition(
                    className = "${className}Record",
                    parentClassName = className,
                    type = it
                )
                is IDLTypeText -> TODO()
                is IDLTypeVariant -> TODO()
                is IDLTypeVec -> TODO()
            }
            kotlinArray.innerClasses.add(arrayClassDefinition)
        }
        return kotlinArray*/
    }
}