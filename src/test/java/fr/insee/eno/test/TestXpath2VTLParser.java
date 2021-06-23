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
	private String test11 = "10 mod 2";
	private String test11bis = "10mod2";
	private String expected11 = "mod(10,2)";
	private String test20 ="kiki $VAR1$ mod $VAR2$ lolo";
	private String expected20 ="kiki mod($VAR1$,$VAR2$) lolo";
	
	// current date
	private String test12 = "current-date()";
	private String expected12 = "current_date()";

	// string length
	private String test13 = "string-length()";
	private String expected13 = "length()";

	// inclusion and not inclusion
	private String expected14 = "cast(VAR,number) in {\"1\", \"2\"}";
	private String expected15 = "cast(VAR,number) not_in {\"1\", \"2\"}";

	// null condition
	//private String test16 = "if ($SEXE$= \"2\") then \"elle\" else \"il\"";
	private String expected16 = "if isnull($SEXE$) then \"il\" else if ($SEXE$= \"2\") then \"elle\" else \"il\"";	

	// conversion d’une date AAAA-MM-JJ en nombre
    private String expected17 = "cast(substr(cast($DATENAIS$,string),1,4)||substr(cast($DATENAIS$,string),6,2)||substr(cast($DATENAIS$,string),9,2),number)";

	// âge en différence de millésime ($AGEMILLESIME$)
	private String expected18 = "cast(substr(cast(current_date(),string),1,4),number)  - cast(substr(cast($DATENAIS$,string),1,4),number)";

	// Booléen anniversaire à venir dans l’année ($FUTURANNIVERSAIRE$)
	private String expected19 = "cast(substr(cast(current_date(),string),6,2) || substr(cast(current_date(),string),9,2),number) < cast(substr(cast($DATENAIS$,string),6,2) || substr(cast($DATENAIS$,string),9,2),number)";

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
		Assertions.assertEquals(expected11, Xpath2VTLParser.parseToVTL(test11));
		Assertions.assertEquals(expected11, Xpath2VTLParser.parseToVTL(test11bis));
		Assertions.assertEquals(expected12, Xpath2VTLParser.parseToVTL(test12));
		Assertions.assertEquals(expected13, Xpath2VTLParser.parseToVTL(test13));
		Assertions.assertEquals(expected20, Xpath2VTLParser.parseToVTL(test20));

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
		Assertions.assertEquals(expected11, Xpath2VTLParser.parseToVTL(expected11));
		Assertions.assertEquals(expected12, Xpath2VTLParser.parseToVTL(expected12));
		Assertions.assertEquals(expected13, Xpath2VTLParser.parseToVTL(expected13));
		Assertions.assertEquals(expected14, Xpath2VTLParser.parseToVTL(expected14));
		Assertions.assertEquals(expected15, Xpath2VTLParser.parseToVTL(expected15));
		Assertions.assertEquals(expected16, Xpath2VTLParser.parseToVTL(expected16));
		Assertions.assertEquals(expected17, Xpath2VTLParser.parseToVTL(expected17));	
		Assertions.assertEquals(expected18, Xpath2VTLParser.parseToVTL(expected18));
		Assertions.assertEquals(expected19, Xpath2VTLParser.parseToVTL(expected19));
		Assertions.assertEquals(expected20, Xpath2VTLParser.parseToVTL(expected20));
	}
	

	
}
