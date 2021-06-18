package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.lunaticxml.LunaticXMLVTLParserPostprocessor;

public class TestLunaticXMLVTLParserPostProcessor {

	private LunaticXMLVTLParserPostprocessor lunaticXMLVtlParserPostprocessor = new LunaticXMLVTLParserPostprocessor();
	private XMLDiff xmlDiff = new XMLDiff();
	
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
	private String expected11 = "mod(10,2)";

	// current date
	private String test12 = "current-date()";
	private String expected12 = "current_date()";

	// string length
	private String test13 = "string-length()";
	private String expected13 = "length()";
		
	@Test
	public void simpleTestXpath2VTL() {
		Assertions.assertEquals(expected1, lunaticXMLVtlParserPostprocessor.parseToVTL(test1));
		Assertions.assertEquals(expected2, lunaticXMLVtlParserPostprocessor.parseToVTL(test2));
		Assertions.assertEquals(expected3, lunaticXMLVtlParserPostprocessor.parseToVTL(test3));
		Assertions.assertEquals(expected4, lunaticXMLVtlParserPostprocessor.parseToVTL(test4));
		Assertions.assertEquals(expected5, lunaticXMLVtlParserPostprocessor.parseToVTL(test5));
		Assertions.assertEquals(expected6, lunaticXMLVtlParserPostprocessor.parseToVTL(test6));
		Assertions.assertEquals(expected7, lunaticXMLVtlParserPostprocessor.parseToVTL(test7));
		Assertions.assertEquals(expected8, lunaticXMLVtlParserPostprocessor.parseToVTL(test8));
		Assertions.assertEquals(expected9, lunaticXMLVtlParserPostprocessor.parseToVTL(test9));
		Assertions.assertEquals(expected10, lunaticXMLVtlParserPostprocessor.parseToVTL(test10));
		//Assertions.assertEquals(expected11, lunaticXMLVtlParserPostprocessor.parseToVTL(test11));
		//Assertions.assertEquals(expected12, lunaticXMLVtlParserPostprocessor.parseToVTL(test12));
		//Assertions.assertEquals(expected13, lunaticXMLVtlParserPostprocessor.parseToVTL(test13));

	}
	@Test
	public void simpleTestVTL2VTL() {
		Assertions.assertEquals(expected1, lunaticXMLVtlParserPostprocessor.parseToVTL(expected1));
		Assertions.assertEquals(expected2, lunaticXMLVtlParserPostprocessor.parseToVTL(expected2));
		Assertions.assertEquals(expected3, lunaticXMLVtlParserPostprocessor.parseToVTL(expected3));
		Assertions.assertEquals(expected4, lunaticXMLVtlParserPostprocessor.parseToVTL(expected4));
		Assertions.assertEquals(expected5, lunaticXMLVtlParserPostprocessor.parseToVTL(expected5));
		Assertions.assertEquals(expected6, lunaticXMLVtlParserPostprocessor.parseToVTL(expected6));
		Assertions.assertEquals(expected7, lunaticXMLVtlParserPostprocessor.parseToVTL(expected7));
		Assertions.assertEquals(expected8, lunaticXMLVtlParserPostprocessor.parseToVTL(expected8));
		Assertions.assertEquals(expected9, lunaticXMLVtlParserPostprocessor.parseToVTL(expected9));
		Assertions.assertEquals(expected10, lunaticXMLVtlParserPostprocessor.parseToVTL(expected10));
		//Assertions.assertEquals(expected11, lunaticXMLVtlParserPostprocessor.parseToVTL(expected11));
		//Assertions.assertEquals(expected12, lunaticXMLVtlParserPostprocessor.parseToVTL(expected12));
		//Assertions.assertEquals(expected13, lunaticXMLVtlParserPostprocessor.parseToVTL(expected13));
	
	}
	
	@Test
	public void simpleWithFileTest() {
		try {
			String basePath = "src/test/resources/lunatic-xml-vtl-parsing";
			
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/test-vtl.xml");
			Files.deleteIfExists(outPath);
			Path outputFilePath = Files.createFile(outPath);
			File in = new File(String.format("%s/in.xml", basePath));
			File outPostProcessing = lunaticXMLVtlParserPostprocessor.process(in, null, "test");
			FileUtils.copyFile(outPostProcessing,outputFilePath.toFile());
			FileUtils.forceDelete(outPostProcessing);
			File expectedFile = new File(String.format("%s/out.xml", basePath));
			Diff diff = xmlDiff.getDiff(outputFilePath.toFile(),expectedFile);
			Assertions.assertFalse(diff::hasDifferences, ()->getDiffMessage(diff, basePath));
			
		} catch (IOException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assertions.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assertions.fail();
		} 
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}

	
}
