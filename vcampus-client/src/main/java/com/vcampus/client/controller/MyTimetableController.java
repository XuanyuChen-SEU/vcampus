package com.vcampus.client.controller;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


import com.vcampus.client.MainApp;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.MyCourse; // 引入表格专用的 DTO
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.stream.Collectors;

public class MyTimetableController  implements IClientController{
    @FXML
    private TableView<MyCourse> myCoursesTable;

    @FXML
    private TableColumn<MyCourse, String> colCourseIdAndName;
    @FXML
    private TableColumn<MyCourse, String> colTeacher;
    @FXML
    private TableColumn<MyCourse, String> colSchedule;
    @FXML
    private TableColumn<MyCourse, String> colCredits;
    @FXML
    private TableColumn<MyCourse, String> colType;
    @FXML
    private TableColumn<MyCourse, String> colCategory;
    @FXML
    private TableColumn<MyCourse, String> colCampus;
    @FXML
    private TableColumn<MyCourse, String> colConflict;
    @FXML
    private TableColumn<MyCourse, Void> colAction;
    // --- ⭐ 1. 注入新控件 ---
    @FXML private TabPane tabPane;
    @FXML private Tab dropLogTab;
    @FXML private TableView<DropLogEntry> dropLogTable;
    @FXML private TableColumn<DropLogEntry, String> colLogCourseIdAndName;
    @FXML private TableColumn<DropLogEntry, String> colLogTeacher;
    @FXML private TableColumn<DropLogEntry, String> colLogType;
    @FXML private TableColumn<DropLogEntry, Double> colLogCredits;
    @FXML private TableColumn<DropLogEntry, String> colLogOperator;
    @FXML private TableColumn<DropLogEntry, String> colLogDropType;
    @FXML private TableColumn<DropLogEntry, String> colLogPriority;

    private final CourseService courseService = new CourseService();
    private final ObservableList<MyCourse> myCoursesData = FXCollections.observableArrayList();
    private final ObservableList<DropLogEntry> dropLogData = FXCollections.observableArrayList();
    private boolean hasLogLoaded = false; // 懒加载标志，防止重复加载
    private final ObservableList<MyCourse> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("“我的课表”模块已加载，正在初始化...");
        registerToMessageController();

        // --- 逻辑清晰的初始化顺序 ---
        // 1. 设置UI组件
        setupMyCoursesTable();
        setupDropLogTable();

        // 2. 绑定数据源
        myCoursesTable.setItems(myCoursesData);
        dropLogTable.setItems(dropLogData);

        // 3. 设置监听器
        dropLogTab.setOnSelectionChanged(event -> {
            if (dropLogTab.isSelected() && !hasLogLoaded) {
                System.out.println("UI: 首次切换到退课日志，正在请求数据...");
                courseService.getDropLog();
                hasLogLoaded = true;
            }
        });

        // 4. 加载默认视图的数据
        courseService.getMySelectedCourses();
    }

    @Override
    public void registerToMessageController() {
        // 通过自身的 Service 获取全局 MessageController
        com.vcampus.client.controller.MessageController messageController =
                courseService.getGlobalSocketClient().getMessageController();

        if (messageController != null) {
            messageController.setMyTimetableController(this);
            System.out.println("MyTimetableController 已成功注册到 MessageController。");
        } else {
            System.err.println("严重错误：MyTimetableController 注册失败，无法获取 MessageController 实例！");
        }
    }

    private void setupMyCoursesTable() {
        colCourseIdAndName.setCellValueFactory(new PropertyValueFactory<>("courseIdAndName"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("scheduleInfo"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colType.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCampus.setCellValueFactory(new PropertyValueFactory<>("campus"));
        colConflict.setCellValueFactory(new PropertyValueFactory<>("conflictStatus"));

        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDrop = new Button("退选");
            {
                btnDrop.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                btnDrop.setOnAction(event -> {
                    MyCourse course = getTableView().getItems().get(getIndex());
                    handleDropCourse(course);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER);
                setGraphic(empty ? null : btnDrop);
            }
        });
    }

    /**
     * 由 MessageController 调用，处理服务器返回的"我的课表"数据
     */
    public void handleMyCoursesResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List) {
                List<Course> coursesFromServer = (List<Course>) message.getData();
    
                // 将服务器返回的 Course 列表，转换为适合表格显示的 MyCourse 列表
                List<MyCourse> myCourses = coursesFromServer.stream()
                        .flatMap(course -> course.getSessions().stream().map(session -> {
                            MyCourse row = new MyCourse();
                            row.setCourseIdAndName(course.getCourseId() + "\n" + course.getCourseName());
                            row.setTeacherName(session.getTeacherName());
                            row.setScheduleInfo(session.getScheduleInfo());
                            // ... (设置其他属性)
                            // TODO: 确保 MyCourse DTO 中有这些 setter 方法
                            row.setCredits(String.valueOf(course.getCredits()));
                            row.setCourseType(course.getCourseType());
                            row.setCategory(course.getCategory());
                            // ⭐ 添加校区设置的调试日志
                            String campus = course.getCampus();
                            System.out.println("设置校区: 原始值='" + campus + "', " +
                                    "是否为null=" + (campus == null) +
                                    ", 是否为空字符串=" + (campus != null && campus.isEmpty()));
    
                            // ⭐ 如果校区为空，设置默认值
                            if (campus == null || campus.trim().isEmpty()) {
                                campus = "九龙湖"; // 设置默认校区
                                System.out.println("校区为空，设置默认值：九龙湖");
                            }
                            row.setCampus(campus);
                            row.setConflictStatus("不冲突");
                            row.setSessionId(session.getSessionId()); // 关键！保存 sessionId 用于退课
                            return row;
                        }))
                        .collect(Collectors.toList());
    
                myCoursesData.setAll(myCourses); // 修复：将数据设置到与表格绑定的列表中
            }
        });
    }

    /**
     * 处理“退选”按钮的点击事件
     */
    private void handleDropCourse(MyCourse course) {
        System.out.println("请求退选: " + course.getSessionId());
        // 直接调用 Service 发送退课请求
        courseService.dropCourse(course.getSessionId());
        // 后续的刷新逻辑由 AcademicController 统一处理
    }

    /**
     * ⭐ 4. 处理响应：创建新的 handleDropLogResponse 方法
     * 由 MessageController 调用，用于接收服务器返回的日志数据并填充到表格中。
     */
    public void handleDropLogResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<DropLogEntry> logs = (List<DropLogEntry>) message.getData();
                System.out.println("UI: 收到退课日志数据 " + logs.size() + " 条，正在填充表格。");
                dropLogData.setAll(logs);
            } else {
                System.err.println("获取退课日志失败: " + message.getMessage());
            }
        });
    }

    /**
     * ⭐ 新增：设置退课日志表格的列
     */
    private void setupDropLogTable() {
        colLogCourseIdAndName.setCellValueFactory(new PropertyValueFactory<>("courseIdAndName"));
        colLogTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        colLogType.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        colLogCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colLogOperator.setCellValueFactory(new PropertyValueFactory<>("droppedBy"));
        colLogDropType.setCellValueFactory(new PropertyValueFactory<>("dropType"));
        colLogPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));
    }


    /**
     * 设置表格的列
     */
    private void setupTableColumns() {
        // 将每一列的 cellValueFactory 绑定到 MyCourse DTO 的对应属性上
        colCourseIdAndName.setCellValueFactory(new PropertyValueFactory<>("courseIdAndName"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("scheduleInfo"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colType.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCampus.setCellValueFactory(new PropertyValueFactory<>("campus"));
        colConflict.setCellValueFactory(new PropertyValueFactory<>("conflictStatus"));

        // 特殊处理“操作”列，为每一行动态创建“退选”按钮
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDrop = new Button("退选");
            {
                // 为按钮设置醒目的红底白字样式
                btnDrop.setStyle(
                        "-fx-background-color: #f44336; " + // 红色背景
                                "-fx-text-fill: white; " +           // 白色文字
                                "-fx-font-weight: bold;"             // 字体加粗
                );

                btnDrop.setOnAction(event -> {
                    MyCourse course = getTableView().getItems().get(getIndex());
                    handleDropCourse(course);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER); // 按钮居中
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDrop);
                }
            }
        });
    }
}