package com.vcampus.client.controller; // 确保包名完全正确

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
        if (topContentBox != null && bottomContentBox != null) {
            // 设置初始位置（在屏幕外）
            topContentBox.setTranslateY(-600);  // 顶部内容从上方滑入
            bottomContentBox.setTranslateY(600); // 底部内容从下方滑入
            
            // 创建一个平移动画，作用于 topContentBox
            TranslateTransition slideInTop = new TranslateTransition(Duration.seconds(0.8), topContentBox);
            slideInTop.setToY(0);
            slideInTop.setInterpolator(Interpolator.EASE_OUT);

            // 创建一个平移动画，作用于 bottomContentBox
            TranslateTransition slideInBottom = new TranslateTransition(Duration.seconds(0.8), bottomContentBox);
            slideInBottom.setToY(0);
            slideInBottom.setInterpolator(Interpolator.EASE_OUT);

            // 给下方的动画增加一点延迟，效果更好
            slideInBottom.setDelay(Duration.millis(200));

            // 播放两个动画
            slideInTop.play();
            slideInBottom.play();
        }
        
        // 设置滚动条样式
        if (scrollPane != null) {
            // 设置滚动条策略
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            
            // 设置内容适应宽度
            scrollPane.setFitToWidth(true);
            
            // 设置平滑滚动
            scrollPane.setPannable(true);
            
            // 确保内容填满整个可用空间
            scrollPane.setFitToHeight(true);
        }
        
        // 确保主内容容器填满可用空间
        if (mainContentBox != null) {
            mainContentBox.setMaxWidth(Double.MAX_VALUE);
            mainContentBox.setMaxHeight(Double.MAX_VALUE);
        }
    }
}
