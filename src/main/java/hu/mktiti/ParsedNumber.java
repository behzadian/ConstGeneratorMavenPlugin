package hu.mktiti;

public class ParsedNumber {
    public final NumberTypes NumberType;
    public final Object TheNumberValue;
    public final short ShortValue;
    public final int IntegerValue;
    public final long LongValue;
    public final float FloatValue;
    public final double DoubleValue;

    public ParsedNumber(NumberTypes numberType, Object number) {
        NumberType = numberType;
        TheNumberValue = number;
        switch (numberType) {
            case Double:
                ShortValue = 0;
                IntegerValue = 0;
                LongValue = 0;
                FloatValue = 0;
                DoubleValue = (double) number;
                break;
            case Float:
                ShortValue = 0;
                IntegerValue = 0;
                LongValue = 0;
                FloatValue = (float) number;
                DoubleValue = 0;
                break;
            case Integer:
                ShortValue = 0;
                IntegerValue = (int) number;
                LongValue = 0;
                FloatValue = 0;
                DoubleValue = 0;
                break;
            case Long:
                ShortValue = 0;
                IntegerValue = 0;
                LongValue = (long) number;
                FloatValue = 0;
                DoubleValue = 0;
                break;
            case Short:
                ShortValue = (short) number;
                IntegerValue = 0;
                LongValue = 0;
                FloatValue = 0;
                DoubleValue = 0;
                break;
            default:
                throw new RuntimeException("");
        }
    }


    public static ParsedNumber parse(String value) {
        try {
            return new ParsedNumber(NumberTypes.Short, Short.parseShort(value));
        } catch (Exception ignored) {
        }
        try {
            return new ParsedNumber(NumberTypes.Integer, Integer.parseInt(value));
        } catch (Exception ignored) {
        }
        try {
            return new ParsedNumber(NumberTypes.Long, Long.parseLong(value));
        } catch (Exception ignored) {
        }
        try {
            return new ParsedNumber(NumberTypes.Float, Float.parseFloat(value));
        } catch (Exception ignored) {
        }
        try {
            return new ParsedNumber(NumberTypes.Double, Double.parseDouble(value));
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String type(ParsedNumber number) {
        return (number == null ? "String" : number.NumberType.javaTypeName);
    }

    public static String value(ParsedNumber number, String raw) {
        return (number == null ? Util.escapeString(raw) : number.TheNumberValue.toString() + number.NumberType.suffix);
    }
}
