package com.vcampus.client.controller;


import com.vcampus.client.service.LibraryService;
import com.vcampus.client.session.UserSession;
import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.Message;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.vcampus.common.enums.ActionType;


import java.time.LocalDate;

public class LibraryController implements IClientController {

    /**
     * FXML 中定义的根内容面板，用于放置所有UI控件。
     */

    @FXML private Label bookIdLabel;
    @FXML private Label bookNameLabel;
    @FXML private Label authorLabel;
    @FXML private Label countLabel;
    @FXML private Label ISBNLabel;
    @FXML private Label publisherLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label borrowStatusLabel;
    // =================================================================

    @FXML private Button editButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @FXML private GridPane bookGridPane; // 必须绑定 FXML 中的 GridPane

    private final LibraryService libraryService = new LibraryService();


    private TextField countField;
    private TextField descriptionField;
    private TextField borrowStatusField;



    private int originalcount;
    private String originaldescription;
    private String originalborrowStatus;


    /**
     * 初始化方法，在视图加载后自动执行。
     * 这是模块的逻辑入口点，适合执行数据加载等初始化任务。
     */
    @FXML
    public void initialize() {
        System.out.println("图书馆模块已加载。");
        registerToMessageController();
        loadCurrentBookInfo();

        editButton.setOnAction(event -> enterEditMode());


        // 示例：调用服务层获取数据并更新UI
        // setupBookTable();
        // loadAllBooks();
    }

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController =
                libraryService.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setLibraryController(this);
        }
    }

    // =================================================================
    //
    // 事件处理方法区
    //
    // 在这里实现 FXML 文件中 onAction 等事件所绑定的方法。
    //
    // 示例:
    // @FXML
    // private void handleSearchBook(ActionEvent event) {
    //     // 搜索图书的逻辑...
    // }
    public void handleBookInfoResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() != null) {
                Book book = (Book) message.getData();
                bookIdLabel.setText(book.getBookId());
                bookNameLabel.setText(book.getBookName());
                authorLabel.setText(book.getAuthor());
                countLabel.setText(String.valueOf(book.getCount()));
                ISBNLabel.setText(book.getISBN());
                publisherLabel.setText(book.getPublisher());
                descriptionLabel.setText(book.getDescription());
                borrowStatusLabel.setText(book.getBorrowStatus());

            } else {
                showError("加载图书信息失败：" + message.getMessage());
            }
        });



        //
        // =================================================================


        // =================================================================
        //
        // 私有辅助方法区
        //
        // 在这里实现模块内部的业务逻辑，例如与服务层交互、更新UI等。
        //
        // 示例:
        // private void loadAllBooks() {
        //     // 从服务器加载图书数据并填充表格的逻辑...
        // }
        //
        // =================================================================

    }
    private void loadCurrentBookInfo() {
        String currentUserId = UserSession.getInstance().getCurrentUserId();
        if (currentUserId != null && !currentUserId.isEmpty()) {
            libraryService.getBookById(currentUserId);
        } else {
            showError("当前没有登录用户，请先登录！");
        }
    }
    public void handleUpdateBookResponse (Message message)
    {
        Platform.runLater(() -> {
            if (message == null) {
                showError("收到空消息");
                return;
            }

            if (message.getAction() != ActionType.UPDATE_BOOK) {
                System.out.println("非书籍更新消息，忽略: " + message.getAction());
                return;
            }

            if (message.isSuccess() && message.getData() instanceof Book) {
                Book updatedBook = (Book) message.getData();


                // 更新界面 Label 显示
                countLabel.setText(String.valueOf(updatedBook.getCount()));
                descriptionLabel.setText(updatedBook.getDescription());
                borrowStatusLabel.setText(updatedBook.getBorrowStatus());

                // 退出编辑模式
                exitEditMode();

                showInfo("图书信息更新成功");
            } else {
                showError("图书信息更新失败：" + message.getMessage());
            }
        });
    }
    private void exitEditMode() {
        editButton.setVisible(true);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        // 移除编辑控件，恢复 Label
        bookGridPane.getChildren().removeAll(countLabel,descriptionLabel, borrowStatusLabel);
        bookGridPane.add(countLabel, 8, 1);
        bookGridPane.add(descriptionLabel, 9, 1);
        bookGridPane.add(borrowStatusLabel, 10, 1);
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("图书信息加载失败");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void enterEditMode() {
        editButton.setVisible(false);
        saveButton.setVisible(true);
        cancelButton.setVisible(true);

        // 保存原值，取消时恢复
        originalcount =Integer.parseInt( countLabel.getText());
        originaldescription = descriptionLabel.getText();
        originalborrowStatus = borrowStatusLabel.getText();

        // 创建可编辑控件



        countField = new TextField(String.valueOf(originalcount));
        descriptionField= new TextField(originaldescription);
        borrowStatusField = new TextField(originalborrowStatus);

        bookGridPane.getChildren().removeAll(countLabel,descriptionLabel,borrowStatusLabel);
        bookGridPane.add(countField, 3, 1); // column 8, row 1
        bookGridPane.add(descriptionField, 5, 1); // column 9, row 1
        bookGridPane.add(borrowStatusField, 6, 1); // column 10, row 1
        // 替换 GridPane 上的 Label
    }

    private void showInfo(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(info);
        alert.showAndWait();
    }

    public void handleRefresh(ActionEvent actionEvent) {
    }

    public void handleSearchBook(ActionEvent actionEvent) {
    }

    public void handleAddNewBook(ActionEvent actionEvent) {
    }
}