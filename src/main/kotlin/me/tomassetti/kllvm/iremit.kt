package me.tomassetti.kllvm

import java.util.*
import kotlin.collections.HashMap



data class Label(val name: String)




class Variable(val type: Type, val name: String) {
    fun allocCode() = "%$name = alloca ${type.IRCode()}"
}

/*class IRCode {

    private val stringConsts = HashMap<String, StringConst>()
    private val mainInstructions = LinkedList<Instruction>()
    private val labels = HashMap<String, Label>()
    private val variables = LinkedList<Variable>()
    private var nextTmpIndex = 0

    fun tempValue(value: Instruction) : TempValue{
        val tempValue = TempValue("tmpValue${nextTmpIndex++}", value)
        addInstruction(tempValue)
        return tempValue
    }

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
                  |define i32 @main(i32, i8**) {
                  |    ${variables.map { it.allocCode() }.joinToString(separator = "\n    ")}
                  |    ${mainInstructions.map { it.IRCode() }.joinToString(separator = "\n    ")}
                  |}
                  |
                  |${IRCode::class.java.getResource("/importedDeclarations.ir").readText()}
                  |""".trimMargin("|")
    }
}*/

fun BlockBuilder.parseIntInput(inputName: String, index: Int) {
    this.addInstruction(TempValue("tmp_input_${inputName}_1", GetElementPtr(Pointer(I8Type), ValueRef("1", Pointer(Pointer(I8Type))), IntConst(index + 1, I64Type))))
    this.addInstruction(TempValue("tmp_input_${inputName}_2", Load(Pointer(I8Type), ValueRef("tmp_input_${inputName}_1", Pointer(Pointer(I8Type))))))
    this.addInstruction(TempValue("tmp_input_${inputName}_3", Call(I32Type, "parseInt",
            this.stringConstForContent("Input $inputName").reference(),
            ValueRef("tmp_input_${inputName}_2", Pointer(I8Type)))))
    this.addInstruction(Store(ValueRef("tmp_input_${inputName}_3", I32Type), ValueRef("input_$inputName", Pointer(I32Type))))
}

fun BlockBuilder.parseFloatInput(inputName: String, index: Int) {
    this.addInstruction(TempValue("tmp_input_${inputName}_1", GetElementPtr(Pointer(I8Type), ValueRef("1", Pointer(Pointer(I8Type))), IntConst(index + 1, I64Type))))
    this.addInstruction(TempValue("tmp_input_${inputName}_2", Load(Pointer(I8Type), ValueRef("tmp_input_${inputName}_1", Pointer(Pointer(I8Type))))))
    this.addInstruction(TempValue("tmp_input_${inputName}_3", Call(FloatType, "parseFloat",
            this.stringConstForContent("Input $inputName").reference(),
            ValueRef("tmp_input_${inputName}_2", Pointer(I8Type)))))
    this.addInstruction(Store(ValueRef("tmp_input_${inputName}_3", FloatType), ValueRef("input_$inputName", Pointer(FloatType))))
}

fun BlockBuilder.saveStringInput(inputName: String, index: Int) {
    this.addInstruction(TempValue("tmp_input_${inputName}_1", GetElementPtr(Pointer(I8Type), ValueRef("1", Pointer(Pointer(I8Type))), IntConst(index + 1, I64Type))))
    this.addInstruction(TempValue("tmp_input_${inputName}_2", Load(Pointer(I8Type), ValueRef("tmp_input_${inputName}_1", Pointer(Pointer(I8Type))))))
    this.addInstruction(Store(ValueRef("tmp_input_${inputName}_2", Pointer(I8Type)), ValueRef("input_$inputName", Pointer(Pointer(I8Type)))))
}

fun main(args: Array<String>) {
    val moduleBuilder = ModuleBuilder()
    moduleBuilder.addImportedDefinition(ModuleBuilder::class.java.getResource("/constants.ir").readText())
    moduleBuilder.addImportedDefinition(ModuleBuilder::class.java.getResource("/error.ir").readText())
    moduleBuilder.addImportedDefinition(ModuleBuilder::class.java.getResource("/parseInt.ir").readText())
    moduleBuilder.addImportedDefinition(ModuleBuilder::class.java.getResource("/parseFloat.ir").readText())

    moduleBuilder.addImportedDeclaration(ModuleBuilder::class.java.getResource("/importedDeclarations.ir").readText())

    val main = moduleBuilder.createFunction("main", I32Type, listOf(I32Type, Pointer(Pointer(I8Type))))

    val errorArgsLabel = main.createBlock("errorArgs")
    val okLabel = main.createBlock("ok")

    main.addInstruction(TempValue("comparison", Comparison(ComparisonType.NotEqual, ValueRef("0", I32Type), IntConst(6))))
    main.addInstruction(IfInstruction(ValueRef("comparison", BooleanType), errorArgsLabel, okLabel))


    errorArgsLabel.addInstruction(Printf(errorArgsLabel.stringConstForContent("Number of args is KO\n").reference()))
    errorArgsLabel.addInstruction(ReturnInt(1))

    val inputA = okLabel.addVariable(I32Type, "input_a")
    val inputB = okLabel.addVariable(FloatType, "input_b")
    val inputC = okLabel.addVariable(Pointer(I8Type), "input_c")
    val inputD = okLabel.addVariable(I32Type, "input_d")
    val inputE = okLabel.addVariable(I32Type, "input_e")

    okLabel.addInstruction(Printf(okLabel.stringConstForContent("Number of args is OK\n").reference()))

    okLabel.parseIntInput("a", 0)
    okLabel.parseFloatInput("b", 1)
    okLabel.saveStringInput("c", 2)
    okLabel.parseIntInput("d", 3)
    okLabel.parseIntInput("e", 4)

    okLabel.addInstruction(ReturnInt(0))
    val ir = moduleBuilder.IRCode()
    println(ir)
}
