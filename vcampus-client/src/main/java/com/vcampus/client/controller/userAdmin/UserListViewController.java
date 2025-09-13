package com.vcampus.client.controller.userAdmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import com.vcampus.client.controller.UserAdminViewController;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.userAdmin.UserListService;
import com.vcampus.client.controller.IClientController;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.User;
import java.util.List;

/**
 * 用户列表查看控制器
 * 负责显示和管理所有用户信息
 * 编写人：谌宣羽
 */
public class UserListViewController implements IClientController{
    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            //messageController.setUserListViewController(this);
        }
    }

    // Service层
    private final UserListService userListService;
    
    // 搜索和筛选组件
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> roleFilterCombo;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    // 用户表格
    @FXML
    private TableView<UserTableItem> userTable;
    
    @FXML
    private TableColumn<UserTableItem, String> idColumn;
    
    @FXML
    private TableColumn<UserTableItem, String> roleColumn;
    
    
    @FXML
    private TableColumn<UserTableItem, Void> actionsColumn;
    
    // 统计信息标签
    @FXML
    private Label totalUsersLabel;
    
    @FXML
    private Label studentCountLabel;
    
    @FXML
    private Label teacherCountLabel;
    
    @FXML
    private Label adminCountLabel;
    
    // 用户数据
    private ObservableList<UserTableItem> userData = FXCollections.observableArrayList();
    
    /**
     * 构造函数
     */
    public UserListViewController() {
        this.userListService = new UserListService();
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 初始化角色筛选下拉框
        initializeRoleFilter();
        registerToMessageController();
        // 初始化表格
        initializeTable();
        
        // 使用统一的搜索功能加载用户数据（全局搜索）
        searchUser("", "全部");
    }
    
    /**
     * 初始化角色筛选下拉框
     */
    private void initializeRoleFilter() {
        roleFilterCombo.setItems(FXCollections.observableArrayList(
            "全部", "学生", "教师", "管理员"
        ));
        roleFilterCombo.setValue("全部");
    }
    
    /**
     * 初始化表格
     */
    private void initializeTable() {
        // 设置表格数据
        userTable.setItems(userData);
        
        // 设置列数据绑定
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        
        // 设置列宽比例，让表格占满整个区域
        idColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.40));      // 40%
        roleColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.30));    // 30%
        actionsColumn.prefWidthProperty().bind(userTable.widthProperty().multiply(0.30));  // 30%
        
        // 设置操作列
        actionsColumn.setCellFactory(new Callback<TableColumn<UserTableItem, Void>, TableCell<UserTableItem, Void>>() {
            @Override
            public TableCell<UserTableItem, Void> call(TableColumn<UserTableItem, Void> param) {
                return new TableCell<UserTableItem, Void>() {
                    private final Button resetButton = new Button("🔄 重置密码");
                    private final Button deleteButton = new Button("🗑️ 删除");
                    
                    {
                        resetButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        
                        resetButton.setOnAction(e -> handleResetPassword(getTableView().getItems().get(getIndex())));
                        deleteButton.setOnAction(e -> handleDeleteUser(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5);
                            buttons.getChildren().addAll(resetButton, deleteButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }
    
    /**
     * 搜索用户方法
     * @param searchText 搜索关键词
     * @param selectedRole 选择的角色
     */
    private void searchUser(String searchText, String selectedRole) {
        try {
            // 使用统一的搜索方法
            Message result = userListService.search(searchText, selectedRole);
            
            if (result.isSuccess()) {
                if (searchText.isEmpty() && "全部".equals(selectedRole)) {
                    System.out.println("成功发送全局搜索请求");
                } else if (!searchText.isEmpty()) {
                    System.out.println("成功发送关键词搜索请求: " + searchText);
                } else {
                    System.out.println("成功发送角色筛选请求: " + selectedRole);
                }
                // 注意：这里只确认请求发送成功，实际数据会在后续的响应处理中更新
            } else {
                System.err.println("发送搜索请求失败: " + result.getMessage());
                showError("发送搜索请求失败: " + result.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("执行搜索时发生异常: " + e.getMessage());
            showError("执行搜索时发生异常: " + e.getMessage());
        }
    }
    
    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        int totalUsers = userData.size();
        int studentCount = (int) userData.stream().filter(user -> "学生".equals(user.getRole())).count();
        int teacherCount = (int) userData.stream().filter(user -> "教师".equals(user.getRole())).count();
        int adminCount = (int) userData.stream().filter(user -> "管理员".equals(user.getRole())).count();
        
        totalUsersLabel.setText("总用户数: " + totalUsers);
        studentCountLabel.setText("学生: " + studentCount);
        teacherCountLabel.setText("教师: " + teacherCount);
        adminCountLabel.setText("管理员: " + adminCount);
    }
    
    
    
    /**
     * 处理重置密码
     */
    private void handleResetPassword(UserTableItem user) {
        // 直接调用父控制器的loadSubView方法
        UserAdminViewController parentController = getParentController();
        if (parentController != null) {
            parentController.loadSubView("/fxml/admin/user/UserPasswordResetView.fxml", user.getId());
            // 更新父控制器的状态和按钮样式
            parentController.setCurrentView("resetPassword");
        } else {
            System.out.println("无法找到父控制器");
        }
    }
    
    /**
     * 获取父控制器
     */
    private UserAdminViewController getParentController() {
        // 通过反射获取父控制器
        try {
            // 获取当前场景
            javafx.scene.Scene scene = userTable.getScene();
            if (scene != null) {
                // 查找场景中的UserAdminViewController
                return findControllerInScene(scene.getRoot());
            }
        } catch (Exception e) {
            System.out.println("获取父控制器失败: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * 在场景中查找UserAdminViewController
     */
    private UserAdminViewController findControllerInScene(javafx.scene.Node node) {
        if (node.getUserData() instanceof UserAdminViewController) {
            return (UserAdminViewController) node.getUserData();
        }
        
        if (node instanceof javafx.scene.Parent) {
            for (javafx.scene.Node child : ((javafx.scene.Parent) node).getChildrenUnmodifiable()) {
                UserAdminViewController controller = findControllerInScene(child);
                if (controller != null) {
                    return controller;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 处理删除用户
     */
    private void handleDeleteUser(UserTableItem user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("删除用户");
        alert.setContentText("确定要删除用户 " + user.getId() + " 吗？此操作不可撤销。");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // 使用Service层发送删除用户请求
                    Message result = userListService.deleteUser(user.getId());
                    
                    if (result.isSuccess()) {
                        System.out.println("成功发送删除用户请求: " + user.getId());
                    } else {
                        // 发送请求失败
                        System.err.println("发送删除用户请求失败: " + result.getMessage());
                        showError("发送删除用户请求失败: " + result.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("发送删除用户请求时发生异常: " + e.getMessage());
                    showError("发送删除用户请求时发生异常: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * 显示错误信息
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText("操作失败");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    /**
     * 用户表格项数据类
     */
    public static class UserTableItem {
        private final String id;
        private final String role;
        
        public UserTableItem(String id, String role) {
            this.id = id;
            this.role = role;
        }
        
        public String getId() { return id; }
        public String getRole() { return role; }
        
        public javafx.beans.property.StringProperty idProperty() {
            return new javafx.beans.property.SimpleStringProperty(id);
        }
        
        public javafx.beans.property.StringProperty roleProperty() {
            return new javafx.beans.property.SimpleStringProperty(role);
        }
    }

    public void handleSearchUsersResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("搜索用户成功: " + message.getMessage());
            userData.clear();
            List<User> users = (List<User>) message.getData();
            if (users != null) {
                for (User user : users) {
                    String role = getRoleFromUserId(user.getUserId());
                    userData.add(new UserTableItem(user.getUserId(), role));
                }
            }
            userTable.setItems(userData);
            updateStatistics();
        } else {
            System.err.println("搜索用户失败: " + message.getMessage());
            showError("搜索用户失败: " + message.getMessage());
        }
    }

    public void handleDeleteUserResponse(Message message) {
        if (message.isSuccess()) {
            System.out.println("删除用户成功: " + message.getMessage());
            // 重新加载用户列表以反映删除结果
            refreshUserList();
            updateStatistics();
        } else {
            System.err.println("删除用户失败: " + message.getMessage());
            showError("删除用户失败: " + message.getMessage());
        }
    }

    /**
     * 根据用户ID获取角色名称
     */
    private String getRoleFromUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return "未知";
        }
        char firstChar = userId.charAt(0);
        switch (firstChar) {
            case '1':
                return "学生";
            case '2':
                return "教师";
            default:
                return "管理员";
        }
    }

    /**
     * 刷新用户列表
     */
    private void refreshUserList() {
        String searchText = searchField.getText();
        String selectedRole = roleFilterCombo.getValue();
        if (selectedRole == null) {
            selectedRole = "全部";
        }
        searchUser(searchText, selectedRole);
    }

    /**
     * 处理搜索按钮点击事件
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        String selectedRole = roleFilterCombo.getValue();
        if (selectedRole == null) {
            selectedRole = "全部";
        }
        searchUser(searchText, selectedRole);
    }

    /**
     * 处理刷新按钮点击事件
     */
    @FXML
    private void handleRefresh() {
        // 清空搜索条件
        searchField.clear();
        roleFilterCombo.setValue("全部");
        
        // 执行搜索
        searchUser("", "全部");
    }
}
