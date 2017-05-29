package me.tomassetti.kllvm

interface Value {
    fun IRCode() : String
    fun type() : Type
}

class StringReference(val stringConst: StringConst) : Value {
    override fun type() = Pointer(I8Type)

    override fun IRCode() = "i8* getelementptr inbounds ([${stringConst.lengthInBytes()} x i8], [${stringConst.lengthInBytes()} x i8]* @${stringConst.id}, i32 0, i32 0)"
}

data class ValueRef(val name: String, val type: Type) : Value {
    override fun type() = type

    override fun IRCode() = "%$name"
}

class IntConst(val value: Int, val type: Type) : Value {
    override fun type() = type

    override fun IRCode(): String = "$value"
}