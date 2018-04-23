package fr.insee.eno.plugins.tableColumnSizeProcessor.calculator.browser;


import java.io.File;
import java.util.HashMap;

import org.w3c.dom.NodeList;
import org.w3c.dom.html.HTMLImageElement;

import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 * @author Karim
 * @version 1.0.0
 *
 * This Class generate the web browser behavior that will calculate the accurat cell size
 */
public class Browser extends Region {

    private final static Long threadWaitTime = 1_500L;
    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();
    private static HashMap<Object, Object> mapResult = new HashMap<Object, Object>();
    private String functionsWidth;
    private String callFunction;
    private Integer width;
    private Integer height;

    public Browser(String htmlFilePath, Integer height, Integer width) {
        //apply the styles
        init(height, width);
        getStyleClass().add("browser");
        File currentDirFile = new File(".");
        String helper = currentDirFile.getAbsolutePath().replace(".","");
        // load the web page
        webEngine.load("file:///"+helper+htmlFilePath);
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                captureView();
                webEngine.executeScript(functionsWidth);
                JSObject result = (JSObject) webEngine.executeScript(callFunction);
                int elementCount = result.toString().split(",").length;
                for (int i = 0; i < elementCount; i++) {
                    mapResult.put(((JSObject) result.getMember("" + i)).getMember("id"), ((JSObject) result.getMember("" + i)).getMember("width"));
                }
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(threadWaitTime);
                            browser.getScene().getWindow().hide();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.run();
            }
        });
        //add the web view to the scene
        getChildren().add(browser);
    }

    private void init(Integer height, Integer width) {
        functionsWidth =
                "function getEachElementWidth(){" +
                        "var result = [];" +
                        "for(var i=0;i<document.getElementsByTagName('td').length;i++){" +
                        "result.push(" +
                        "{" +
                        "id :document.getElementsByTagName(\"td\")[i].getAttribute(\"id\")," +
                        "width : document.getElementsByTagName(\"td\")[i].offsetWidth" +
                        "}" +
                        ");" +
                        "}" +
                        "return result" +
                        "}";
        callFunction = "getEachElementWidth();";
        this.height = height;
        this.width = width;
    }

    public HashMap<Object, Object> getMapResult() {
        return mapResult;
    }

    public void setMapResult(HashMap<Object, Object> mapResult) {
        this.mapResult = mapResult;
    }

    @Override
    protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }

    @Override
    protected double computePrefWidth(double height) {
        return width;
    }

    @Override
    protected double computePrefHeight(double width) {
        return height;
    }

    private void captureView() {
        NodeList nodeList = webEngine.getDocument().getElementsByTagName("img");
        for (int i = 0; i < nodeList.getLength(); i++) {
            HTMLImageElement n = (HTMLImageElement) nodeList.item(i);
            String path = n.getSrc();
            if (!path.startsWith("file:///")) {
                path = "file:///" + path;
            }
            n.setSrc(path);
        }
    }


}
