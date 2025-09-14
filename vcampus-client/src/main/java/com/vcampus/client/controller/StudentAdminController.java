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
import javafx.scene.layout.GridPane;

import java.util.List;

public class StudentAdminController implements IClientController {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private TableView<Student> studentTable;

    @FXML private TableColumn<Student, String> colUserId;
    @FXML private TableColumn<Student, String> colStudentId;
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
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colCollege.setCellValueFactory(new PropertyValueFactory<>("college"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 操作列 - 查看详细 + 修改
        colAction.setCellFactory(param -> new TableCell<Student, Void>() {
            private final Button btnDetail = new Button("查看详细");
            private final Button btnEdit = new Button("修改");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(5, btnDetail, btnEdit);

            {
                btnDetail.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showStudentDetail(student);
                });

                btnEdit.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    showEditDialog(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
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

    /** 查看学生详细信息 */
    private void showStudentDetail(Student s) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("学生详细信息");
        alert.setHeaderText("学号：" + s.getStudentId() + " - " + s.getName());
        alert.setContentText(
                "用户ID: " + s.getUserId() + "\n" +
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

    /** 修改学生信息对话框 */
    private void showEditDialog(Student s) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("修改学生信息");
        dialog.setHeaderText("修改学生: " + s.getStudentId() + " - " + s.getName());

        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField(s.getName());
        TextField genderField = new TextField(s.getGender());
        TextField collegeField = new TextField(s.getCollege());
        TextField majorField = new TextField(s.getMajor());
        TextField gradeField = new TextField(String.valueOf(s.getGrade()));

        grid.add(new Label("姓名:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("性别:"), 0, 1); grid.add(genderField, 1, 1);
        grid.add(new Label("学院:"), 0, 2); grid.add(collegeField, 1, 2);
        grid.add(new Label("专业:"), 0, 3); grid.add(majorField, 1, 3);
        grid.add(new Label("年级:"), 0, 4); grid.add(gradeField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                s.setName(nameField.getText());
                s.setGender(genderField.getText());
                s.setCollege(collegeField.getText());
                s.setMajor(majorField.getText());
                try {
                    s.setGrade(Integer.parseInt(gradeField.getText()));
                } catch (NumberFormatException ignored) {}
                return s;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedStudent -> {
            studentAdminService.updateStudent(updatedStudent);
            studentTable.refresh();
        });
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

    /** 处理服务端返回的更新结果 */
    public void handleUpdateStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("更新成功", message.getMessage());
                loadAllStudent();
            } else {
                showAlert("更新失败", message.getMessage());
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
