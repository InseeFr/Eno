package fr.insee.eno.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;
import fr.insee.eno.exception.EnoGenerationException;
import fr.insee.eno.parameters.ENOParameters;
import fr.insee.eno.params.ValorizatorParameters;
import fr.insee.eno.params.ValorizatorParametersImpl;
import fr.insee.eno.preprocessing.DDISplittingPreprocessor;
import fr.insee.eno.utils.FileArchiver;
import fr.insee.eno.utils.FolderCleaner;

public class MultiModelService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultiModelService.class);

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

		byte[] metadataBytes = metadata!=null ? IOUtils.toByteArray(metadata):null;
		byte[] specificTreatmentBytes = specificTreatment !=null ? IOUtils.toByteArray(specificTreatment):null;
		byte[] mappingBytes = mapping !=null ? IOUtils.toByteArray(mapping):null;

		String surveyName = params.getParameters()!=null?params.getParameters().getCampagne():"test";
		cleanTempFolder(surveyName);

		File folderTemp = new File(Constants.TEMP_FOLDER_PATH + "/" + surveyName);

		List<File> ddiFiles = ddiSplitPreprocessor.splitDDI(inputFile, surveyName);

		// nbThreads = min between number of available CPUs and number of input files
		int nbThreads = Math.min(Runtime.getRuntime().availableProcessors(), ddiFiles.size());
		ExecutorService generationThreadsService = Executors.newFixedThreadPool(nbThreads);
		
		Path outputZipPath = Paths.get(folderTemp.getAbsolutePath()+"/"+ surveyName+".zip");
		Files.deleteIfExists(outputZipPath);
		File outputZip = new File(outputZipPath.toString());

		try {
			List<Callable<File>> generationTasks = initGenerationTasks(ddiFiles,params,metadataBytes,specificTreatmentBytes,mappingBytes);
			List<Future<File>> outputsFutureFile = generationThreadsService.invokeAll(generationTasks);

			FileOutputStream fileOutputStream = new FileOutputStream(outputZip);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			LOGGER.info("Archive file initalized to :"+outputZip.getAbsolutePath());

			for(Future<File> future : outputsFutureFile) {
				FileArchiver.writeToZipFile(surveyName, future.get().getAbsolutePath(), zipOutputStream, params.getPipeline().getOutFormat());
			}

			zipOutputStream.close();
			fileOutputStream.close();

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new EnoGenerationException("An error was occured during thread execution");
		} finally {
			if(generationThreadsService != null) {
				generationThreadsService.shutdown();
			}
			LOGGER.info("Cleaning temp files into :"+folderTemp);
			FolderCleaner cleanerService = new FolderCleaner();
			cleanerService.cleanOneFolderExceptGeneratedFile(folderTemp, outputZip);
		}

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

		ValorizatorParameters valorizatorParameters = new ValorizatorParametersImpl();

		byte[] paramsBytes = params!=null ? IOUtils.toByteArray(params):null;
		byte[] metadataBytes = metadata!=null ? IOUtils.toByteArray(metadata):null;
		byte[] specificTreatmentBytes = specificTreatment !=null ? IOUtils.toByteArray(specificTreatment):null;
		byte[] mappingBytes = mapping !=null ? IOUtils.toByteArray(mapping):null;

		ENOParameters enoParameters = valorizatorParameters.getParameters(new ByteArrayInputStream(paramsBytes));
		String surveyName = enoParameters.getParameters()!=null?enoParameters.getParameters().getCampagne():"test";
		cleanTempFolder(surveyName);

		File folderTemp = new File(Constants.TEMP_FOLDER_PATH + "/" + surveyName);

		List<File> ddiFiles = ddiSplitPreprocessor.splitDDI(inputFile, surveyName);
		
		// nbThreads = min between number of available CPUs and number of input files
		int nbThreads = Math.min(Runtime.getRuntime().availableProcessors(), ddiFiles.size());
		ExecutorService generationThreadsService = Executors.newFixedThreadPool(nbThreads);
		
		Path outputZipPath = Paths.get(folderTemp.getAbsolutePath()+"/"+ surveyName+".zip");
		Files.deleteIfExists(outputZipPath);
		File outputZip = new File(outputZipPath.toString());

		try {
			List<Callable<File>> generationTasks = initGenerationTasks(ddiFiles,paramsBytes,metadataBytes,specificTreatmentBytes,mappingBytes);
			List<Future<File>> outputsFutureFile = generationThreadsService.invokeAll(generationTasks);

			FileOutputStream fileOutputStream = new FileOutputStream(outputZip);
			ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
			LOGGER.info("Archive file initalized to :"+outputZip.getAbsolutePath());

			for(Future<File> future : outputsFutureFile) {
				FileArchiver.writeToZipFile(surveyName, future.get().getAbsolutePath(), zipOutputStream, enoParameters.getPipeline().getOutFormat());
			}

			zipOutputStream.close();
			fileOutputStream.close();

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new EnoGenerationException("An error was occured during thread execution");
		} finally {
			if(generationThreadsService != null) {
				generationThreadsService.shutdown();
			}
			LOGGER.info("Cleaning temp files into :"+folderTemp);
			FolderCleaner cleanerService = new FolderCleaner();
			cleanerService.cleanOneFolderExceptGeneratedFile(folderTemp, outputZip);
		}

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
	 * It creates a list of Callable<File> in order to create multiple threads (one per file)
	 * @param ddiFiles : list of input files
	 * @param paramsBytes
	 * @param metadataBytes
	 * @param specificTreatmentBytes
	 * @param mappingBytes
	 * @return list of Callable<File> 
	 * @throws IOException
	 */
	private List<Callable<File>> initGenerationTasks(List<File> ddiFiles, byte[] paramsBytes, byte[] metadataBytes, byte[] specificTreatmentBytes, byte[] mappingBytes) throws IOException{
		LOGGER.info("Creation of new Tasks (which will be executed on separated threads) to transform the models in parallel");
		List<Callable<File>> generationTasks = new ArrayList<Callable<File>>();
		for(File ddiFile : ddiFiles) {
			File movedFile = moveFile(ddiFile);
			generationTasks.add( ()-> {
				ParameterizedGenerationService parameterizedGenerationServiceThread = new ParameterizedGenerationService(false,getTempSurveyFolder(movedFile));
				File output = parameterizedGenerationServiceThread.generateQuestionnaire(
						movedFile, 
						paramsBytes!=null? new ByteArrayInputStream(paramsBytes):null, 
						metadataBytes!=null? new ByteArrayInputStream(metadataBytes):null,
						specificTreatmentBytes!=null ? new ByteArrayInputStream(specificTreatmentBytes):null, 
						mappingBytes!=null ? new ByteArrayInputStream(mappingBytes):null);
				return output;});
		}
		return generationTasks;

	}

	/**
	 * It creates a list of Callable<File> in order to create multiple threads (one per file)
	 * @param ddiFiles : list of input files
	 * @param enoParameters
	 * @param metadataBytes
	 * @param specificTreatmentBytes
	 * @param mappingBytes
	 * @return list of Callable<File> 
	 * @throws IOException
	 */
	private List<Callable<File>> initGenerationTasks(List<File> ddiFiles, ENOParameters enoParameters, byte[] metadataBytes, byte[] specificTreatmentBytes, byte[] mappingBytes) throws IOException{
		LOGGER.info("Creation of new Tasks (which will be executed on separated threads) to transform the models in parallel");
		List<Callable<File>> generationTasks = new ArrayList<Callable<File>>();
		for(File ddiFile : ddiFiles) {
			File movedFile = moveFile(ddiFile);
			generationTasks.add( ()-> {
				ParameterizedGenerationService parameterizedGenerationServiceThread = new ParameterizedGenerationService(false,getTempSurveyFolder(movedFile));
				File output = parameterizedGenerationServiceThread.generateQuestionnaire(
						movedFile, 
						enoParameters, 
						metadataBytes!=null? new ByteArrayInputStream(metadataBytes):null,
						specificTreatmentBytes!=null ? new ByteArrayInputStream(specificTreatmentBytes):null, 
						mappingBytes!=null ? new ByteArrayInputStream(mappingBytes):null);
				return output;});
		}
		return generationTasks;

	}

	/**
	 * Move a file to a sub-directory whose name is the file's name
	 * The goal is to prevent parallel treatments from interfering
	 * @param file to move
	 * @return the file moved
	 * @throws IOException
	 */
	private File moveFile(File file) throws IOException {
		Path newDirectoryPath = Paths.get(file.getParent() + "/"+ FilenameUtils.removeExtension(file.getName()));
		Files.createDirectories(newDirectoryPath);	
		Path movedPath = Paths.get(newDirectoryPath.toString()+"/"+file.getName());
		Files.move(file.toPath(), movedPath);
		return movedPath.toFile();
	}


	private String getTempSurveyFolder(File ddifile) {
		return ddifile.getParentFile().getParentFile().getName() + "/" + ddifile.getParentFile().getName();
	}

	/**
	 * Clean the temp dir if it exists
	 * 
	 * @throws IOException
	 * 
	 */
	public void cleanTempFolder(String name) throws IOException {
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
