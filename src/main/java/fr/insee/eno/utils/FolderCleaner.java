package fr.insee.eno.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.insee.eno.Constants;

/**
 * Service used to clean folders (deletes all files)
 * 
 * @author gerose
 *
 */
public class FolderCleaner {

	private final static Logger logger = LogManager.getLogger(FolderCleaner.class);

	/**
	 * Method representing the Clean ant target : Cleaning the current
	 * questionnaireFolder (with the created survey name) Cleaning the temp
	 * folder Cleaning the test folder
	 * 
	 * @param questionnaireFolder
	 *            : the folder that has to be either created or cleaned (having
	 *            the survey's name)
	 * @throws Exception
	 *             : FileNotfound / NoAccess mainly
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
	 * @param folder
	 *            : the folder to be cleaned
	 * @throws Exception
	 *             : FileNotfound / NoAccess mainly
	 */
	public void cleanOneFolder(File folder) throws IOException {
		logger.debug("Cleaning " + folder);
		if (folder.exists() && Files.isDirectory(folder.toPath())) {
			FileUtils.cleanDirectory(folder);
		}		
	}
}
