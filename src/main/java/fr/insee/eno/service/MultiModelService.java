package fr.insee.eno.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.preprocessing.DDISplittingPreprocessor;
import fr.insee.eno.utils.FileArchiver;
import fr.insee.eno.utils.FolderCleaner;

public class MultiModelService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultiModelService.class);

	private ParameterizedGenerationService parameterizedGenerationService = new ParameterizedGenerationService(false);
	private ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();
	
	private DDISplittingPreprocessor ddiSplitPreprocessor = new DDISplittingPreprocessor();

	/**
	 * It generates Zip file using parameterizedGenerationService
	 * @param inputFile : the xml input File which contains multiple ddi instrument in the same file (required)
	 * @param params : java object ENOParameter (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in FRModeleColtranePostProcessor (optional)
	 * @return the Zip file which contains all generated files
	 * @throws Exception
	 */
	public File generateQuestionnaire(File inputFile, ENOParameters params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception{
		LOGGER.info("MultiModel Generation of questionnaire -- STARTED --");		

		String surveyName = params.getParameters()!=null?params.getParameters().getCampagne():"test";
		cleanTempFolder(surveyName);
		
		File folderTemp = new File(Constants.TEMP_FOLDER_PATH + "/" + surveyName);
		
		List<File> ddiFiles = ddiSplitPreprocessor.splitDDI(inputFile, surveyName);

		Path outputZipPath = Paths.get(folderTemp.getAbsolutePath()+"/"+ surveyName+".zip");
		Files.deleteIfExists(outputZipPath);
		File outputZip = new File(outputZipPath.toString());
		FileOutputStream fileOutputStream = new FileOutputStream(outputZip);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		LOGGER.info("Archive file initalized to :"+outputZip.getAbsolutePath());
		
		for(File ddiFile : ddiFiles) {
			File output = parameterizedGenerationService.generateQuestionnaire(ddiFile, params, metadata, specificTreatment, mapping);
			FileArchiver.writeToZipFile(output.getAbsolutePath(), zipOutputStream);
			FolderCleaner cleanService = new FolderCleaner();
			cleanService.specialCleaningFiles(folderTemp, output.getParentFile().getName());
		}
		
		zipOutputStream.close();
		fileOutputStream.close();

		LOGGER.info("MultiModel Generation of questionnaire -- END --");
		return outputZip;

	}

	/**
	 * It generates a Zip file using parameterizedGenerationService
	 * @param inputFile : the xml input File which contains multiple ddi instrument in the same file (required)
	 * @param params : InputStream of parameters xml file (required)
	 * @param metadata : InputStream of metadata xml file (optional)
	 * @param specificTreatment : InputStream of an xsl sheet (optional)
	 * @param mapping : InputStream of a xml file using in FRModeleColtranePostProcessor (optional)
	 * @return the Zip file which contains all generated files
	 * @throws Exception
	 */
	public File generateQuestionnaire(File inputFile, InputStream params, InputStream metadata, InputStream specificTreatment, InputStream mapping) throws Exception {		
		LOGGER.info("MultiModel Generation of questionnaire -- STARTED --");
		
		byte[] paramsBytes = IOUtils.toByteArray(params);
		
		ENOParameters enoParameters = valorizatorParameters.getParameters(new ByteArrayInputStream(paramsBytes));
		String surveyName = enoParameters.getParameters()!=null?enoParameters.getParameters().getCampagne():"test";
		cleanTempFolder(surveyName);
		
		File folderTemp = new File(Constants.TEMP_FOLDER_PATH + "/" + surveyName);
		
		
		List<File> ddiFiles = ddiSplitPreprocessor.splitDDI(inputFile, surveyName);

		Path outputZipPath = Paths.get(folderTemp.getAbsolutePath()+"/"+ surveyName+".zip");
		Files.deleteIfExists(outputZipPath);
		File outputZip = new File(outputZipPath.toString());
		FileOutputStream fileOutputStream = new FileOutputStream(outputZip);
		ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
		LOGGER.info("Archive file initalized to :"+outputZip.getAbsolutePath());
		
		for(File ddiFile : ddiFiles) {
			File output = parameterizedGenerationService.generateQuestionnaire(ddiFile, new ByteArrayInputStream(paramsBytes), metadata, specificTreatment, mapping);
			FileArchiver.writeToZipFile(output.getAbsolutePath(), zipOutputStream);
			FolderCleaner cleanService = new FolderCleaner();			
			cleanService.specialCleaningFiles(folderTemp, output.getParentFile().getName());
		}
		
		zipOutputStream.close();
		fileOutputStream.close();

		LOGGER.info("MultiModel Generation of questionnaire -- END --");
		return outputZip;

	}

	/**
	 * It generates Zip file using parameterizedGenerationService
	 * @param inputFile : the xml input File which contains multiple ddi instrument in the same file (required)
	 * @param params : xml File of ENOParameter (required)
	 * @param metadata : xml File of metadata (optional)
	 * @param specificTreatment : xsl file of the xsl sheet (optional)
	 * @param mapping : a xml File using in FRModeleColtranePostProcessor (optional)
	 * @return the Zip file which contains all generated file
	 * @throws Exception
	 */
	public File generateQuestionnaire(File inputFile, File params, File metadata, File specificTreatment, File mapping)  throws Exception{
		File output = null;

		InputStream parametersIS = null;
		InputStream metadataIS = null;
		InputStream specificTreatmentIS = null;
		InputStream mappingIS = null;
		parametersIS = params!=null ? FileUtils.openInputStream(params):null;
		metadataIS = metadata!=null ? FileUtils.openInputStream(metadata):null;
		specificTreatmentIS = specificTreatment!=null ? FileUtils.openInputStream(specificTreatment):null;
		mappingIS = mapping!=null ? FileUtils.openInputStream(mapping):null;
		output = generateQuestionnaire(inputFile, parametersIS, metadataIS, specificTreatmentIS, mappingIS);

		if(parametersIS!=null) {parametersIS.close();};
		if(metadataIS!=null) {metadataIS.close();};
		if(specificTreatmentIS!=null) {specificTreatmentIS.close();};
		if(mappingIS!=null) {mappingIS.close();};

		return output;

	}
	
	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 * 
	 */
	public void cleanTempFolder(String name) throws IOException {
		FolderCleaner cleanService = new FolderCleaner();
		if (Constants.TEMP_FOLDER_PATH != null) {
			File folderTemp = new File(Constants.TEMP_FOLDER_PATH + "/" + name);
			cleanTempFolder(folderTemp);
		} else {
			LOGGER.debug("Temp Folder is null");
		}
	}
	
	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 * 
	 */
	private void cleanTempFolder(File folder) throws IOException {
		FolderCleaner cleanService = new FolderCleaner();
		if (folder != null) {
			cleanService.cleanOneFolder(folder);
		} else {
			LOGGER.debug("Temp Folder is null");
		}
	}

}
