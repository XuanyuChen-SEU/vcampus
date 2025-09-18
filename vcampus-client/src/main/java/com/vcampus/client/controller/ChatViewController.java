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
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * ChatViewController（修改版）
 * - 支持全局提示词（Prompt）动态控制 AI 风格
 */
public class ChatViewController {

    @FXML private VBox chatPane;
    @FXML private TextField messageField;
    @FXML private ScrollPane chatScrollPane;
    @FXML private BorderPane mainPane;
    @FXML private ImageView watermarkLogo;

    private final String QWEN_API_KEY = "sk-6bbf80746736489788f22425cbe0040f";
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String GLOBAL_SYSTEM_PROMPT = """
你是校园系统的智能助手“V-Campus AI”，只能用中文回答。
你已经熟悉校园系统的功能和使用说明，包括学生信息、教师信息、学籍管理、PDF导出、休学/复学申请等操作。
你的任务是帮助用户解答校园系统相关问题，提供操作指导和使用建议。
回答应尽量简明，最多不超过50字，必要时可给出步骤或示例。
如果用户的问题不在系统功能范围内，请礼貌地说明无法回答。
""";

    @FXML
    public void initialize() {
        chatPane.heightProperty().addListener(observable -> chatScrollPane.setVvalue(1D));
        addAIMessage("您好！我是由阿里通义千问驱动的 V-Campus 智能助手。");

        FadeTransition ft = new FadeTransition(Duration.seconds(4), watermarkLogo);
        ft.setFromValue(0.1);
        ft.setToValue(0.3);
        ft.setAutoReverse(true);
        ft.setCycleCount(FadeTransition.INDEFINITE);
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

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {

                // 使用 classpath 读取 Word 文件内容
                String wordContent = readWordFile("/ai_prompt/系统使用说明.docx");

                JsonObject systemMessage = new JsonObject();
                systemMessage.addProperty("role", "system");
                systemMessage.addProperty("content",
                        GLOBAL_SYSTEM_PROMPT + "\n以下是系统功能说明:\n" + wordContent);

                JsonObject userMessage = new JsonObject();
                userMessage.addProperty("role", "user");
                userMessage.addProperty("content", userInput);

                JsonArray messages = new JsonArray();
                messages.add(systemMessage);
                messages.add(userMessage);

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", "qwen-turbo");
                requestBody.add("messages", messages);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + QWEN_API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestBody)))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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

    // ======================================
    // 读取 Word 文件（classpath 方式）
    // ======================================
    public String readWordFile(String resourcePath) {
        StringBuilder sb = new StringBuilder();

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("找不到资源文件: " + resourcePath);
                return "";
            }

            try (XWPFDocument document = new XWPFDocument(is)) {
                for (XWPFParagraph para : document.getParagraphs()) {
                    sb.append(para.getText()).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    // ======================================
    // UI 更新方法
    // ======================================
    private void addUserMessage(String message) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_RIGHT);

        Label bubbleLabel = new Label(message);
        bubbleLabel.getStyleClass().addAll("chat-bubble", "user-bubble");
        bubbleLabel.maxWidthProperty().bind(chatPane.widthProperty().multiply(0.7));

        row.getChildren().add(bubbleLabel);
        chatPane.getChildren().add(row);
    }

    private void addAIMessageLabel(Label messageLabel) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);

        messageLabel.getStyleClass().addAll("chat-bubble", "ai-bubble");
        messageLabel.maxWidthProperty().bind(chatPane.widthProperty().multiply(0.7));

        row.getChildren().add(messageLabel);
        chatPane.getChildren().add(row);
    }

    private void addAIMessage(String message) {
        addAIMessageLabel(new Label(message));
    }
}
