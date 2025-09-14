package com.vcampus.client.controller;
import com.vcampus.client.MainApp;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.MyCourse; // 引入我们为表格定义的DTO
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


import com.vcampus.client.MainApp;
import com.vcampus.client.service.CourseService;
import com.vcampus.common.dto.Course;
import com.vcampus.common.dto.Message;
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

    private final CourseService courseService = new CourseService();
    private final ObservableList<MyCourse> tableData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("“我的课表”模块已加载，正在初始化...");
        registerToMessageController();

        // 1. 绑定表格列到 DTO 属性
        colCourseIdAndName.setCellValueFactory(new PropertyValueFactory<>("courseIdAndName"));
        colTeacher.setCellValueFactory(new PropertyValueFactory<>("teacherName"));
        colSchedule.setCellValueFactory(new PropertyValueFactory<>("scheduleInfo"));
        colCredits.setCellValueFactory(new PropertyValueFactory<>("credits"));
        colType.setCellValueFactory(new PropertyValueFactory<>("courseType"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCampus.setCellValueFactory(new PropertyValueFactory<>("campus"));
        colConflict.setCellValueFactory(new PropertyValueFactory<>("conflictStatus"));

        // ... (为其他列也这样绑定)

        // 2. ⭐ 核心修改：为“操作”列的退选按钮设置样式
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDrop = new Button("退选");
            {
                // ⭐ 在这里为按钮直接添加红底白字的样式
                btnDrop.setStyle(
                        "-fx-background-color: #f44336; " + // 红色背景
                                "-fx-text-fill: white; " +           // 白色文字
                                "-fx-font-weight: bold; " +          // 字体加粗
                                "-fx-background-radius: 4;"         // 圆角
                );

                btnDrop.setOnAction(event -> {
                    MyCourse course = getTableView().getItems().get(getIndex());
                    handleDropCourse(course);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDrop);
                }
            }
        });

        // 3. 将表格的数据源设置为我们的 ObservableList
        myCoursesTable.setItems(tableData);

        // 4. 发起数据请求
        courseService.getMySelectedCourses();
    }

    @Override
    public void registerToMessageController() {
        // 通过自身的 Service 获取全局 MessageController
        com.vcampus.client.controller.MessageController messageController =
                courseService.getGlobalSocketClient().getMessageController();

        if (messageController != null) {
            messageController.setMyTimetableController(this);
            System.out.println("ShopController 已成功注册到 MessageController。");
        } else {
            System.err.println("严重错误：ShopController 注册失败，无法获取 MessageController 实例！");
        }
    }

    /**
     * 由 MessageController 调用，处理服务器返回的“我的课表”数据
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
                             row.setCampus(course.getCampus());
                             row.setConflictStatus("不冲突");
                            row.setSessionId(session.getSessionId()); // 关键！保存 sessionId 用于退课
                            return row;
                        }))
                        .collect(Collectors.toList());

                tableData.setAll(myCourses);
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
