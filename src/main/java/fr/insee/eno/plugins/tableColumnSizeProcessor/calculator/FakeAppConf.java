package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.utils.MainUtil;

public class FakeAppConf {

	final static Logger logger = LoggerFactory.getLogger(FakeAppConf.class);

	private MainUtil mainUtil;

	public final static String HTML_FILE_NAME = "test.html";

	private Integer width;
	private Integer height;
	private String inputFilePath;
	private String outputFilePath;
	private String htmlFilePath;
	private String imageDirectoryPath;
	private Map<String, String> xmlConf;

	public FakeAppConf() {
		logger.debug("Call Constructor without args");
	}

	public void init(String inFileName, String outFileName, String xmlConfFile) throws Exception {
		logger.debug("Call init method with args");
		this.mainUtil = MainUtil.getInstance();
		mainUtil.builHelper();
		mainUtil.checkInput(inFileName, outFileName, xmlConfFile);
		xmlConf = mainUtil.getXmlConf(new File(xmlConfFile));
		mainUtil.checkBeforeStart(xmlConf);
		width = mainUtil.getPageWidthFromXMlFile(xmlConf);
		height = mainUtil.getPageHeightFromXMlFile(xmlConf);
		inputFilePath = inFileName;
		outputFilePath = outFileName;
		htmlFilePath = FakeAppConf.HTML_FILE_NAME;
		imageDirectoryPath = xmlConf.get("imageDirectory");

	}

	public FakeAppConf(String[] args) throws Exception {
		logger.debug("Call Constructor with args");
		Map<String, String> argsMap = new HashMap<>();
		for (String arg : args) {
			if (arg.contains("=")) {
				argsMap.put(arg.substring(0, arg.indexOf('=')), arg.substring(arg.indexOf('=') + 1));
			}
		}
		init(argsMap.get("inFileName"), argsMap.get("outFileName"), argsMap.get("xmlConfFile"));

	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public void setInputFilePath(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public String getHtmlFilePath() {
		return htmlFilePath;
	}

	public void setHtmlFilePath(String htmlFilePath) {
		this.htmlFilePath = htmlFilePath;
	}

	public String getImageDirectoryPath() {
		return imageDirectoryPath;
	}

	public void setImageDirectoryPath(String imageDirectoryPath) {
		this.imageDirectoryPath = imageDirectoryPath;
	}

}
