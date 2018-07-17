package fr.insee.eno.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Assert;
import org.xmlunit.diff.Diff;

import fr.insee.eno.GenerationService;
import fr.insee.eno.generation.DDI2FRGenerator;
import fr.insee.eno.postprocessing.NoopPostprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;

public class DummyTestDDI2XForms {

	public static void main(String[] args) {
		
		String basePathDDI2XFORMS = "src/test/resources/ddi-to-xform";
		GenerationService genServiceDDI2XFORMS = new GenerationService(new DDIPreprocessor(), new DDI2FRGenerator(),
				new NoopPostprocessor());
		File in = new File(String.format("%s/in.xml", basePathDDI2XFORMS));
		
		try {
			File output = genServiceDDI2XFORMS.generateQuestionnaire(in, null,"test");
			System.out.println(output.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
	}

}
