package com.vcampus.client.controller.libraryAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.LibraryService;
import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * 创建图书子视图控制器 (仿照 UserCreateViewController 编写)
 */
public class BookCreateViewController implements IClientController {

    // FXML 控件
    @FXML private TextField bookIdField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private TextField publisherField;
    @FXML private TextField descriptionField;
    @FXML private Button createButton;
    @FXML private Button resetButton;

    // 服务
    private final LibraryService libraryService = new LibraryService();

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController =
                MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setBookCreateViewController(this);
        }
    }

    @FXML
    public void initialize() {
        registerToMessageController();
    }

    /**
     * 处理“创建图书”按钮点击事件
     */
    @FXML
    private void handleCreate(ActionEvent event) {
        String bookId = bookIdField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String isbn = isbnField.getText();
        String publisher = publisherField.getText();
        String description = descriptionField.getText();

        // 简单的输入验证
        if (bookId.isEmpty() || title.isEmpty() || author.isEmpty() || isbn.isEmpty()) {
            showAlert("输入错误", "书籍ID、书名、作者和ISBN为必填项！");
            return;
        }

        Book newBook = new Book(bookId, title, author, isbn, publisher, description, "在馆");

        // 发送异步请求到服务器
        libraryService.addBook(newBook);
    }

    /**
     * 处理“重置表单”按钮点击事件
     */
    @FXML
    private void handleReset(ActionEvent event) {
        bookIdField.clear();
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        publisherField.clear();
        descriptionField.clear();
    }

    /**
     * 处理来自服务器的创建图书响应
     * @param message 服务器返回的消息
     */
    public void handleCreateBookResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("操作成功", "新图书《" + titleField.getText() + "》已成功创建！");
                handleReset(null); // 创建成功后清空表单
            } else {
                showAlert("操作失败", "创建图书失败: " + message.getMessage());
            }
        });
    }

    /**
     * 显示提示/错误信息的对话框
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}