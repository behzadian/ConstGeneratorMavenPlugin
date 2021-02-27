package hu.mktiti;

public enum NumberTypes {
    Short("short", "Short", ""),
    Integer("int", "Integer", ""),
    Long("long", "Long", "L"),
    Float("float", "Float", "F"),
    Double("double", "Double", "d");

    public final String javaTypeName;
    public final String kotlinTypeName;
    public final String suffix;

    NumberTypes(String javaTypeName, String kotlinTypeName, String suffix) {
        this.javaTypeName = javaTypeName;
        this.kotlinTypeName = kotlinTypeName;
        this.suffix = suffix;
    }
}
