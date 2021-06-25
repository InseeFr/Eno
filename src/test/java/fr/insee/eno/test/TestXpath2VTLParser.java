package fr.insee.eno.test;

import fr.insee.eno.utils.Xpath2VTLParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestXpath2VTLParser {

	@Test
	@DisplayName("concat(x,y,z, \"concat(a,b,c)\")")
	public void parseToVTLTest_concat() {
		String xpathExpression = "concat(x,y,z, \"concat(a,b,c)\")";
		String vtlExpected = "x || y || z ||  \"concat(a,b,c)\"";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}


	@Test
	@DisplayName("substring(x,y,z)")
	public void parseToVTLTest_substr() {
		String xpathExpression = "substring(x,y,z)";
		String vtlExpected = "substr(x,y,z)";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("x!=y")
	public void parseToVTLTest_different() {
		String xpathExpression = "x!=y";
		String vtlExpected = "x &lt;&gt; y"; // x <> y
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("x div y")
	public void parseToVTLTest_division() {
		String xpathExpression = "x div y";
		String vtlExpected = "x / y";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("x != '1'")
	public void parseToVTLTest_different_litteral() {
		String xpathExpression = "x != '1'";
		String vtlExpected ="x  &lt;&gt;  \"1\"";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("concat(substring(x,y,z),a,concat(x,substring(x1,y1,z1)),b)!='abc'")
	public void parseToVTLTest_substring_concat() {
		String xpathExpression = "concat(substring(x,y,z),a,concat(x,substring(x1,y1,z1)),b)!='abc'";
		String vtlExpected = "substr(x,y,z) || a || x || substr(x1,y1,z1) || b &lt;&gt; \"abc\"";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("cast(cast(ABCD,integer),string) = '3'")
	public void parseToVTLTest_cast_string() {
		String xpathExpression = "cast(cast(ABCD,integer),string) = '3'";
		String vtlExpected = "cast(cast(ABCD,integer),string) = \"3\"";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("cast(cast(ABCD,integer),string) = '3' (space)")
	public void parseToVTLTest_cast_string_withSpace() {
		String xpathExpression = "cast(cast(ABCD,integer),string) = '3'"+" ";
		String vtlExpected = "cast(cast(ABCD,integer),string) = \"3\"";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("cast(cast(ABCD,string),integer) = '3'")
	public void parseToVTLTest_cast_integer() {
		String xpathExpression = "cast(cast(ABCD,string),integer) = '3'";
		String vtlExpected = "cast(cast(ABCD,string),integer) = 3";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("cast(cast(ABCD,string),integer) = null")
	public void parseToVTLTest_cast_null() {
		String xpathExpression = "cast(cast(ABCD,string),integer) = null";
		String vtlExpected = "isnull(cast(cast(ABCD,string),integer))";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("10mod2")
	public void parseToVTLTest_mod() {
		String xpathExpression = "10mod2";
		String vtlExpected = "mod(10,2)";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("121 mod 11")
	public void parseToVTLTest_mod_space() {
		String xpathExpression = "121 mod 11";
		String vtlExpected = "mod(121,11)";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("kiki $VAR1$ mod $VAR2$ lolo")
	public void parseToVTLTest_mod_dollar() {
		String xpathExpression ="kiki $VAR1$ mod $VAR2$ lolo";
		String vtlExpected ="kiki mod($VAR1$,$VAR2$) lolo";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}


	@Test
	@DisplayName("current-date()")
	public void parseToVTLTest_current_date() {
		String xpathExpression = "current-date()";
		String vtlExpected = "current_date()";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("string-length()")
	public void parseToVTLTest_length() {
		String xpathExpression = "string-length()";
		String vtlExpected= "length()";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("cast(VAR,number) in {\"1\", \"2\"}")
	public void parseToVTLTest_in() {
		String xpathExpression = "cast(VAR,number) in {\"1\", \"2\"}";
		String vtlExpected = "cast(VAR,number) in {\"1\", \"2\"}";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}

	@Test
	@DisplayName("cast(VAR,number) not_in {\"1\", \"2\"}")
	public void parseToVTLTest_not_in() {
		String xpathExpression = "cast(VAR,number) not_in {\"1\", \"2\"}";
		String vtlExpected = "cast(VAR,number) not_in {\"1\", \"2\"}";
		assertEquals(vtlExpected, Xpath2VTLParser.parseToVTL(xpathExpression));
	}


	// null condition
	// String xpathExpression16 = "if ($SEXE$= \"2\") then \"elle\" else \"il\"";
	 //String vtlExpected = "if isnull($SEXE$) then \"il\" else if ($SEXE$= \"2\") then \"elle\" else \"il\"";

	// conversion d’une date AAAA-MM-JJ en nombre


	@Test
	@DisplayName("x || y || z ||  \"concat(a,b,c)\"")
	public void parseToVTLTest_vtlIsUnchanged_concat() {

		String vtl = "x || y || z ||  \"concat(a,b,c)\"";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}


	@Test
	@DisplayName("substr(x,y,z)")
	public void parseToVTLTest_vtlIsUnchanged_substr() {

		String vtl = "substr(x,y,z)";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("x &lt;&gt; y")
	public void parseToVTLTest_vtlIsUnchanged_different() {

		String vtl = "x &lt;&gt; y"; // x <> y
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("x / y")
	public void parseToVTLTest_vtlIsUnchanged_division() {

		String vtl = "x / y";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("x  &lt;&gt;  \"1\"")
	public void parseToVTLTest_vtlIsUnchanged_different_litteral() {

		String vtl ="x  &lt;&gt;  \"1\"";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("substr(x,y,z) || a || x || substr(x1,y1,z1) || b &lt;&gt; \"abc\"")
	public void parseToVTLTest_vtlIsUnchanged_substring_concat() {

		String vtl = "substr(x,y,z) || a || x || substr(x1,y1,z1) || b &lt;&gt; \"abc\"";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(cast(ABCD,integer),string) = \"3\"")
	public void parseToVTLTest_vtlIsUnchanged_cast_string() {

		String vtl = "cast(cast(ABCD,integer),string) = \"3\"";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(cast(ABCD,string),integer) = 3")
	public void parseToVTLTest_vtlIsUnchanged_cast_integer() {

		String vtl = "cast(cast(ABCD,string),integer) = 3";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("isnull(cast(cast(ABCD,string),integer))")
	public void parseToVTLTest_vtlIsUnchanged_cast_null() {

		String vtl = "isnull(cast(cast(ABCD,string),integer))";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}


	@Test
	@DisplayName("mod(10,2)")
	public void parseToVTLTest_vtlIsUnchanged_mod() {

		String vtl = "mod(10,2)";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("kiki $VAR1$ mod $VAR2$ lolo")
	public void parseToVTLTest_vtlIsUnchanged_mode_dollar() {

		String vtl ="kiki mod($VAR1$,$VAR2$) lolo";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}


	@Test
	@DisplayName("current_date()")
	public void parseToVTLTest_vtlIsUnchanged_current_date() {

		String vtl = "current_date()";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("length()")
	public void parseToVTLTest_vtlIsUnchanged_length() {

		String vtl= "length()";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(VAR,number) in {\"1\", \"2\"}")
	public void parseToVTLTest_vtlIsUnchanged_in() {

		String vtl = "cast(VAR,number) in {\"1\", \"2\"}";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(VAR,number) not_in {\"1\", \"2\"}")
	public void parseToVTLTest_vtlIsUnchanged_not_in() {

		String vtl = "cast(VAR,number) not_in {\"1\", \"2\"}";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(substr(cast($DATENAIS$,string),1,4)||substr(cast($DATENAIS$,string),6,2)||substr(cast($DATENAIS$,string),9,2),number)")
	public void parseToVTLTest_vtlIsUnchanged_date() {
		String vtl = "cast(substr(cast($DATENAIS$,string),1,4)||substr(cast($DATENAIS$,string),6,2)||substr(cast($DATENAIS$,string),9,2),number)";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(substr(cast(current_date(),string),1,4),number)  - cast(substr(cast($DATENAIS$,string),1,4),number)")
	public void parseToVTLTest_vtlIsUnchanged_age() {
		String vtl = "cast(substr(cast(current_date(),string),1,4),number)  - cast(substr(cast($DATENAIS$,string),1,4),number)";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}

	@Test
	@DisplayName("cast(substr(cast(current_date(),string),6,2) || substr(cast(current_date(),string),9,2),number) < cast(substr(cast($DATENAIS$,string),6,2) || substr(cast($DATENAIS$,string),9,2),number)")
	public void parseToVTLTest_vtlIsUnchanged_birthday() {
		String vtl = "cast(substr(cast(current_date(),string),6,2) || substr(cast(current_date(),string),9,2),number) < cast(substr(cast($DATENAIS$,string),6,2) || substr(cast($DATENAIS$,string),9,2),number)";
		assertEquals(vtl, Xpath2VTLParser.parseToVTL(vtl));
	}
}

