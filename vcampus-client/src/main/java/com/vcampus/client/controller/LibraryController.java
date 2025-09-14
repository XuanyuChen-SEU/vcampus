package com.vcampus.client.controller;


import com.vcampus.client.MainApp;
import com.vcampus.client.service.LibraryService;
import com.vcampus.client.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

// 引入您的数据模型类
// client/controller/LibraryController.java

import java.awt.Desktop; // 【新增】用于打开文件
import java.io.File;      // 【新增】用于操作文件
import java.io.IOException; // 【新增】用于处理异常
// ... 其他已有 import
import com.vcampus.common.enums.LibAdminView;
import com.vcampus.common.enums.LibUserView;
import com.vcampus.common.dto.*;
//import com.vcampus.common.dto.BorrowLog;
//import com.vcampus.common.dto.Book;
//import com.vcampus.common.dto.User;
//import com.vcampus.common.dto.UserBorrowStatus;
//import com.vcampus.common.dto.Message;

import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;


public class LibraryController implements IClientController {

    // ================== UI 控件声明区 ==================
    @FXML private Label libraryLabel;
    @FXML private Button viewToggleButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private HBox userButtonsHBox;
    @FXML private Button myBorrowsButton;
    @FXML private Button renewAllButton;
    @FXML private HBox adminButtonsHBox;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button modifyButton;
    @FXML private TableView<Object> bookTable;
    @FXML private Label userInfoLabel;
    @FXML private Button borrowButton;


    // --- 表格列定义 ---
    @FXML private TableColumn<Object, String> colBookId;
    @FXML private TableColumn<Object, String> colBookName;
    @FXML private TableColumn<Object, String> colAuthor;
    @FXML private TableColumn<Object, String> colISBN;
    @FXML private TableColumn<Object, String> colPublisher;
    @FXML private TableColumn<Object, String> colDescription;
    @FXML private TableColumn<Object, String> colBorrowStatus;
    @FXML private TableColumn<Object, String> colDueDate;
    @FXML private TableColumn<Object, String> colUserId;
    // 【新增】下面这两行
    @FXML private TableColumn<Object, String> colBorrowerName;
    @FXML private TableColumn<Object, String> colBorrowDate;
    @FXML private TableColumn<Object, String> colUserName;
    @FXML private TableColumn<Object, String> colUserIdentity;
    @FXML private TableColumn<Object, String> colBorrowedBook1;
    @FXML private TableColumn<Object, String> colBorrowedBook2;
    @FXML private TableColumn<Object, String> colBorrowedBook3;

    // ================== 状态管理变量 ==================
    private boolean isAdmin ;
//    private Integer currentViewIndex = 1;
//    //1. 所有图书（默认）
//    //2. 我的借阅
//    //3. 借阅历史

    private LibAdminView currentLibAdminView;
    private LibUserView currentLibUserView;

    private final LibraryService libraryService = new LibraryService();
    private User currentUser;


    // ================== 初始化与核心逻辑 ==================
    @FXML
    public void initialize() {
        System.out.println("图书馆控制器初始化完成！");

        // [重要] 在此切换 true/false 来测试不同角色
        this.isAdmin = false;

        ;//管理员角色
        this.currentLibAdminView = LibAdminView.ALL_BOOKS;
        this.currentLibUserView = LibUserView.ALL_BOOKS;

        setcurrentUser(); // 先设置为空，等到登录成功后再设置用户信息
        setupUIForRole();
        addTableSelectionListener();
        loadInitialAllData();
        setupOverdueRowFactory();
        configureColumnFactories();
        registerToMessageController();
        setupRowDoubleClickListener();
    }

    private void setupOverdueRowFactory() {
        bookTable.setRowFactory(tv -> new TableRow<Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                // 首先移除样式，防止行复用时样式错乱
                getStyleClass().remove("overdue-row");

                if (empty || item == null) {
                    // 如果行是空的，则不进行任何操作
                    return;
                }

                // 只在“我的借阅”视图下，并且行数据是BorrowLog类型时才进行处理
                if (currentLibUserView == LibUserView.MY_BORROWS && item instanceof BorrowLog) {
                    BorrowLog log = (BorrowLog) item;
                    try {
                        // 将字符串格式的应还日期解析为LocalDate对象
                        LocalDate dueDate = LocalDate.parse(log.getDueDate());
                        // 获取当前真实时间
                        LocalDate now = LocalDate.now();

                        // 如果应还日期在当前日期之前，则添加高亮样式
                        if (dueDate.isBefore(now)) {
                            getStyleClass().add("overdue-row");
                        }
                    } catch (DateTimeParseException e) {
                        // 如果日期格式解析失败，在控制台打印错误，避免程序崩溃
                        System.err.println("无法解析日期: " + log.getDueDate());
                    }
                }
            }
        });

    }

    /**
     * 【新增】为表格添加鼠标双击事件监听。
     */
    private void setupRowDoubleClickListener() {
        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // 检查是否为双击
                if (currentLibUserView == LibUserView.MY_BORROWS) { // 检查是否在“我的借阅”视图
                    Object selectedItem = bookTable.getSelectionModel().getSelectedItem();
                    if (selectedItem instanceof BorrowLog) {
                        // 调用发起请求的方法
                        openBookPdf((BorrowLog) selectedItem);
                    }
                }
            }
        });
    }
    /**
     * 【修改后】处理双击事件，现在仅负责发起获取PDF的异步请求。
     * @param log 选中的借阅记录
     */
    private void openBookPdf(BorrowLog log) {
        String bookId = log.getBookId();
        // 调用Service发请求，这个方法会立即返回，界面不会卡顿
        libraryService.getBookPdf(bookId);
    }
    @Override
    public void registerToMessageController() {
        // 通过自身的 Service 获取全局 MessageController
        com.vcampus.client.controller.MessageController messageController =
                libraryService.getGlobalSocketClient().getMessageController();

        if (messageController != null) {
            messageController.setLibraryController(this);
            System.out.println("LibraryController 已成功注册到 MessageController。");
        } else {
            System.err.println("严重错误：LibraryController 注册失败，无法获取 MessageController 实例！");
        }
    }
    /**
     * 【重构后的方法，取代了之前的 load... 方法】
     * 这个方法由外部调用（比如 MainViewController 或 initialize 自身），
     * 用于设置当前用户，并触发初始数据的加载。
     */
    public void setcurrentUser() {
        UserSession userSession = MainApp.getGlobalUserSession();
        User user = new User();
        user.setUserId(userSession.getCurrentUserId());
        this.currentUser = user;
        updateUserInfo();
        loadInitialAllData();
    }
    private void updateUserInfo() {
        Platform.runLater(() -> {
            if (currentUser != null) {
                userInfoLabel.setText("用户ID: " + currentUser.getUserId());
            }
        });
    }




    /**
     * 根据角色设置UI布局和按钮的初始状态
     */
    private void setupUIForRole() {
        libraryLabel.setVisible(!isAdmin);
        libraryLabel.setManaged(!isAdmin);
        viewToggleButton.setVisible(isAdmin);
        viewToggleButton.setManaged(isAdmin);

        userButtonsHBox.setVisible(!isAdmin);
        userButtonsHBox.setManaged(!isAdmin);
        adminButtonsHBox.setVisible(isAdmin);
        adminButtonsHBox.setManaged(isAdmin);

        if (isAdmin) {
            // 管理员模式下，所有功能按钮默认禁用
            setAdminActionButtonsDisabled(true);
        }
        userInfoLabel.setText(isAdmin ? "用户: 管理员" : "用户: 学生/教师");
    }

    /**
     * 为TableView添加选择监听器，用于控制管理员按钮的可用状态
     */
    private void addTableSelectionListener() {
        bookTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (isAdmin && currentLibAdminView == LibAdminView.ALL_BOOKS) {
                        boolean isItemSelected = (newSelection != null);
                        setAdminActionButtonsDisabled(!isItemSelected);
                    }
                }
        );
    }

    /**
     * 统一配置所有TableColumn的CellValueFactory
     */
    private void configureColumnFactories() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colISBN.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        colPublisher.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colBorrowStatus.setCellValueFactory(new PropertyValueFactory<>("borrowStatus"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colBorrowerName.setCellValueFactory(new PropertyValueFactory<>("username"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUserIdentity.setCellValueFactory(new PropertyValueFactory<>("identity"));
        colBorrowedBook1.setCellValueFactory(new PropertyValueFactory<>("borrowedBook1"));
        colBorrowedBook2.setCellValueFactory(new PropertyValueFactory<>("borrowedBook2"));
        colBorrowedBook3.setCellValueFactory(new PropertyValueFactory<>("borrowedBook3"));
    }

    /**
     * 加载初始数据视图
     */
    private void loadInitialAllData() {
        showAllBooksView();
        //refreshBookTable();
    }







    private void updateBookDisplay(List<Book> books) {
        Platform.runLater(() -> bookTable.getItems().setAll(books));
    }

    private void updateBorrowLogDisplay(List<BorrowLog> logs) {
        Platform.runLater(() -> bookTable.getItems().setAll(logs));
    }

    private void updateUserStatusDisplay(List<UserBorrowStatus> statuses) {
        Platform.runLater(() -> bookTable.getItems().setAll(statuses));
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("操作失败");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
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


    @FXML
    private void handleBorrowBook(ActionEvent event) {
        // 从表格中获取当前选中的项
        Object selectedItem = bookTable.getSelectionModel().getSelectedItem();

        // 1. 检查是否选中了一本书
        if (selectedItem == null || !(selectedItem instanceof Book)) {
            showError("请先从列表中选择一本要借阅的图书。");
            return;
        }

        // 2. 检查当前用户是否存在
        if (currentUser == null) {
            showError("无法识别当前用户信息，请重新登录。");
            return;
        }

        Book selectedBook = (Book) selectedItem;



        String status = selectedBook.getBorrowStatus().trim(); // 使用trim()去除前后空白
        if (!"在馆".equals(status)) {
            showError("操作失败：这本书当前状态为[" + status + "]，无法借阅。");
            return;
        }
        // ================== 【核心修改点】结束 ==================


        // 4. (原第3步) 弹出对话框，向用户确认操作
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("确认借阅");
        confirmation.setHeaderText("您确定要借阅《" + selectedBook.getBookName() + "》吗？");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Message response = libraryService.borrowBook(currentUser.getUserId(), selectedBook.getBookId());
            System.out.println("借阅请求响应: " + response); // 新增此行查看完整响应
            if (response.isSuccess()) {
                showAlert("成功", "图书借阅成功！");
                showAllBooksView();
                // 【核心修改】
                // 调用我们专门的刷新方法，而不是旧的 loadInitialAllData()

            } else {
                showError("借阅失败：" + response.getMessage());
            }
        }
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
    }




// ================== 异步响应处理入口 (借鉴ShopController) ==================
    // 当 MessageController 收到服务器推送的消息后，会调用这些方法

    public void handleGetAllBooksResponse(Message message) {
        if (message.isSuccess()) {
            updateBookDisplay((List<Book>) message.getData());
        } else {
            showError("异步加载书籍列表失败: " + message.getMessage());
        }
    }

    public void handleGetMyBorrowsResponse(Message message) {
        if (message.isSuccess()) {
            updateBorrowLogDisplay((List<BorrowLog>) message.getData());
        } else {
            showError("异步加载我的借阅失败: " + message.getMessage());
        }
    }







    // ================== 事件处理方法 ==================

    @FXML private void handleViewToggle(ActionEvent event) {
        if (!isAdmin) return;
        switch (currentLibAdminView) {
            case ALL_BOOKS: showAdminBorrowHistoryView(); break;
            case BORROW_HISTORY: showAllUsersStatusView(); break;
            case ALL_USERS_STATUS: showAllBooksView(); break;
        }
    }

    @FXML private void handleShowMyBorrows(ActionEvent event) {
        if (isAdmin) return;
        if (currentLibUserView == LibUserView.ALL_BOOKS) {
            showUserMyBorrowsView();
        } else {
            showAllBooksView();

        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText();

        // 1. 判断搜索框是否为空
        if (keyword == null || keyword.trim().isEmpty()) {
            // --- 为空：重新加载当前视图的全部数据 ---
            System.out.println("搜索框为空，重新加载当前视图...");
            if (isAdmin) {
                switch (currentLibAdminView) {
                    case ALL_BOOKS:
                        showAllBooksView();
                        break;
                    case BORROW_HISTORY:
                        showAdminBorrowHistoryView();
                        break;
                    case ALL_USERS_STATUS:
                        showAllUsersStatusView();
                        break;
                }
            } else { // 学生或教师
                switch (currentLibUserView) {
                    case ALL_BOOKS:
                        showAllBooksView();
                        break;
                    case MY_BORROWS:
                        showUserMyBorrowsView();
                        break;
                }
            }
        } else {
            // --- 不为空：根据当前视图执行 específicos 搜索 ---
            System.out.println("正在搜索: " + keyword);
            if (isAdmin) {
                switch (currentLibAdminView) {
                    case ALL_BOOKS:
                        performBookSearch(keyword);
                        break;
                    case BORROW_HISTORY:
                        // 【新】调用搜索借阅历史的服务
                        Message historyResponse = libraryService.searchBorrowHistory(keyword);
                        if (historyResponse.isSuccess()) updateBorrowLogDisplay((List<BorrowLog>) historyResponse.getData());
                        else showError("搜索借阅历史失败: " + historyResponse.getMessage());
                        break;
                    case ALL_USERS_STATUS:
                        // 【新】调用搜索用户的服务
                        Message userResponse = libraryService.searchUserStatus(keyword);
                        if (userResponse.isSuccess()) updateUserStatusDisplay((List<UserBorrowStatus>) userResponse.getData());
                        else showError("搜索用户失败: " + userResponse.getMessage());
                        break;
                }
            } else { // 学生或教师
                switch (currentLibUserView) {
                    case ALL_BOOKS:
                        performBookSearch(keyword);
                        break;
                    case MY_BORROWS:
                        if (currentUser == null) {
                            showError("无法获取当前用户信息！");
                            return;
                        }
                        // 【新】调用在“我的借阅”中搜索的服务
                        Message myBorrowsResponse = libraryService.searchMyBorrows(currentUser.getUserId(), keyword);
                        if (myBorrowsResponse.isSuccess()) updateBorrowLogDisplay((List<BorrowLog>) myBorrowsResponse.getData());
                        else showError("在我的借阅中搜索失败: " + myBorrowsResponse.getMessage());
                        break;
                }
            }
        }
    }



    private void performBookSearch(String keyword) {
        Message response = libraryService.searchBooks(keyword);
        if (response.isSuccess()) {
            updateBookDisplay((List<Book>) response.getData());
        } else {
            showError("图书搜索失败: " + response.getMessage());
        }
    }

    @FXML private void handleAddBook(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("书籍Id,书籍名称,作者,ISBN,出版社,描述");
        dialog.setTitle("添加新书");
        dialog.setHeaderText("请输入新书信息，用英文逗号分隔");
        dialog.setContentText("格式:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(bookInfo -> {
            String[] parts = bookInfo.split(",");
            if (parts.length == 6) {
                Book newBook = new Book();
                newBook.setBookId(parts[0]);
                newBook.setBookName(parts[1]);
                newBook.setAuthor(parts[2]);
                newBook.setISBN(parts[3]);
                newBook.setPublisher(parts[4]);
                newBook.setDescription(parts[5]);
                newBook.setBorrowStatus("在馆");
                // 其他属性可由服务器设置默认值

                Message response = libraryService.addBook(newBook);
                if (response.isSuccess()) {
                    showAlert("成功", "书籍添加成功！");
                    showAllBooksView();
                } else {
                    showError("添加失败: " + response.getMessage());
                    showAllBooksView();
                }
            } else {
                showError("输入格式不正确！");
                showAllBooksView();
            }
            showAllBooksView();
        });
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
    }
    @FXML private void handleDeleteBook(ActionEvent event) {

        Object selectedItem = bookTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem instanceof Book)) {
            showError("请先在列表中选择一本要删除的图书！");
            return;
        }
        Book selectedBook = (Book) selectedItem;

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("确认删除");
        confirmation.setHeaderText("您确定要删除《" + selectedBook.getBookName() + "》吗？");
        confirmation.setContentText("此操作不可撤销。");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Message response = libraryService.deleteBook(selectedBook.getBookId());
            if (response.isSuccess()) {
                showAlert("成功", "书籍已删除。");
                showAllBooksView();
            } else {
                showError("删除失败: " + response.getMessage());
                showAllBooksView();
            }
            showAllBooksView();
        }
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();

    }
    @FXML private void handleModifyBook(ActionEvent event) {

        Object selectedItem = bookTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem instanceof Book)) {
            showError("请先在列表中选择一本要修改的图书！");
            return;
        }
        Book selectedBook = (Book) selectedItem;

        // 步骤 2: 创建一个预先填充好当前书籍信息的字符串
        String currentInfo = String.join(",",
                selectedBook.getBookName(),
                selectedBook.getAuthor(),
                selectedBook.getISBN(),
                selectedBook.getPublisher(),
                selectedBook.getDescription()
        );

        // 步骤 3: 创建并显示一个带有默认值的对话框
        TextInputDialog dialog = new TextInputDialog(currentInfo);
        dialog.setTitle("修改书籍信息");
        dialog.setHeaderText("您正在修改《" + selectedBook.getBookName() + "》");
        dialog.setContentText("请按格式修改 (书籍名称,作者,ISBN,出版社,描述):");

        // 步骤 4: 处理用户的输入
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(bookInfo -> {
            String[] parts = bookInfo.split(",");
            if (parts.length == 5) {
                // 步骤 5: 更新 'selectedBook' 对象的属性，而不是创建新对象
                selectedBook.setBookName(parts[0].trim());
                selectedBook.setAuthor(parts[1].trim());
                selectedBook.setISBN(parts[2].trim());
                selectedBook.setPublisher(parts[3].trim());
                selectedBook.setDescription(parts[4].trim());

                // 步骤 6: 调用 'modifyBook' 服务
                Message response = libraryService.modifyBook(selectedBook);
                if (response.isSuccess()) {
                    showAlert("成功", "书籍信息已更新！");
                    // 步骤 7: 刷新列表以显示更新后的信息
                    showAllBooksView();
                } else {
                    showError("修改失败: " + response.getMessage());
                }
            } else {
                showError("输入格式不正确！");
            }
        });
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();

    }

    @FXML private void handleRenewAll(ActionEvent event)
    {
        /* TODO: 实现一键续借逻辑 */
        if (currentUser == null) {
            showError("无法获取用户信息，请重新登录。");
            return;
        }
        Message response = libraryService.renewAllBooks(currentUser.getUserId());
        if (response.isSuccess()) {
            showAlert("成功", "所有已借阅书籍已成功续借！");
            // 如果当前在“我的借阅”视图，则刷新它
            if (!isAdmin && currentLibUserView == LibUserView.MY_BORROWS) {
                showUserMyBorrowsView();
            }
        } else {
            showError("续借失败: " + response.getMessage());
        }
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();
        showAllBooksView();



    }


    // ================== 视图切换辅助方法 ==================

    private void showAllBooksView() {
        if (isAdmin) {
            currentLibAdminView = LibAdminView.ALL_BOOKS;
            viewToggleButton.setText("查看借阅记录");
            // 【修改点】确保按钮组可见，并根据是否有选中项来决定是否可用
            adminButtonsHBox.setVisible(true);
            // 切换回此视图时，重置按钮为禁用状态，等待用户选择
            setAdminActionButtonsDisabled(false);
        } else {
            currentLibUserView = LibUserView.ALL_BOOKS;
            myBorrowsButton.setText("我的借阅");
        }
        bookTable.getColumns().setAll(colBookId, colBookName, colAuthor, colISBN, colPublisher, colDescription, colBorrowStatus);
        System.out.println("切换到【所有图书】视图");


        System.out.println("【步骤3】showAllBooksView被调用，准备发送'获取所有图书'的请求...");
        Message response = libraryService.getAllBooks();
        if(response.isSuccess()) {
            updateBookDisplay((List<Book>) response.getData());
        } else {
            showError("加载书籍列表失败: " + response.getMessage());
        }


    }

    private void showAdminBorrowHistoryView() {
        currentLibAdminView = LibAdminView.BORROW_HISTORY;
        viewToggleButton.setText("查看借阅情况");
        // 【修改点】确保按钮组可见，但始终设置为禁用状态
        adminButtonsHBox.setVisible(true);
        setAdminActionButtonsDisabled(true);
        bookTable.getColumns().setAll(colBookId, colBookName, colUserId, colBorrowerName, colBorrowDate, colDueDate);
        System.out.println("切换到【管理员-借阅记录】视图");
        // TODO: 加载 BookProcess 类型的数据
        // ... (逻辑不变，但现在会触发数据加载)
        Message response = libraryService.getAdminBorrowHistory();
        if(response.isSuccess()) {
            updateBorrowLogDisplay((List<BorrowLog>) response.getData());
        } else {
            showError("加载借阅记录失败: " + response.getMessage());
        }



    }

    private void showAllUsersStatusView() {
        currentLibAdminView = LibAdminView.ALL_USERS_STATUS;
        viewToggleButton.setText("查看所有图书");
        // 【修改点】确保按钮组可见，但始终设置为禁用状态
        adminButtonsHBox.setVisible(true);
        setAdminActionButtonsDisabled(true);
        bookTable.getColumns().setAll(colUserId, colUserName, colUserIdentity, colBorrowedBook1, colBorrowedBook2, colBorrowedBook3);
        System.out.println("切换到【管理员-所有人借阅情况】视图");
        // TODO: 加载 Borrower 类型的数据
        // ... (逻辑不变，但现在会触发数据加载)
        Message response = libraryService.getAllUsersStatus();
        if(response.isSuccess()) {
            updateUserStatusDisplay((List<UserBorrowStatus>) response.getData());
        } else {
            showError("加载用户借阅情况失败: " + response.getMessage());
        }
    }

    private void showUserMyBorrowsView() {
        currentLibUserView = LibUserView.MY_BORROWS;
        myBorrowsButton.setText("返回书库");
        colBorrowDate.setVisible(true); // 确保可见
        colDueDate.setVisible(true);
        bookTable.getColumns().setAll(colBookId, colBookName, colBorrowDate,colDueDate);
        System.out.println("切换到【用户-我的借阅】视图");
        // TODO: 加载当前用户的 BookProcess 类型数据
        if (currentUser == null) return;
        Message response = libraryService.getMyBorrows(currentUser.getUserId());
        if(response.isSuccess()) {
            updateBorrowLogDisplay((List<BorrowLog>) response.getData());
        } else {
            showError("加载我的借阅失败: " + response.getMessage());
        }
    }

    /**
     * 新增一个辅助方法，用于统一设置管理员操作按钮的禁用状态
     * @param disabled true为禁用，false为启用
     */
    private void setAdminActionButtonsDisabled(boolean disabled) {
        addButton.setDisable(disabled);
        deleteButton.setDisable(disabled);
        modifyButton.setDisable(disabled);
    }

    /**
     * 【新增】处理所有返回“书籍列表”的异步响应。
     * (由 MessageController 在收到 LIBRARY_GET_ALL_BOOKS 和 LIBRARY_SEARCH_BOOKS 类型消息时调用)
     */
    public void handleBookListResponse(Message message) {
        if (message.isSuccess()) {
            // 这个方法之前已经存在，现在作为异步入口
            updateBookDisplay((List<Book>) message.getData());




        } else {
            showError("异步加载书籍列表失败: " + message.getMessage());
        }
    }

    /**
     * 【新增】处理所有返回“借阅记录列表”的异步响应。
     * (由 MessageController 在收到 LIBRARY_GET_MY_BORROWS 和 LIBRARY_GET_ADMIN_BORROW_HISTORY 类型消息时调用)
     */
    public void handleBorrowLogResponse(Message message) {
        if (message.isSuccess()) {
            // 这个方法之前已经存在，现在作为异步入口
            updateBorrowLogDisplay((List<BorrowLog>) message.getData());
        } else {
            showError("异步加载借阅记录失败: " + message.getMessage());
        }
    }

    /**
     * 【新增】处理返回“用户借阅状态列表”的异步响应。
     * (由 MessageController 在收到 LIBRARY_GET_ALL_USERS_STATUS 类型消息时调用)
     */
    public void handleUserStatusResponse(Message message) {
        if (message.isSuccess()) {
            // 这个方法之前已经存在，现在作为异步入口
            updateUserStatusDisplay((List<UserBorrowStatus>) message.getData());
        } else {
            showError("异步加载用户借阅情况失败: " + message.getMessage());
        }
    }

    /**
     * 【新增】处理从服务器返回的PDF文件响应。
     * 这个方法将由 MessageController 在收到响应后调用。
     * @param response 包含PDF字节或错误信息的 Message
     */
    public void handleGetBookPdfResponse(Message response) {
        if (response != null && response.isSuccess()) {
            byte[] pdfBytes = (byte[]) response.getData();
            if (pdfBytes != null && pdfBytes.length > 0) {
                // 必须使用 Platform.runLater，以确保文件和UI操作在JavaFX主线程上执行
                Platform.runLater(() -> {
                    try {
                        // 创建临时文件
                        File tempFile = File.createTempFile("vcampus_book_" + ((BorrowLog) bookTable.getSelectionModel().getSelectedItem()).getBookId(), ".pdf");
                        Files.write(tempFile.toPath(), pdfBytes);
                        tempFile.deleteOnExit(); // 程序退出时自动删除

                        // 打开文件
                        Desktop.getDesktop().open(tempFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                        showAlert("文件操作失败", "无法创建或打开临时PDF文件。");
                    }
                });
            }
        } else {
            String errorMsg = (response != null) ? response.getMessage() : "未能从服务器获取响应。";
            // 确保在UI线程中显示弹窗
            Platform.runLater(() -> showAlert("获取失败", errorMsg));
        }
    }


//    /**
//     * 核心刷新方法：从服务器获取最新的图书列表并更新UI表格。
//     */
//    private void refreshBookTable() {
//        System.out.println("CLIENT: 正在向服务器请求最新的图书列表...");
//
//        // 1. 调用客户端的 Service，从服务器获取最新的图书列表
//        Message response = libraryService.getAllBooks(); // 这会发送 LIBRARY_GET_ALL_BOOKS 请求
//
//        if (response != null && response.isSuccess()) {
//            // 2. 从 Message 中解析出数据 (服务端返回的是 List<Book>)
//            List<Book> latestBookList = (List<Book>) response.getData();
//
//            if (latestBookList != null) {
//                System.out.println("CLIENT: 成功获取到 " + latestBookList.size() + " 本书。正在刷新表格...");
//
//                // 3. 【最关键的一步】使用 setAll 来更新 TableView 的数据源。
//                // 这是触发 JavaFX TableView UI 更新的标准、最高效的方式。
//                bookTable.getItems().setAll(latestBookList);
//
//                System.out.println("CLIENT: 表格UI刷新完成。");
//            }
//        } else {
//            // 如果获取失败，显示错误
//            String errorMsg = (response != null) ? response.getMessage() : "无法连接到服务器";
//            showError("数据刷新失败：" + errorMsg);
//        }
//    }

// 在 LibraryController.java 中

}