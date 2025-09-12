package com.vcampus.client.controller.userAdmin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.vcampus.client.MainApp;
import com.vcampus.client.controller.IClientController;
import com.vcampus.client.service.userAdmin.ForgetPasswordTableService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.PasswordResetApplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

/**
 * 忘记密码申请查看控制器
 * 负责显示和管理忘记密码申请
 * 编写人：谌宣羽
 */
public class ForgetPasswordTableViewController implements IClientController {

    @Override
    public void registerToMessageController() {
        com.vcampus.client.controller.MessageController messageController = 
            MainApp.getGlobalSocketClient().getMessageController();
        if (messageController != null) {
            messageController.setForgetPasswordTableViewController(this);
        }
    }

    // Service层
    private final ForgetPasswordTableService forgetPasswordTableService;
    
    // 界面组件
    @FXML
    private VBox titleContainer;
    
    @FXML
    private HBox actionContainer;
    
    @FXML
    private Button refreshButton;
    
    // 表格组件
    @FXML
    private TableView<PasswordResetApplication> applicationTable;
    
    @FXML
    private TableColumn<PasswordResetApplication, String> userIdColumn;
    
    @FXML
    private TableColumn<PasswordResetApplication, String> submitTimeColumn;
    
    @FXML
    private TableColumn<PasswordResetApplication, String> actionColumn;
    
    // 数据列表
    private ObservableList<PasswordResetApplication> applicationList;
    
    /**
     * 构造函数
     */
    public ForgetPasswordTableViewController() {
        this.forgetPasswordTableService = new ForgetPasswordTableService();
    }

    /**
     * 初始化方法
     */
    @FXML
    public void initialize() {
        registerToMessageController();
        initializeTable();
        loadAllApplications();
    }
    
    /**
     * 初始化表格
     */
    private void initializeTable() {
        // 设置列的数据绑定
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        // 设置提交时间列的格式化
        submitTimeColumn.setCellValueFactory(cellData -> {
            LocalDateTime dateTime = cellData.getValue().getSubmitTime();
            return new javafx.beans.property.SimpleStringProperty(
                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );
        });
        
        // 设置操作列
        actionColumn.setCellFactory(new Callback<TableColumn<PasswordResetApplication, String>, TableCell<PasswordResetApplication, String>>() {
            @Override
            public TableCell<PasswordResetApplication, String> call(TableColumn<PasswordResetApplication, String> param) {
                return new TableCell<PasswordResetApplication, String>() {
                    private final Button approveButton = new Button("✅ 批准");
                    private final Button rejectButton = new Button("❌ 拒绝");
                    
                    {
                        approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10px;");
                        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 10px;");
                        
                        approveButton.setOnAction(e -> handleApproveApplication(getTableView().getItems().get(getIndex())));
                        rejectButton.setOnAction(e -> handleRejectApplication(getTableView().getItems().get(getIndex())));
                    }
                    
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(5);
                            hbox.getChildren().addAll(approveButton, rejectButton);
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });
        
        // 设置列宽比例，让表格占满整个区域
        userIdColumn.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.30));      // 30%
        submitTimeColumn.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.50));  // 50%
        actionColumn.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.20));      // 20%
        
        // 初始化数据列表
        applicationList = FXCollections.observableArrayList();
        applicationTable.setItems(applicationList);
    }
    
    /**
     * 处理刷新按钮点击
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        loadAllApplications();
    }
    
    /**
     * 加载所有申请
     */
    private void loadAllApplications() {
        Message response = forgetPasswordTableService.getAllApplications();
        
        if (response.isSuccess() && response.getData() != null) {
            try {
                @SuppressWarnings("unchecked")
                List<PasswordResetApplication> applications = (List<PasswordResetApplication>) response.getData();
                applicationList.clear();
                applicationList.addAll(applications);
            } catch (ClassCastException e) {
                System.err.println("数据类型转换错误: " + e.getMessage());
                applicationList.clear();
            }
        } else {
            System.err.println("获取申请列表失败: " + (response.getMessage() != null ? response.getMessage() : "未知错误"));
            applicationList.clear();
        }
    }
    
    /**
     * 处理批准申请
     */
    private void handleApproveApplication(PasswordResetApplication application) {
        // 显示确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认批准");
        confirmAlert.setHeaderText("确认批准密码重置申请");
        confirmAlert.setContentText("您确定要批准用户 " + application.getUserId() + " 的密码重置申请吗？\n\n此操作将更新该用户的密码。");
        
        // 设置按钮文本
        ButtonType yesButton = new ButtonType("确认批准");
        ButtonType noButton = new ButtonType("取消");
        confirmAlert.getButtonTypes().setAll(yesButton, noButton);
        
        // 显示对话框并处理结果
        confirmAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                // 用户确认批准
                forgetPasswordTableService.approveApplication(application.getUserId());
            }
            // 如果用户选择取消，则不执行任何操作
        });
    }
    
    /**
     * 处理拒绝申请
     */
    private void handleRejectApplication(PasswordResetApplication application) {
        // 显示确认对话框
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认拒绝");
        confirmAlert.setHeaderText("确认拒绝密码重置申请");
        confirmAlert.setContentText("您确定要拒绝用户 " + application.getUserId() + " 的密码重置申请吗？\n\n此操作将删除该申请记录。");
        
        // 设置按钮文本
        ButtonType yesButton = new ButtonType("确认拒绝");
        ButtonType noButton = new ButtonType("取消");
        confirmAlert.getButtonTypes().setAll(yesButton, noButton);
        
        // 显示对话框并处理结果
        confirmAlert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == yesButton) {
                // 用户确认拒绝
                forgetPasswordTableService.rejectApplication(application.getUserId());
            }
            // 如果用户选择取消，则不执行任何操作
        });
    }

    /*
     * 处理获取忘记密码申请响应
     * @param message 获取忘记密码申请响应消息
     */
    public void handleGetForgetPasswordTableResponse(Message message) {
        if (message.isSuccess()) {
            @SuppressWarnings("unchecked")
            List<PasswordResetApplication> applications = (List<PasswordResetApplication>) message.getData();
            applicationList.clear();
            applicationList.addAll(applications);
        }
    }
    
    /*
     * 处理批准忘记密码申请响应
     * @param message 批准忘记密码申请响应消息
     */
    public void handleApproveForgetPasswordApplicationResponse(Message message) {
        if (message.isSuccess()) {
            loadAllApplications();
        }
    }
    
    /*
     * 处理拒绝忘记密码申请响应
     * @param message 拒绝忘记密码申请响应消息
     */
    public void handleRejectForgetPasswordApplicationResponse(Message message) {
        if (message.isSuccess()) {
            loadAllApplications();
        }
    }
}
