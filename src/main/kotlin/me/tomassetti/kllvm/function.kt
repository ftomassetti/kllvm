package me.tomassetti.kllvm

import java.util.*

data class Label(val name: String)

interface Variable {
    fun allocCode() : String
    fun reference() : Value
}

class LocalVariable(val name: String, val type: Type) : Variable {
    override fun allocCode() = "%$name = alloca ${type.IRCode()}"
    override fun reference() = LocalValueRef("$name", Pointer(type))
}

class BlockBuilder(val functionBuilder: FunctionBuilder, val name: String? = null) {
    private val instructions = LinkedList<Instruction>()

    fun addInstruction(instruction: Instruction) {
        instructions.add(instruction)
    }

    fun tempValue(value: Instruction): TempValue {
        val tempValue = TempValue("tmpValue${functionBuilder.tmpIndex()}", value)
        addInstruction(tempValue)
        return tempValue
    }

    fun IRCode(): String {
        return """|${if (name == null) "; unnamed block" else "$name:"}
                  |    ${instructions.map { it.IRCode() }.joinToString(separator = "\n    ")}
                  |""".trimMargin("|")
    }

    fun stringConstForContent(content: String): StringConst {
        return this.functionBuilder.stringConstForContent(content)
    }

    fun addVariable(type: Type, name: String): Variable {
        return this.functionBuilder.addLocalVariable(type, name)
    }

    fun assignVariable(variable: Variable, value: Value) {
        this.addInstruction(Store(value, variable.reference()))
    }

    fun label() : Label {
        if (name == null) throw UnsupportedOperationException()
        return Label(name)
    }

    fun load(value: Value) : Value {
        val tempValue = tempValue(Load(value))
        return tempValue.reference()
    }

}

class FunctionBuilder(val moduleBuilder: ModuleBuilder, val name: String, val returnType: Type, val paramTypes: List<Type>) {
    private val variables = LinkedList<LocalVariable>()
    private var nextTmpIndex = 0
    private val blocks = LinkedList<BlockBuilder>()

    init {
        blocks.add(BlockBuilder(this))
    }

    fun tmpIndex() : Int {
        return nextTmpIndex++
    }

    fun addLocalVariable(type: Type, name: String) : LocalVariable {
        val variable = LocalVariable(name, type)
        variables.add(variable)
        return variable
    }

    fun IRCode() : String {
        return """|define ${returnType.IRCode()} @$name(${paramTypes.map(Type::IRCode).joinToString(separator = ", ")}) {
                  |    ${variables.map { it.allocCode() }.joinToString(separator = "\n    ")}
                  |    ${blocks.map { it.IRCode() }.joinToString(separator = "\n    ")}
                  |}
                  |""".trimMargin("|")
    }

    fun addInstruction(instruction: Instruction) {
        entryBlock().addInstruction(instruction)
    }

    fun stringConstForContent(content: String) : StringConst {
        return this.moduleBuilder.stringConstForContent(content)
    }

    fun createBlock(name: String): BlockBuilder {
        val block = BlockBuilder(this, name)
        blocks.add(block)
        return block
    }

    fun tempValue(value: Instruction) : TempValue {
        return entryBlock().tempValue(value)
    }

    fun entryBlock() = blocks.first

    fun paramReference(index: Int) : Value {
        if (index < 0 || index >= paramTypes.size) {
            throw IllegalArgumentException("Expected an index between 0 and ${paramTypes.size - 1}, found $index")
        }
        return LocalValueRef("$index", paramTypes[index])
    }
}
