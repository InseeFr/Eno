package fr.insee.eno.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.parameters.OutFormat;

public class FileArchiver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileArchiver.class);
	
	private static final String FR_ZIP_ENTRY_PATH_FORMAT = "orbeon/fr/%s/%s/form/%s";
	private static final String DEFAULT_ZIP_ENTRY_PATH_FORMAT = "%s/%s/%s";
	/**
     * Add a file into Zip file.
     * 
     * @param filePath
     * @param zipStream
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeToZipFile(String surveyName, String filePath, ZipOutputStream zipStream, OutFormat outFormat)
            throws FileNotFoundException, IOException {
    	
    	LOGGER.info("Writing file : '"+filePath+ "' to archive file.");

        File file = new File(filePath);
        FileInputStream fileIS = new FileInputStream(file);
        
        
        ZipEntry zipEntry=null;        
        switch (outFormat) {
		case FR:
			zipEntry = new ZipEntry(String.format(FR_ZIP_ENTRY_PATH_FORMAT, surveyName, file.getParentFile().getName(),file.getName()));
			break;
		default:
			zipEntry = new ZipEntry(String.format(DEFAULT_ZIP_ENTRY_PATH_FORMAT, surveyName, file.getParentFile().getParentFile().getName(),file.getName()));
			break;
		}
        
        zipStream.putNextEntry(zipEntry);
        zipStream.write(IOUtils.toByteArray(fileIS));
        zipStream.closeEntry();
        fileIS.close();
    }

}
