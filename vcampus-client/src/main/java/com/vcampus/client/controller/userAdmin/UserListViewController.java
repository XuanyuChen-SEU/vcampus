package com.vcampus.client.controller.userAdmin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

/**
 * 用户列表查看控制器
 * 负责显示和管理所有用户信息
 * 编写人：AI Assistant
 */
public class UserListViewController {

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
    private TableColumn<UserTableItem, String> statusColumn;
    
    @FXML
    private TableColumn<UserTableItem, String> lastLoginColumn;
    
    @FXML
    private TableColumn<UserTableItem, Void> actionsColumn;
    
    // 统计信息标签
    @FXML
    private Label totalUsersLabel;
    
    @FXML
    private Label activeUsersLabel;
    
    @FXML
    private Label studentCountLabel;
    
    @FXML
    private Label teacherCountLabel;
    
    @FXML
    private Label adminCountLabel;
    
    // 用户数据
    private ObservableList<UserTableItem> userData = FXCollections.observableArrayList();

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        // 初始化角色筛选下拉框
        initializeRoleFilter();
        
        // 初始化表格
        initializeTable();
        
        // 加载用户数据
        loadUserData();
        
        // 更新统计信息
        updateStatistics();
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
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        lastLoginColumn.setCellValueFactory(cellData -> cellData.getValue().lastLoginProperty());
        
        // 设置操作列
        actionsColumn.setCellFactory(new Callback<TableColumn<UserTableItem, Void>, TableCell<UserTableItem, Void>>() {
            @Override
            public TableCell<UserTableItem, Void> call(TableColumn<UserTableItem, Void> param) {
                return new TableCell<UserTableItem, Void>() {
                    private final Button editButton = new Button("✏️ 编辑");
                    private final Button resetButton = new Button("🔄 重置密码");
                    private final Button deleteButton = new Button("🗑️ 删除");
                    
                    {
                        editButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        resetButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        deleteButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 6 2 6;");
                        
                        editButton.setOnAction(e -> handleEditUser(getTableView().getItems().get(getIndex())));
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
                            buttons.getChildren().addAll(editButton, resetButton, deleteButton);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }
    
    /**
     * 加载用户数据
     */
    private void loadUserData() {
        // TODO: 从服务器获取真实用户数据
        // 这里使用模拟数据
        userData.clear();
        userData.addAll(
            new UserTableItem("1234567", "学生", "活跃", "2024-01-15 10:30"),
            new UserTableItem("2234567", "教师", "活跃", "2024-01-15 09:15"),
            new UserTableItem("3234567", "管理员", "活跃", "2024-01-15 08:45"),
            new UserTableItem("1234568", "学生", "离线", "2024-01-14 16:20"),
            new UserTableItem("2234568", "教师", "活跃", "2024-01-15 11:00")
        );
    }
    
    /**
     * 更新统计信息
     */
    private void updateStatistics() {
        int totalUsers = userData.size();
        int activeUsers = (int) userData.stream().filter(user -> "活跃".equals(user.getStatus())).count();
        int studentCount = (int) userData.stream().filter(user -> "学生".equals(user.getRole())).count();
        int teacherCount = (int) userData.stream().filter(user -> "教师".equals(user.getRole())).count();
        int adminCount = (int) userData.stream().filter(user -> "管理员".equals(user.getRole())).count();
        
        totalUsersLabel.setText("总用户数: " + totalUsers);
        activeUsersLabel.setText("活跃用户: " + activeUsers);
        studentCountLabel.setText("学生: " + studentCount);
        teacherCountLabel.setText("教师: " + teacherCount);
        adminCountLabel.setText("管理员: " + adminCount);
    }
    
    /**
     * 处理搜索
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim();
        String selectedRole = roleFilterCombo.getValue();
        
        // TODO: 实现搜索逻辑
        System.out.println("搜索: " + searchText + ", 角色: " + selectedRole);
    }
    
    /**
     * 处理刷新
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUserData();
        updateStatistics();
    }
    
    /**
     * 处理编辑用户
     */
    private void handleEditUser(UserTableItem user) {
        // TODO: 编辑用户功能
        System.out.println("编辑用户: " + user.getId());
    }
    
    /**
     * 处理重置密码
     */
    private void handleResetPassword(UserTableItem user) {
        // TODO: 重置密码功能
        System.out.println("重置密码: " + user.getId());
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
                // TODO: 实现删除逻辑
                System.out.println("删除用户: " + user.getId());
                userData.remove(user);
                updateStatistics();
            }
        });
    }
    
    /**
     * 用户表格项数据类
     */
    public static class UserTableItem {
        private final String id;
        private final String role;
        private final String status;
        private final String lastLogin;
        
        public UserTableItem(String id, String role, String status, String lastLogin) {
            this.id = id;
            this.role = role;
            this.status = status;
            this.lastLogin = lastLogin;
        }
        
        public String getId() { return id; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public String getLastLogin() { return lastLogin; }
        
        public javafx.beans.property.StringProperty idProperty() {
            return new javafx.beans.property.SimpleStringProperty(id);
        }
        
        public javafx.beans.property.StringProperty roleProperty() {
            return new javafx.beans.property.SimpleStringProperty(role);
        }
        
        public javafx.beans.property.StringProperty statusProperty() {
            return new javafx.beans.property.SimpleStringProperty(status);
        }
        
        public javafx.beans.property.StringProperty lastLoginProperty() {
            return new javafx.beans.property.SimpleStringProperty(lastLogin);
        }
    }
}
