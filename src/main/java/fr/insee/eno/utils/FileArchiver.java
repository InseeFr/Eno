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

public class FileArchiver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileArchiver.class);
	

	/**
     * Add a file into Zip file.
     * 
     * @param filePath
     * @param zipStream
     * @throws FileNotFoundException
     * @throws IOException
     */

    public static void writeToZipFile(String zipEntryFileName, String filePath, ZipOutputStream zipStream)
            throws FileNotFoundException, IOException {
    	
    	
    	LOGGER.info("Writing file : '"+filePath+ "' to archive file.");

        File file = new File(filePath);
        FileInputStream fileIS = new FileInputStream(file);
        
        
        ZipEntry zipEntry= new ZipEntry(zipEntryFileName);
        
        zipStream.putNextEntry(zipEntry);
        zipStream.write(IOUtils.toByteArray(fileIS));
        zipStream.closeEntry();
        fileIS.close();
    }
    
}
