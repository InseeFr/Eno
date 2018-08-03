package fr.insee.eno;

import com.google.inject.AbstractModule;

import fr.insee.eno.generation.DDI2PDFGenerator;
import fr.insee.eno.generation.Generator;
import fr.insee.eno.postprocessing.PDFStep3TableColumnPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.DDIPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

/**
 * Defines the context on the generation, i.e. which processors and generator to
 * use for DDI to ODT generation.
 */
public class DDI2PDFContext extends AbstractModule {

	@Override
	protected void configure() {
		bind(Preprocessor.class).to(DDIPreprocessor.class);
		bind(Generator.class).to(DDI2PDFGenerator.class);
		bind(Postprocessor.class).to(PDFStep3TableColumnPostprocessor.class);
	}

}
