package hu.mktiti;

import java.io.PrintWriter;
import java.util.Map;

final class JavaPrinter implements ConstPrinter {

    private final PrinterConf conf;

    JavaPrinter(final PrinterConf conf) {
        this.conf = conf;
    }

    @Override
    public String getDirPath() {
        return "java" + "/" + conf.packageName.replace(".", "/");
    }

    @Override
    public String getFilename() {
        return conf.className + ".java";
    }

    @Override
    public boolean isValid(final String variable) {
        return Util.isValidJavaName(variable);
    }

    @Override
    public void print(final Map<String, String> variables, final PrintWriter target) {
        target.print("package ");
        target.print(conf.packageName);
        target.println(";");
        target.println();

        if (conf.comment != null) {
            target.print("// ");
            target.println(conf.comment);
            target.println();
        }

        target.print(classVisibilityString(conf.visibility));
        target.print("final class ");
        target.print(conf.className);
        target.println(" {\n");

        target.print("\tprivate ");
        target.print(conf.className);
        target.println("() {");
        target.println("\t\tthrow new AssertionError(\"Generated static utility class, should not be instantiated\");");
        target.println("\t}\n");

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            if (conf.useGetters) {
                target.println(getterString(entry.getKey(), entry.getValue(), conf.visibility));
            } else {
                target.println(memberString(entry.getKey(), entry.getValue(), conf.visibility));
            }
        }

        target.println("}");

    }

    private static String classVisibilityString(final PrinterConf.Visibility visibility) {
        return (visibility == PrinterConf.Visibility.PUBLIC) ? "public " : "";
    }

    private static String memberVisibilityString(final PrinterConf.Visibility visibility) {
        if (visibility == PrinterConf.Visibility.PUBLIC) {
            return "public ";
        }
        return "";
    }

    private static String getterString(final String name, final String value, final PrinterConf.Visibility visibility) {
        ParsedNumber number = ParsedNumber.parse(value);
        return "\t"
                + memberVisibilityString(visibility)
                + "static "
                + ParsedNumber.type(number) + " "
                + Util.getGetterName(name) + "() {\n"
                + "\t\treturn "
                + ParsedNumber.value(number, value)
                + ";\n"
                + "\t}\n";
    }

    private static String memberString(final String name, final String value, final PrinterConf.Visibility visibility) {
        ParsedNumber number = ParsedNumber.parse(value);
        return "\t"
                + memberVisibilityString(visibility)
                + "static final "
                + ParsedNumber.type(number) + " "
                + name
                + " = "
                + ParsedNumber.value(number, value)
                + ";\n";
    }
}