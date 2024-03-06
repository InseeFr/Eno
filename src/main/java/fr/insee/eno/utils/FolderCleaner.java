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
	 * Generic method to clean one folder
	 * 
	 * @param folder : the folder to be cleaned
	 * @throws IOException : FileNotfound / NoAccess mainly
	 */
	public static void cleanOneFolder(File folder) throws IOException {
		logger.debug("Cleaning " + folder);
		if (folder.exists() && Files.isDirectory(folder.toPath())) {
			FileUtils.cleanDirectory(folder);
		}
		Files.deleteIfExists(Paths.get(folder.toURI()));
	}
}
