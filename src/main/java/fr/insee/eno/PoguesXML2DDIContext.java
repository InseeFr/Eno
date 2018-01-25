package fr.insee.eno;

import com.google.inject.AbstractModule;

import fr.insee.eno.generation.Generator;
import fr.insee.eno.generation.PoguesXML2DDIGenerator;
import fr.insee.eno.postprocessing.DDIPostprocessor;
import fr.insee.eno.postprocessing.Postprocessor;
import fr.insee.eno.preprocessing.PoguesXMLPreprocessor;
import fr.insee.eno.preprocessing.Preprocessor;

/**
 * Defines the context on the generation, i.e. which processors and generator to
 * use for DDI to ODT generation.
 */
public class PoguesXML2DDIContext extends AbstractModule {

	@Override
	protected void configure() {
		bind(Preprocessor.class).to(PoguesXMLPreprocessor.class);
		bind(Generator.class).to(PoguesXML2DDIGenerator.class);
		bind(Postprocessor.class).to(DDIPostprocessor.class);
	}

}
