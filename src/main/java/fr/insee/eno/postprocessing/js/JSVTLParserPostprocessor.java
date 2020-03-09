package fr.insee.eno.postprocessing.js;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.PostProcessing;
import fr.insee.eno.postprocessing.Postprocessor;

/**
 * Customization of JS postprocessor.
 */
public class JSVTLParserPostprocessor implements Postprocessor {



	private static final Logger logger = LoggerFactory.getLogger(JSVTLParserPostprocessor.class);


	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputCustomFOFile = new File(input.getParent(),
				Constants.BASE_NAME_FORM_FILE +
				Constants.FINAL_JS_EXTENSION);
		logger.info("Start JS parsing xpath to vtl post-processing");

		String inputString = FileUtils.readFileToString(input, StandardCharsets.UTF_8);
		try {
			FileUtils.writeStringToFile(outputCustomFOFile, parseToVTLInNodes(inputString), StandardCharsets.UTF_8);
		}catch(Exception e) {
			String errorMessage = "An error was occured during the " + toString() + " transformation. "+e.getMessage();
			logger.error(errorMessage);
			throw new EnoGenerationException(errorMessage);
		}
		logger.info("End JS parsing xpath to vtl post-processing");

		return outputCustomFOFile;
	}
	public static final String XPATH_CONCAT_FUNCTION = "concat";
	public static final String XPATH_SUBSTRING_FUNCTION = "substring";
	public static final String XPATH_CAST_FUNCTION = "cast";
	public static final String XPATH_DIVISION_FUNCTION = " div ";
	public static final String XPATH_NOT_EQUAL_TO = "!=";

	public static final String VTL_CONCAT_FUNCTION = "||";
	public static final String VTL_SUBSTRING_FUNCTION = "substr";
	public static final String VTL_CAST_FUNCTION = "cast";
	public static final String VTL_DIVISION_FUNCTION = " / ";
	public static final String VTL_NOT_EQUAL_TO = " &lt;&gt; ";

	public static final String XML_NODE_LABEL = "label";
	public static final String XML_NODE_CONDITIONFILTER = "conditionFilter";
	public static final String XML_NODE_VALUE = "value";
	public static final String XML_NODE_EXPRESSION = "expression";

	public String parseToVTLInNodes(String input) {
		String possibleNodes = "("+XML_NODE_LABEL+"|"+XML_NODE_CONDITIONFILTER+"|"+XML_NODE_VALUE+"|"+XML_NODE_EXPRESSION+")";
		Pattern pattern = Pattern.compile("(<"+possibleNodes+">)((.)*?)(</"+possibleNodes+">)");

		Matcher matcher = pattern.matcher(input);
		StringBuffer stringBuffer = new StringBuffer();
		while(matcher.find()){
			//matcher.group(0)=all expression
			//matcher.group(1)=start of xml node ex:<label>
			//matcher.group(2)=name of xml node ex:label
			//matcher.group(3)=content of xml node
			//matcher.group(4)=last char in group(3) ?
			//matcher.group(5)=end of xml node ex:</label>
			//matcher.group(6)=name of xml node ex:label
			String replacement = matcher.group(1) + parseToVTL(matcher.group(3)) + matcher.group(5);
			matcher.appendReplacement(stringBuffer,"");
			stringBuffer.append(replacement);
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
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
	 *  	cast(ABCD,string) = '1' -> cast(ABCD,string) = \"1\"
	 *  	cast(ABCD,integer) = '1' -> cast(ABCD,integer) = 1
	 *    
	 * @param input : the string to parse
	 * @return finalString : the result of parsing
	 */
	public String parseToVTL(String input) {
		String finalString="";String context="";
		List<String> listContext = new ArrayList<String>();
		boolean isBetweenRealDoubleQuote=false;
		boolean isBetweenRealSimpleQuote=false;
		String contentBetweenSimpleQuote="";
		String lastCastType=""; // number, string or integer TODO:date/duration/etc
		for(int i=0;i<input.length();i++) {
			char c = input.charAt(i);
			context = (c==',') ? "" : context+c;
			
			// order functions by descending length
			if(context.contains(XPATH_NOT_EQUAL_TO) && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_NOT_EQUAL_TO, VTL_NOT_EQUAL_TO);
			}
			else if(context.contains(XPATH_DIVISION_FUNCTION) && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_DIVISION_FUNCTION, VTL_DIVISION_FUNCTION);
			}
			
			switch (c) {
			case '(':
				// order functions by descending length
				if(context.contains(XPATH_CONCAT_FUNCTION) && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_CONCAT_FUNCTION, "");
					listContext.add(XPATH_CONCAT_FUNCTION);
				}
				else if(context.contains(XPATH_SUBSTRING_FUNCTION) && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_SUBSTRING_FUNCTION, VTL_SUBSTRING_FUNCTION)+c;
					listContext.add(XPATH_SUBSTRING_FUNCTION);
				}
				else {
					finalString+=c;
					listContext.add(context.replace("(", ""));
				}
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				context="";
				break;
			case '\"':
				if(getLastChar(finalString)!='\\') {
					isBetweenRealDoubleQuote=!isBetweenRealDoubleQuote;					
				}
				finalString+=c;
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				break;
			case '\'':
				finalString += c;
				// remove "'" around number when the last casting function is to integer or number
				if(!isBetweenRealDoubleQuote) {
					isBetweenRealSimpleQuote=!isBetweenRealSimpleQuote;
					if(isNumeric(contentBetweenSimpleQuote) && isCastingToIntegerOrNumber(lastCastType)) {
						finalString = replaceLast(finalString, "'"+contentBetweenSimpleQuote+"'", contentBetweenSimpleQuote);
					} 
					else if(isCastingToString(lastCastType)) {
						finalString = replaceLast(finalString, "'"+contentBetweenSimpleQuote+"'", "\""+contentBetweenSimpleQuote+"\"");
					}
					contentBetweenSimpleQuote="";
				}
				break;
			case ',':
				finalString += getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) && !isBetweenRealDoubleQuote ? " "+VTL_CONCAT_FUNCTION+" " : c;
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				break;
			case ')':
				finalString += getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) ? "" : c;				
				if(getLastElement(listContext).contains(XPATH_CAST_FUNCTION)) {
					lastCastType=context.replace(")", "");
				}
				removeLast(listContext);
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				context="";
				break;
			default:
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				finalString+=c;
				break;
			}
		}
		return finalString;
	}

	private boolean isCastingToString(String castType) {
		return castType.equals("string");
	}

	private boolean isCastingToIntegerOrNumber(String castType) {
		return castType.equals("integer") || castType.equals("number");
	}

	public char getLastChar(String text) {
		return text.isEmpty() ? 0: text.charAt(text.length()-1);
	}

	public String getLastElement(List<String> contexts) {
		return contexts.isEmpty() ?"": contexts.get(contexts.size()-1);
	}

	public void removeLast(List<String> contexts) {
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
	public String replaceLast(String string, String substring, String replacement){
		int index = string.lastIndexOf(substring);
		if (index == -1)
			return string;
		return string.substring(0, index) + replacement + string.substring(index+substring.length());
	}
	
	public boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	public String toString() {
		return PostProcessing.JS_VTL_PARSER.name();
	}

}
