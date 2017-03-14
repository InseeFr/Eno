package fr.insee.eno;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Only for dev purposes.
 * */
public class DummyMain {
	
	private static final Logger logger = LogManager.getLogger(DummyMain.class);

	public static void main(String[] args) {
		logger.info("Starting generation program");
		Injector injector = Guice.createInjector(new DDI2FRContext());
		GenerationService service = injector.getInstance(GenerationService.class);
		try {
			File generatedFile = service.generateQuestionnaire("questionnaires/simpsons/ddi/simpsons.xml", null);
			logger.info("Generation successful! >> " + generatedFile);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
