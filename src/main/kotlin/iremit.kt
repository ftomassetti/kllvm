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
}

interface Instruction {
    fun IRCode() : String
}

interface ValueInstruction : Instruction {
    val id: String
}

class Printf(val message: StringConst) : ValueInstruction {
    companion object {
        var nextN = 0
    }

    private val n: Int = nextN++

    override val id: String
        get() = "printf_$n"

    override fun IRCode(): String {
        return "%$id = call i32 (i8*, ...) @printf(${message.IRPointerReference()})"
    }
}

class ReturnInt(val value: Int) : Instruction {
    override fun IRCode() = "ret i32 $value"
}

data class Label(val name: String)

class PlaceLabel(val label: Label) : Instruction {
    override fun IRCode() = "${label.name}:"
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

object IntType : Type {
    override fun IRCode() = "i32"
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

class IntConst(val value: Int) : Value {
    override fun IRCode(): String = "$value"

}

class Store(val type: Type, val value: Value, val destination: Value) : Instruction {
    override fun IRCode(): String {
        return "store ${type.IRCode()} ${value.IRCode()}, ${type.IRCode()}* ${destination.IRCode()}"
    }
}

class GetElementPtr(val type: Type, val pointer: Value, val index: Value) : Value {
    override fun IRCode() = "getelementptr inbounds ${type.IRCode()}, ${type.IRCode()}* ${pointer.IRCode()}, ${index.IRCode()}"
}

class IRCode {

    private val stringConsts = HashMap<String, StringConst>()
    private val mainInstructions = LinkedList<Instruction>()
    private val labels = HashMap<String, Label>()

    fun getLabel(name: String) : Label {
        if (!labels.containsKey(name)) {
            labels[name] = Label(name)
        }
        return labels[name]!!
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
        return """${stringConsts.values.map { it.IRDeclaration() }.joinToString(separator = "\n")}
                  |
                  |define i32 @main(i32, i8**) #0 {
                  |    ${mainInstructions.map { it.IRCode() }.joinToString(separator = "\n    ")}
                  |}
                  |
                  |declare i32 @printf(i8*, ...)""".trimMargin("|")
    }
}

fun main(args: Array<String>) {
    val irBuilder = IRCode()
    //val exitLabel = irBuilder.getLabel("exit")
    val errorArgsLabel = irBuilder.getLabel("errorArgs")
    val okLabel = irBuilder.getLabel("ok")

    irBuilder.addInstruction(TempValue("comparison", Comparison(ComparisonType.NotEqual, ValueRef(IntType, "0"), IntConst(2))))
    irBuilder.addInstruction(IfInstruction(ValueRef(BooleanType, "comparison"), errorArgsLabel, okLabel))

    irBuilder.addInstruction(PlaceLabel(errorArgsLabel))
    irBuilder.addInstruction(Printf(irBuilder.stringConstForContent("Number of args is KO\n")))
    irBuilder.addInstruction(ReturnInt(1))

    irBuilder.addInstruction(PlaceLabel(okLabel))
    irBuilder.addInstruction(Printf(irBuilder.stringConstForContent("Number of args is OK\n")))
    //irBuilder.addInstruction(JumpInstruction(exitLabel))
    //irBuilder.addInstruction(PlaceLabel(exitLabel))
    irBuilder.addInstruction(ReturnInt(0))
    val ir = irBuilder.asString()
    println(ir)
}
