package fr.insee.eno.postprocessing;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;

/**
 * Customization of JS postprocessor.
 */
public class JSVTLParserPostprocessor implements Postprocessor {

	

	private static final Logger logger = LoggerFactory.getLogger(JSVTLParserPostprocessor.class);


	@Override
	public File process(File input, byte[] parameters, String surveyName) throws Exception {

		File outputCustomFOFile = new File(
				input.getPath().replace(Constants.EXTERNALIZE_VARIABLES_JS_EXTENSION, Constants.FINAL_JS_EXTENSION));
		logger.info("Start JS parsing xpath to vtl post-processing");

		String inputString = FileUtils.readFileToString(input, StandardCharsets.UTF_8);

		FileUtils.writeStringToFile(outputCustomFOFile, fasterReplacement(inputString), StandardCharsets.UTF_8);
		logger.info("End JS parsing xpath to vtl post-processing");

		return outputCustomFOFile;
	}


	public static final String CONCAT_FUNCTION = "concat";
	public static final String SUBSTRING_FUNCTION = "substring";
	public static final String CAST_FUNCTION = "cast";
	
	public static final String XML_NODE_LABEL = "label";
	public static final String XML_NODE_CONDITIONFILTER = "conditionFilter";
	public static final String XML_NODE_VALUE = "value";
	
	public String fasterReplacement(String input) {
		String possibleNodes = "("+XML_NODE_LABEL+"|"+XML_NODE_CONDITIONFILTER+"|"+XML_NODE_VALUE+")";
		Pattern pattern = Pattern.compile("(<"+possibleNodes+">)(.*|\n)(</"+possibleNodes+">)");
		
		Matcher matcher = pattern.matcher(input);
		StringBuffer stringBuffer = new StringBuffer();
		while(matcher.find()){
			//matcher.group(0)=all expression
			//matcher.group(1)=start of xml node ex:<label>
			//matcher.group(2)=name of xml node ex:label
			//matcher.group(3)=content of xml node
			//matcher.group(4)=end of xml node ex:</label>
			//matcher.group(5)=name of xml node ex:label
			matcher.appendReplacement(stringBuffer,
					matcher.group(1)
				  + parseToVTL(matcher.group(3))
				  + matcher.group(4));
		}
		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}



	public String parseToVTL(String input) {
		input = input.replaceAll("!=", " &lt;&gt; ");
		String finalString="";
		List<String> list = new ArrayList<String>();
		String context="";
		for(int i=0;i<input.length();i++) {
			char c = input.charAt(i);
			context = (c==',') ? "" : context+c;
			switch (c) {
			case '(':
				context = context.replace("(", "");
				if(context.contains(CONCAT_FUNCTION)) {
					finalString = finalString.replaceFirst(CONCAT_FUNCTION, "");
					list.add(CONCAT_FUNCTION);
				}
				else if(context.contains(SUBSTRING_FUNCTION)) {
					finalString = finalString.replaceFirst(SUBSTRING_FUNCTION, "substr")+c;
					list.add(SUBSTRING_FUNCTION);
				}
				else {
					finalString+=c;
					list.add(context);
				}
				context="";
				break;
			case ',':
				finalString += getContext(list).equals(CONCAT_FUNCTION) ? " || " : c;
				break;
			case ')':
				finalString += getContext(list).equals(CONCAT_FUNCTION) ? "" : c;
				removeLast(list);
				context="";
				break;
			default:
				finalString+=c;
				break;
			}
		}
		return finalString;
	}

	public String getContext(List<String> contexts) {
		return contexts.isEmpty() ?"": contexts.get(contexts.size()-1);
	}

	public void removeLast(List<String> contexts) {
		if (!contexts.isEmpty()) {
			contexts.remove(contexts.size()-1);
		}
	}

}
