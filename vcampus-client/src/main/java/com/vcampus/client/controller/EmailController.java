package com.vcampus.client.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vcampus.client.session.UserSession;
import com.vcampus.client.service.EmailService;
import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.ActionType;
import com.vcampus.common.enums.EmailStatus;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;

/**
 * 邮件系统控制器
 * 处理邮件相关的UI交互和网络请求
 * 编写人：AI Assistant
 */
public class EmailController implements IClientController {

    // ==================== FXML 组件 ====================
    
    @FXML private BorderPane rootPane;
    @FXML private TextField searchField;
    @FXML private Button composeButton;
    @FXML private Button refreshButton;
    
    // 邮件分类标签
    @FXML private Button inboxTab;
    @FXML private Button sentTab;
    @FXML private Button draftTab;
    
    // 邮件列表
    @FXML private TableView<Email> emailTable;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, String> timeColumn;
    @FXML private TableColumn<Email, String> statusColumn;
    
    // 分页控制
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    
    // 邮件详情
    @FXML private Label detailSubjectLabel;
    @FXML private Label detailSenderLabel;
    @FXML private Label detailRecipientLabel;
    @FXML private Label detailTimeLabel;
    @FXML private Label detailStatusLabel;
    @FXML private Label detailContentLabel;
    
    // 操作按钮
    @FXML private Button markReadButton;
    @FXML private Button markUnreadButton;
    @FXML private Button continueComposeButton;
    @FXML private Button deleteButton;
    @FXML private Button replyButton;
    
    // 状态栏
    @FXML private Label unreadCountLabel;

    // ==================== 状态变量 ====================
    
    private String currentTab = "inbox"; // 当前选中的标签页
    private int currentPage = 1;
    private int pageSize = 10;
    private Email selectedEmail = null;
    private ObservableList<Email> emailList = FXCollections.observableArrayList();
    private String currentUserId;
    
    // 服务层
    private EmailService emailService;

    // ==================== 初始化方法 ====================
    
    @FXML
    private void initialize() {
        currentUserId = UserSession.getInstance().getCurrentUserId();
        
        // 初始化服务层
        emailService = new EmailService();
        
        // 注册到MessageController
        registerToMessageController();
        
        // 初始化表格列
        initializeTableColumns();
        
        // 设置表格选择监听器
        emailTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // 调用readEmail方法获取完整的邮件详情
                    emailService.readEmail(newSelection.getEmailId(), currentUserId);
                }
            }
        );
        
        // 初始化标签页样式
        updateTabStyles();
        
        // 初始化表格行样式
        updateTableRowStyles();
        
        // 加载初始数据
        loadEmails();
        updateUnreadCount();
    }

    /**
     * 初始化表格列
     */
    private void initializeTableColumns() {
        updateTableColumns();
        
        subjectColumn.setCellValueFactory(cellData -> {
            Email email = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> email.getSubject());
        });
        
        timeColumn.setCellValueFactory(cellData -> {
            Email email = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> formatDateTime(email.getSendTime()));
        });
        
        statusColumn.setCellValueFactory(cellData -> {
            Email email = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> getStatusText(email.getStatus()));
        });
    }
    
    /**
     * 根据当前标签页更新表格列标题和内容
     */
    private void updateTableColumns() {
        senderColumn.setCellValueFactory(cellData -> {
            Email email = cellData.getValue();
            if ("inbox".equals(currentTab)) {
                // 收件箱显示发件人
                return javafx.beans.binding.Bindings.createStringBinding(() -> email.getSenderId());
            } else if ("sent".equals(currentTab)) {
                // 发件箱显示收件人
                return javafx.beans.binding.Bindings.createStringBinding(() -> email.getRecipientId());
            } else if ("draft".equals(currentTab)) {
                // 草稿箱显示收件人
                return javafx.beans.binding.Bindings.createStringBinding(() -> email.getRecipientId());
            } else {
                return javafx.beans.binding.Bindings.createStringBinding(() -> email.getSenderId());
            }
        });
        
        // 更新列标题
        if ("inbox".equals(currentTab)) {
            senderColumn.setText("发件人");
        } else if ("sent".equals(currentTab)) {
            senderColumn.setText("收件人");
        } else if ("draft".equals(currentTab)) {
            senderColumn.setText("收件人");
        } else {
            senderColumn.setText("发件人");
        }
    }

    // ==================== IClientController 接口实现 ====================
    
    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            com.vcampus.client.MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setEmailController(this);
            System.out.println("INFO: EmailController 已成功注册到 MessageController。");
        } else {
            System.err.println("严重错误：EmailController 注册失败！");
        }
    }

    // ==================== 标签页切换 ====================
    
    @FXML
    private void handleInboxTab(ActionEvent event) {
        currentTab = "inbox";
        currentPage = 1; // 重置到第一页
        updateTabStyles();
        updateTableColumns();
        loadEmails();
    }

    @FXML
    private void handleSentTab(ActionEvent event) {
        currentTab = "sent";
        currentPage = 1; // 重置到第一页
        updateTabStyles();
        updateTableColumns();
        loadEmails();
    }

    @FXML
    private void handleDraftTab(ActionEvent event) {
        currentTab = "draft";
        currentPage = 1; // 重置到第一页
        updateTabStyles();
        updateTableColumns();
        loadEmails();
    }

    /**
     * 更新标签页样式
     */
    private void updateTabStyles() {
        // 重置所有标签页样式
        inboxTab.getStyleClass().removeAll("email-tab-active");
        sentTab.getStyleClass().removeAll("email-tab-active");
        draftTab.getStyleClass().removeAll("email-tab-active");
        
        // 设置当前标签页为激活状态
        switch (currentTab) {
            case "inbox":
                inboxTab.getStyleClass().add("email-tab-active");
                break;
            case "sent":
                sentTab.getStyleClass().add("email-tab-active");
                break;
            case "draft":
                draftTab.getStyleClass().add("email-tab-active");
                break;
        }
    }

    // ==================== 邮件操作 ====================
    
    @FXML
    private void handleComposeEmail(ActionEvent event) {
        try {
            System.out.println("开始加载写邮件界面...");
            
            // 加载写邮件视图
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/email/ComposeEmailView.fxml"));
            System.out.println("FXML加载器创建成功");
            
            BorderPane composeView = loader.load();
            System.out.println("FXML文件加载成功");
            
            // 获取父容器并替换整个内容
            Parent parent = rootPane.getParent();
            if (parent instanceof BorderPane) {
                BorderPane parentPane = (BorderPane) parent;
                parentPane.setCenter(composeView);
            } else {
                // 如果没有父容器或父容器不是BorderPane，直接替换rootPane的内容
                rootPane.getChildren().clear();
                rootPane.setTop(null);
                rootPane.setCenter(composeView);
                rootPane.setBottom(null);
            }
            System.out.println("界面替换成功");
            
        } catch (IOException e) {
            System.err.println("加载写邮件界面失败: " + e.getMessage());
            e.printStackTrace();
            showAlert("错误", "无法打开写邮件界面: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("加载写邮件界面时发生未知错误: " + e.getMessage());
            e.printStackTrace();
            showAlert("错误", "加载写邮件界面时发生错误: " + e.getMessage());
        }
    }

    /**
     * 继续编写草稿邮件
     */
    private void continueComposeEmail(Email draftEmail) {
        try {
            System.out.println("开始继续编写草稿邮件...");
            
            // 加载写邮件视图
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/email/ComposeEmailView.fxml"));
            BorderPane composeView = loader.load();
            
            // 获取控制器并设置草稿内容
            ComposeEmailController composeController = loader.getController();
            composeController.setDraftEmail(draftEmail);
            
            // 获取父容器并替换整个内容
            Parent parent = rootPane.getParent();
            if (parent instanceof BorderPane) {
                BorderPane parentPane = (BorderPane) parent;
                parentPane.setCenter(composeView);
            } else {
                // 如果没有父容器或父容器不是BorderPane，直接替换rootPane的内容
                rootPane.getChildren().clear();
                rootPane.setTop(null);
                rootPane.setCenter(composeView);
                rootPane.setBottom(null);
            }
            
            System.out.println("继续编写界面加载成功");
            
        } catch (Exception e) {
            System.err.println("继续编写草稿失败: " + e.getMessage());
            e.printStackTrace();
            showAlert("错误", "无法继续编写草稿: " + e.getMessage());
        }
    }

    /**
     * 回复邮件
     */
    private void replyToEmail(Email originalEmail) {
        try {
            System.out.println("开始回复邮件...");
            
            // 加载写邮件视图
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/email/ComposeEmailView.fxml"));
            BorderPane composeView = loader.load();
            
            // 获取控制器并设置回复内容
            ComposeEmailController composeController = loader.getController();
            composeController.setReplyToEmail(originalEmail);
            
            // 获取父容器并替换整个内容
            Parent parent = rootPane.getParent();
            if (parent instanceof BorderPane) {
                BorderPane parentPane = (BorderPane) parent;
                parentPane.setCenter(composeView);
            } else {
                // 如果没有父容器或父容器不是BorderPane，直接替换rootPane的内容
                rootPane.getChildren().clear();
                rootPane.setTop(null);
                rootPane.setCenter(composeView);
                rootPane.setBottom(null);
            }
            
            System.out.println("回复界面加载成功");
            
        } catch (Exception e) {
            System.err.println("回复邮件失败: " + e.getMessage());
            e.printStackTrace();
            showAlert("错误", "无法回复邮件: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadEmails();
        updateUnreadCount();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();
        currentPage = 1; // 搜索时重置到第一页
        if (keyword.isEmpty()) {
            loadEmails();
        } else {
            searchEmails(keyword);
        }
    }

    @FXML
    private void handleMarkAsRead(ActionEvent event) {
        if (selectedEmail != null) {
            markEmailAsRead(selectedEmail.getEmailId());
        }
    }

    @FXML
    private void handleMarkAsUnread(ActionEvent event) {
        if (selectedEmail != null) {
            markEmailAsUnread(selectedEmail.getEmailId());
        }
    }

    @FXML
    private void handleDeleteEmail(ActionEvent event) {
        if (selectedEmail != null) {
            deleteEmail(selectedEmail.getEmailId());
        }
    }

    @FXML
    private void handleReplyEmail(ActionEvent event) {
        if (selectedEmail != null) {
            replyToEmail(selectedEmail);
        }
    }

    @FXML
    private void handleContinueCompose(ActionEvent event) {
        if (selectedEmail != null) {
            continueComposeEmail(selectedEmail);
        }
    }

    // ==================== 分页控制 ====================
    
    @FXML
    private void handlePrevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            loadEmails();
        }
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        currentPage++;
        loadEmails();
    }

    // ==================== 网络请求方法 ====================
    
    /**
     * 加载邮件列表
     */
    private void loadEmails() {
        switch (currentTab) {
            case "sent":
                emailService.getSentEmails(currentUserId, currentPage, pageSize);
                break;
            case "draft":
                emailService.getDraftEmails(currentUserId, currentPage, pageSize);
                break;
            default:
                emailService.getInboxEmails(currentUserId, currentPage, pageSize);
                break;
        }
    }

    /**
     * 搜索邮件
     */
    private void searchEmails(String keyword) {
        emailService.searchEmails(currentUserId, keyword, currentPage, pageSize);
    }

    /**
     * 标记邮件为已读
     */
    private void markEmailAsRead(String emailId) {
        emailService.markAsRead(emailId, currentUserId);
    }

    /**
     * 标记邮件为未读
     */
    private void markEmailAsUnread(String emailId) {
        emailService.markAsUnread(emailId, currentUserId);
    }

    /**
     * 删除邮件
     */
    private void deleteEmail(String emailId) {
        emailService.deleteEmail(emailId, currentUserId);
    }

    /**
     * 更新未读邮件数量
     */
    private void updateUnreadCount() {
        emailService.getUnreadCount(currentUserId);
    }

    // ==================== UI 更新方法 ====================
    
    /**
     * 显示邮件详情
     */
    private void showEmailDetail(Email email) {
        selectedEmail = email;
        
        detailSubjectLabel.setText(email.getSubject());
        detailSenderLabel.setText(email.getSenderId());
        detailRecipientLabel.setText(email.getRecipientId());
        detailTimeLabel.setText(formatDateTime(email.getSendTime()));
        detailStatusLabel.setText(getStatusText(email.getStatus()));
        detailContentLabel.setText(email.getContent() != null ? email.getContent() : "无内容");
        
        // 根据当前标签页显示不同的操作按钮
        updateActionButtons(email);
    }
    
    /**
     * 根据当前标签页和邮件状态更新操作按钮
     */
    private void updateActionButtons(Email email) {
        // 先隐藏所有按钮
        markReadButton.setVisible(false);
        markUnreadButton.setVisible(false);
        continueComposeButton.setVisible(false);
        deleteButton.setVisible(false);
        replyButton.setVisible(false);
        
        switch (currentTab) {
            case "inbox":
                // 收件箱：显示标记已读/未读、删除、回复按钮
                deleteButton.setVisible(true);
                replyButton.setVisible(true);
                if (email.getStatus() == EmailStatus.READ) {
                    markUnreadButton.setVisible(true);
                } else {
                    markReadButton.setVisible(true);
                }
                break;
                
            case "sent":
                // 发件箱：只显示删除按钮，不显示标记和回复功能
                deleteButton.setVisible(true);
                break;
                
            case "draft":
                // 草稿箱：显示继续编写、删除按钮
                continueComposeButton.setVisible(true);
                deleteButton.setVisible(true);
                break;
        }
    }
    
    /**
     * 更新表格行的CSS样式
     */
    private void updateTableRowStyles() {
        // 使用TableView的setRowFactory来动态设置样式
        emailTable.setRowFactory(tableView -> {
            javafx.scene.control.TableRow<Email> row = new javafx.scene.control.TableRow<Email>() {
                @Override
                protected void updateItem(Email email, boolean empty) {
                    super.updateItem(email, empty);
                    
                    if (empty || email == null) {
                        setStyle("");
                        getStyleClass().removeAll("read", "unread");
                    } else {
                        // 清除所有状态样式
                        getStyleClass().removeAll("read", "unread");
                        
                        // 根据邮件状态添加相应样式
                        if (email.getStatus() == EmailStatus.READ) {
                            getStyleClass().add("read");
                        } else {
                            getStyleClass().add("unread");
                        }
                    }
                }
            };
            return row;
        });
    }

    /**
     * 清空邮件详情
     */
    private void clearEmailDetail() {
        selectedEmail = null;
        
        detailSubjectLabel.setText("请选择一封邮件查看详情");
        detailSenderLabel.setText("");
        detailRecipientLabel.setText("");
        detailTimeLabel.setText("");
        detailStatusLabel.setText("");
        detailContentLabel.setText("");
        
        // 隐藏操作按钮
        markReadButton.setVisible(false);
        markUnreadButton.setVisible(false);
        deleteButton.setVisible(false);
        replyButton.setVisible(false);
    }

    /**
     * 更新分页信息
     */
    private void updatePageInfo() {
        pageInfoLabel.setText("第 " + currentPage + " 页");
        
        // 更新分页按钮状态
        prevPageButton.setDisable(currentPage <= 1);
        nextPageButton.setDisable(emailList.size() < pageSize);
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
                    if (message.isSuccess()) {
                        showAlert("成功", "邮件发送成功！");
                    } else {
                        showAlert("错误", "邮件发送失败: " + message.getMessage());
                    }
                    break;
                    
                case EMAIL_SAVE_DRAFT:
                    if (message.isSuccess()) {
                        showAlert("成功", "草稿保存成功！");
                    } else {
                        showAlert("错误", "草稿保存失败: " + message.getMessage());
                    }
                    break;
                    
                case EMAIL_GET_INBOX:
                case EMAIL_GET_SENT:
                case EMAIL_GET_DRAFT:
                case EMAIL_SEARCH:
                    if (message.isSuccess()) {
                        List<Email> emails = emailService.handleEmailListResponse(message);
                        if (emails != null) {
                            emailList.clear();
                            emailList.addAll(emails);
                            emailTable.setItems(emailList);
                            updatePageInfo();
                            // 更新表格行样式
                            Platform.runLater(() -> updateTableRowStyles());
                        }
                    } else {
                        showAlert("错误", "获取邮件失败: " + message.getMessage());
                    }
                    break;
                    
                case EMAIL_READ:
                    if (message.isSuccess()) {
                        Email email = emailService.handleEmailResponse(message);
                        if (email != null) {
                            showEmailDetail(email);
                        }
                    } else {
                        showAlert("错误", "读取邮件失败: " + message.getMessage());
                    }
                    break;
                    
                case EMAIL_DELETE:
                    if (message.isSuccess()) {
                        loadEmails();
                        clearEmailDetail();
                    } else {
                        showAlert("错误", "删除失败: " + message.getMessage());
                    }
                    break;
                    
                case EMAIL_MARK_READ:
                case EMAIL_MARK_UNREAD:
                    if (message.isSuccess()) {
                        loadEmails();
                        updateUnreadCount();
                        // 如果当前有选中的邮件，重新获取其详情以更新按钮状态
                        if (selectedEmail != null) {
                            emailService.readEmail(selectedEmail.getEmailId(), currentUserId);
                        }
                    } else {
                        showAlert("错误", "状态更新失败: " + message.getMessage());
                    }
                    break;
                    
                case EMAIL_GET_UNREAD_COUNT:
                    if (message.isSuccess()) {
                        String count = (String) message.getData();
                        unreadCountLabel.setText("未读邮件: " + count);
                    }
                    break;
                    
                case EMAIL_BATCH_MARK_READ:
                case EMAIL_BATCH_DELETE:
                    if (message.isSuccess()) {
                        loadEmails();
                        updateUnreadCount();
                    } else {
                        showAlert("错误", "批量操作失败: " + message.getMessage());
                    }
                    break;
                    
                default:
                    System.out.println("未处理的邮件响应类型: " + actionType);
            }
        });
    }

    // ==================== 工具方法 ====================
    
    /**
     * 格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"));
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(EmailStatus status) {
        if (status == null) return "";
        switch (status) {
            case DRAFT: return "草稿";
            case SENT: return "未读";
            case READ: return "已读";
            default: return "";
        }
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
