package fr.insee.eno.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.insee.eno.utils.Xpath2VTLParser;

public class TestXpath2VTLParser {
	
	// concat
	private String test1 = "concat(x,y,z, \"concat(a,b,c)\")";
	private String expected1 = "x || y || z ||  \"concat(a,b,c)\"";		

	private String test2 = "substring(x,y,z)";
	private String expected2 = "substr(x,y,z)";
	
	// different
	private String test3 = "x!=y";
	private String expected3 = "x &lt;&gt; y"; // x <> y

	// division
	private String test4 = "x div y";
	private String expected4 = "x / y";
	
	private String test5 = "x != '1'";
	private String expected5 ="x  &lt;&gt;  \"1\"";
	
	private String test6 = "\"x != '1'\"";
	private String expected6 ="\"x != '1'\"";
	
	//substring - concat
	private String test7 = "concat(substring(x,y,z),a,concat(x,substring(x1,y1,z1)),b)!='abc'";
	private String expected7 = "substr(x,y,z) || a || x || substr(x1,y1,z1) || b &lt;&gt; \"abc\"";

	private String test8 = "cast(cast(ABCD,integer),string) = '3'";
	private String expected8 = "cast(cast(ABCD,integer),string) = \"3\"";
	
	private String test9 = "cast(cast(ABCD,string),integer) = '3'";
	private String expected9 = "cast(cast(ABCD,string),integer) = 3";

	private String test10 = "cast(cast(ABCD,string),integer) = null";
	private String expected10 = "isnull(cast(cast(ABCD,string),integer))";

	// modulo
	//private String test11 = "10 mod 2";
	//private String expected11 = "mod(10,2)";

	// current date
	//private String test12 = "current-date()";
	//private String expected12 = "current_date()";

	// string length
	//private String test13 = "string-length()";
	//private String expected13 = "length()";
		
	@Test
	public void simpleTestXpath2VTL() {
		Assertions.assertEquals(expected1, Xpath2VTLParser.parseToVTL(test1));
		Assertions.assertEquals(expected2, Xpath2VTLParser.parseToVTL(test2));
		Assertions.assertEquals(expected3, Xpath2VTLParser.parseToVTL(test3));
		Assertions.assertEquals(expected4, Xpath2VTLParser.parseToVTL(test4));
		Assertions.assertEquals(expected5, Xpath2VTLParser.parseToVTL(test5));
		Assertions.assertEquals(expected6, Xpath2VTLParser.parseToVTL(test6));
		Assertions.assertEquals(expected7, Xpath2VTLParser.parseToVTL(test7));
		Assertions.assertEquals(expected8, Xpath2VTLParser.parseToVTL(test8));
		Assertions.assertEquals(expected9, Xpath2VTLParser.parseToVTL(test9));
		Assertions.assertEquals(expected10, Xpath2VTLParser.parseToVTL(test10));
		//Assertions.assertEquals(expected11, Xpath2VTLParser.parseToVTL(test11));
		//Assertions.assertEquals(expected12, Xpath2VTLParser.parseToVTL(test12));
		//Assertions.assertEquals(expected13, Xpath2VTLParser.parseToVTL(test13));

	}
	@Test
	public void simpleTestVTL2VTL() {
		Assertions.assertEquals(expected1, Xpath2VTLParser.parseToVTL(expected1));
		Assertions.assertEquals(expected2, Xpath2VTLParser.parseToVTL(expected2));
		Assertions.assertEquals(expected3, Xpath2VTLParser.parseToVTL(expected3));
		Assertions.assertEquals(expected4, Xpath2VTLParser.parseToVTL(expected4));
		Assertions.assertEquals(expected5, Xpath2VTLParser.parseToVTL(expected5));
		Assertions.assertEquals(expected6, Xpath2VTLParser.parseToVTL(expected6));
		Assertions.assertEquals(expected7, Xpath2VTLParser.parseToVTL(expected7));
		Assertions.assertEquals(expected8, Xpath2VTLParser.parseToVTL(expected8));
		Assertions.assertEquals(expected9, Xpath2VTLParser.parseToVTL(expected9));
		Assertions.assertEquals(expected10, Xpath2VTLParser.parseToVTL(expected10));
		//Assertions.assertEquals(expected11, Xpath2VTLParser.parseToVTL(expected11));
		//Assertions.assertEquals(expected12, Xpath2VTLParser.parseToVTL(expected12));
		//Assertions.assertEquals(expected13, Xpath2VTLParser.parseToVTL(expected13));
	
	}
	

	
}
