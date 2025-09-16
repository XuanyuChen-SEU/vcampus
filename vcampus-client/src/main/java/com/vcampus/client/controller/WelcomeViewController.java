package com.vcampus.client.controller; // 确保包名完全正确

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeViewController implements Initializable {

    @FXML
    private VBox topContentBox;

    @FXML
    private VBox bottomContentBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 确保 FXML 中 topContentBox 和 bottomContentBox 的 fx:id 已经设置
        if (topContentBox != null && bottomContentBox != null) {
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
    }
}
