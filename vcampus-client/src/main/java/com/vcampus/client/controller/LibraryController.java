package com.vcampus.client.controller;


import com.vcampus.client.MainApp;
import com.vcampus.client.service.LibraryService;
import com.vcampus.client.session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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

import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class LibraryController implements IClientController {

    // UI 控件声明
    @FXML private Label libraryLabel;
    @FXML private Button viewToggleButton;
    @FXML private TextField searchField;
    @FXML private HBox userButtonsHBox;
    @FXML private Button myBorrowsButton;
    @FXML private HBox adminButtonsHBox;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button modifyButton;
    @FXML private TableView<Object> bookTable;
    @FXML private Label userInfoLabel;
    @FXML private Button borrowButton;
    @FXML private HBox topBarHBox; // 【新增】顶部工具栏的引用
    @FXML private Button literatureSearchButton;
    @FXML private Button returnFromAcademicViewButton;
    @FXML private BorderPane rootPane;

    // 【新增】一个变量来保存图书馆的主视图内容
    private Node libraryCenterView;
    private List<Node> originalTopBarChildren; // 【新增】用于保存原始的顶部栏控件
    private HBox academicTopBarContent; // 【新增】用于存放新的学术网站按钮
    private AcademicSearchController academicSearchController; // 【新增】用于引用WebView的控制器
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
    // 状态管理变量
    private boolean isAdmin;
    private LibAdminView currentLibAdminView = LibAdminView.ALL_BOOKS;
    private LibUserView currentLibUserView = LibUserView.ALL_BOOKS;
    private final LibraryService libraryService = new LibraryService();
    private User currentUser;


    // ================== 初始化与核心逻辑 ==================
    @FXML
    public void initialize() {
        System.out.println("图书馆控制器初始化...");
        registerToMessageController(); // 必须先注册，才能接收后续请求的响应
        setCurrentUser();
        setupUIForRole();
        configureColumnFactories();
        addTableSelectionListener();
        setupOverdueRowFactory();
        setupRowDoubleClickListener();

        // 初始化时异步加载所有图书
        loadInitialAllData();
        // 【新增】在初始化时，保存原始的中央视图
        if (rootPane != null) {
            libraryCenterView = rootPane.getCenter();
        } else {
            System.err.println("错误: 未能获取到根布局 rootPane，请检查 FXML 文件中的 fx:id 设置。");
        }
        createAcademicTopBar();
    }
    /**
     * 【最终完整版】预创建学术模式下的顶部按钮栏
     * - 包含了全部四个网站的按钮
     * - 修正了所有按钮的文字和图标
     */
    private void createAcademicTopBar() {
        // --- 1. 创建 arXiv 按钮 (带图标) ---
        Button arxivBtn = new Button("arXiv");
        try {
            Image arxivImg = new Image(getClass().getResourceAsStream("/images/LibraryIcons/arxiv_icon.png"));
            ImageView arxivIconView = new ImageView(arxivImg);
            arxivIconView.setFitHeight(16);
            arxivIconView.setFitWidth(16);
            arxivBtn.setGraphic(arxivIconView);
            arxivBtn.setContentDisplay(ContentDisplay.LEFT);
        } catch (Exception e) {
            System.err.println("未能加载 arXiv 图标");
        }

        // --- 2. 创建 OpenReview 按钮 (使用指定文字) ---
        Button openReviewBtn = new Button("OpenReview.net");

        // --- 3. 创建谷歌学术按钮 (使用完整中文文字) ---
        Button googleScholarBtn = new Button("GoogleScholar");
        try {
            Image googleScholarImg = new Image(getClass().getResourceAsStream("/images/LibraryIcons/GoogleScholar_icon.png"));
            ImageView googleScholarIconView = new ImageView(googleScholarImg);
            googleScholarIconView.setFitHeight(16);
            googleScholarIconView.setFitWidth(16);
            googleScholarBtn.setGraphic(googleScholarIconView);
            googleScholarBtn.setContentDisplay(ContentDisplay.LEFT);
        } catch (Exception e) {
            System.err.println("未能加载 googleScholar 图标");
        }
        // --- 4. 创建 Z-Library 按钮 ---
        Button zlibraryBtn = new Button("Z-Library");

        // --- 设置样式 ---
        arxivBtn.getStyleClass().add("action-button");
        openReviewBtn.getStyleClass().add("action-button");
        googleScholarBtn.getStyleClass().add("action-button");
        zlibraryBtn.getStyleClass().add("action-button");

        // --- 设置点击回调 ---
        arxivBtn.setOnAction(event -> loadAcademicWebsite("arXiv"));
        openReviewBtn.setOnAction(event -> loadAcademicWebsite("OpenReview"));
        googleScholarBtn.setOnAction(event -> loadAcademicWebsite("GoogleScholar"));
        zlibraryBtn.setOnAction(event -> loadAcademicWebsite("ZLibrary"));

        // --- 将所有四个按钮添加到 HBox 中 ---
        this.academicTopBarContent = new HBox(15, arxivBtn, openReviewBtn, googleScholarBtn, zlibraryBtn);
        this.academicTopBarContent.setAlignment(Pos.CENTER_LEFT);
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
        com.vcampus.client.controller.MessageController messageController = MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setLibraryController(this);
        } else {
            System.err.println("严重错误：LibraryController 注册失败！");
        }
    }
    /**
     * 【重构后的方法，取代了之前的 load... 方法】
     * 这个方法由外部调用（比如 MainViewController 或 initialize 自身），
     * 用于设置当前用户，并触发初始数据的加载。
     */
    public void setCurrentUser() {
        UserSession userSession = UserSession.getInstance();
        if (userSession.isLoggedIn()) {
            this.currentUser = new User();
            this.currentUser.setUserId(userSession.getCurrentUserId());
            this.isAdmin = userSession.getCurrentUserRole().getDesc().equals("图书馆管理员");
            updateUserInfo();
        }
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


    @FXML private void handleBorrowBook(ActionEvent event) {
        Object selectedItem = bookTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null || !(selectedItem instanceof Book)) {
            showError("请先选择一本要借阅的图书。"); return;
        }
        if (currentUser == null) {
            showError("无法识别用户信息，请重新登录。"); return;
        }
        Book selectedBook = (Book) selectedItem;
        if (!"在馆".equals(selectedBook.getBorrowStatus().trim())) {
            showError("这本书当前状态为[" + selectedBook.getBorrowStatus() + "]，无法借阅。"); return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "您确定要借阅《" + selectedBook.getBookName() + "》吗？", ButtonType.OK, ButtonType.CANCEL);
        confirmation.setTitle("确认借阅");
        confirmation.setHeaderText(null);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                libraryService.borrowBook(currentUser.getUserId(), selectedBook.getBookId());
            }
        });
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

    @FXML private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText();
        if (keyword == null || keyword.trim().isEmpty()) {
            loadInitialAllData(); // 刷新
        } else {
            libraryService.searchBooks(keyword);
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
        TextInputDialog dialog = new TextInputDialog("ID,书名,作者,ISBN,出版社,描述");
        dialog.setTitle("添加新书");
        dialog.setHeaderText("请输入新书信息，用英文逗号分隔");
        dialog.showAndWait().ifPresent(bookInfo -> {
            String[] parts = bookInfo.split(",");
            if (parts.length == 6) {
                Book newBook = new Book(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], "在馆");
                libraryService.addBook(newBook);
            } else {
                showError("输入格式不正确！");
            }
        });
    }
    @FXML private void handleDeleteBook(ActionEvent event) {
        Object selectedItem = bookTable.getSelectionModel().getSelectedItem();
        if (!(selectedItem instanceof Book)) {
            showError("请选择一本要删除的图书！"); return;
        }
        Book selectedBook = (Book) selectedItem;
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "您确定要删除《" + selectedBook.getBookName() + "》吗？", ButtonType.OK, ButtonType.CANCEL);
        confirmation.setTitle("确认删除");
        confirmation.setHeaderText(null);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                libraryService.deleteBook(selectedBook.getBookId());
            }
        });
    }
    @FXML private void handleModifyBook(ActionEvent event) {
        Object selectedItem = bookTable.getSelectionModel().getSelectedItem();
        if (!(selectedItem instanceof Book)) {
            showError("请选择一本要修改的图书！"); return;
        }
        Book selectedBook = (Book) selectedItem;
        String currentInfo = String.join(",", selectedBook.getBookName(), selectedBook.getAuthor(), selectedBook.getISBN(), selectedBook.getPublisher(), selectedBook.getDescription());
        TextInputDialog dialog = new TextInputDialog(currentInfo);
        dialog.setTitle("修改书籍信息");
        dialog.setHeaderText("您正在修改《" + selectedBook.getBookName() + "》");
        dialog.showAndWait().ifPresent(bookInfo -> {
            String[] parts = bookInfo.split(",");
            if (parts.length == 5) {
                selectedBook.setBookName(parts[0].trim());
                selectedBook.setAuthor(parts[1].trim());
                selectedBook.setISBN(parts[2].trim());
                selectedBook.setPublisher(parts[3].trim());
                selectedBook.setDescription(parts[4].trim());
                libraryService.modifyBook(selectedBook);
            } else {
                showError("输入格式不正确！");
            }
        });
    }

    @FXML private void handleRenewAll(ActionEvent event) {
        if (currentUser == null) {
            showError("无法获取用户信息。"); return;
        }
        libraryService.renewAllBooks(currentUser.getUserId());
    }


    // ================== 视图切换辅助方法 ==================

    private void showAllBooksView() {
        configureTableForBooks();
        // 【修正】只发送请求，由响应处理器负责更新UI
        libraryService.getAllBooks();
    }

    private void showAdminBorrowHistoryView() {
        configureTableForAdminHistory();
        // 【修正】只发送请求
        libraryService.getAdminBorrowHistory();
    }

    private void showAllUsersStatusView() {
        configureTableForAllUsersStatus();
        // 【修正】只发送请求
        libraryService.getAllUsersStatus();
    }

    private void showUserMyBorrowsView() {
        configureTableForMyBorrows();
        if (currentUser == null) return;
        // 【修正】只发送请求
        libraryService.getMyBorrows(currentUser.getUserId());
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
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                updateBookDisplay((List<Book>) message.getData());
            } else {
                showError("加载书籍列表失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增】处理所有返回“借阅记录列表”的异步响应。
     * (由 MessageController 在收到 LIBRARY_GET_MY_BORROWS 和 LIBRARY_GET_ADMIN_BORROW_HISTORY 类型消息时调用)
     */
    public void handleBorrowLogResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                updateBorrowLogDisplay((List<BorrowLog>) message.getData());
            } else {
                showError("加载借阅记录失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增】处理返回“用户借阅状态列表”的异步响应。
     * (由 MessageController 在收到 LIBRARY_GET_ALL_USERS_STATUS 类型消息时调用)
     */
    public void handleUserStatusResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                updateUserStatusDisplay((List<UserBorrowStatus>) message.getData());
            } else {
                showError("加载用户借阅情况失败: " + message.getMessage());
            }
        });
    }

    /**
     * 【新增】处理从服务器返回的PDF文件响应。
     * 这个方法将由 MessageController 在收到响应后调用。
     * @param response 包含PDF字节或错误信息的 Message
     */
    public void handleGetBookPdfResponse(Message response) {
        if (response != null && response.isSuccess()) {
            byte[] pdfBytes = (byte[]) response.getData();
            Platform.runLater(() -> {
                try {
                    File tempFile = File.createTempFile("vcampus_book_", ".pdf");
                    Files.write(tempFile.toPath(), pdfBytes);
                    tempFile.deleteOnExit();
                    Desktop.getDesktop().open(tempFile);
                } catch (IOException e) {
                    showAlert("文件操作失败", "无法创建或打开临时PDF文件。");
                }
            });
        } else {
            showError("未能从服务器获取PDF文件。");
        }
    }
    public void handleBookUpdateResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("操作成功", message.getMessage());
                showAllBooksView(); // 操作成功后刷新主列表
            } else {
                showError(message.getMessage());
            }
        });
    }
    /**
     * 点击“文献检索”按钮时，显示网站选择页面
     */
    @FXML
    private void handleShowAcademicView() {
        try {
            // 1. 隐藏顶部工具栏并切换底部按钮
            topBarHBox.setVisible(false);
            literatureSearchButton.setVisible(false);
            literatureSearchButton.setManaged(false);
            returnFromAcademicViewButton.setVisible(true);
            returnFromAcademicViewButton.setManaged(true);

            // 2. 加载并显示“网站选择”视图
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library/AcademicChoiceView.fxml"));
            Parent choiceView = loader.load();
            AcademicChoiceController choiceController = loader.getController();

            // 3. 设置回调：当用户做出选择后，加载WebView并动态修改顶部栏
            choiceController.setOnChoiceMade(siteName -> {
                // 保存原始的顶部栏子节点
                if (originalTopBarChildren == null) {
                    originalTopBarChildren = new ArrayList<>(topBarHBox.getChildren());
                }
                // 加载对应的网站
                loadAcademicWebsite(siteName);
            });

            rootPane.setCenter(choiceView);

        } catch (Exception e) {
            e.printStackTrace();
            showError("无法加载学术网站选择页面: " + e.getMessage());
        }
    }

    /**
     * 【最终修正版】根据用户的选择，加载对应的学术网站
     * - 新增了对 "GoogleScholar" 的处理逻辑
     */
    private void loadAcademicWebsite(String siteName) {
        try {
            // ... (切换顶部栏和加载WebView的代码不变) ...
            topBarHBox.getChildren().clear();
            topBarHBox.getChildren().add(academicTopBarContent);
            topBarHBox.setVisible(true);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/library/AcademicSearchView.fxml"));
            Parent academicView = loader.load();
            this.academicSearchController = loader.getController();

            String query = searchField.getText();
            String url;

            if ("arXiv".equals(siteName)) {
                url = (query == null || query.trim().isEmpty()) ? "https://arxiv.org" :
                        "https://arxiv.org/search/?query=" + URLEncoder.encode(query, "UTF-8");

            } else if ("OpenReview".equals(siteName)) {
                url = (query == null || query.trim().isEmpty()) ? "https://openreview.net" :
                        "https://openreview.net/search?q=" + URLEncoder.encode(query, "UTF-8");

            } else if("GoogleScholar".equals(siteName)){ // 谷歌学术
                url = (query == null || query.trim().isEmpty()) ? "https://scholar.google.com/" :
                        "https://scholar.google.com/scholar?q=" + URLEncoder.encode(query, "UTF-8");
            } else { // 【新增】处理 Z-Library 的 case
                // 注意：Z-Library 域名经常变化，这里使用一个当前可用的
                url = (query == null || query.trim().isEmpty()) ? "https://z-lib.is/" :
                        "https://z-lib.is/s/" + URLEncoder.encode(query, "UTF-8");
            }

            academicSearchController.loadURL(url);
            rootPane.setCenter(academicView);

        } catch (Exception e) {
            e.printStackTrace();
            showError("无法加载学术网站页面: " + e.getMessage());
        }
    }

    @FXML
    private void handleReturnFromAcademicView() {
        // 恢复主视图
        rootPane.setCenter(libraryCenterView);

        // 恢复原始的顶部栏内容和按钮
        if (originalTopBarChildren != null) {
            topBarHBox.getChildren().setAll(originalTopBarChildren);
        }
        topBarHBox.setVisible(true);
        literatureSearchButton.setVisible(true);
        literatureSearchButton.setManaged(true);
        returnFromAcademicViewButton.setVisible(false);
        returnFromAcademicViewButton.setManaged(false);

        // 清理引用，以便下次能正确执行
        this.academicSearchController = null;
    }
    /**
     * 【核心】辅助方法，用于统一管理进入/退出学术检索模式的UI变化
     * @param isAcademicMode true为进入学术模式，false为退出
     */
    private void setAcademicMode(boolean isAcademicMode) {
        // 控制顶部工具栏的可见性
        if (topBarHBox != null) {
            topBarHBox.setVisible(!isAcademicMode);
            topBarHBox.setManaged(!isAcademicMode);
        }

        // 控制底部按钮的切换
        literatureSearchButton.setVisible(!isAcademicMode);
        literatureSearchButton.setManaged(!isAcademicMode);
        returnFromAcademicViewButton.setVisible(isAcademicMode);
        returnFromAcademicViewButton.setManaged(isAcademicMode);
    }
// ================== UI配置与辅助方法 (结构优化) ==================

    private void configureTableForBooks() {
        if (isAdmin) {
            currentLibAdminView = LibAdminView.ALL_BOOKS;
            viewToggleButton.setText("查看借阅记录");
            adminButtonsHBox.setVisible(true);
            setAdminActionButtonsDisabled(bookTable.getSelectionModel().getSelectedItem() == null);
        } else {
            currentLibUserView = LibUserView.ALL_BOOKS;
            myBorrowsButton.setText("我的借阅");
        }
        bookTable.getColumns().setAll(colBookId, colBookName, colAuthor, colISBN, colPublisher, colDescription, colBorrowStatus);
    }

    private void configureTableForAdminHistory() {
        currentLibAdminView = LibAdminView.BORROW_HISTORY;
        viewToggleButton.setText("查看借阅情况");
        adminButtonsHBox.setVisible(false);
        bookTable.getColumns().setAll(colBookId, colBookName, colUserId, colBorrowerName, colBorrowDate, colDueDate);
    }

    private void configureTableForAllUsersStatus() {
        currentLibAdminView = LibAdminView.ALL_USERS_STATUS;
        viewToggleButton.setText("查看所有图书");
        adminButtonsHBox.setVisible(false);
        bookTable.getColumns().setAll(colUserId, colUserName, colUserIdentity, colBorrowedBook1, colBorrowedBook2, colBorrowedBook3);
    }

    private void configureTableForMyBorrows() {
        currentLibUserView = LibUserView.MY_BORROWS;
        myBorrowsButton.setText("返回书库");
        bookTable.getColumns().setAll(colBookId, colBookName, colBorrowDate, colDueDate);
    }

}