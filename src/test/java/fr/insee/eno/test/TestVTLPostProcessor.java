package fr.insee.eno.test;

import org.junit.Test;

import fr.insee.eno.postprocessing.JSVTLParserPostprocessor;
import org.junit.Assert;

public class TestVTLPostProcessor {

	private JSVTLParserPostprocessor jsvtlParserPostprocessor = new JSVTLParserPostprocessor();
		
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

	
}
