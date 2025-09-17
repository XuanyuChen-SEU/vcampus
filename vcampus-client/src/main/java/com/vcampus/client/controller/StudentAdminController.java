package com.vcampus.client.controller;

import com.vcampus.client.service.StudentAdminService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.StudentLeaveApplication;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StudentAdminController implements IClientController {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private TableView<Student> studentTable;
    @FXML private Button btnAdjustStatus;

    @FXML private TableColumn<Student, Boolean> colSelect;
    @FXML private TableColumn<Student, String> colUserId;
    @FXML private TableColumn<Student, String> colStudentId;
    @FXML private TableColumn<Student, String> colName;
    @FXML private TableColumn<Student, String> colGender;
    @FXML private TableColumn<Student, String> colCollege;
    @FXML private TableColumn<Student, String> colMajor;
    @FXML private TableColumn<Student, Integer> colGrade;
    @FXML private TableColumn<Student, String> colStudentStatus;
    @FXML private TableColumn<Student, Void> colAction;
    @FXML private Button btnSelectAll; // 新增：全选/全不选按钮
    @FXML private Button btnStudentList;       // 学生列表
    @FXML private Button btnApplicationList;   // 申请列表
    @FXML private TableView<StudentLeaveApplication> applicationTable;
    @FXML private TableColumn<StudentLeaveApplication, String> colAppStudentId;
    @FXML private TableColumn<StudentLeaveApplication, String> colAppName;
    @FXML private TableColumn<StudentLeaveApplication, String> colAppReason;
    @FXML private TableColumn<StudentLeaveApplication, String> colAppStatus;
    @FXML private TableColumn<StudentLeaveApplication, Void> colAppAction;


    private final StudentAdminService studentAdminService = new StudentAdminService();
    private final ObservableList<Student> studentData = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredData = new FilteredList<>(studentData, s -> true);
    private final ObservableList<StudentLeaveApplication> applicationData = FXCollections.observableArrayList();

    // 存储筛选选项
    private final Set<String> selectedGrades = new HashSet<>();
    private final Set<String> selectedMajors = new HashSet<>();
    private final Set<String> selectedStatuses = new HashSet<>();
    private boolean allSelected = false; // 当前全选状态

    @FXML
    public void initialize() {
        registerToMessageController();

        // 列绑定
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colCollege.setCellValueFactory(new PropertyValueFactory<>("college"));
        colMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colStudentStatus.setCellValueFactory(new PropertyValueFactory<>("student_status"));
        colAppStudentId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colAppName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colAppReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colAppStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 添加多选筛选按钮
        addFilterToGradeColumn();
        addFilterToMajorColumn();
        addFilterToStudentStatusColumn();

        studentTable.setPlaceholder(new Label("暂无学生数据"));

        applicationTable.setItems(applicationData);
        applicationTable.setPlaceholder(new Label("暂无申请数据"));
        applicationTable.setVisible(false); // 默认隐藏

        // 自定义单元格渲染
        colStudentStatus.setCellFactory(column -> new TableCell<Student, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "在读" -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        case "休学" -> setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                        case "毕业" -> setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });
        // 操作列
        colAction.setCellFactory(param -> new TableCell<Student, Void>() {
            private final Button btnDetail = new Button("查看详细");
            private final Button btnEdit = new Button("修改");
            private final HBox box = new HBox(6, btnDetail, btnEdit);

            {
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

        studentTable.setItems(filteredData);
        studentTable.getSelectionModel().setCellSelectionEnabled(false);
        studentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 搜索按钮 & 回车
        searchButton.setOnAction(event -> updateFilter());
        searchField.setOnAction(event -> updateFilter());

        btnSelectAll.getStyleClass().add("all-button");
        btnAdjustStatus.getStyleClass().add("status-button");
        btnStudentList.getStyleClass().add("studentlist-button");
        btnApplicationList.getStyleClass().add("applicationlist-button");
        // 批量学籍状态调整
        btnAdjustStatus.setOnAction(e -> adjustSelectedStudentStatus());

        // 添加选择列
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        studentTable.setEditable(true);

        // 全选/全不选按钮逻辑
        btnSelectAll.setText("全选");
        btnSelectAll.setOnAction(e -> {
            allSelected = !allSelected;
            // 当前显示的数据行才操作
            filteredData.forEach(s -> s.setSelected(allSelected));
            btnSelectAll.setText(allSelected ? "全不选" : "全选");
            studentTable.refresh();
        });
        btnStudentList.setOnAction(e -> {
            studentTable.setVisible(true);
            applicationTable.setVisible(false);
        });

        btnApplicationList.setOnAction(e -> {
            studentTable.setVisible(false);
            applicationTable.setVisible(true);
            loadAllApplications(); // 加载申请列表数据
        });
        loadAllStudent();
    }

    private void loadAllStudent() {
        studentAdminService.getAllStudents();
    }

    /** 搜索 + 多选筛选同时生效 */
    private void updateFilter() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        filteredData.setPredicate(s -> {
            // 搜索条件
            boolean matchKeyword = keyword.isEmpty() ||
                    (s.getStudentId() != null && s.getStudentId().toLowerCase().contains(keyword)) ||
                    (s.getName() != null && s.getName().toLowerCase().contains(keyword));

            // 年级筛选
            boolean matchGrade = selectedGrades.isEmpty() || selectedGrades.contains(String.valueOf(s.getGrade()));

            // 专业筛选
            boolean matchMajor = selectedMajors.isEmpty() || (s.getMajor() != null && selectedMajors.contains(s.getMajor()));

            // 学籍状态筛选
            boolean matchStatus = selectedStatuses.isEmpty() ||
                    (s.getStudent_status() != null && selectedStatuses.contains(s.getStudent_status()));

            return matchKeyword && matchGrade && matchMajor && matchStatus;
        });
    }

    /** 年级筛选（多选 Popup） */
    private void addFilterToGradeColumn() {
        colGrade.setText(null);
        Button filterBtn = new Button("🔍");
        filterBtn.getStyleClass().add("filter-button");
        Popup popup = new Popup();
        popup.setAutoHide(true);

        filterBtn.setOnAction(e -> {
            if (!popup.isShowing()) {
                VBox box = new VBox(5);
                box.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: gray;");

                List<String> grades = studentData.stream()
                        .map(s -> String.valueOf(s.getGrade()))
                        .distinct().sorted()
                        .collect(Collectors.toList());

                for (String g : grades) {
                    CheckBox cb = new CheckBox(g);
                    cb.setSelected(selectedGrades.contains(g));
                    cb.selectedProperty().addListener((obs, oldV, newV) -> {
                        if (newV) selectedGrades.add(g);
                        else selectedGrades.remove(g);
                        updateFilter();
                    });
                    box.getChildren().add(cb);
                }

                popup.getContent().clear();
                popup.getContent().add(box);
                popup.show(filterBtn, filterBtn.localToScreen(0, filterBtn.getHeight()).getX(),
                        filterBtn.localToScreen(0, filterBtn.getHeight()).getY());
            } else {
                popup.hide();
            }
        });

        HBox header = new HBox(3, new Label("年级"), filterBtn);
        header.setAlignment(Pos.CENTER);
        colGrade.setGraphic(header);
    }

    /** 专业筛选（多选 Popup） */
    private void addFilterToMajorColumn() {
        colMajor.setText(null);
        Button filterBtn = new Button("🔍");
        filterBtn.getStyleClass().add("filter-button");
        Popup popup = new Popup();
        popup.setAutoHide(true);

        filterBtn.setOnAction(e -> {
            if (!popup.isShowing()) {
                VBox box = new VBox(5);
                box.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: gray;");

                List<String> majors = studentData.stream()
                        .map(Student::getMajor)
                        .filter(m -> m != null)
                        .distinct().sorted()
                        .collect(Collectors.toList());

                for (String m : majors) {
                    CheckBox cb = new CheckBox(m);
                    cb.setSelected(selectedMajors.contains(m));
                    cb.selectedProperty().addListener((obs, oldV, newV) -> {
                        if (newV) selectedMajors.add(m);
                        else selectedMajors.remove(m);
                        updateFilter();
                    });
                    box.getChildren().add(cb);
                }

                popup.getContent().clear();
                popup.getContent().add(box);
                popup.show(filterBtn, filterBtn.localToScreen(0, filterBtn.getHeight()).getX(),
                        filterBtn.localToScreen(0, filterBtn.getHeight()).getY());
            } else {
                popup.hide();
            }
        });

        HBox header = new HBox(3, new Label("专业"), filterBtn);
        header.setAlignment(Pos.CENTER);
        colMajor.setGraphic(header);
    }

    /** 学籍状态筛选（多选 Popup） */
    private void addFilterToStudentStatusColumn() {
        colStudentStatus.setText(null);
        Button filterBtn = new Button("🔍");
        filterBtn.getStyleClass().add("filter-button");
        Popup popup = new Popup();
        popup.setAutoHide(true);

        filterBtn.setOnAction(e -> {
            if (!popup.isShowing()) {
                VBox box = new VBox(5);
                box.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: gray;");

                List<String> statuses = studentData.stream()
                        .map(Student::getStudent_status)
                        .filter(st -> st != null)
                        .distinct().sorted()
                        .collect(Collectors.toList());

                for (String st : statuses) {
                    CheckBox cb = new CheckBox(st);
                    cb.setSelected(selectedStatuses.contains(st));
                    cb.selectedProperty().addListener((obs, oldV, newV) -> {
                        if (newV) selectedStatuses.add(st);
                        else selectedStatuses.remove(st);
                        updateFilter();
                    });
                    box.getChildren().add(cb);
                }

                popup.getContent().clear();
                popup.getContent().add(box);
                popup.show(filterBtn, filterBtn.localToScreen(0, filterBtn.getHeight()).getX(),
                        filterBtn.localToScreen(0, filterBtn.getHeight()).getY());
            } else {
                popup.hide();
            }
        });

        HBox header = new HBox(3, new Label("状态"), filterBtn);
        header.setAlignment(Pos.CENTER);
        colStudentStatus.setGraphic(header);
    }


    /** 查看学生详细 */
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
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                s.setUserId(userIdField.getText());
                s.setStudentId(studentIdField.getText());
                s.setName(nameField.getText());
                s.setGender(genderField.getText());
                s.setCollege(collegeField.getText());
                s.setMajor(majorField.getText());
                try { s.setGrade(Integer.parseInt(gradeField.getText())); } catch (NumberFormatException ignored){}
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

    /** 消息处理方法 */
    public void handleAllStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List<?> list) {
                studentData.clear();
                for (Object obj : list) if (obj instanceof Student s) studentData.add(s);
            } else {
                showAlert("获取所有学生信息失败", message.getMessage());
            }
        });
    }

    public void handleSearchStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List<?> list) {
                studentData.clear();
                for (Object obj : list) if (obj instanceof Student s) studentData.add(s);
            } else showAlert("搜索学生信息失败", message.getMessage());
        });
    }

    public void handleInfoStudentAdminResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof Student s) showStudentDetail(s);
            else showAlert("学生详细信息查询失败", message.getMessage());
        });
    }

    public void handleUpdateStudentResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("更新成功", message.getMessage());
                loadAllStudent();
            } else showAlert("更新失败", message.getMessage());
        });
    }

    @Override
    public void registerToMessageController() {
        if (studentAdminService.getGlobalSocketClient() != null &&
                studentAdminService.getGlobalSocketClient().getMessageController() != null) {
            studentAdminService.getGlobalSocketClient().getMessageController().setStudentAdminController(this);
        }
    }

    private void adjustSelectedStudentStatus() {
        // 只获取当前筛选显示的学生
        List<Student> selectedStudents = filteredData.stream()
                .filter(Student::isSelected)
                .collect(Collectors.toList());

        if (selectedStudents.isEmpty()) {
            showAlert("提示", "请先选择要调整的学生！");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("在读", "在读", "休学", "毕业");
        dialog.setTitle("批量调整学籍状态");
        dialog.setHeaderText("请选择新的学籍状态");
        dialog.setContentText("学籍状态：");

        dialog.showAndWait().ifPresent(status -> {
            for (Student s : selectedStudents) {
                s.setStudent_status(status);
                studentAdminService.updateStudent(s);
            }
            studentTable.refresh();
            showAlert("成功", "已将 " + selectedStudents.size() + " 名学生的学籍状态调整为：" + status);
        });
    }

    private void loadAllApplications() {
        studentAdminService.getAllApplications(); // 假设服务端提供获取申请列表的方法
    }

    public void handleAllApplicationsResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess() && message.getData() instanceof List<?> list) {
                applicationData.clear();
                for (Object obj : list) if (obj instanceof StudentLeaveApplication app) applicationData.add(app);
            } else {
                showAlert("获取申请列表失败", message.getMessage());
            }
        });
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
