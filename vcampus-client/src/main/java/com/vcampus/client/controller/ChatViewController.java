package com.vcampus.client.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 【最终完整版】
 * - 对接 Alibaba Tongyi Qwen (通义千问) 的 "OpenAI 兼容模式" API
 * - 包含了健壮的错误处理和UI布局修正
 */
public class ChatViewController {

    @FXML private VBox chatPane;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;
    // 【新增】为 FXML 中新增的控件添加引用
    @FXML private BorderPane mainPane;
    @FXML private ImageView watermarkLogo;

    // 【核心】请将这里替换为您自己的 阿里云 DashScope API-KEY
    private final String QWEN_API_KEY = "sk-6bbf80746736489788f22425cbe0040f";

    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        // 让滚动条自动滚动到底部
        chatPane.heightProperty().addListener(observable -> chatScrollPane.setVvalue(1D));
        addAIMessage("您好！我是由阿里通义千问驱动的 V-Campus 智能助手。");
        // 【新增】为水印 Logo 添加柔和的呼吸动画
        FadeTransition ft = new FadeTransition(Duration.seconds(4), watermarkLogo);
        ft.setFromValue(0.1); // 从 10% 透明度开始
        ft.setToValue(0.3);   // 到 30% 透明度结束
        ft.setAutoReverse(true); // 自动反向播放
        ft.setCycleCount(FadeTransition.INDEFINITE); // 无限循环
        ft.play();
    }

    @FXML
    private void handleSendMessage() {
        String message = messageField.getText();
        if (message.isEmpty() || QWEN_API_KEY.equals("YOUR_DASHSCOPE_API_KEY_HERE")) {
            if (QWEN_API_KEY.equals("YOUR_DASHSCOPE_API_KEY_HERE")) {
                addAIMessage("错误：请先在 ChatViewController.java 中设置您的阿里云API-KEY。");
            }
            return;
        }
        addUserMessage(message);
        messageField.clear();
        getRealAIResponse(message);
    }

    private void getRealAIResponse(String userInput) {
        Label thinkingLabel = new Label("思考中...");
        addAIMessageLabel(thinkingLabel);

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                // 1. 构建 OpenAI 兼容模式的 JSON 请求体
                JsonObject userMessage = new JsonObject();
                userMessage.addProperty("role", "user");
                userMessage.addProperty("content", userInput);

                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content", "You are a helpful assistant.");

                JsonArray messages = new JsonArray();
                messages.add(systemMessage);
                messages.add(userMessage);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", "qwen-turbo"); // 使用性价比高的 turbo 模型
                requestBody.add("messages", messages);

                // 2. 创建指向 OpenAI 兼容模式 Endpoint 的 HTTP POST 请求
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + QWEN_API_KEY) // 直接使用 Bearer Token 认证
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                        .build();

                // 3. 发送请求并获取响应
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // 4. 解析 OpenAI 兼容模式的 JSON 响应
                if (response.statusCode() == 200) {
                    JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
                    if (responseJson.has("choices")) {
                        return responseJson.getAsJsonArray("choices")
                                .get(0).getAsJsonObject()
                                .getAsJsonObject("message")
                                .get("content").getAsString();
                    } else {
                        throw new RuntimeException("API 返回错误: " + response.body());
                    }
                } else {
                    throw new RuntimeException("API 请求失败，HTTP状态码: " + response.statusCode() + " - " + response.body());
                }
            }
        };

        task.setOnSucceeded(event -> thinkingLabel.setText(task.getValue()));
        task.setOnFailed(event -> {
            thinkingLabel.setText("抱歉，调用AI服务失败。\n原因: " + task.getException().getMessage());
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    // ==========================================================
    // UI 更新方法 (已修正布局问题)
    // ==========================================================

    private void addUserMessage(String message) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_RIGHT);

        Label bubbleLabel = new Label(message);
        bubbleLabel.getStyleClass().addAll("chat-bubble", "user-bubble");

        // 【核心修正】用代码强制绑定Label的最大宽度
        // 它的最大宽度是父容器chatPane宽度的70%
        bubbleLabel.maxWidthProperty().bind(chatPane.widthProperty().multiply(0.7));

        row.getChildren().add(bubbleLabel);
        chatPane.getChildren().add(row);
    }

    private void addAIMessageLabel(Label messageLabel) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        messageLabel.getStyleClass().addAll("chat-bubble", "ai-bubble");

        // 【核心修正】同样为AI消息的Label强制绑定最大宽度
        messageLabel.maxWidthProperty().bind(chatPane.widthProperty().multiply(0.7));

        row.getChildren().add(messageLabel);
        chatPane.getChildren().add(row);
    }

    private void addAIMessage(String message) {
        addAIMessageLabel(new Label(message));
    }
}