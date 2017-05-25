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

class IRCode {

    private val stringConsts = HashMap<String, StringConst>()

    fun stringConstForContent(content: String) : StringConst {
        if (!stringConsts.containsKey(content)) {
            stringConsts[content] = StringConst("stringConst${stringConsts.size}", content)
        }
        return stringConsts[content]!!
    }

    fun asString() : String {
        val sc = stringConstForContent("Hello, Federico, You rocks!!!!\n")
        return """${stringConsts.values.map { it.IRDeclaration() }.joinToString(separator = "\n")}
                  |
                  |define i32 @main(i32, i8**) #0 {
                  |     %_ = call i32 (i8*, ...) @printf(${sc.IRPointerReference()})
                  |     ret i32 0
                  |}
                  |
                  |declare i32 @printf(i8*, ...)""".trimMargin("|")
    }
}


fun main(args: Array<String>) {
    val ir = IRCode().asString()
    println(ir)
}
