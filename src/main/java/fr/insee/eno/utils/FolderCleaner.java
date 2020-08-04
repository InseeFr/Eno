package fr.insee.eno.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.Constants;

/**
 * Service used to clean folders (deletes all files)
 * 
 * @author gerose
 *
 */
public class FolderCleaner {

	private final static Logger logger = LoggerFactory.getLogger(FolderCleaner.class);

	/**
	 * Method representing the Clean ant target : Cleaning the current
	 * questionnaireFolder (with the created survey name) Cleaning the temp folder
	 * Cleaning the test folder
	 * 
	 * @param questionnaireFolder : the folder that has to be either created or
	 *                            cleaned (having the survey's name)
	 * @throws Exception : FileNotfound / NoAccess mainly
	 */
	public void cleanTarget(String questionnaireFolder) throws Exception {

		String tempFolder = Constants.TEMP_FOLDER_PATH + "/temp";
		String testFolder = Constants.TEMP_FOLDER_PATH + "/test";

		logger.debug("Cleaning directories : " + tempFolder + " | " + testFolder + " | " + questionnaireFolder);

		FileUtils.forceMkdir(new File(tempFolder));
		FileUtils.cleanDirectory(new File(tempFolder));

		FileUtils.forceMkdir(new File(testFolder));
		FileUtils.cleanDirectory(new File(testFolder));

		FileUtils.forceMkdir(new File(questionnaireFolder));
		FileUtils.cleanDirectory(new File(questionnaireFolder));

	}

	/**
	 * Generic method to clean one folder
	 * 
	 * @param folder : the folder to be cleaned
	 * @throws IOException : FileNotfound / NoAccess mainly
	 */
	public void cleanOneFolder(File folder) throws IOException {
		logger.debug("Cleaning " + folder);
		if (folder.exists() && Files.isDirectory(folder.toPath())) {
			FileUtils.cleanDirectory(folder);
		}
	}

	/**
	 * Method to clean all files in folder except one
	 * 
	 * @param folder : the folder to be cleaned
	 * @param generatedFile : the file to not delete
	 * @throws IOException : FileNotfound / NoAccess mainly
	 */
	public void cleanOneFolderExceptGeneratedFile(File folder, File generatedFile) throws IOException  {
		logger.debug("Special cleaning into : " + folder+" (deleting all files except : "+generatedFile.getName() +")");
		if (folder.exists() && Files.isDirectory(folder.toPath())) {
			File[] matchCleaningInput = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if(Files.isDirectory(Paths.get(dir+"/"+name))){
						try {
							cleanOneFolderExceptGeneratedFile(Paths.get(dir+"/"+name).toFile(),generatedFile);
						} catch (IOException e) {
							logger.error(e.getMessage());
						}
					}
					return !name.equals(generatedFile.getName());
				}
			});
			for(File file : matchCleaningInput) {
				if(Files.isDirectory(file.toPath())) {
					if(file.list().length==0) {
						FileUtils.forceDelete(file);
					}
				}
				else {
					FileUtils.forceDelete(file);
				}
			}		
		}
	}
}
