package com.vcampus.client.controller;

import com.vcampus.client.service.StudentAdminService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.enums.ActionType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class StudentAdminController implements IClientController {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private TableView<Student> studentTable;

    @FXML private TableColumn<Student, String> colUserId;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colCardId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colGender;
    @FXML private TableColumn<Student, String> colCollege;
    @FXML private TableColumn<Student, String> colMajor;
    @FXML private TableColumn<Student, Integer> colGrade;
    @FXML private TableColumn<Student, Void> colAction;

    private final StudentAdminService studentAdminService = new StudentAdminService();
    private final ObservableList<Student> studentData = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredData = new FilteredList<>(studentData, s -> true);

    @FXML
    public void initialize() {
        registerToMessageController();

        // 绑定表格列
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colCardId.setCellValueFactory(new PropertyValueFactory<>("cardId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colCollege.setCellValueFactory(new PropertyValueFactory<>("college"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 操作列
        colAction.setCellFactory(param -> new TableCell<Student, Void>() {
            private final Button btn = new Button("查看详细");

            {
                btn.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showStudentDetail(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        studentTable.setItems(filteredData);

        // 搜索事件
        searchButton.setOnAction(event -> handleSearch());

        // 初始加载所有学生
        loadAllStudent();
    }

    private void loadAllStudent() {
        studentAdminService.getAllStudents();
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            filteredData.setPredicate(s -> true); // 显示全部
        } else {
            filteredData.setPredicate(s ->
                    s.getStudentId().contains(keyword) || s.getName().contains(keyword)
            );
        }
    }

    private void showStudentDetail(Student s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("学生详细信息");
        alert.setHeaderText("学号：" + s.getStudentId() + " - " + s.getName());
        alert.setContentText(
                "用户ID: " + s.getUserId() + "\n" +
                        "一卡通号: " + s.getCardId() + "\n" +
                        "姓名: " + s.getName() + "\n" +
                        "性别: " + s.getGender() + "\n" +
                        "学院: " + s.getCollege() + "\n" +
                        "专业: " + s.getMajor() + "\n" +
                        "年级: " + s.getGrade() + "\n" +
                        "出生日期: " + s.getBirth_date() + "\n" +
                        "籍贯: " + s.getNative_place() + "\n" +
                        "政治面貌: " + s.getPolitics_status() + "\n" +
                        "学籍状态: " + s.getStudent_status() + "\n\n" +
                        "联系方式:\n" +
                        "  手机号: " + s.getPhone() + "\n" +
                        "  邮箱: " + s.getEmail() + "\n" +
                        "  宿舍地址: " + s.getDormAddress() + "\n\n" +
                        "父亲信息:\n" +
                        "  姓名: " + s.getFatherName() + "\n" +
                        "  手机: " + s.getFatherPhone() + "\n" +
                        "  政治面貌: " + s.getFatherPoliticsStatus() + "\n" +
                        "  工作单位: " + s.getFatherWorkUnit() + "\n\n" +
                        "母亲信息:\n" +
                        "  姓名: " + s.getMotherName() + "\n" +
                        "  手机: " + s.getMotherPhone() + "\n" +
                        "  政治面貌: " + s.getMotherPoliticsStatus() + "\n" +
                        "  工作单位: " + s.getMotherWorkUnit()
        );
        alert.showAndWait();
    }

    /** 处理服务端返回的全部学生信息 */
    public void handleAllStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List<?> list) {
                studentData.clear();
                for (Object obj : list) {
                    if (obj instanceof Student s) studentData.add(s);
                }
            } else {
                showAlert("获取所有学生信息失败", message.getMessage());
            }
        });
    }

    /** 处理服务端返回的搜索结果学生信息 */
    public void handleSearchStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List<?> list) {
                studentData.clear();
                for (Object obj : list) {
                    if (obj instanceof Student s) studentData.add(s);
                }
            } else {
                showAlert("搜索学生信息失败", message.getMessage());
            }
        });
    }

    /** 处理服务端返回的单个学生详细信息 */
    public void handleInfoStudentAdminResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof Student s) {
                showStudentDetail(s);
            } else {
                showAlert("学生详细信息查询失败", message.getMessage());
            }
        });
    }

    @Override
    public void registerToMessageController() {
        if (studentAdminService.getGlobalSocketClient() != null &&
                studentAdminService.getGlobalSocketClient().getMessageController() != null) {
            studentAdminService.getGlobalSocketClient().getMessageController().setStudentAdminController(this);
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
