package fr.insee.eno.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

/**
 * Created by I6VWID on 15/01/18.
 */
public class XMLDiff { 

    public XMLDiff() {
        
    }
    
    public Diff getDiff(InputStream input, String expectedFilePath) throws Exception {
        System.out.println(String.format("Diff input with %s", expectedFilePath));
        InputStream expected = null;
        try {
            expected = XMLDiff.class.getClassLoader().getResourceAsStream(expectedFilePath);
            return DiffBuilder
                    .compare(Input.fromStream(expected))
                    .withTest(new String(IOUtils.toByteArray(input), "UTF-8"))
                    .ignoreWhitespace()
                    .normalizeWhitespace()
                    .checkForIdentical()
                    .build();
        } catch (Exception e) {
            throw e;
        } finally {
            close(expected, input);
        }
    }

    public Diff getDiff(String inputFilePath, String expectedFilePath) throws Exception {
        System.out.println(String.format("Diff %s with %s", inputFilePath, expectedFilePath));
        try {
        	InputStream input = XMLDiff.class.getClassLoader().getResourceAsStream(inputFilePath);
            return getDiff(input, expectedFilePath);
        } catch (Exception e) {
            throw e;
        }
    }


    private void close(InputStream expected, InputStream input) throws Exception{
        try {
            if(null != input){
            	input.close();
            }
            if(null != expected){
                expected.close();
            }
        } catch (IOException e) {
            throw e;
        }
    }

}
