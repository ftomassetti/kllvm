package me.tomassetti.kllvm

import java.util.*

interface Instruction {
    fun IRCode() : String
    fun type() : Type?
}

class ReturnInt(val value: Int) : Instruction {
    override fun type() = null

    override fun IRCode() = "ret i32 $value"
}

class Load(val value: Value) : Instruction {
    override fun IRCode() = "load ${type().IRCode()}, ${value.type().IRCode()} ${value.IRCode()}"
    override fun type() = (value.type() as Pointer).element
}

class IfInstruction(val condition: Value, val yesLabel: BlockBuilder, val noLabel: BlockBuilder) : Instruction {
    override fun IRCode() = "br ${condition.type().IRCode()} ${condition.IRCode()}, label %${yesLabel.name}, label %${noLabel.name}"
    override fun type() = null
}

class JumpInstruction(val label: Label) : Instruction {
    override fun IRCode() = "br label %${label.name}"
    override fun type() = null
}

enum class ComparisonType(val code: String) {
    Equal("eq"),
    NotEqual("ne")
}

data class Comparison(val comparisonType: ComparisonType, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "icmp ${comparisonType.code} ${left.type().IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = BooleanType
}

class TempValue(val name: String, val value: Instruction) : Instruction {
    override fun IRCode(): String = "%$name = ${value.IRCode()}"
    fun reference() = ValueRef(name, value.type()!!)
    override fun type() = value.type()!!
}


class Store(val value: Value, val destination: Value) : Instruction {
    override fun IRCode(): String {
        return "store ${value.type().IRCode()} ${value.IRCode()}, ${destination.type().IRCode()} ${destination.IRCode()}"
    }
    override fun type() = null
}

class GetElementPtr(val type: Type, val pointer: Value, val index: Value) : Instruction {
    override fun IRCode() = "getelementptr inbounds ${type.IRCode()}, ${pointer.IRCode()}, ${index.IRCode()}"
    override fun type() = type
}

class Call(val returnType: Type, val name: String, vararg params: Value) : Instruction {
    private val _params = LinkedList<Value>()
    init {
        params.forEach { _params.add(it) }
    }
    override fun IRCode(): String {
        return "call ${returnType.IRCode()} @$name(${_params.map {"${it.type().IRCode()} ${it.IRCode()}"}.joinToString(separator = ", ")})"
    }
    override fun type() = returnType
}

class Printf(val value: Value) : Instruction {

    override fun IRCode(): String {
        return "call i32 (i8*, ...) @printf(i8* ${value.IRCode()})"
    }
    override fun type() = null
}

class SignedIntDivision(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "sdiv ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatDivision(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "fdiv ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class IntMultiplication(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "mul ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatMultiplication(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "fmul ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class IntAddition(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "add ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatAddition(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "fadd ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class IntSubtraction(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "sub ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatSubtraction(val type: Type, val left: Value, val right: Value) : Instruction {
    override fun IRCode() = "fsub ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}