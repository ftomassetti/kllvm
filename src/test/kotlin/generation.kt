package me.tomassetti.kllvm

import kotlin.test.assertEquals
import org.junit.Test as test

class GenerationTests {

    @test fun generateSum() {
        val EXIT_CODE_OK = 0
        val EXIT_CODE_WRONG_PARAMS = 1
        val N_PARAMS_EXPECTED = 2
        val STRING_TYPE = Pointer(I8Type)

        val module = ModuleBuilder()
        val mainFunction = module.createMainFunction()

        val atoiDeclaration = FunctionDeclaration("atoi", I32Type, listOf(), varargs = true)
        module.addDeclaration(atoiDeclaration)
        module.addDeclaration(FunctionDeclaration("printf", I32Type, listOf(STRING_TYPE), varargs = true))

        val okParamsBlock = mainFunction.createBlock("okParams")
        val koParamsBlock = mainFunction.createBlock("koParams")

        val comparisonResult = mainFunction.tempValue(Comparison(ComparisonType.Equal,
                mainFunction.paramReference(0), IntConst(N_PARAMS_EXPECTED + 1, I32Type)))
        mainFunction.addInstruction(IfInstruction(comparisonResult.reference(), okParamsBlock, koParamsBlock))

        // OK Block : convert to int, sum, and print
        val aAsStringPtr = okParamsBlock.tempValue(GetElementPtr(STRING_TYPE, mainFunction.paramReference(1), IntConst(1, I64Type)))
        val aAsString = okParamsBlock.load(aAsStringPtr.reference())
        val aAsInt = okParamsBlock.tempValue(CallWithBitCast(atoiDeclaration, aAsString))
        val bAsStringPtr = okParamsBlock.tempValue(GetElementPtr(STRING_TYPE, mainFunction.paramReference(1), IntConst(2, I64Type)))
        val bAsString = okParamsBlock.load(bAsStringPtr.reference())
        val bAsInt = okParamsBlock.tempValue(CallWithBitCast(atoiDeclaration, bAsString))
        val sum = okParamsBlock.tempValue(IntAddition(aAsInt.reference(), bAsInt.reference()))
        okParamsBlock.addInstruction(Printf(mainFunction.stringConstForContent("Result: %d\n").reference(), sum.reference()))
        okParamsBlock.addInstruction(ReturnInt(EXIT_CODE_OK))

        // KO Block : error message and exit
        koParamsBlock.addInstruction(Printf(mainFunction.stringConstForContent("Please specify two arguments").reference()))
        koParamsBlock.addInstruction(ReturnInt(EXIT_CODE_WRONG_PARAMS))

        println(module.IRCode())
        assertEquals("""@stringConst0 = private unnamed_addr constant [12 x i8] c"Result: %d\0A\00"
@stringConst1 = private unnamed_addr constant [29 x i8] c"Please specify two arguments\00"



declare i32 @atoi(...)
declare i32 @printf(i8*, ...)

define i32 @main(i32, i8**) {

    ; unnamed block
    %tmpValue0 = icmp eq i32 %0, 3
    br i1 %tmpValue0, label %okParams, label %koParams

    okParams:
    %tmpValue1 = getelementptr inbounds i8*, i8** %1, i64 1
    %tmpValue2 = load i8*, i8** %tmpValue1
    %tmpValue3 = call i32 (i8*, ...) bitcast (i32 (...)* @atoi to i32 (i8*, ...)*)(i8* %tmpValue2)
    %tmpValue4 = getelementptr inbounds i8*, i8** %1, i64 2
    %tmpValue5 = load i8*, i8** %tmpValue4
    %tmpValue6 = call i32 (i8*, ...) bitcast (i32 (...)* @atoi to i32 (i8*, ...)*)(i8* %tmpValue5)
    %tmpValue7 = add i32 %tmpValue3, %tmpValue6
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([12 x i8], [12 x i8]* @stringConst0, i32 0, i32 0), i32 %tmpValue7)
    ret i32 0

    koParams:
    call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([29 x i8], [29 x i8]* @stringConst1, i32 0, i32 0))
    ret i32 1

}""", module.IRCode().trim())
    }

}
