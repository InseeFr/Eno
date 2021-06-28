package fr.insee.eno.utils;

import fr.insee.eno.exception.UnsupportedPatternException;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
	public static final String VTL_NOT_EQUAL_TO = " <> ";
	public static final String VTL_EQUAL_TO_NULL_FUNCTION = "isnull";
	public static final String VTL_CURRENT_DATE = "current_date";
	public static final String VTL_LENGTH_FUNCTION = "length";
	public static final String VTL_MOD = "mod";

	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	private static final Pattern patternMod = Pattern.compile("(\\$?\\w*(?:\\d|(?:[a-zA-Z] )|\\$))(?:\\s*"+XPATH_MOD+"\\s*)((?:\\d|(?: [a-zA-Z])|\\$)\\w*\\$?)\\s+");
	private static final Pattern patternEqualsNull = Pattern.compile("(cast\\((.)*,(\\s)*(\\w+)\\)) "+FAKE_XPATH_EQUAL_TO_NULL);
	private static final Pattern patternNumerique=Pattern.compile("-?[0-9]+\\.?[0-9]*|-?[0-9]*\\.?[0-9]+");
	private static final Pattern patternCommaParenthesis=Pattern.compile(".*,\\s*\\(");


	private final Set<String> nodes;
	private final Charset charset;
	private final XMLEventFactory eventFactory = XMLEventFactory.newInstance();

	public Xpath2VTLParser(Set<String> nodes, Charset charset){
		this.nodes=Objects.requireNonNull(nodes);
		this.charset=charset==null?DEFAULT_CHARSET:charset;
	}

	private Stream<Pair<QName, XMLEvent>> streamOfXmlEventsWithTagNames(XMLEventReader eventReader){
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<>() {

			private final Deque<QName> stack = new LinkedList<>(Collections.singleton(new QName("ROOT")));

			@Override
			public boolean hasNext() {
				return eventReader.hasNext();
			}

			@Override
			public Pair<QName, XMLEvent> next() {
				XMLEvent xmlEvent = (XMLEvent) Objects.requireNonNull(eventReader.next());
				if (xmlEvent.isStartElement()) {
					stack.push(xmlEvent.asStartElement().getName());
				}
				if (xmlEvent.isEndElement()) {
					stack.pop();
				}
				return Pair.of(stack.getFirst(), xmlEvent);
			}
		},0), false);
	}

	private XMLEvent parseXPath2VTLInLegitimatePairs(Pair<QName, XMLEvent> xmlEventWithTagName){
		XMLEvent retour;
		if (nodes.contains(xmlEventWithTagName.getLeft().getLocalPart())
				&& xmlEventWithTagName.getRight().isCharacters()
				&& !xmlEventWithTagName.getRight().asCharacters().isWhiteSpace()) {
			retour = eventFactory.createCharacters(Xpath2VTLParser.parseToVTL(xmlEventWithTagName.getRight().asCharacters().getData()));
		} else {
			retour = xmlEventWithTagName.getRight();
		}
		return retour;
	}

	public OutputStream parseXPathToVTLFromInputStreamInNodes(InputStream inputStream, OutputStream outputStream) throws XMLStreamException {

		XMLEventReader eventReader = XMLInputFactory.newFactory()
				.createXMLEventReader(Objects.requireNonNull(inputStream), this.charset.name());
		XMLEventWriter eventWriter = XMLOutputFactory.newFactory()
				.createXMLEventWriter(Objects.requireNonNull(outputStream), this.charset.name());

		streamOfXmlEventsWithTagNames(eventReader)
				.map(this::parseXPath2VTLInLegitimatePairs)
				.forEach(e -> {
					try {
						eventWriter.add(e);
					} catch (XMLStreamException xmlStreamException) {
						throw new RuntimeException(xmlStreamException);
					}
				});
		eventWriter.close();
		eventReader.close();

		return outputStream;
	}

	// TODO iter over a stream of subsequences of the file instead of a String with the whole file content
	// the method should iter over all subsequences and filter the ones which are legitimate to parse (cf. possibleNodes)
	public static String parseToVTLInNodes(String input, String possibleNodes) {

		Pattern pattern = Pattern.compile("(<"+possibleNodes+">)((.)*?)(</"+possibleNodes+">)");

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
	 *  	x!=y -> x <> y
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
		if (patternCommaParenthesis.matcher(input).matches() ){
			throw new UnsupportedPatternException("\""+input+"\" matches .*,\\s*\\(");
		}
		String correctedInput;
		if (!endWithWhitespaceCharacter(input)){
			correctedInput=input+" ";
		}else{
			correctedInput=input;
		}
		StringBuilder finalString=new StringBuilder();
		StringBuilder context=new StringBuilder();
		List<String> listContext = new ArrayList<>();
		boolean isBetweenRealDoubleQuote=false;
		boolean isBetweenRealSimpleQuote=false;
		String contentBetweenSimpleQuote="";
		String lastCastType=""; // number, string or integer TODO:date/duration/etc
		for(char c : correctedInput.toCharArray()) {
			context.append( (c==',') ? "" : c);
			
			// order functions by descending length
			if(context.indexOf(XPATH_MOD)>=0 && !isBetweenRealDoubleQuote) {
				finalString.append(c);
				Matcher m = patternMod.matcher(finalString);
				boolean result = m.find();
				if (result) {
					String replacement=VTL_MOD + "(" + m.group(1) + "," + m.group(2) + ") ";
					do {
						finalString.replace(m.start(),m.end(),replacement);
						result = m.find();
					} while (result);
				}
				continue;
			}
			else if(context.indexOf(XPATH_CURRENT_DATE)>=0 && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_CURRENT_DATE, VTL_CURRENT_DATE);
			}			
			else if(context.indexOf(XPATH_NOT_EQUAL_TO)>=0 && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_NOT_EQUAL_TO, VTL_NOT_EQUAL_TO);
			}
			else if(context.indexOf(XPATH_DIVISION_FUNCTION)>=0 && !isBetweenRealDoubleQuote) {
				finalString = replaceLast(finalString, XPATH_DIVISION_FUNCTION, VTL_DIVISION_FUNCTION);
			}
			else if(context.indexOf(FAKE_XPATH_EQUAL_TO_NULL)>=0 && !isBetweenRealDoubleQuote){
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

				if(context.indexOf(XPATH_CONCAT_FUNCTION)>=0 && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_CONCAT_FUNCTION, "");
					listContext.add(XPATH_CONCAT_FUNCTION);
				}
				else if(context.indexOf(XPATH_LENGTH_FUNCTION)>=0 && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_LENGTH_FUNCTION, VTL_LENGTH_FUNCTION).append(c);
					listContext.add(XPATH_LENGTH_FUNCTION);}
				else if(context.indexOf(XPATH_SUBSTRING_FUNCTION)>=0 && !isBetweenRealDoubleQuote) {
					finalString = replaceLast(finalString, XPATH_SUBSTRING_FUNCTION, VTL_SUBSTRING_FUNCTION).append(c);
					listContext.add(XPATH_SUBSTRING_FUNCTION);
				}
				else {
					finalString.append(c);
					listContext.add(context.toString().replace("(", ""));
				}
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				context.delete(0, context.length());
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
				context.delete(0, context.length());
				break;
			case ')':
				finalString.append(getLastElement(listContext).equals(XPATH_CONCAT_FUNCTION) ? "" : c);
				if(getLastElement(listContext).contains(XPATH_CAST_FUNCTION)) {
					lastCastType=context.toString().replace(")", "");
				}
				removeLast(listContext);
				contentBetweenSimpleQuote+=isBetweenRealSimpleQuote ? c:"";
				context.delete(0, context.length());
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
