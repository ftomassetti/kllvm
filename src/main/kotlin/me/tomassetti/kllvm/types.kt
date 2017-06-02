package me.tomassetti.kllvm

interface Type {
    fun IRCode() : String
    fun isFloatingPointType() : Boolean = false
    fun isIntegerType() : Boolean = false
}

object BooleanType : Type {
    override fun isIntegerType() = true
    override fun IRCode() = "i1"
}

object I8Type : Type {
    override fun isIntegerType() = true
    override fun IRCode() = "i8"
}

object I32Type : Type {
    override fun isIntegerType() = true
    override fun IRCode() = "i32"
}

object I64Type : Type {
    override fun isIntegerType() = true

    override fun IRCode() = "i64"
}

object FloatType : Type {
    override fun isFloatingPointType() = true
    override fun IRCode() = "float"
}

object DoubleType : Type {
    override fun isFloatingPointType() = true
    override fun IRCode() = "float"
}

data class Pointer(val element: Type) : Type {
    override fun IRCode() = "${element.IRCode()}*"
}

object VoidType : Type {
    override fun IRCode() = "void"
}

class CustomType(val code: String) : Type {
    override fun IRCode() = code
}
