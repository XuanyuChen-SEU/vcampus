package com.vcampus.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.control.ButtonType;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.EmailAdminService;
import com.vcampus.common.dto.Email;
import com.vcampus.common.dto.Message;
import com.vcampus.common.enums.EmailStatus;
import java.util.List;
import java.util.Map;

/**
 * 邮件管理员控制器
 * 简洁版本：只提供查看所有邮件、搜索和统计功能
 */
public class EmailAdminViewController {
    
    @FXML private TableView<Email> emailsTable;
    @FXML private TableColumn<Email, String> emailIdColumn;
    @FXML private TableColumn<Email, String> senderColumn;
    @FXML private TableColumn<Email, String> recipientColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, String> timeColumn;
    @FXML private TableColumn<Email, EmailStatus> statusColumn;
    @FXML private TableColumn<Email, Void> actionsColumn;
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    
    // 用户ID搜索控件
    @FXML private TextField userSearchField;
    @FXML private Button userSearchButton;
    @FXML private Button clearUserSearchButton;
    
    // 分页控件
    @FXML private Button firstPageButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Button lastPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private ComboBox<Integer> pageSizeCombo;
    
    @FXML private Label totalCountLabel;
    @FXML private Label statusLabel;
    
    private EmailAdminService emailAdminService;
    private ObservableList<Email> emailsList;
    
    // 分页相关变量
    private int currentPage = 1;
    private int pageSize = 20;
    private int totalPages = 1;
    private String currentKeyword = "";
    private String currentUserSearch = "";
    
    /**
     * 初始化控制器
     */
    @FXML
    private void initialize() {
        emailAdminService = new EmailAdminService();
        emailsList = FXCollections.observableArrayList();
        
        // 设置表格列
        setupTableColumns();
        
        // 设置分页控件
        setupPaginationControls();
        
        // 注册到MessageController
        registerToMessageController();
        
        // 加载所有邮件
        loadAllEmails();
    }
    
    /**
     * 注册到MessageController
     */
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setEmailAdminViewController(this);
        }
    }
    
    /**
     * 设置分页控件
     */
    private void setupPaginationControls() {
        // 设置每页显示数量选项
        pageSizeCombo.getItems().addAll(10, 20, 50, 100);
        pageSizeCombo.setValue(pageSize);
        pageSizeCombo.setOnAction(e -> {
            pageSize = pageSizeCombo.getValue();
            currentPage = 1;
            loadCurrentPage();
        });
        
        // 更新分页按钮状态
        updatePaginationButtons();
    }
    
    /**
     * 设置表格列
     */
    private void setupTableColumns() {
        emailIdColumn.setCellValueFactory(new PropertyValueFactory<>("emailId"));
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("senderId"));
        recipientColumn.setCellValueFactory(new PropertyValueFactory<>("recipientId"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("sendTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // 设置状态列的自定义显示
        statusColumn.setCellFactory(column -> new TableCell<Email, EmailStatus>() {
            @Override
            protected void updateItem(EmailStatus status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status.toString());
                    switch (status) {
                        case SENT:
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case READ:
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case DRAFT:
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #757575;");
                    }
                }
            }
        });
        
        // 设置操作列
        actionsColumn.setCellFactory(column -> new TableCell<Email, Void>() {
            private final Button viewButton = new Button("查看");
            private final Button deleteButton = new Button("删除");
            
            {
                viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 10px;");
                
                viewButton.setOnAction(event -> {
                    Email email = getTableView().getItems().get(getIndex());
                    showEmailDetails(email);
                });
                
                deleteButton.setOnAction(event -> {
                    Email email = getTableView().getItems().get(getIndex());
                    deleteEmail(email);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5);
                    hbox.getChildren().addAll(viewButton, deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }
    
    /**
     * 加载所有邮件
     */
    private void loadAllEmails() {
        currentKeyword = "";
        currentPage = 1;
        loadCurrentPage();
    }
    
    /**
     * 加载当前页
     */
    private void loadCurrentPage() {
        Platform.runLater(() -> statusLabel.setText("正在加载邮件..."));
        
        if (!currentUserSearch.isEmpty()) {
            // 按用户ID搜索
            emailAdminService.searchEmailsByUser(currentUserSearch, (currentPage - 1) * pageSize, pageSize);
        } else if (!currentKeyword.isEmpty()) {
            // 按主题搜索
            emailAdminService.searchEmails(currentKeyword, (currentPage - 1) * pageSize, pageSize);
        } else {
            // 获取所有邮件
            emailAdminService.getAllEmails((currentPage - 1) * pageSize, pageSize);
        }
    }
    
    /**
     * 处理搜索按钮点击
     */
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        currentKeyword = keyword;
        currentPage = 1;
        loadCurrentPage();
    }
    
    /**
     * 处理刷新按钮点击
     */
    @FXML
    private void handleRefresh() {
        searchField.clear();
        userSearchField.clear();
        currentKeyword = "";
        currentUserSearch = "";
        loadAllEmails();
    }
    
    /**
     * 处理用户ID搜索按钮点击
     */
    @FXML
    private void handleUserSearch() {
        String userId = userSearchField.getText().trim();
        currentUserSearch = userId;
        currentKeyword = ""; // 清除主题搜索
        searchField.clear();
        currentPage = 1;
        loadCurrentPage();
    }
    
    /**
     * 处理清除用户搜索按钮点击
     */
    @FXML
    private void handleClearUserSearch() {
        userSearchField.clear();
        currentUserSearch = "";
        loadAllEmails();
    }
    
    /**
     * 处理首页按钮点击
     */
    @FXML
    private void handleFirstPage() {
        currentPage = 1;
        loadCurrentPage();
    }
    
    /**
     * 处理上一页按钮点击
     */
    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            loadCurrentPage();
        }
    }
    
    /**
     * 处理下一页按钮点击
     */
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            loadCurrentPage();
        }
    }
    
    /**
     * 处理末页按钮点击
     */
    @FXML
    private void handleLastPage() {
        currentPage = totalPages;
        loadCurrentPage();
    }
    
    /**
     * 更新分页按钮状态
     */
    private void updatePaginationButtons() {
        Platform.runLater(() -> {
            firstPageButton.setDisable(currentPage <= 1);
            prevPageButton.setDisable(currentPage <= 1);
            nextPageButton.setDisable(currentPage >= totalPages);
            lastPageButton.setDisable(currentPage >= totalPages);
            pageInfoLabel.setText(String.format("第 %d 页，共 %d 页", currentPage, totalPages));
        });
    }
    
    /**
     * 处理获取所有邮件的响应
     */
    public void handleGetAllEmailsResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseData = (Map<String, Object>) message.getData();
                
                @SuppressWarnings("unchecked")
                List<Email> emails = (List<Email>) responseData.get("emails");
                int totalCount = (Integer) responseData.get("totalCount");
                int serverTotalPages = (Integer) responseData.get("totalPages");
                
                emailsList.clear();
                emailsList.addAll(emails);
                emailsTable.setItems(emailsList);
                
                // 使用服务器返回的总页数
                totalPages = serverTotalPages;
                updatePaginationButtons();
                
                totalCountLabel.setText("总邮件数: " + totalCount);
                statusLabel.setText("加载完成，共 " + totalCount + " 封邮件，当前第 " + currentPage + " 页");
            } else {
                statusLabel.setText("加载失败: " + message.getMessage());
                showAlert("错误", "加载邮件失败", message.getMessage());
            }
        });
    }
    
    /**
     * 处理搜索邮件的响应
     */
    public void handleSearchEmailsResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseData = (Map<String, Object>) message.getData();
                
                @SuppressWarnings("unchecked")
                List<Email> emails = (List<Email>) responseData.get("emails");
                int totalCount = (Integer) responseData.get("totalCount");
                int serverTotalPages = (Integer) responseData.get("totalPages");
                String keyword = (String) responseData.get("keyword");
                
                emailsList.clear();
                emailsList.addAll(emails);
                emailsTable.setItems(emailsList);
                
                // 使用服务器返回的总页数
                totalPages = serverTotalPages;
                updatePaginationButtons();
                
                statusLabel.setText("搜索完成，关键词 '" + keyword + "' 找到 " + totalCount + " 封邮件，当前第 " + currentPage + " 页");
            } else {
                statusLabel.setText("搜索失败: " + message.getMessage());
                showAlert("错误", "搜索邮件失败", message.getMessage());
            }
        });
    }
    
    /**
     * 处理用户搜索邮件的响应
     */
    public void handleSearchEmailsByUserResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseData = (Map<String, Object>) message.getData();
                
                @SuppressWarnings("unchecked")
                List<Email> emails = (List<Email>) responseData.get("emails");
                int totalCount = (Integer) responseData.get("totalCount");
                int serverTotalPages = (Integer) responseData.get("totalPages");
                String userId = (String) responseData.get("userId");
                
                emailsList.clear();
                emailsList.addAll(emails);
                emailsTable.setItems(emailsList);
                
                // 使用服务器返回的总页数
                totalPages = serverTotalPages;
                updatePaginationButtons();
                
                statusLabel.setText("用户搜索完成，用户 '" + userId + "' 找到 " + totalCount + " 封邮件，当前第 " + currentPage + " 页");
            } else {
                statusLabel.setText("用户搜索失败: " + message.getMessage());
                showAlert("错误", "搜索用户邮件失败", message.getMessage());
            }
        });
    }
    
    /**
     * 显示警告对话框
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * 显示邮件详情
     */
    private void showEmailDetails(Email email) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("邮件详情");
        alert.setHeaderText("邮件ID: " + email.getEmailId());
        
        StringBuilder content = new StringBuilder();
        content.append("发件人: ").append(email.getSenderId()).append("\n");
        content.append("收件人: ").append(email.getRecipientId()).append("\n");
        content.append("主题: ").append(email.getSubject()).append("\n");
        content.append("发送时间: ").append(email.getSendTime()).append("\n");
        content.append("状态: ").append(email.getStatus()).append("\n");
        content.append("是否有附件: ").append(email.isHasAttachment() ? "是" : "否").append("\n");
        content.append("\n邮件内容:\n");
        content.append(email.getContent());
        
        alert.setContentText(content.toString());
        alert.getDialogPane().setPrefSize(600, 400);
        alert.showAndWait();
    }
    
    /**
     * 删除邮件
     */
    private void deleteEmail(Email email) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认删除");
        confirmAlert.setHeaderText("确认删除邮件");
        confirmAlert.setContentText("确定要删除邮件 \"" + email.getSubject() + "\" 吗？\n此操作不可撤销！");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                emailAdminService.adminDeleteEmail(email.getEmailId());
                statusLabel.setText("正在删除邮件...");
            }
        });
    }
    
    /**
     * 处理删除邮件的响应
     */
    public void handleDeleteEmailResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                statusLabel.setText("邮件删除成功");
                // 刷新当前页
                loadCurrentPage();
            } else {
                statusLabel.setText("删除失败: " + message.getMessage());
                showAlert("错误", "删除邮件失败", message.getMessage());
            }
        });
    }
}
