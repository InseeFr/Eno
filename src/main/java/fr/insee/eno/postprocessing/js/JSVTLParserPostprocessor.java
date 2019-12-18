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


	public static final String CONCAT_FUNCTION = "concat";
	public static final String SUBSTRING_FUNCTION = "substring";
	public static final String CAST_FUNCTION = "cast";

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



	public String parseToVTL(String input) {
		input = input.replaceAll("!=", " &lt;&gt; ");
		String finalString="";String context="";
		List<String> listContext = new ArrayList<String>();
		boolean isBetweenRealQuote=false;
		for(int i=0;i<input.length();i++) {
			char c = input.charAt(i);
			context = (c==',') ? "" : context+c;
			switch (c) {
			case '(':
				// order functions by descending length
				if(context.contains(CONCAT_FUNCTION)) {
					finalString = finalString.replaceFirst(CONCAT_FUNCTION, "");
					listContext.add(CONCAT_FUNCTION);
				}
				else if(context.contains(SUBSTRING_FUNCTION)) {
					finalString = finalString.replaceFirst(SUBSTRING_FUNCTION, "substr")+c;
					listContext.add(SUBSTRING_FUNCTION);
				}
				else {
					finalString+=c;
					listContext.add(context.replace("(", ""));
				}
				context="";
				break;
			case '\"':
				if(getLastChar(finalString)!='\\') {
					isBetweenRealQuote=!isBetweenRealQuote;
				}				
				finalString+=c;
				break;
			case ',':
				finalString += getLastElement(listContext).equals(CONCAT_FUNCTION) && !isBetweenRealQuote ? " || " : c;
				break;
			case ')':
				finalString += getLastElement(listContext).equals(CONCAT_FUNCTION) ? "" : c;
				removeLast(listContext);
				context="";
				break;
			default:
				finalString+=c;
				break;
			}
		}
		return finalString;
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

	public String toString() {
		return PostProcessing.JS_VTL_PARSER.name();
	}

}
