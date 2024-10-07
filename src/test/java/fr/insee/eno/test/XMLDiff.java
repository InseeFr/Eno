package fr.insee.eno.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.input.CommentLessSource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * Created by I6VWID on 15/01/18.
 */
public class XMLDiff {

    final Logger logger = LoggerFactory.getLogger(XMLDiff.class);

    public Diff getDiff(File input, File expected) {
        logger.debug("Diff {} with {}", input.getAbsolutePath(), expected.getAbsolutePath());
        CommentLessSource inputStream = null;
        CommentLessSource expectedStream = null;

        inputStream = new CommentLessSource(new StreamSource(input));
        expectedStream = new CommentLessSource(new StreamSource(expected));

        return DiffBuilder
                .compare(expectedStream)
                .withAttributeFilter(attr -> !attr.getName().equals("enoCoreVersion"))
                .withTest(inputStream)
                .ignoreWhitespace()
                .normalizeWhitespace()
                .checkForIdentical()
                .build();
    }

    public Diff getDiff(String inputFilePath, String expectedFilePath) throws Exception {
        File inputFile = new File(inputFilePath);
        File expectedFile = new File(expectedFilePath);
        try {
            return getDiff(inputFile, expectedFile);
        } catch (Exception e) {
            throw e;
        }
    }

}
