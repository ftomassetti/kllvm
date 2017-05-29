package me.tomassetti.kllvm

import java.util.*

interface Instruction {
    fun IRCode() : String
}

class ReturnInt(val value: Int) : Instruction {
    override fun IRCode() = "ret i32 $value"
}

class PlaceLabel(val label: Label) : Instruction {
    override fun IRCode() = "${label.name}:"
}

class Load(val type: Type, val value: Value) : Instruction {
    override fun IRCode() = "load ${type.IRCode()}, ${value.IRCode()}"
}

class IfInstruction(val condition: Value, val yesLabel: BlockBuilder, val noLabel: BlockBuilder) : Instruction {
    override fun IRCode() = "br ${condition.IRCode()}, label %${yesLabel.name}, label %${noLabel.name}"
}

class JumpInstruction(val label: Label) : Instruction {
    override fun IRCode() = "br label %${label.name}"
}

enum class ComparisonType(val code: String) {
    Equal("eq"),
    NotEqual("ne")
}

data class Comparison(val comparisonType: ComparisonType, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "icmp ${comparisonType.code} ${left.IRCode()}, ${right.IRCode()}"

}

class TempValue(val name: String, val instruction: Instruction) : Instruction {
    override fun IRCode(): String = "%$name = ${instruction.IRCode()}"
}


class Store(val value: Value, val destination: Value) : Instruction {
    override fun IRCode(): String {
        return "store ${value.IRCode()}, ${destination.IRCode()}"
    }
}

class GetElementPtr(val type: Type, val pointer: Value, val index: Value) : Value, Instruction {
    override fun IRCode() = "getelementptr inbounds ${type.IRCode()}, ${pointer.IRCode()}, ${index.IRCode()}"
}

class Call(val returnType: Type, val name: String, vararg params: Value) : Value, Instruction {
    private var _params : MutableList<Value> = LinkedList<Value>()
    init {
        params.forEach { _params.add(it) }
    }
    override fun IRCode(): String {
        return "call ${returnType.IRCode()} @$name(${_params.map { it.IRCode() }.joinToString(separator = ", ")})"
    }
}

class Printf(val value: Value) : Instruction {

    override fun IRCode(): String {
        return "call i32 (i8*, ...) @printf(${value.IRCode()})"
    }
}