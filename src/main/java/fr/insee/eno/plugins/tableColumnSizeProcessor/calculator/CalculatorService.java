package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculatorService {

	
	final static Logger logger = LoggerFactory.getLogger(CalculatorService.class);
	


	public CalculatorService() {

	}

	public void tableColumnSizeProcessor(String inFileName, String outFileName, String xmlConfFile) throws Exception {
		
		
		logger.debug("tableColumnSizeProcessor - Start");
		FakeApp.run(inFileName,outFileName,xmlConfFile);
		logger.debug("tableColumnSizeProcessor - END");

	}

}
