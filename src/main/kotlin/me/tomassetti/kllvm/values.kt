package me.tomassetti.kllvm

interface Value {
    fun IRCode() : String
}

class StringReference(val stringConst: StringConst) : Value {
    override fun IRCode() = "i8* getelementptr inbounds ([${stringConst.lengthInBytes()} x i8], [${stringConst.lengthInBytes()} x i8]* @${stringConst.id}, i32 0, i32 0)"
}


data class ValueRef(val name: String, val type: Type? = null) : Value {
    override fun IRCode() = "${type?.IRCode() ?: ""} %$name"
}

class IntConst(val value: Int, val type: Type? = null) : Value {
    override fun IRCode(): String = "${type?.IRCode() ?: ""} $value"

}