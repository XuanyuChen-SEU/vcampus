package com.vcampus.client.controller;

import com.vcampus.client.service.StudentAdminService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

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

        // 表格列绑定（属性名请与 Student 类的 getter 名称匹配）
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colCollege.setCellValueFactory(new PropertyValueFactory<>("college"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 表格无数据时的占位提示
        studentTable.setPlaceholder(new Label("暂无学生数据"));

        // 操作列 - 查看详细 + 修改（带索引边界保护）
        colAction.setCellFactory(param -> new TableCell<Student, Void>() {
            private final Button btnDetail = new Button("查看详细");
            private final Button btnEdit = new Button("修改");
            private final HBox box = new HBox(6, btnDetail, btnEdit);

            {
                // 给按钮添加样式类（样式在 CSS 中定义）
                btnDetail.getStyleClass().add("clear-button");
                btnEdit.getStyleClass().add("create-button");

                btnDetail.setOnAction(event -> {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) return;
                    Student student = getTableView().getItems().get(idx);
                    showStudentDetail(student);
                });

                btnEdit.setOnAction(event -> {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) return;
                    Student student = getTableView().getItems().get(idx);
                    showEditDialog(student);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        // 绑定数据源
        studentTable.setItems(filteredData);

        // 搜索按钮与回车触发
        searchButton.setOnAction(event -> handleSearch());
        searchField.setOnAction(event -> handleSearch());

        // 初始加载所有学生
        loadAllStudent();
    }

    private void loadAllStudent() {
        studentAdminService.getAllStudents();
    }

    private void handleSearch() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();
        if (keyword.isEmpty()) {
            filteredData.setPredicate(s -> true);
        } else {
            filteredData.setPredicate(s ->
                    (s.getStudentId() != null && s.getStudentId().contains(keyword)) ||
                            (s.getName() != null && s.getName().contains(keyword))
            );
        }
    }

    /** 查看学生详细信息 */
    private void showStudentDetail(Student s) {
        if (s == null) return;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("学生详细信息");
        alert.setHeaderText("学号：" + s.getStudentId() + " - " + s.getName());
        StringBuilder sb = new StringBuilder();
        sb.append("用户ID: ").append(s.getUserId()).append("\n")
                .append("姓名: ").append(s.getName()).append("\n")
                .append("性别: ").append(s.getGender()).append("\n")
                .append("学院: ").append(s.getCollege()).append("\n")
                .append("专业: ").append(s.getMajor()).append("\n")
                .append("年级: ").append(s.getGrade()).append("\n")
                .append("出生日期: ").append(s.getBirth_date()).append("\n")
                .append("籍贯: ").append(s.getNative_place()).append("\n")
                .append("政治面貌: ").append(s.getPolitics_status()).append("\n")
                .append("学籍状态: ").append(s.getStudent_status()).append("\n\n")
                .append("联系方式:\n  手机号: ").append(s.getPhone()).append("\n  邮箱: ").append(s.getEmail())
                .append("\n  宿舍地址: ").append(s.getDormAddress()).append("\n\n")
                .append("父亲信息:\n  姓名: ").append(s.getFatherName()).append("\n  手机: ").append(s.getFatherPhone())
                .append("\n  政治面貌: ").append(s.getFatherPoliticsStatus()).append("\n  工作单位: ").append(s.getFatherWorkUnit())
                .append("\n\n母亲信息:\n  姓名: ").append(s.getMotherName()).append("\n  手机: ").append(s.getMotherPhone())
                .append("\n  政治面貌: ").append(s.getMotherPoliticsStatus()).append("\n  工作单位: ").append(s.getMotherWorkUnit());
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /** 修改学生信息对话框 */
    private void showEditDialog(Student s) {
        if (s == null) return;

        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("修改学生信息");
        dialog.setHeaderText("修改学生: " + s.getStudentId() + " - " + s.getName());

        ButtonType saveButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 创建输入框
        TextField userIdField = new TextField(s.getUserId());
        TextField studentIdField = new TextField(s.getStudentId());
        TextField nameField = new TextField(s.getName());
        TextField genderField = new TextField(s.getGender());
        TextField collegeField = new TextField(s.getCollege());
        TextField majorField = new TextField(s.getMajor());
        TextField gradeField = new TextField(String.valueOf(s.getGrade()));
        TextField birthDateField = new TextField(s.getBirth_date());
        TextField nativePlaceField = new TextField(s.getNative_place());
        TextField politicsStatusField = new TextField(s.getPolitics_status());
        TextField studentStatusField = new TextField(s.getStudent_status());

        // 放到网格
        int row = 0;
        grid.add(new Label("用户ID:"), 0, row); grid.add(userIdField, 1, row++);
        grid.add(new Label("学号:"), 0, row); grid.add(studentIdField, 1, row++);
        grid.add(new Label("姓名:"), 0, row); grid.add(nameField, 1, row++);
        grid.add(new Label("性别:"), 0, row); grid.add(genderField, 1, row++);
        grid.add(new Label("学院:"), 0, row); grid.add(collegeField, 1, row++);
        grid.add(new Label("专业:"), 0, row); grid.add(majorField, 1, row++);
        grid.add(new Label("年级:"), 0, row); grid.add(gradeField, 1, row++);
        grid.add(new Label("出生日期:"), 0, row); grid.add(birthDateField, 1, row++);
        grid.add(new Label("籍贯:"), 0, row); grid.add(nativePlaceField, 1, row++);
        grid.add(new Label("政治面貌:"), 0, row); grid.add(politicsStatusField, 1, row++);
        grid.add(new Label("学籍状态:"), 0, row); grid.add(studentStatusField, 1, row++);

        dialog.getDialogPane().setContent(grid);

        // 保存按钮逻辑
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    s.setUserId(userIdField.getText());
                    s.setStudentId(studentIdField.getText());
                } catch (IllegalArgumentException ex) {
                    // 如果用户输入非法 ID，这里直接报错并不关闭窗口
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                    return null;
                }
                s.setName(nameField.getText());
                s.setGender(genderField.getText());
                s.setCollege(collegeField.getText());
                s.setMajor(majorField.getText());
                try {
                    s.setGrade(Integer.parseInt(gradeField.getText()));
                } catch (NumberFormatException ignored) {}
                s.setBirth_date(birthDateField.getText());
                s.setNative_place(nativePlaceField.getText());
                s.setPolitics_status(politicsStatusField.getText());
                s.setStudent_status(studentStatusField.getText());
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
