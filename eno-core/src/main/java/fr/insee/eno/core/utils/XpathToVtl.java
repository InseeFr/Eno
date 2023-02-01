package fr.insee.eno.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XpathToVtl {

    public static final String XPATH_CONCAT_FUNCTION = "concat";
    public static final String XPATH_SUBSTRING_FUNCTION = "substring";
    public static final String XPATH_CAST_FUNCTION = "cast";
    public static final String XPATH_DIVISION_FUNCTION = " div ";
    public static final String XPATH_NOT_EQUAL_TO = "!=";
    public static final String FAKE_XPATH_EQUAL_TO_NULL = "= null";

    public static final String VTL_CONCAT_FUNCTION = "||";
    public static final String VTL_SUBSTRING_FUNCTION = "substr";
    public static final String VTL_CAST_FUNCTION = "cast";
    public static final String VTL_DIVISION_FUNCTION = " / ";
    public static final String VTL_NOT_EQUAL_TO = " &lt;&gt; ";
    public static final String VTL_EQUAL_TO_NULL_FUNCTION = "isnull";

    private XpathToVtl() {}

    /**
     * This function translates XPATH expression to <a href="https://sdmx.org/?page_id=5096">VTL</a> expression.
     * Examples:
     *     x!=y return x &lt;&gt; y (x &lt;&gt; y)
     *     x div y return x / y
     *     substring(A,1,2) return substr(A,1,2)
     *     concat(A,B,C) return A || B || C
     *     cast(ABCD,integer) = '1' return cast(ABCD,integer) = 1
     *     'ABCD' return "ABCD"
     *     \"hello I'm very happy to be 'here' \" || cast('2021',string) return \" hello I'm very happy to be 'here' \" || cast("2021",string)
     *     cast(A,string) = null return isnull(cast(A,string))
     *
     * @param xpathExpression : the string to parse
     * @return the result of parsing
     */
    public static String parseToVTL(String xpathExpression) {
        // Output string
        String vtlExpression="";
        // Current string read before a '(' (if there is the char ',' (comma), context is reset)
        String context="";
        // List which contains all context (the last context corresponds to the function wrote before '(' )
        List<String> listContext = new ArrayList<>();
        // Local boolean, true if the current char is between the char \" literally (and not " char), so if true, the current char is plain text
        boolean isBetweenRealDoubleQuote=false;
        // Local boolean, true if the current char is between ' literally
        boolean isBetweenRealSimpleQuote=false;
        // Local string to the content between two simple quotes
        String contentBetweenSimpleQuote="";
        // A string which defines what is the type fo the cast function (example : cast(ABCD,string) return string)
        String lastCastType=""; // number, string or integer TODO:date/duration/etc

        //
        for(int i=0;i<xpathExpression.length();i++) {
            char c = xpathExpression.charAt(i);
            context = (c==',') ? "" : context+c;

            // order functions by descending length
            if(context.contains(XPATH_NOT_EQUAL_TO) && !isBetweenRealDoubleQuote) {
                vtlExpression = replaceLast(vtlExpression, XPATH_NOT_EQUAL_TO, VTL_NOT_EQUAL_TO);
            }
            else if(context.contains(XPATH_DIVISION_FUNCTION) && !isBetweenRealDoubleQuote) {
                vtlExpression = replaceLast(vtlExpression, XPATH_DIVISION_FUNCTION, VTL_DIVISION_FUNCTION);
            }
            else if(context.contains(FAKE_XPATH_EQUAL_TO_NULL) && !isBetweenRealDoubleQuote){
                vtlExpression+=c;
                Pattern pattern = Pattern.compile("(cast\\((.)*,(\\s)*(\\w+)\\)) "+FAKE_XPATH_EQUAL_TO_NULL);
                Matcher m = pattern.matcher(vtlExpression);
                if(m.find()) vtlExpression = m.replaceAll(VTL_EQUAL_TO_NULL_FUNCTION+"($1)");
                continue;
            }

            switch (c) {
                case '(' -> {
                    // order functions by descending length
                    if (context.contains(XPATH_CONCAT_FUNCTION) && !isBetweenRealDoubleQuote) {
                        vtlExpression = replaceLast(vtlExpression, XPATH_CONCAT_FUNCTION, "");
                        listContext.add(XPATH_CONCAT_FUNCTION);
                    } else if (context.contains(XPATH_SUBSTRING_FUNCTION) && !isBetweenRealDoubleQuote) {
                        vtlExpression = replaceLast(vtlExpression, XPATH_SUBSTRING_FUNCTION, VTL_SUBSTRING_FUNCTION) + c;
                        listContext.add(XPATH_SUBSTRING_FUNCTION);
                    } else {
                        vtlExpression += c;
                        listContext.add(context.replace("(", ""));
                    }
                    contentBetweenSimpleQuote += isBetweenRealSimpleQuote ? c : "";
                    context = "";
                }
                case '\"' -> {
                    if (getLastChar(vtlExpression) != '\\') {
                        isBetweenRealDoubleQuote = !isBetweenRealDoubleQuote;
                    }
                    vtlExpression += c;
                    contentBetweenSimpleQuote += isBetweenRealSimpleQuote ? c : "";
                }
                case '\'' -> {
                    vtlExpression += c;
                    // remove "'" around number when the last casting function is to integer or number
                    if (!isBetweenRealDoubleQuote) {
                        isBetweenRealSimpleQuote = !isBetweenRealSimpleQuote;
                        if (isNumeric(contentBetweenSimpleQuote) && isCastingToIntegerOrNumber(lastCastType)) {
                            vtlExpression = replaceLast(vtlExpression, "'" + contentBetweenSimpleQuote + "'", contentBetweenSimpleQuote);
                        } else {
                            vtlExpression = replaceLast(vtlExpression, "'" + contentBetweenSimpleQuote + "'", "\"" + contentBetweenSimpleQuote + "\"");
                        }
                        contentBetweenSimpleQuote = "";
                    }
                }
                case ',' -> {
                    vtlExpression += getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) && !isBetweenRealDoubleQuote ? " " + VTL_CONCAT_FUNCTION + " " : c;
                    contentBetweenSimpleQuote += isBetweenRealSimpleQuote ? c : "";
                }
                case ')' -> {
                    vtlExpression += getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) ? "" : c;
                    if (getLastElement(listContext).contains(XPATH_CAST_FUNCTION)) {
                        lastCastType = context.replace(")", "");
                    }
                    removeLast(listContext);
                    contentBetweenSimpleQuote += isBetweenRealSimpleQuote ? c : "";
                    context = "";
                }
                default -> {
                    contentBetweenSimpleQuote += isBetweenRealSimpleQuote ? c : "";
                    vtlExpression += c;
                }
            }
        }
        return vtlExpression;
    }

    private static boolean isCastingToString(String castType) {
        return castType.equals("string");
    }

    private static boolean isCastingToIntegerOrNumber(String castType) {
        return castType.equals("integer") || castType.equals("number");
    }

    public static char getLastChar(String text) {
        return text.isEmpty() ? 0: text.charAt(text.length()-1);
    }

    public static String getLastElement(List<String> contexts) {
        return contexts.isEmpty() ?"": contexts.get(contexts.size()-1);
    }

    public static void removeLast(List<String> contexts) {
        if (!contexts.isEmpty()) {
            contexts.remove(contexts.size()-1);
        }
    }

    /**
     * Function which replaces the last occurrences of a string to another in the input
     * @param string : the input
     * @param substring : the target to replace
     * @param replacement : the replacement
     * @return the new string
     */
    public static String replaceLast(String string, String substring, String replacement){
        int index = string.lastIndexOf(substring);
        if (index == -1)
            return string;
        return string.substring(0, index) + replacement + string.substring(index+substring.length());
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
