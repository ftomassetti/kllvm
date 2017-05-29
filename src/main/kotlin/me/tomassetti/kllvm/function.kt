package me.tomassetti.kllvm

import java.util.*

class BlockBuilder(val functionBuilder: FunctionBuilder, val name: String? = null) {
    private val instructions = LinkedList<Instruction>()

    fun addInstruction(instruction: Instruction) {
        instructions.add(instruction)
    }

    fun tempValue(value: Instruction) : TempValue {
        val tempValue = TempValue("tmpValue${functionBuilder.tmpIndex()}", value)
        addInstruction(tempValue)
        return tempValue
    }

    fun IRCode() : String {
        return """|${if (name==null) "; unnamed block" else "$name:"}
                  |    ${instructions.map { it.IRCode() }.joinToString(separator = "\n    ")}
                  |""".trimMargin("|")
    }

    fun stringConstForContent(content: String) : StringConst {
        return this.functionBuilder.stringConstForContent(content)
    }

    fun addVariable(type: Type, name: String) : Variable {
        return this.functionBuilder.addVariable(type, name)
    }
}

class FunctionBuilder(val moduleBuilder: ModuleBuilder, val name: String, val returnType: Type, val paramTypes: List<Type>) {
    private val variables = LinkedList<Variable>()
    private var nextTmpIndex = 0
    private val blocks = LinkedList<BlockBuilder>()

    init {
        blocks.add(BlockBuilder(this))
    }

    fun tmpIndex() : Int {
        return nextTmpIndex++
    }

    fun addVariable(type: Type, name: String) : Variable {
        val variable = Variable(type, name)
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
        this.blocks.first.addInstruction(instruction)
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
        return blocks.first.tempValue(value)
    }
}
