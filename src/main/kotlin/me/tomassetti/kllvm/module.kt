package me.tomassetti.kllvm

import java.util.*

private fun String.IRCode() = "c\"${this.replace("\n", "\\0A")}\\00\""

data class StringConst(val id: String, val content: String) {
    fun lengthInBytes() : Int {
        return content.length + 1
    }
    fun IRDeclaration() : String {
        return "@$id = private unnamed_addr constant [${lengthInBytes()} x i8] ${content.IRCode()}"
    }
    fun reference() = StringReference(this)
}


class ModuleBuilder {
    private val stringConsts = HashMap<String, StringConst>()
    private val importedDeclarations = LinkedList<String>()
    private val importedDefinitions = LinkedList<String>()
    private val functions = LinkedList<FunctionBuilder>()

    fun stringConstForContent(content: String) : StringConst {
        if (!stringConsts.containsKey(content)) {
            stringConsts[content] = StringConst("stringConst${stringConsts.size}", content)
        }
        return stringConsts[content]!!
    }

    fun addImportedDeclaration(code: String) {
        importedDeclarations.add(code)
    }

    fun addImportedDefinition(code: String) {
        importedDefinitions.add(code)
    }

    fun createFunction(name: String, returnType: Type, paramTypes: List<Type>) : FunctionBuilder {
        val function = FunctionBuilder(this, name, returnType, paramTypes)
        functions.add(function)
        return function
    }

    fun IRCode() : String {
        return """|${stringConsts.values.map { it.IRDeclaration() }.joinToString(separator = "\n")}
                  |
                  |${importedDefinitions.joinToString(separator = "\n")}
                  |
                  |${functions.map { it.IRCode() }.joinToString(separator = "\n\n")}
                  |
                  |${importedDeclarations.joinToString(separator = "\n")}
                  |""".trimMargin("|")
    }
}