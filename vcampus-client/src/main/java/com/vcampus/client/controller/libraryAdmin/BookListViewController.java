package com.vcampus.client.controller.libraryAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.LibraryService;
import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;

public class BookListViewController implements IClientController {

    private final LibraryService libraryService = new LibraryService();
    private ObservableList<Book> bookData = FXCollections.observableArrayList();

    @FXML private TextField searchField;
    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> publisherColumn;
    @FXML private TableColumn<Book, String> descriptionColumn;
    @FXML private TableColumn<Book, String> statusColumn;
    @FXML private TableColumn<Book, Void> actionsColumn;
    @FXML private Label totalBooksLabel;
    @FXML private Label availableBooksLabel;
    @FXML private Label borrowedBooksLabel;

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController =
                MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setBookListViewController(this);
        }
    }

    @FXML
    public void initialize() {
        registerToMessageController();
        configureColumnFactories();
        initializeActionsColumn();
        bookTable.setItems(bookData);
        loadInitialData();
    }

    private void configureColumnFactories() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("borrowStatus"));
    }

    private void initializeActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️ 修改");
            private final Button deleteButton = new Button("🗑️ 删除");
            private final HBox pane = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> handleEditBook(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> handleDeleteBook(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });
    }

    private void loadInitialData() {
        libraryService.getAllBooks(); // 发送请求，由响应处理器更新UI
    }

    @FXML
    public void handleRefresh(ActionEvent actionEvent) {
        searchField.clear();
        loadInitialData();
    }

    @FXML
    public void handleSearch(ActionEvent actionEvent) {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadInitialData();
        } else {
            libraryService.searchBooks(keyword); // 发送请求
        }
    }

    private void handleEditBook(Book book) {
        String currentInfo = String.join(",",
                book.getBookName(), book.getAuthor(), book.getISBN(),
                book.getPublisher(), book.getDescription());

        TextInputDialog dialog = new TextInputDialog(currentInfo);
        dialog.setTitle("修改书籍信息");
        dialog.setHeaderText("您正在修改《" + book.getBookName() + "》");
        dialog.setContentText("格式: 书名,作者,ISBN,出版社,描述");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(bookInfo -> {
            String[] parts = bookInfo.split(",");
            if (parts.length == 5) {
                book.setBookName(parts[0].trim());
                book.setAuthor(parts[1].trim());
                book.setISBN(parts[2].trim());
                book.setPublisher(parts[3].trim());
                book.setDescription(parts[4].trim());
                libraryService.modifyBook(book); // 发送修改请求
            } else {
                showAlert("错误", "输入格式不正确！");
            }
        });
    }

    private void handleDeleteBook(Book book) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("确认删除");
        confirmation.setHeaderText("您确定要删除《" + book.getBookName() + "》吗？");
        confirmation.setContentText("此操作不可撤销。");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            libraryService.deleteBook(book.getBookId()); // 发送删除请求
        }
    }

    private void updateStatistics(List<Book> books) {
        long total = books.size();
        long available = books.stream().filter(book -> "在馆".equals(book.getBorrowStatus().trim())).count();
        long borrowed = total - available;

        totalBooksLabel.setText("总书籍: " + total);
        availableBooksLabel.setText("在馆: " + available);
        borrowedBooksLabel.setText("已借出: " + borrowed);
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // ==========================================================
    // 服务器响应处理方法
    // ==========================================================

    public void handleBookListResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                List<Book> books = (List<Book>) message.getData();
                bookData.setAll(books);
                updateStatistics(books);
            } else {
                showAlert("加载失败", "获取图书列表失败: " + message.getMessage());
            }
        });
    }



    public void handleBookUpdateResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("操作成功", message.getMessage());
                handleRefresh(null); // 刷新列表
            } else {
                showAlert("操作失败", message.getMessage());
            }
        });
    }
}