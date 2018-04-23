package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.utils;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.Main;

/**
 * Singleton Util class
 * @author Karim
 */
public class MainUtil {

    private static Map<String,String> argsMap;
    private static String helper;
    private static MainUtil __instance;

    private MainUtil() {
    }

    /**
     *
     * @return
     */
    public static MainUtil getInstance(){
        if(__instance == null)
            __instance = new MainUtil();
        return __instance;
    }

    /**
     * Get main args and check values and file/directory
     * @param args
     * @return
     * @throws Exception
     */
    public static Map<String,String> makeArgsMap(String[] args) throws Exception {
        argsMap = new HashMap<>();
        File fIn;
        File fOut;
        for (String arg : args) {
            if (arg.contains("=")) {
                argsMap.put(arg.substring(0, arg.indexOf('=')),
                        arg.substring(arg.indexOf('=') + 1));
            }
        }
        if (!argsMap.containsKey("inFileName") || !argsMap.containsKey("outFileName")|| !argsMap.containsKey("xmlConfFile")) {
            throw new Exception("inFileName, outFileName and xmlConfFile are required params,\n please insert all of them.");
        } else {
            fIn = new File(argsMap.get("inFileName"));
            fOut = new File(argsMap.get("outFileName"));
            File fXmlFile = new File(argsMap.get("xmlConfFile"));
            if (!fIn.exists())
                throw new Exception("No file found by inFileName given: "+fIn.getAbsolutePath()+", please insert a valid and existing inFileName");
            if (fOut.exists()) {
                if (fOut.delete()) {
                    System.out.println(fOut.getName() + " is deleted!");
                } else {
                    throw new Exception("Cannot Delete File : " + fIn.getAbsolutePath() + ", Maybe he is used by other program");
                }
            }
            if (!fXmlFile.exists())
                throw new Exception("No file found by xmlConfFile given "+fXmlFile.getAbsolutePath()+", try with a valid and existing xmlConfFile name");
            argsMap.putAll(getXmlConf(fXmlFile));
            if(!argsMap.containsKey("imageDirectory"))
                throw new Exception("the xml file given "+fXmlFile.getAbsolutePath()+",not contains ImageDirectory path");

        }
        return argsMap;
    }

    /**
     * Read xml conf file
     * @param xmlConf
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private static Map<String,String> getXmlConf(File xmlConf) throws ParserConfigurationException, IOException, SAXException {
        Map<String,String> result = new HashMap<>();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlConf);
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("TableColumnSizeProcessor").item(0).getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if(!nNode.getNodeName().equals("#text"))
                result.put(nNode.getNodeName(),nNode.getChildNodes().item(0).getNodeValue());
        }
        return result;
    }

    /**
     * Last check before start runing calculation
     */
    public static void checkBeforeStart() {
        try{
            Integer confPageWidth;
            Integer confPageHeight;
            try {
                confPageWidth = Integer.parseInt(argsMap.get("pageWidth"));
            } catch (Exception e) {
                confPageWidth = 0;
            }
            try {
                confPageHeight = Integer.parseInt(argsMap.get("pageHeight"));
            } catch (Exception e) {
                confPageHeight = 0;
            }
            if (Main.getWidth() == null){
                if(confPageWidth>0)
                    Main.setWidth(confPageWidth);
                else
                    Main.setWidth(718);
            }
            if (Main.getHeight() == null) {
                if(confPageHeight>0)
                    Main.setHeight(confPageHeight);
                else
                    Main.setHeight(720);
            }
            if (Main.getInputFilePath() == null)
                Main.setInputFilePath(argsMap.get("inFileName"));
            if (Main.getOutputFilePath() == null)
                Main.setOutputFilePath(argsMap.get("outFileName"));
            if (Main.getHtmlFilePath() == null)
                Main.setHtmlFilePath(Main.HTML_FILE_NAME);
            if (Main.getImageDirectoryPath() == null)
                Main.setImageDirectoryPath(argsMap.get("imageDirectory"));
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void builHelper(){
        helper = "";
        try {
            helper = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            helper = helper.substring(0, helper.lastIndexOf("/"));
        } catch (URISyntaxException e) {
            helper = "";
            e.printStackTrace();
        }
    }
}
