package hu.mktiti

fun String.toNumberOrNull(): Number? {
    return toIntOrNull() ?: toLongOrNull() ?: toFloatOrNull() ?: toDoubleOrNull();
}

enum class NumberTypes(val javaTypeName: String, val kotlinTypeName: String, val suffix: String) {
    Short("short","Short",""),
    Integer("int", "Integer",""),
    Long("long", "Long","L"),
    Float("float", "Float","F"),
    Double("double", "Double","d")
}

fun String.numberParsedJavaTypeName(): NumberTypes? {
    return when {
        toShortOrNull() != null -> NumberTypes.Short
        toIntOrNull() != null -> NumberTypes.Integer
        toLongOrNull() != null -> NumberTypes.Long
        toFloatOrNull() != null -> NumberTypes.Float
        toDoubleOrNull() != null -> NumberTypes.Double
        else -> null
    }
}

fun Number.numberJavaCode(numberType: NumberTypes): String {
    return when (numberType) {
        NumberTypes.Short -> toString()
        NumberTypes.Integer -> toString()
        NumberTypes.Long -> toString() + "L"
        NumberTypes.Float -> toString() + "F"
        NumberTypes.Double -> toString() + "d"
    }
}