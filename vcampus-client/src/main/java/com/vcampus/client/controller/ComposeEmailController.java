package com.vcampus.client.controller;


import com.vcampus.client.session.UserSession;
import com.vcampus.client.service.ComposeEmailService;
import com.vcampus.client.service.EmailService;
import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

/**
 * 写邮件控制器
 * 处理邮件编写和发送功能
 * 编写人：AI Assistant
 */
public class ComposeEmailController implements IClientController {

    // ==================== FXML 组件 ====================
    
    @FXML private BorderPane rootPane;
    @FXML private TextField recipientField;
    @FXML private TextField subjectField;
    @FXML private TextArea contentArea;
    @FXML private Button saveDraftButton;
    @FXML private Button sendButton;
    @FXML private Button cancelButton;
    @FXML private Label charCountLabel;

    // ==================== 状态变量 ====================
    
    private String currentUserId;
    private Email replyToEmail = null; // 如果是回复邮件
    
    // 服务层
    private ComposeEmailService emailService;
    private EmailService emailServiceForDelete;

    // ==================== 初始化方法 ====================
    
    @FXML
    private void initialize() {
        currentUserId = UserSession.getInstance().getCurrentUserId();
        
        // 初始化服务层
        emailService = new ComposeEmailService();
        emailServiceForDelete = new EmailService();
        
        // 注册到MessageController
        registerToMessageController();
        
        // 设置字符计数监听器
        contentArea.textProperty().addListener((obs, oldText, newText) -> {
            updateCharCount(newText.length());
        });
        
        updateCharCount(0);
    }

    // ==================== IClientController 接口实现 ====================
    
    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            com.vcampus.client.MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setComposeEmailController(this);
            System.out.println("INFO: ComposeEmailController 已成功注册到 MessageController。");
        } else {
            System.err.println("严重错误：ComposeEmailController 注册失败！");
        }
    }

    // ==================== 事件处理方法 ====================
    
    @FXML
    private void handleSendEmail(ActionEvent event) {
        if (!validateInput()) {
            return;
        }
        
        sendButton.setDisable(true);
        
        // 创建邮件对象
        Email email = new Email();
        email.setSenderId(currentUserId);
        email.setRecipientId(recipientField.getText().trim());
        email.setSubject(subjectField.getText().trim());
        email.setContent(contentArea.getText().trim());
        
        // 使用服务层发送邮件
        emailService.sendEmail(email);
    }

    @FXML
    private void handleSaveDraft(ActionEvent event) {
        if (subjectField.getText().trim().isEmpty() && contentArea.getText().trim().isEmpty()) {
            showAlert("提示", "请至少填写主题或内容");
            return;
        }
        
        saveDraftButton.setDisable(true);
        
        // 创建草稿邮件对象
        Email email = new Email();
        email.setSenderId(currentUserId);
        email.setRecipientId(recipientField.getText().trim());
        email.setSubject(subjectField.getText().trim());
        email.setContent(contentArea.getText().trim());
        
        // 使用服务层保存草稿
        emailService.saveDraft(email);
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // 检查是否有未保存的内容
        if (hasUnsavedContent()) {
            Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
            confirmAlert.setTitle("确认");
            confirmAlert.setHeaderText("有未保存的内容");
            confirmAlert.setContentText("确定要取消吗？未保存的内容将丢失。");
            
            confirmAlert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    returnToEmailList();
                }
            });
        } else {
            returnToEmailList();
        }
    }

    // ==================== 公共方法 ====================
    
    /**
     * 设置回复邮件
     * @param email 要回复的邮件
     */
    public void setReplyToEmail(Email email) {
        this.replyToEmail = email;
        
        if (email != null) {
            // 设置收件人
            recipientField.setText(email.getSenderId());
            
            // 设置主题
            String originalSubject = email.getSubject();
            if (!originalSubject.startsWith("Re: ")) {
                subjectField.setText("Re: " + originalSubject);
            } else {
                subjectField.setText(originalSubject);
            }
            
            // 设置内容
            String replyContent = "\n\n--- 原邮件内容 ---\n" + 
                                "发件人: " + email.getSenderId() + "\n" +
                                "时间: " + email.getSendTime() + "\n" +
                                "主题: " + email.getSubject() + "\n\n" +
                                email.getContent() + "\n\n" +
                                "--- 回复内容 ---\n";
            contentArea.setText(replyContent);
            
            // 将光标移到回复内容开始位置
            contentArea.positionCaret(replyContent.indexOf("--- 回复内容 ---") + "--- 回复内容 ---\n".length());
        }
    }

    /**
     * 设置草稿邮件内容
     * @param draftEmail 草稿邮件
     */
    public void setDraftEmail(Email draftEmail) {
        this.replyToEmail = draftEmail; // 复用这个字段来存储草稿
        
        if (draftEmail != null) {
            // 设置收件人
            recipientField.setText(draftEmail.getRecipientId());
            
            // 设置主题
            subjectField.setText(draftEmail.getSubject());
            
            // 设置内容
            contentArea.setText(draftEmail.getContent() != null ? draftEmail.getContent() : "");
            
            // 更新字符计数
            updateCharCount(contentArea.getText().length());
        }
    }

    // ==================== 私有方法 ====================
    
    /**
     * 验证输入
     */
    private boolean validateInput() {
        String recipient = recipientField.getText().trim();
        String subject = subjectField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (recipient.isEmpty()) {
            showAlert("错误", "请输入收件人");
            recipientField.requestFocus();
            return false;
        }
        
        if (subject.isEmpty()) {
            showAlert("错误", "请输入邮件主题");
            subjectField.requestFocus();
            return false;
        }
        
        if (content.isEmpty()) {
            showAlert("错误", "请输入邮件内容");
            contentArea.requestFocus();
            return false;
        }
        
        // 验证收件人格式（简单验证）
        if (!recipient.matches("\\d{7}")) {
            showAlert("错误", "收件人ID格式不正确，应为7位数字");
            recipientField.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * 检查是否有未保存的内容
     */
    private boolean hasUnsavedContent() {
        return !recipientField.getText().trim().isEmpty() ||
               !subjectField.getText().trim().isEmpty() ||
               !contentArea.getText().trim().isEmpty();
    }

    /**
     * 清空表单
     */
    private void clearForm() {
        recipientField.clear();
        subjectField.clear();
        contentArea.clear();
        updateCharCount(0);
    }

    /**
     * 返回邮件列表
     */
    private void returnToEmailList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/email/EmailView.fxml"));
            BorderPane emailView = loader.load();
            
            // 获取父容器并替换整个内容
            Parent parent = rootPane.getParent();
            if (parent instanceof BorderPane) {
                BorderPane parentPane = (BorderPane) parent;
                parentPane.setCenter(emailView);
            } else {
                // 如果没有父容器或父容器不是BorderPane，直接替换rootPane的内容
                rootPane.getChildren().clear();
                rootPane.setTop(null);
                rootPane.setCenter(emailView);
                rootPane.setBottom(null);
            }
            
        } catch (Exception e) {
            showAlert("错误", "无法返回邮件列表: " + e.getMessage());
        }
    }

    // ==================== 服务端响应处理 ====================
    
    /**
     * 处理服务端邮件响应
     * @param message 服务端响应消息
     */
    public void handleEmailResponse(Message message) {
        Platform.runLater(() -> {
            ActionType actionType = message.getAction();
            
            switch (actionType) {
                case EMAIL_SEND:
                    sendButton.setDisable(false);
                    if (emailService.handleSendResponse(message)) {
                        showAlert("成功", "邮件发送成功！");
                        
                        // 如果是继续编写草稿，发送成功后删除原草稿
                        if (replyToEmail != null && replyToEmail.getStatus() == com.vcampus.common.enums.EmailStatus.DRAFT) {
                            emailServiceForDelete.deleteEmail(replyToEmail.getEmailId(), currentUserId);
                        }
                        
                        clearForm();
                        returnToEmailList();
                    } else {
                        String errorMsg = emailService.getErrorMessage(message);
                        showAlert("错误", "邮件发送失败: " + errorMsg);
                    }
                    break;
                    
                case EMAIL_SAVE_DRAFT:
                    saveDraftButton.setDisable(false);
                    if (emailService.handleSaveDraftResponse(message)) {
                        showAlert("成功", "草稿保存成功！");
                        
                        // 如果是继续编写草稿，保存成功后删除原草稿
                        if (replyToEmail != null && replyToEmail.getStatus() == com.vcampus.common.enums.EmailStatus.DRAFT) {
                            emailServiceForDelete.deleteEmail(replyToEmail.getEmailId(), currentUserId);
                        }
                        
                        clearForm();
                        returnToEmailList();
                    } else {
                        String errorMsg = emailService.getErrorMessage(message);
                        showAlert("错误", "草稿保存失败: " + errorMsg);
                    }
                    break;
                    
                default:
                    // 其他邮件响应类型由EmailController处理
                    break;
            }
        });
    }


    /**
     * 更新字符计数
     */
    private void updateCharCount(int count) {
        charCountLabel.setText("字符数: " + count);
    }

    /**
     * 显示警告对话框
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
