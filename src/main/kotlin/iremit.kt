import java.util.*
import kotlin.collections.HashMap

private fun String.IREscape() : String {
    return this.replace("\n", "\\0A") + "\\00"
}

data class StringConst(val id: String, val content: String) {
    fun length() : Int {
        return content.length + 1
    }
    fun IRDeclaration() : String {
       return "@$id = private unnamed_addr constant [${length()} x i8] c\"${content.IREscape()}\", align 1"
    }
    fun IRPointerReference() : String {
        return "i8* getelementptr inbounds ([${length()} x i8], [${length()} x i8]* @$id, i32 0, i32 0)"
    }
    fun reference() = StringReference(this)
}

class StringReference(val stringConst: StringConst) : Value {
    override fun IRCode() = "i8* getelementptr inbounds ([${stringConst.length()} x i8], [${stringConst.length()} x i8]* @${stringConst.id}, i32 0, i32 0)"

}

interface Instruction {
    fun IRCode() : String
}

interface ValueInstruction : Instruction {
    val id: String
}

class Printf(val message: StringConst) : Instruction {

    override fun IRCode(): String {
        return "call i32 (i8*, ...) @printf(${message.IRPointerReference()})"
    }
}

class ReturnInt(val value: Int) : Instruction {
    override fun IRCode() = "ret i32 $value"
}

data class Label(val name: String)

class PlaceLabel(val label: Label) : Instruction {
    override fun IRCode() = "${label.name}:"
}

class Load(val type: Type, val value: Value) : Instruction {
    override fun IRCode() = "load ${type.IRCode()}, ${value.IRCode()}"
}

interface Value {
    fun IRCode() : String
}

interface Type {
    fun IRCode() : String
}

object BooleanType : Type {
    override fun IRCode() = "i1"
}

object I8Type : Type {
    override fun IRCode() = "i8"
}

object I32Type : Type {
    override fun IRCode() = "i32"
}

object I64Type : Type {
    override fun IRCode() = "i64"
}

object FloatType : Type {
    override fun IRCode() = "float"
}

data class Pointer(val element: Type) : Type {
    override fun IRCode() = "${element.IRCode()}*"
}

data class ValueRef(val type: Type, val name: String) : Value {
    override fun IRCode() = "${type.IRCode()} %$name"
}

class IfInstruction(val condition: Value, val yesLabel: Label, val noLabel: Label) : Instruction {
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

class IntConst(val value: Int, val type: Type? = null) : Value {
    override fun IRCode(): String = "${type?.IRCode() ?: ""} $value"

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


class Variable(val type: Type, val name: String) {
    fun allocCode() = "%$name = alloca ${type.IRCode()}"
}

class IRCode {

    private val stringConsts = HashMap<String, StringConst>()
    private val mainInstructions = LinkedList<Instruction>()
    private val labels = HashMap<String, Label>()
    private val variables = LinkedList<Variable>()

    fun getLabel(name: String) : Label {
        if (!labels.containsKey(name)) {
            labels[name] = Label(name)
        }
        return labels[name]!!
    }

    fun addVariable(type: Type, name: String) : Variable {
        val variable = Variable(type, name)
        variables.add(variable)
        return variable
    }

    fun addInstruction(instruction: Instruction) {
        mainInstructions.add(instruction)
    }

    fun stringConstForContent(content: String) : StringConst {
        if (!stringConsts.containsKey(content)) {
            stringConsts[content] = StringConst("stringConst${stringConsts.size}", content)
        }
        return stringConsts[content]!!
    }

    fun asString() : String {
        return """|${stringConsts.values.map { it.IRDeclaration() }.joinToString(separator = "\n")}
                  |
                  |${IRCode::class.java.getResource("/constants.ir").readText()}
                  |${IRCode::class.java.getResource("/error.ir").readText()}
                  |${IRCode::class.java.getResource("/parseInt.ir").readText()}
                  |${IRCode::class.java.getResource("/parseFloat.ir").readText()}
                  |
                  |define i32 @main(i32, i8**) #0 {
                  |    ${variables.map { it.allocCode() }.joinToString(separator = "\n    ")}
                  |    ${mainInstructions.map { it.IRCode() }.joinToString(separator = "\n    ")}
                  |}
                  |
                  |${IRCode::class.java.getResource("/importedDeclarations.ir").readText()}
                  |""".trimMargin("|")
    }
}

fun main(args: Array<String>) {
    val irBuilder = IRCode()
    //val exitLabel = irBuilder.getLabel("exit")
    val errorArgsLabel = irBuilder.getLabel("errorArgs")
    val okLabel = irBuilder.getLabel("ok")

    irBuilder.addInstruction(TempValue("comparison", Comparison(ComparisonType.NotEqual, ValueRef(I32Type, "0"), IntConst(4))))
    irBuilder.addInstruction(IfInstruction(ValueRef(BooleanType, "comparison"), errorArgsLabel, okLabel))

    irBuilder.addInstruction(PlaceLabel(errorArgsLabel))
    irBuilder.addInstruction(Printf(irBuilder.stringConstForContent("Number of args is KO\n")))
    irBuilder.addInstruction(ReturnInt(1))

    irBuilder.addInstruction(PlaceLabel(okLabel))

    val inputA = irBuilder.addVariable(I32Type, "input_a")
    val inputB = irBuilder.addVariable(FloatType, "input_b")
    val inputC = irBuilder.addVariable(Pointer(I8Type), "input_c")

    irBuilder.addInstruction(Printf(irBuilder.stringConstForContent("Number of args is OK\n")))

    irBuilder.addInstruction(TempValue("tmp_input_a_1", GetElementPtr(Pointer(I8Type), ValueRef(Pointer(Pointer(I8Type)), "1"), IntConst(1, I64Type))))
    irBuilder.addInstruction(TempValue("tmp_input_a_2", Load(Pointer(I8Type), ValueRef(Pointer(Pointer(I8Type)), "tmp_input_a_1"))))
    irBuilder.addInstruction(TempValue("tmp_input_a_3", Call(I32Type, "parseInt",
            irBuilder.stringConstForContent("Input a").reference(),
            ValueRef(Pointer(I8Type), "tmp_input_a_2"))))
    irBuilder.addInstruction(Store(ValueRef(I32Type, "tmp_input_a_3"), ValueRef(Pointer(I32Type), "input_a")))

    //%tmp1 = getelementptr inbounds i8*, i8** %1, i64 1
    //%tmp2 = load i8*, i8** %tmp1, align 8
    //%tmp3 = call i32 @parseInt(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @stringConst2, i32 0, i32 0), i8* %tmp2)
    //store i32 %tmp3, i32* %input_a, align 4

    irBuilder.addInstruction(ReturnInt(0))
    val ir = irBuilder.asString()
    println(ir)
}
