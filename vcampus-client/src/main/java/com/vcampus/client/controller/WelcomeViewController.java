package com.vcampus.client.controller; // 确保包名完全正确

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeViewController implements Initializable {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox mainContentBox;

    @FXML
    private VBox topContentBox;

    @FXML
    private VBox bottomContentBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 设置滚动条样式
        if (scrollPane != null) {
            // 设置滚动条策略
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            
            // 设置内容适应宽度
            scrollPane.setFitToWidth(true);
            
            // 设置平滑滚动
            scrollPane.setPannable(true);
        }
    }
}
