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

data class GlobalVariable(val name: String, val type: Type, val value: Any) : Variable {
    fun IRDeclaration() : String {
        return "@$name = global ${type.IRCode()} $value"
    }
    override fun reference() = GlobalValueRef(name, type)
    override fun allocCode() = "@$name = alloca ${type.IRCode()}"
}

class ModuleBuilder {
    private val stringConsts = HashMap<String, StringConst>()
    private val importedDeclarations = LinkedList<String>()
    private val importedDefinitions = LinkedList<String>()
    private val functions = LinkedList<FunctionBuilder>()
    private val globalVariables = LinkedList<GlobalVariable>()

    fun intGlobalVariable(name: String, type: Type = I32Type, value: Int = 0) : GlobalVariable {
        val gvar = GlobalVariable(name, type, value)
        globalVariables.add(gvar)
        return gvar
    }

    fun floatGlobalVariable(name: String, type: Type = FloatType, value: Float = 0.0f) : GlobalVariable {
        val gvar = GlobalVariable(name, type, value)
        globalVariables.add(gvar)
        return gvar
    }

    fun stringGlobalVariable(name: String, type: Type = Pointer(I8Type), value: Any = Null(Pointer(I8Type))) : GlobalVariable {
        val gvar = GlobalVariable(name, type, value)
        globalVariables.add(gvar)
        return gvar
    }

    fun globalVariable(name: String, type: Type, value: Any) : GlobalVariable {
        val gvar = GlobalVariable(name, type, value)
        globalVariables.add(gvar)
        return gvar
    }

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
                  |${globalVariables.map { it.IRDeclaration() }.joinToString(separator = "\n")}
                  |
                  |${importedDefinitions.joinToString(separator = "\n")}
                  |
                  |${functions.map { it.IRCode() }.joinToString(separator = "\n\n")}
                  |
                  |${importedDeclarations.joinToString(separator = "\n")}
                  |""".trimMargin("|")
    }
}