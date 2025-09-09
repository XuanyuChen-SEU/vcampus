package com.vcampus.client.controller;

import com.vcampus.common.dto.ClassSession;
import com.vcampus.common.dto.Course;
import com.vcampus.common.enums.CourseStatus;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import com.vcampus.common.dto.Message;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AcademicController implements IClientController {

    //我先明确一下,这个controller是处理ui前端变化的，也是接收服务端发来的信息并依此改变ui的
    //然后message controller是一个中转站（相当于线桥中的分配管理器）所有的controller注册在他这里之后
    //它就能明确哪个分配器，同时他也是靠近服务器的人之一（另外一人是service)，因为它封装了Socket(处理消息接收），并依此
    //联系各个controller和服务层
    //当然各个controller封装了service，利用service进行消息的发送，这里服务层同样也是靠近服务器的人之一，
    //这里总结一下：客户端发送：ui->各个controller->service------>socket-------->服务端
    //            客户端接收：ui<-各个controller<-message controller<-------socket<--------服务端

    //现在好像我明白了这是一个三明治结构，而这个是对外层进行操控的一个组件，他不是整体的控制器，他是局部的控制器

    /**
     * FXML 中定义的根内容面板，用于放置所有UI控件。
     */
    @FXML
    private Pane contentPane;

    @FXML private Button timetableButton;
    @FXML private Button selectCoursesButton;

    // =================================================================
    //
    // UI 控件声明区
    //
    // 在这里使用 @FXML 声明 FXML 文件中定义的控件。
    // 变量名必须与 FXML 文件中的 fx:id 完全一致。
    //
    // 示例:
    //查看我的课表的按钮
    @FXML
    private Button viewCourseTableButton;

    //所有选课课表
    @FXML
    private TableView<Course> CoursesTable;

    //我的课表
    @FXML
    private TableView<Course> myCoursesTable;

    //查看所有选课课表的按钮
    @FXML
    private Button viewAllCoursesButton;

    @FXML
    private TableColumn<Course, String> courseIdColumn;

    @FXML
    private TableColumn<Course, String> courseNameColumn;

    @FXML
    private TableColumn<Course, String> teacherColumn;

    @FXML
    private TableColumn<Course, Integer> capacityColumn;

    @FXML
    private TableColumn<Course, Integer> enrolledColumn;

    @FXML
    private TableColumn<Course, Void> actionColumn;

    @FXML
    private TableColumn<Course, String> classroomColumn;

    @FXML
    private TableColumn<Course, String> timeSlotColumn;


    /**
     * 初始化方法，在视图加载后自动执行。
     * 这是模块的逻辑入口点，适合执行数据加载等初始化任务。
     */
    @FXML
    public void initialize() {
        System.out.println("教务模块已加载。");
        handleShowSelectCourses(null);//默认进行选课画面
        // 示例：调用服务层获取数据并更新UI
        // setupBookTable();
        // loadAllBooks();
    }

    @FXML
    private void handleShowTimetable(ActionEvent event) {
        //其中一个按钮，代表着我的课表
        System.out.println("切换到'我的课表'视图");
        // loadView("/com/vcampus/client/view/TimetableView.fxml");
        updateButtonStyles(timetableButton);
        contentPane.getChildren().clear(); // 临时清空
    }

    //这里拉取课表应该是请求服务器，服务器从数据库中拉出来
    @FXML
    private void handleShowSelectCourses(ActionEvent event) {
        System.out.println("切换到'选课'视图");
        updateButtonStyles(selectCoursesButton);
        // ⭐ 串联的入口：加载“中间层” CourseCard 列表
        loadCourseCards();//这个应该只是接口，具体实现应该在service层
    }


    //必须做的一步：注册到message controller(线桥），要不然无法住转发
    @Override
    public void registerToMessageController() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'registerToMessageController'");
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
    //
    /**
     * 核心方法：加载所有课程卡片到主内容区
     */
    private void loadCourseCards() {
        // TODO: 在这里通过 Service 从服务器获取真实的课程列表
        List<Course> courses = createMockCourses(); // 使用模拟数据，这里自己测试一下吧

        try {
            contentPane.getChildren().clear();

            VBox courseListContainer = new VBox(); // 垂直容器，用于放置所有课程行
            courseListContainer.setStyle("-fx-background-color: white;");

            // 遍历课程数据，为每一门课创建一个 CourseCard 实例
            for (Course course : courses) {
                // 1. 加载“中间层” FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/academic/CourseCard.fxml"));
                Node courseCardNode = loader.load();

                // 2. 获取“中间层”的 Controller
                CourseCardController courseCardController = loader.getController();

                // 3. 将数据和“最终的回调函数”传递给它
                courseCardController.setData(course, this::handleFinalSelectAction);

                // 4. 将加载好的课程行 Node 添加到 VBox 容器中
                courseListContainer.getChildren().add(courseCardNode);
            }

            // 为了让长列表可以滚动，将 VBox 放入一个 ScrollPane
            ScrollPane scrollPane = new ScrollPane(courseListContainer);
            scrollPane.setFitToWidth(true); // 宽度自适应
            scrollPane.setStyle("-fx-background-color: transparent;"); // 背景透明

            // 5. 将滚动面板最终放入主内容区
            contentPane.getChildren().add(scrollPane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这是最顶层的回调方法，负责处理来自最内层教学班卡片的选课/退课请求。
     * @param session 被操作的教学班对象
     */
    private void handleFinalSelectAction(ClassSession session) {
        System.out.println("✅ 总指挥官收到命令: " + (session.isSelectedByStudent() ? "退选" : "选择") + " 教学班 " + session.getSessionId());

        // TODO: 在这里调用你的网络服务(Service)，向服务器发送请求
        // e.g., boolean success = courseService.select(session.getSessionId());
        //这里需要我再看一下网络服务现在怎么样了

        // 操作完成后，为了同步最新状态，刷新整个课程列表
        System.out.println("正在刷新课程列表以同步最新状态...");
        loadCourseCards();
    }


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

    //更新按钮风格（换上不同css文件，然后就可实现不同的按钮🔘，可能有一个可以点击，另一个点击不了，然后有一个点击不了，另一个可以点击）
    private void updateButtonStyles(Button activeButton) {
//        if (currentActiveButton != null) {
//            currentActiveButton.getStyleClass().remove("active-tab-button");
//        }
//        activeButton.getStyleClass().add("active-tab-button");
//        currentActiveButton = activeButton;
    }

    // 小测一手数据
    // 模拟数据，请替换为您的真实数据来源
    private List<Course> createMockCourses() {
        List<Course> courses = new ArrayList<>();
        List<ClassSession> sessions1 = List.of(
                new ClassSession("S01", "[01] 宋安娜教师", "1-16周 周二 1-2节", 31, 0, false),
                new ClassSession("S02", "[02] 宋安娜教师", "1-16周 周五 6-7节", 31, 0, false)
        );
        courses.add(new Course("B17M0010", "大学英语II", "必修", "外国语学院", CourseStatus.NOT_SELECTED, sessions1));

        List<ClassSession> sessions2 = List.of(
                new ClassSession("S03", "[01] 李教授", "1-8周 周一 3-4节", 50, 50, false)
        );
        courses.add(new Course("B08M3000", "计算机网络", "必修", "计算机学院", CourseStatus.FULL, sessions2));

        List<ClassSession> sessions3 = List.of(
                new ClassSession("S04", "[01] 赵老师", "1-16周 周三 1-2节", 40, 39, true)
        );
        courses.add(new Course("B07M1010", "数学分析", "必修", "理学院", CourseStatus.SELECTED, sessions3));
        return courses;
    }
}
