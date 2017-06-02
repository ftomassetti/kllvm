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

class Return(val value: Value) : Instruction {
    override fun type() = null

    override fun IRCode() = "ret ${value.type().IRCode()} ${value.IRCode()}"
}

class ReturnVoid : Instruction {
    override fun IRCode() = "ret void"

    override fun type() = VoidType
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
    fun reference() = LocalValueRef(name, value.type()!!)
    override fun type() = value.type()!!
}

class Store(val value: Value, val destination: Value) : Instruction {
    override fun IRCode(): String {
        return "store ${value.type().IRCode()} ${value.IRCode()}, ${destination.type().IRCode()} ${destination.IRCode()}"
    }
    override fun type() = null
}

class GetElementPtr(val type: Type, val pointer: Value, val index: Value) : Instruction {
    override fun IRCode() = "getelementptr inbounds ${type.IRCode()}, ${pointer.type().IRCode()} ${pointer.IRCode()}, i64 ${index.IRCode()}"
    override fun type() = Pointer(type)
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

class CallWithBitCast(val declaration: FunctionDeclaration, vararg params: Value) : Instruction {
    private val _params = LinkedList<Value>()
    init {
        params.forEach { _params.add(it) }
    }
    override fun IRCode(): String {
        val argTypesStrs = LinkedList<String>()
        _params.forEach { argTypesStrs.add(it.type().IRCode()) }
        if (declaration.varargs) {
            argTypesStrs.add("...")
        }
        val adaptedSignature = "${declaration.returnType.IRCode()} (${argTypesStrs.joinToString(separator = ", ")})"
        val paramsStr = _params.map { "${it.type().IRCode()} ${it.IRCode()}" }.joinToString(separator = ", ")
        return "call $adaptedSignature bitcast (${declaration.ptrSignature()} @${declaration.name} to $adaptedSignature*)($paramsStr)"
    }
    override fun type() = declaration.returnType
}

class Printf(val stringFormat: Value, vararg params: Value) : Instruction {
    private val _params = params

    override fun IRCode(): String {
        var paramsString = ""
        _params.forEach { paramsString += ", ${it.type().IRCode()} ${it.IRCode()}" }
        return "call i32 (i8*, ...) @printf(i8* ${stringFormat.IRCode()}$paramsString)"
    }
    override fun type() = null
}

class SignedIntDivision(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "sdiv ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatDivision(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "fdiv ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class IntMultiplication(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "mul ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatMultiplication(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "fmul ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class IntAddition(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "add ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatAddition(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "fadd ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class IntSubtraction(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "sub ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class FloatSubtraction(val left: Value, val right: Value) : Instruction {
    val type = left.type()
    override fun IRCode() = "fsub ${type.IRCode()} ${left.IRCode()}, ${right.IRCode()}"
    override fun type() = type
}

class ConversionFloatToSignedInt(val value: Value, val targetType: Type) : Instruction {
    override fun IRCode() = "fptosi ${value.type().IRCode()} ${value.IRCode()} to ${targetType.IRCode()}"

    override fun type() = targetType
}

class ConversionSignedIntToFloat(val value: Value, val targetType: Type) : Instruction {
    override fun IRCode() = "sitofp ${value.type().IRCode()} ${value.IRCode()} to ${targetType.IRCode()}"

    override fun type() = targetType
}
