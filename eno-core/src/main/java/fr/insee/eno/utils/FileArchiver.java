package fr.insee.eno.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileArchiver {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileArchiver.class);

    public static void writeToZipOS(String zipEntryFileName, ByteArrayOutputStream fileStream, ZipOutputStream zipStream)
            throws IOException {
        LOGGER.info("Writing file : '"+zipEntryFileName+ "' to archive file.");

        ZipEntry zipEntry= new ZipEntry(zipEntryFileName);

        zipStream.putNextEntry(zipEntry);
        zipStream.write(fileStream.toByteArray());
        zipStream.closeEntry();
        fileStream.close();
    }
    
}
