package fr.insee.eno;

import com.google.inject.AbstractModule;

/**
 * Defines the context on the generation, i.e. which processors and generator to
 * use for DDI to XForms generation.
 */
public class DDI2FRContext extends AbstractModule {

	@Override
	protected void configure() {
		bind(Preprocessor.class).to(DDIPreprocessor.class);
		bind(Generator.class).to(DDI2FRGenerator.class);
		bind(Postprocessor.class).to(NoopPostprocessor.class);
	}

}
