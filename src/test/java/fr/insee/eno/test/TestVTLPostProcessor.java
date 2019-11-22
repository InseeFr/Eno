package fr.insee.eno.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.Diff;

import fr.insee.eno.Constants;
import fr.insee.eno.postprocessing.js.JSVTLParserPostprocessor;

public class TestVTLPostProcessor {

	private JSVTLParserPostprocessor jsvtlParserPostprocessor = new JSVTLParserPostprocessor();
	private XMLDiff xmlDiff = new XMLDiff();
		
	@Test
	public void simpleTest() {
		
		String test1 = "concat(x,y,z)";
		String expected1 = "x || y || z";		

		String test2 = "substring(x,y,z)";
		String expected2 = "substr(x,y,z)";
		
		String test3 = "x!=y";
		String expected3 = "x &lt;&gt; y";
		
		String test4 = "concat(substring(x,y,z),a,concat(x,substring(x1,y1,z1)),b)!='abc'";
		String expected4 = "substr(x,y,z) || a || x || substr(x1,y1,z1) || b &lt;&gt; 'abc'";
		
		Assert.assertEquals(expected1, jsvtlParserPostprocessor.parseToVTL(test1));
		Assert.assertEquals(expected2, jsvtlParserPostprocessor.parseToVTL(test2));
		Assert.assertEquals(expected3, jsvtlParserPostprocessor.parseToVTL(test3));
		Assert.assertEquals(expected4, jsvtlParserPostprocessor.parseToVTL(test4));
	}
	
	@Test
	public void simpleWithFileTest() {
		try {
			String basePath = "src/test/resources/vtl-parsing";
			
			Path outPath = Paths.get(Constants.TEMP_FOLDER_PATH + "/test-vtl.xml");
			Files.deleteIfExists(outPath);
			Path outputFilePath = Files.createFile(outPath);
			File in = new File(String.format("%s/in.xml", basePath));
			File outPostProcessing = jsvtlParserPostprocessor.process(in, null, "test");
			FileUtils.copyFile(outPostProcessing,outputFilePath.toFile());
			FileUtils.forceDelete(outPostProcessing);
			File expectedFile = new File(String.format("%s/out.xml", basePath));
			Diff diff = xmlDiff.getDiff(outputFilePath.toFile(),expectedFile);
			Assert.assertFalse(getDiffMessage(diff, basePath), diff.hasDifferences());
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (NullPointerException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			Assert.fail();
		} 
	}
	
	private String getDiffMessage(Diff diff, String path) {
		return String.format("Transformed output for %s should match expected XML document:\n %s", path,
				diff.toString());
	}

	
}
