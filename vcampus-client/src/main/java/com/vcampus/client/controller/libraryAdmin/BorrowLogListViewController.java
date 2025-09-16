package com.vcampus.client.controller.libraryAdmin;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.LibraryService;
import com.vcampus.common.dto.BorrowLog;
import com.vcampus.common.dto.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * 借阅记录列表子视图控制器
 * 【新增】添加了修改期限和强制归还的操作功能
 */
public class BorrowLogListViewController implements IClientController {

    private final LibraryService libraryService = new LibraryService();
    private ObservableList<BorrowLog> borrowLogData = FXCollections.observableArrayList();

    // FXML 控件
    @FXML private TextField searchField;
    @FXML private TableView<BorrowLog> borrowLogTable;
    @FXML private TableColumn<BorrowLog, String> logIdColumn;
    @FXML private TableColumn<BorrowLog, String> bookNameColumn;
    @FXML private TableColumn<BorrowLog, String> userIdColumn;
    @FXML private TableColumn<BorrowLog, String> usernameColumn;
    @FXML private TableColumn<BorrowLog, String> borrowDateColumn;
    @FXML private TableColumn<BorrowLog, String> dueDateColumn;
    @FXML private TableColumn<BorrowLog, Void> actionsColumn; // 【新增】操作列的引用
    @FXML private Label totalLogsLabel;

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController =
                MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setBorrowLogListViewController(this);
        }
    }

    @FXML
    public void initialize() {
        registerToMessageController();
        configureColumnFactories();
        initializeActionsColumn(); // 【新增】初始化操作列
        borrowLogTable.setItems(borrowLogData);
        loadInitialData();
    }

    private void configureColumnFactories() {
        logIdColumn.setCellValueFactory(new PropertyValueFactory<>("logId"));
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
    }

    /**
     * 【新增】初始化操作列，为每一行添加按钮
     */
    private void initializeActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️ 修改期限");
            private final Button returnButton = new Button("✅ 强制归还");
            private final HBox pane = new HBox(5, editButton, returnButton);

            {
                editButton.setOnAction(event -> handleEditDueDate(getTableView().getItems().get(getIndex())));
                returnButton.setOnAction(event -> handleForceReturn(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadInitialData() {
        libraryService.getAdminBorrowHistory();
    }

    @FXML
    private void handleRefresh(ActionEvent actionEvent) {
        searchField.clear();
        loadInitialData();
    }

    @FXML
    private void handleSearch(ActionEvent actionEvent) {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadInitialData();
        } else {
            libraryService.searchBorrowHistory(keyword);
        }
    }

    /**
     * 【新增】处理“修改期限”按钮的点击事件
     */
    private void handleEditDueDate(BorrowLog log) {
        TextInputDialog dialog = new TextInputDialog(log.getDueDate());
        dialog.setTitle("修改应还日期");
        dialog.setHeaderText("正在为《" + log.getBookName() + "》修改应还日期");
        dialog.setContentText("请输入新的应还日期 (格式: YYYY-MM-DD):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newDate -> {
            try {
                // 验证日期格式是否正确
                LocalDate.parse(newDate);
                log.setDueDate(newDate);
                // 发送更新请求
                libraryService.updateBorrowLog(log);
            } catch (DateTimeParseException e) {
                showAlert("格式错误", "日期格式不正确，请输入 YYYY-MM-DD 格式。");
            }
        });
    }

    /**
     * 【新增】处理“强制归还”按钮的点击事件
     */
    private void handleForceReturn(BorrowLog log) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("确认强制归还");
        confirmation.setHeaderText("您确定要将《" + log.getBookName() + "》强制归还吗？");
        confirmation.setContentText("此操作将删除借阅记录，并使该书状态变为“在馆”。");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 复用已有的还书服务
            libraryService.returnBook(log.getLogId(), log.getBookId());
        }
    }

    private void updateStatistics(List<BorrowLog> logs) {
        totalLogsLabel.setText("总记录数: " + logs.size());
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

    public void handleBorrowLogListResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                List<BorrowLog> logs = (List<BorrowLog>) message.getData();
                borrowLogData.setAll(logs);
                updateStatistics(logs);
            } else {
                showAlert("加载失败", "获取借阅记录失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增】处理更新和删除操作的响应
     */
    public void handleBorrowLogUpdateResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("操作成功", message.getMessage());
                handleRefresh(null); // 操作成功后刷新列表
            } else {
                showAlert("操作失败", message.getMessage());
            }
        });
    }
}