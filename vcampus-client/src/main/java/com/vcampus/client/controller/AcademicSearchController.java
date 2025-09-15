package com.vcampus.client.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * 【简化版】不再需要处理返回按钮的回调
 */
public class AcademicSearchController {
    @FXML private WebView webView;
    private WebEngine webEngine;

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();
    }

    public void loadURL(String url) {
        webEngine.load(url);
    }
}