package com.vcampus.client.controller;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * WelcomeViewController 是系统欢迎界面的控制器类。
 * <p>
 * 主要负责：
 * <ul>
 *     <li>初始化欢迎界面布局</li>
 *     <li>实现顶部和底部内容的滑入动画效果</li>
 *     <li>配置 ScrollPane 的滚动条和布局行为</li>
 * </ul>
 * <p>
 * 此控制器与 FXML 文件中的 ScrollPane 和 VBox 元素绑定。
 */
public class WelcomeViewController implements Initializable {

    /** 界面的滚动容器，用于显示所有内容并支持滚动 */
    @FXML
    private ScrollPane scrollPane;

    /** 主内容容器，包含顶部和底部内容 */
    @FXML
    private VBox mainContentBox;

    /** 顶部内容容器，进入界面时会从上方滑入 */
    @FXML
    private VBox topContentBox;

    /** 底部内容容器，进入界面时会从下方滑入 */
    @FXML
    private VBox bottomContentBox;

    /**
     * 初始化方法，在界面加载时自动调用。
     * <p>
     * 功能：
     * <ul>
     *     <li>设置顶部和底部内容的初始位置</li>
     *     <li>创建并播放滑入动画</li>
     *     <li>配置 ScrollPane 的滚动条策略和内容适应</li>
     *     <li>确保主内容容器最大化填充可用空间</li>
     * </ul>
     *
     * @param location  FXML 文件的 URL（可用于加载资源）
     * @param resources 国际化资源包（可用于多语言支持）
     */
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
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setFitToWidth(true);   // 内容适应宽度
            scrollPane.setPannable(true);     // 支持平滑拖拽
            scrollPane.setFitToHeight(true);  // 内容填满可用高度
        }

        // 确保主内容容器填满可用空间
        if (mainContentBox != null) {
            mainContentBox.setMaxWidth(Double.MAX_VALUE);
            mainContentBox.setMaxHeight(Double.MAX_VALUE);
        }
    }
}
