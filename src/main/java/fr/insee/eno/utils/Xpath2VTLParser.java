package fr.insee.eno.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Xpath2VTLParser {
	
	public static final String XPATH_CONCAT_FUNCTION = "concat";
	public static final String XPATH_SUBSTRING_FUNCTION = "substring";
	public static final String XPATH_CAST_FUNCTION = "cast";
	public static final String XPATH_DIVISION_FUNCTION = " div ";
	public static final String XPATH_NOT_EQUAL_TO = "!=";
	public static final String FAKE_XPATH_EQUAL_TO_NULL = "= null";
	public static final String XPATH_CURRENT_DATE = "current-date";
	public static final String XPATH_LENGTH_FUNCTION = "string-length";
	public static final String XPATH_MOD = "mod";
	
	public static final String VTL_CONCAT_FUNCTION = "||";
	public static final String VTL_SUBSTRING_FUNCTION = "substr";
	public static final String VTL_CAST_FUNCTION = "cast";
	public static final String VTL_DIVISION_FUNCTION = " / ";
	public static final String VTL_NOT_EQUAL_TO = " &lt;&gt; ";
	public static final String VTL_EQUAL_TO_NULL_FUNCTION = "isnull";
	public static final String VTL_CURRENT_DATE = "current_date";
	public static final String VTL_LENGTH_FUNCTION = "length";
	public static final String VTL_MOD = "mod";

	private static final Pattern patternMod = Pattern.compile("((\\w|\\$)+)"+ "(\\s*"+XPATH_MOD+"\\s*)" + "((\\w|\\$)+)\\s+");
	private static final Pattern patternEqualsNull = Pattern.compile("(cast\\((.)*,(\\s)*(\\w+)\\)) "+FAKE_XPATH_EQUAL_TO_NULL);
	private static final Pattern patternNumerique=Pattern.compile("-?[0-9]+\\.?[0-9]*|-?[0-9]*\\.?[0-9]+");

	public static String parseToVTLInNodes(String input, String possibleNodes) {
		Pattern pattern = Pattern.compile("(<"+possibleNodes+">)((.|\\R)*?)(</"+possibleNodes+">)");

		Matcher matcher = pattern.matcher(input);
		StringBuilder stringBuilder = new StringBuilder();
		while(matcher.find()){
			//matcher.group(0)=all expression
			//matcher.group(1)=start of xml node ex:<label>
			//matcher.group(2)=name of xml node ex:label
			//matcher.group(3)=content of xml node
			//matcher.group(4)=last char in group(3) ?
			//matcher.group(5)=end of xml node ex:</label>
			//matcher.group(6)=name of xml node ex:label
			String replacement = matcher.group(1) + Xpath2VTLParser.parseToVTL(matcher.group(3)) + matcher.group(5);
			matcher.appendReplacement(stringBuilder,"");
			stringBuilder.append(replacement);
		}
		matcher.appendTail(stringBuilder);
		return stringBuilder.toString();
	}



	/**
	 * This function translates XPATH expression to VTL(sdmx) expression
	 * 
	 * Definition of used variables in this function:
	 * 	- finalString is the output
	 * 	- context is the current string read before a '(' (if there is the char , (comma), context is reset)
	 *  - listContext is the list which contains all context (the last context corresponds to the function wrote before '(' )
	 *  - isBetweenRealDoubleQuote : boolean, true if the current char is between the char \" literally (and not " char), so if true, the current char is plain text
	 *  - isBetweenRealSimpleQuote : boolean, true if the current char is between ' literally 
	 *  - lastCastType is a string which defines what is the type fo the cast function (example : cast(ABCD,string) -> string)
	 *  
	 *  Transformations: 
	 *  	x!=y -> x &lt;&gt; y (x <> y)
	 *  	x div y -> x / y
	 *  	substring(A,1,2) -> substr(A,1,2)
	 *  	concat(A,B,C) -> A || B || C
	 *  	cast(ABCD,integer) = '1' -> cast(ABCD,integer) = 1
	 *  	'ABCD' -> "ABCD"
	 *  	\"hello I'm very happy to be 'here' \" || cast('2021',string) -> \" hello I'm very happy to be 'here' \" || cast("2021",string)
	 * 		cast(A,string) = null -> isnull(cast(A,string))
	 *
	 * @param input : the string to parse
	 * @return finalString : the result of parsing
	 */
	public static String parseToVTL(String input) {
		if (input ==null){
			throw new NullPointerException("parameter input in Xpath2VTLParser.parseToVTL shouldn't be null");
		}
		String correctedInput;
		if (!endWithWhitespaceCharacter(input)){
			correctedInput=input+" ";
		}else{
			correctedInput=input;
		}
		StringBuilder finalString=new StringBuilder();
		String context="";
		List<String> listContext = new ArrayList<>();
		boolean isBetweenRealDoubleQuote=false;
		boolean isBetweenRealSimpleQuote=false;
		String contentBetweenSimpleQuote="";
		String lastCastType=""; // number, string or integer TODO:date/duration/etc
		for(char c : correctedInput.toCharArray()) {
			context = (c==',') ? "" : context+c;
			
			// order functions by descending length
			if(context.contains(XPATH_MOD) && !isBetweenRealDoubleQuote) {
				finalString.append(c);
				Matcher m = patternMod.matcher(finalString);
				boolean result = m.find();
				if (result) {
					String replacement=VTL_MOD + "(" + m.group(1) + "," + m.group(4) + ") ";
					do {
						finalString.replace(m.start(),m.end(),replacement);
						result = m.find();
					} while (result);
				}
				continue;
			}
			else if(context.contains(XPATH_CURRENT_DATE) && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_CURRENT_DATE, VTL_CURRENT_DATE);
			}			
			else if(context.contains(XPATH_NOT_EQUAL_TO) && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_NOT_EQUAL_TO, VTL_NOT_EQUAL_TO);
			}
			else if(context.contains(XPATH_DIVISION_FUNCTION) && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_DIVISION_FUNCTION, VTL_DIVISION_FUNCTION);
			}
			else if(context.contains(FAKE_XPATH_EQUAL_TO_NULL) && !isBetweenRealDoubleQuote){
				finalString.append(c);

				Matcher m = patternEqualsNull.matcher(finalString);
				if(m.find()){
					finalString.replace(m.start(),m.end(),VTL_EQUAL_TO_NULL_FUNCTION+"("+m.group(1)+")");
				}

				continue;
			}
			
			switch (c) {
			case '(':
				// order functions by descending length
				if(context.contains(XPATH_CONCAT_FUNCTION) && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_CONCAT_FUNCTION, "");
					listContext.add(XPATH_CONCAT_FUNCTION);
				}
				else if(context.contains(XPATH_LENGTH_FUNCTION) && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_LENGTH_FUNCTION, VTL_LENGTH_FUNCTION).append(c);
					listContext.add(XPATH_LENGTH_FUNCTION);}
				else if(context.contains(XPATH_SUBSTRING_FUNCTION) && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_SUBSTRING_FUNCTION, VTL_SUBSTRING_FUNCTION).append(c);
					listContext.add(XPATH_SUBSTRING_FUNCTION);
				}
				else {
					finalString.append(c);
					listContext.add(context.replace("(", ""));
				}
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				context="";
				break;
			case '\"':
				if(getLastChar(finalString)!='\\') {
					isBetweenRealDoubleQuote=!isBetweenRealDoubleQuote;					
				}
				finalString.append(c);
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				break;
			case '\'':
				finalString.append(c);
				// remove "'" around number when the last casting function is to integer or number
				if(!isBetweenRealDoubleQuote) {
					isBetweenRealSimpleQuote=!isBetweenRealSimpleQuote;
					if(isNumeric(contentBetweenSimpleQuote) && isCastingToIntegerOrNumber(lastCastType)) {
						finalString = replaceLast(finalString, "'"+contentBetweenSimpleQuote+"'", contentBetweenSimpleQuote);
					}
					else {
						finalString = replaceLast(finalString, "'"+contentBetweenSimpleQuote+"'", "\""+contentBetweenSimpleQuote+"\"");
					}
					contentBetweenSimpleQuote="";
				}
				break;
			case ',':
				finalString.append(getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) && !isBetweenRealDoubleQuote ? " "+VTL_CONCAT_FUNCTION+" " : c);
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				break;
			case ')':
				finalString.append(getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) ? "" : c);
				if(getLastElement(listContext).contains(XPATH_CAST_FUNCTION)) {
					lastCastType=context.replace(")", "");
				}
				removeLast(listContext);
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				context="";
				break;
			default:
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				finalString.append(c);
				break;
			}
		}
		if (endWithWhitespaceCharacter(finalString)){
			finalString.deleteCharAt(finalString.length()-1);
		}
		return finalString.toString();
	}

	private static boolean endWithWhitespaceCharacter(CharSequence input){
		return Pattern.matches("\\s",input.subSequence(input.length()-1, input.length()));
	}


	private static boolean isCastingToIntegerOrNumber(String castType) {
		return castType.equals("integer") || castType.equals("number");
	}

	public static char getLastChar(StringBuilder text) {
		return text.length()==0 ? 0: text.charAt(text.length()-1);
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
	 * Function which replaces the last occurences of a string to another in the input
	 * @param string : the input
	 * @param substring : the target to replace
	 * @param replacement : the replacement
	 * @return the new string
	 */
	public static StringBuilder replaceLast(StringBuilder string, String substring, String replacement){

		int index = string.lastIndexOf(substring);
		if (index == -1)
			return string;
		return string.replace(index, index+substring.length(), replacement);
	}
	
	public static boolean isNumeric(String strNum) {

		return patternNumerique.matcher(strNum).matches();

	}




}
