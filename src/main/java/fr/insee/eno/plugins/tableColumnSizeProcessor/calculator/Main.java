package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.browser.Browser;
import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.utils.MainUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private final static Double PX_TO_MM_COEF = 0.26458333;
    public final static String HTML_FILE_NAME = "test.html";

    private static Document doc;
    private static Browser browser;

    private static Integer width;
    private static Integer height;
    private static String inputFilePath;
    private static String outputFilePath;
    private static String htmlFilePath;
    private static String imageDirectoryPath;
    private static Map<String, String> argsMap;


    @Override
    public void start(Stage stage) {
        // create the scene
        stage.setTitle("web view");
        browser = new Browser(htmlFilePath, height, width);
        Scene scene = new Scene(browser, Main.getWidth(), Main.getHeight(), Color.web("#666970"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //Read Input File
        try {
            MainUtil.getInstance().builHelper();
            argsMap = MainUtil.getInstance().makeArgsMap(args);
            MainUtil.getInstance().checkBeforeStart();
            InputXml inputXml = new InputXml(Main.inputFilePath, htmlFilePath, imageDirectoryPath);
            doc = inputXml.calculateNewTableDimensions();
            HashMap<Object, Object> idWidthValues;
            launch(args);
            idWidthValues = browser.getMapResult();
            for (Map.Entry<Object, Object> entry : idWidthValues.entrySet()) {
                String key = ((String) entry.getKey());
                String val = (((Integer) entry.getValue() * PX_TO_MM_COEF) + 1) + "";
                Element element = ((Element) XPathFactory.newInstance().newXPath().evaluate("//*[@id='" + key + "']", doc, XPathConstants.NODE));
                if (element != null)
                    element.setAttribute("width", val + "mm");
            }
            for (int i = 0; i < doc.getElementsByTagName("fo:table").getLength(); i++) {
                ((Element) doc.getElementsByTagName("fo:table").item(i)).setAttribute("max-width", "190mm");
            }
            generateOutPutFile();
            deleteHtmlTempFile();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    private static void deleteHtmlTempFile(){
        File file = new File(HTML_FILE_NAME);
        file.delete();
    }

    private static void generateOutPutFile() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result2 = new StreamResult(new File(outputFilePath));
        transformer.transform(source, result2);
    }


    public static Integer getWidth() {
        return width;
    }

    public static void setWidth(Integer width) {
        Main.width = width;
    }

    public static Integer getHeight() {
        return height;
    }

    public static void setHeight(Integer height) {
        Main.height = height;
    }

    public static String getInputFilePath() {
        return inputFilePath;
    }

    public static void setInputFilePath(String inputFilePath) {
        Main.inputFilePath = inputFilePath;
    }

    public static String getOutputFilePath() {
        return outputFilePath;
    }

    public static void setOutputFilePath(String outputFilePath) {
        Main.outputFilePath = outputFilePath;
    }

    public static String getImageDirectoryPath() {
        return imageDirectoryPath;
    }

    public static void setImageDirectoryPath(String imageDirectoryPath) {
        Main.imageDirectoryPath = imageDirectoryPath;
    }

    public static String getHtmlFilePath() {
        return htmlFilePath;
    }

    public static void setHtmlFilePath(String htmlFilePath) {
        Main.htmlFilePath = htmlFilePath;
    }
}