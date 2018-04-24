package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.browser.Browser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FakeApp extends Application {

	private static FakeAppConf fakeAppConf;

	private final static Double PX_TO_MM_COEF = 0.26458333;

	private static Document doc;
	private static Browser browser;

	final static Logger logger = LoggerFactory.getLogger(FakeApp.class);

	@Override
	public void start(Stage stage) {
		// create the scene
		stage.setTitle("web view");
		logger.debug("Call Constructor Browser with " + fakeAppConf.getHtmlFilePath() + ", height : "
				+ fakeAppConf.getHeight() + ", width : " + fakeAppConf.getWidth());
		browser = new Browser(fakeAppConf.getHtmlFilePath(), fakeAppConf.getHeight(), fakeAppConf.getWidth());
		logger.debug("Browser init");
		Scene scene = new Scene(browser, fakeAppConf.getWidth(), fakeAppConf.getHeight(), Color.web("#666970"));
		logger.debug("Scene init");
		stage.setScene(scene);
		logger.debug("Show app");
		stage.show();
	}

	public FakeApp() {
		logger.debug("Call Constructor without args");
	}

	public static void run(String inFileName, String outFileName, String xmlConfFile) throws Exception {

		fakeAppConf = new FakeAppConf();
		fakeAppConf.init(inFileName, outFileName, xmlConfFile);
		InputXml inputXml = new InputXml(inFileName, fakeAppConf.getHtmlFilePath(), fakeAppConf.getImageDirectoryPath());
		doc = inputXml.calculateNewTableDimensions();
		launch();
		HashMap<Object, Object> idWidthValues;
		// launch(args);
		idWidthValues = browser.getMapResult();
		for (Map.Entry<Object, Object> entry : idWidthValues.entrySet()) {
			String key = ((String) entry.getKey());
			String val = (((Integer) entry.getValue() * PX_TO_MM_COEF) + 1) + "";
			Element element = ((Element) XPathFactory.newInstance().newXPath().evaluate("//*[@id='" + key + "']", doc,
					XPathConstants.NODE));
			if (element != null)
				element.setAttribute("width", val + "mm");
		}
		for (int i = 0; i < doc.getElementsByTagName("fo:table").getLength(); i++) {
			((Element) doc.getElementsByTagName("fo:table").item(i)).setAttribute("max-width", "190mm");
		}
		generateOutPutFile();
		deleteHtmlTempFile();

	}

	private static void deleteHtmlTempFile() {
		File file = new File(fakeAppConf.getHtmlFilePath());
		file.delete();
	}

	private static void generateOutPutFile() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result2 = new StreamResult(new File(fakeAppConf.getOutputFilePath()));
		transformer.transform(source, result2);
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

}
