package com.vcampus.client.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vcampus.client.service.StudentAdminService;
import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.common.dto.Teacher;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class StudentAdminController implements IClientController {

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private TableView<Student> studentTable;
    @FXML private Button btnAdjustStatus;
    @FXML private Button btnTeacherList;


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
    @FXML private Button btnRefreshList; // 刷新按钮
    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, Boolean> colTeacherSelect;
    @FXML private TableColumn<Teacher, String> colTeacherUserId;
    @FXML private TableColumn<Teacher, String> colTeacherName;
    @FXML private TableColumn<Teacher, String> colTeacherGender;
    @FXML private TableColumn<Teacher, String> colTeacherCollege;
    @FXML private TableColumn<Teacher, String> colTeacherDepartment;
    @FXML private TableColumn<Teacher, String> colTeacherTitle;
    @FXML private TableColumn<Teacher, String> colTeacherPhone;
    @FXML private TableColumn<Teacher, String> colTeacherEmail;
    @FXML private TableColumn<Teacher, String> colTeacherOffice;
    @FXML private TableColumn<Teacher, Void> colTeacherAction;

    private final ObservableList<Teacher> teacherData = FXCollections.observableArrayList();
    private final FilteredList<Teacher> filteredTeacherData = new FilteredList<>(teacherData, t -> true);
    private final Set<String> selectedDepartments = new HashSet<>();
    private final Set<String> selectedTitles = new HashSet<>();


    private final StudentAdminService studentAdminService = new StudentAdminService();
    private final ObservableList<Student> studentData = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredData = new FilteredList<>(studentData, s -> true);
    private final ObservableList<StudentLeaveApplication> applicationData = FXCollections.observableArrayList();
    private boolean isBatchUpdating = false;

    // 存储筛选选项
    private final Set<String> selectedGrades = new HashSet<>();
    private final Set<String> selectedMajors = new HashSet<>();
    private final Set<String> selectedStatuses = new HashSet<>();
    private boolean allSelected = false; // 当前全选状态
    private enum CurrentTable { STUDENT, APPLICATION, TEACHER}
    private CurrentTable currentTable = CurrentTable.STUDENT;


    @FXML
    public void initialize() {
        // 注册消息控制器
        registerToMessageController();

        // ========================
        // 1. 学生表列绑定
        // ========================
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

        // ========================
        // 2. 学生表列宽绑定
        // ========================
        colSelect.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.05));
        colUserId.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.07));
        colStudentId.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.10));
        colName.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.08));
        colGender.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.04));
        colCollege.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.10));
        colMajor.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.13));
        colGrade.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.08));
        colStudentStatus.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.10));
        colAction.prefWidthProperty().bind(studentTable.widthProperty().multiply(0.25));

        // ========================
        // 3. 教师表列绑定
        // ========================
        colTeacherUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        colTeacherName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeacherGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colTeacherCollege.setCellValueFactory(new PropertyValueFactory<>("college"));
        colTeacherDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colTeacherTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTeacherPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colTeacherEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTeacherOffice.setCellValueFactory(new PropertyValueFactory<>("office"));

        // ========================
        // 4. 教师表列宽绑定
        // ========================
        colTeacherUserId.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.08));
        colTeacherName.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.12));
        colTeacherCollege.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.12));
        colTeacherGender.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.06));
        colTeacherDepartment.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.12));
        colTeacherTitle.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.10));
        colTeacherPhone.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.10));
        colTeacherEmail.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.10));
        colTeacherOffice.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.10));
        colTeacherAction.prefWidthProperty().bind(teacherTable.widthProperty().multiply(0.10));

        // ========================
        // 5. 申请表列宽绑定
        // ========================
        colAppStudentId.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.15));
        colAppName.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.15));
        colAppReason.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.30));
        colAppStatus.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.15));
        colAppAction.prefWidthProperty().bind(applicationTable.widthProperty().multiply(0.25));

        // ========================
        // 6. 添加表格数据
        // ========================
        studentTable.setItems(filteredData);
        teacherTable.setItems(filteredTeacherData);
        applicationTable.setItems(applicationData);

        studentTable.setPlaceholder(new Label("暂无学生数据"));
        teacherTable.setPlaceholder(new Label("暂无教师数据"));
        applicationTable.setPlaceholder(new Label("暂无申请数据"));

        applicationTable.setVisible(false); // 默认隐藏

        // ========================
        // 7. 学生表单元格自定义渲染
        // ========================
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

        // ========================
        // 8. 学生表操作列
        // ========================
        colAction.setCellFactory(param -> new TableCell<Student, Void>() {
            private final Button btnDetail = new Button("查看详细");
            private final Button btnEdit = new Button("修改");
            private final HBox box = new HBox(6, btnDetail, btnEdit);

            {
                btnDetail.getStyleClass().add("clear-button");
                btnEdit.getStyleClass().add("create-button");
                btnDetail.setMaxWidth(Double.MAX_VALUE);
                btnEdit.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(btnDetail, Priority.ALWAYS);
                HBox.setHgrow(btnEdit, Priority.ALWAYS);

                box.widthProperty().addListener((obs, oldW, newW) -> {
                    btnDetail.setPrefWidth(newW.doubleValue() * 0.5 - 3);
                    btnEdit.setPrefWidth(newW.doubleValue() * 0.5 - 3);
                });

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

        // ========================
        // 9. 教师表操作列
        // ========================
        colTeacherAction.setCellFactory(param -> new TableCell<Teacher, Void>() {
            private final Button btnEdit = new Button("修改");

            {
                btnEdit.getStyleClass().add("create-button");
                btnEdit.setMaxWidth(Double.MAX_VALUE);

                btnEdit.setOnAction(event -> {
                    int idx = getIndex();
                    if (idx < 0 || idx >= getTableView().getItems().size()) return;
                    Teacher teacher = getTableView().getItems().get(idx);
                    showEditTeacherDialog(teacher);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEdit);
            }
        });

        // ========================
        // 10. 申请表操作列
        // ========================
        colAppAction.setCellFactory(col -> new TableCell<StudentLeaveApplication, Void>() {
            private final Button approveBtn = new Button("√通过");
            private final Button rejectBtn = new Button("×不通过");
            private final HBox buttonBox = new HBox(8, approveBtn, rejectBtn);

            {
                buttonBox.setAlignment(Pos.CENTER);
                approveBtn.setMaxWidth(Double.MAX_VALUE);
                rejectBtn.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(approveBtn, Priority.ALWAYS);
                HBox.setHgrow(rejectBtn, Priority.ALWAYS);

                buttonBox.widthProperty().addListener((obs, oldW, newW) -> {
                    approveBtn.setPrefWidth(newW.doubleValue() * 0.5 - 4);
                    rejectBtn.setPrefWidth(newW.doubleValue() * 0.5 - 4);
                });

                approveBtn.getStyleClass().add("approve-button");
                rejectBtn.getStyleClass().add("reject-button");

                approveBtn.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e -> handleReject(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    StudentLeaveApplication app = getTableView().getItems().get(getIndex());
                    if ("已通过".equals(app.getStatus())
                            || "未通过".equals(app.getStatus())
                            || "已撤回".equals(app.getStatus())) {
                        setGraphic(null);
                    } else {
                        setGraphic(buttonBox);
                    }
                }
            }
        });

        // ========================
        // 11. 申请状态列颜色显示
        // ========================
        colAppStatus.setCellFactory(column -> new TableCell<StudentLeaveApplication, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "已通过" -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        case "未通过" -> setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        case "待审批" -> setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                        case "已撤回" -> setStyle("-fx-text-fill: #6c757d; -fx-font-weight: bold;");
                        default -> setStyle("");
                    }
                }
            }
        });

        // ========================
        // 12. 搜索框 & 按钮事件
        // ========================
        searchButton.setOnAction(e -> updateFilterBasedOnCurrentTable());
        searchField.setOnAction(e -> updateFilterBasedOnCurrentTable());

        // ========================
        // 13. 按钮样式设置
        // ========================
        btnSelectAll.getStyleClass().add("all-button");
        btnAdjustStatus.getStyleClass().add("status-button");
        btnStudentList.getStyleClass().add("studentlist-button");
        btnApplicationList.getStyleClass().add("applicationlist-button");
        btnRefreshList.getStyleClass().add("refresh-button");
        btnTeacherList.getStyleClass().add("teacherlist-button");

        // ========================
        // 14. 批量学籍状态调整
        // ========================
        btnAdjustStatus.setOnAction(e -> adjustSelectedStudentStatus());

        // ========================
        // 15. 刷新按钮事件
        // ========================
        btnRefreshList.setOnAction(e -> refreshAllData());

        // ========================
        // 16. 学生选择列设置
        // ========================
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));
        colSelect.setEditable(true);
        studentTable.setEditable(true);

        // ========================
        // 17. 全选/全不选按钮逻辑
        // ========================
        btnSelectAll.setText("全选");
        btnSelectAll.setOnAction(e -> {
            allSelected = !allSelected;
            filteredData.forEach(s -> s.setSelected(allSelected));
            btnSelectAll.setText(allSelected ? "全不选" : "全选");
            studentTable.refresh();
        });

        // ========================
        // 18. 切换显示表格按钮事件
        // ========================
        btnTeacherList.setOnAction(e -> {
            currentTable = CurrentTable.TEACHER;
            searchField.setPromptText("按工号/姓名搜索教师");
            searchField.clear();
            studentTable.setVisible(false);
            applicationTable.setVisible(false);
            teacherTable.setVisible(true);
            btnSelectAll.setVisible(false);
            btnAdjustStatus.setVisible(false);
            loadAllTeachers();
        });

        btnStudentList.setOnAction(e -> handleShowStudentList());
        btnApplicationList.setOnAction(e -> handleShowApplicationList());

        // ========================
        // 19. 学生表多选 & 搜索
        // ========================
        studentTable.getSelectionModel().setCellSelectionEnabled(false);
        studentTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // ========================
        // 20. 添加筛选功能
        // ========================
        addFilterToGradeColumn();
        addFilterToMajorColumn();
        addFilterToStudentStatusColumn();
        addFilterToTeacherDepartmentColumn();
        addFilterToTeacherTitleColumn();

        // ========================
        // 21. 加载学生数据
        // ========================
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

        // 政治面貌下拉选择
        ChoiceBox<String> politicsStatusChoice = new ChoiceBox<>();
        politicsStatusChoice.getItems().addAll("群众", "共青团员", "中共党员", "其他");
        politicsStatusChoice.setValue(s.getPolitics_status() != null ? s.getPolitics_status() : "群众");

        // 学籍状态下拉选择
        ChoiceBox<String> studentStatusChoice = new ChoiceBox<>();
        studentStatusChoice.getItems().addAll("在读", "休学", "毕业");
        studentStatusChoice.setValue(s.getStudent_status() != null ? s.getStudent_status() : "在读");

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
        grid.add(new Label("政治面貌:"), 0, row); grid.add(politicsStatusChoice, 1, row++);
        grid.add(new Label("学籍状态:"), 0, row); grid.add(studentStatusChoice, 1, row++);

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
                s.setPolitics_status(politicsStatusChoice.getValue());
                s.setStudent_status(studentStatusChoice.getValue());
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
                if (!isBatchUpdating) { // 仅单个更新时弹窗
                    showAlert("更新成功", message.getMessage());
                }
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

    private void adjustSelectedStudentStatus() {
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
            }

            // 批量更新标记 + 调用服务
            isBatchUpdating = true;
            studentAdminService.updateStudents(selectedStudents);
            isBatchUpdating = false;

            // ✅ 移除手动弹窗（改由回调统一提示）
            // ✅ 移除手动刷新（改由回调统一刷新）

            // 仅重置选择状态
            filteredData.forEach(s -> s.setSelected(false));
            allSelected = false;
            btnSelectAll.setText("全选");
            studentTable.refresh();
        });
    }

    /**
     * 处理学生批量更新（学籍状态调整）的响应
     * 对应 adjustSelectedStudentStatus() 方法发起的批量更新请求
     */
    public void handleBatchUpdateStudentsResponse(Message message) {
        Platform.runLater(() -> {
            // 1. 重置批量更新标记（无论成功失败，都需恢复默认状态）
            isBatchUpdating = false;

            // 2. 根据响应结果处理UI反馈
            if (message.isSuccess()) {
                // 批量更新成功：弹窗提示 + 刷新学生数据（确保表格显示最新状态）
                showAlert("批量更新成功", "已成功调整选中学生的学籍状态");
                loadAllStudent(); // 重新加载所有学生数据，同步最新状态
            } else {
                // 批量更新失败：弹窗提示失败原因，方便排查问题
                String errorMsg = message.getMessage() == null || message.getMessage().isEmpty()
                        ? "未知错误，请检查网络或服务端状态"
                        : message.getMessage();
                showAlert("批量更新失败", "调整学籍状态失败：" + errorMsg);
            }

            // 3. 重置表格选择状态（避免用户后续操作混淆）
            filteredData.forEach(student -> student.setSelected(false));
            allSelected = false; // 重置全选标记
            btnSelectAll.setText("全选"); // 恢复全选按钮文本
            studentTable.refresh(); // 刷新表格，显示最新选择状态
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

    // 审核通过
    private void handleApprove(StudentLeaveApplication app) {
        if (app == null) return;
        app.setStatus("已通过");
        applicationTable.refresh();
        studentAdminService.updateApplicationStatus(app.getApplicationId(), "已通过");
    }

    // 审核不通过
    private void handleReject(StudentLeaveApplication app) {
        if (app == null) return;
        app.setStatus("未通过");
        applicationTable.refresh();
        studentAdminService.updateApplicationStatus(app.getApplicationId(), "未通过");
    }

    private void handleShowStudentList() {
        currentTable = CurrentTable.STUDENT;
        searchField.setPromptText("按学号/姓名搜索学生");
        searchField.clear();

        studentTable.setVisible(true);
        applicationTable.setVisible(false);
        teacherTable.setVisible(false); // ✅ 隐藏教师表

        btnSelectAll.setVisible(true);
        btnAdjustStatus.setVisible(true);

        loadAllStudent(); // 刷新学生表格
    }



    private void handleShowApplicationList() {
        currentTable = CurrentTable.APPLICATION;
        searchField.setPromptText("按学号/姓名搜索申请");
        searchField.clear();

        studentTable.setVisible(false);
        applicationTable.setVisible(true);
        teacherTable.setVisible(false); // ✅ 隐藏教师表

        btnSelectAll.setVisible(false);
        btnAdjustStatus.setVisible(false);

        loadAllApplications(); // 刷新申请表格
    }





    public void handleUpdateStatusResponse(Message message) {
        Platform.runLater(() -> {
            if (message.isSuccess()) {
                showAlert("成功", message.getMessage());

                if (message.getData() instanceof StudentLeaveApplication updatedApp) {
                    // 更新申请表格
                    applicationTable.getItems().stream()
                            .filter(app -> app.getApplicationId().equals(updatedApp.getApplicationId()))
                            .findFirst()
                            .ifPresent(app -> {
                                app.setStatus(updatedApp.getStatus());
                                applicationTable.refresh();
                            });

                    // 自动刷新学生列表或申请列表
                    refreshAllData();
                }
            } else {
                showAlert("失败", message.getMessage());
            }
        });
    }


    /** 刷新学生列表和申请列表 */
    private void refreshAllData() {
        loadAllStudent();
        loadAllApplications();
        loadAllTeachers();
    }

    /** 根据当前表格选择搜索逻辑 */
    /**
     * 统一的搜索/筛选入口 — 根据当前显示的表格 (currentTable) 执行相应筛选逻辑
     */
    private void updateFilterBasedOnCurrentTable() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        switch (currentTable) {
            case STUDENT -> {
                // 学生表的复合筛选（搜索 + 年级/专业/学籍状态多选筛选）
                filteredData.setPredicate(s -> {
                    if (s == null) return false;
                    boolean matchKeyword = keyword.isEmpty()
                            || (s.getStudentId() != null && s.getStudentId().toLowerCase().contains(keyword))
                            || (s.getName() != null && s.getName().toLowerCase().contains(keyword));

                    boolean matchGrade = selectedGrades.isEmpty() || selectedGrades.contains(String.valueOf(s.getGrade()));
                    boolean matchMajor = selectedMajors.isEmpty() || (s.getMajor() != null && selectedMajors.contains(s.getMajor()));
                    boolean matchStatus = selectedStatuses.isEmpty() || (s.getStudent_status() != null && selectedStatuses.contains(s.getStudent_status()));

                    return matchKeyword && matchGrade && matchMajor && matchStatus;
                });
            }
            case APPLICATION -> {
                // 申请表仅按学号/姓名搜索（即时构造 FilteredList 并 setItems）
                String kw = keyword;
                FilteredList<StudentLeaveApplication> filteredApps =
                        new FilteredList<>(applicationData, app -> {
                            if (app == null) return false;
                            if (kw.isEmpty()) return true;
                            String sid = app.getStudentId() == null ? "" : app.getStudentId().toLowerCase();
                            String sname = app.getStudentName() == null ? "" : app.getStudentName().toLowerCase();
                            return sid.contains(kw) || sname.contains(kw);
                        });
                applicationTable.setItems(filteredApps);
            }
            case TEACHER -> {
                // 教师表搜索 + 院系/职称多选筛选
                // Ensure filteredTeacherData exists and teacherData is populated
                filteredTeacherData.setPredicate(t -> {
                    if (t == null) return false;
                    boolean matchKeyword = keyword.isEmpty()
                            || (t.getUserId() != null && t.getUserId().toLowerCase().contains(keyword))
                            || (t.getName() != null && t.getName().toLowerCase().contains(keyword))
                            || (t.getDepartment() != null && t.getDepartment().toLowerCase().contains(keyword));

                    boolean matchDept = selectedDepartments.isEmpty() || (t.getDepartment() != null && selectedDepartments.contains(t.getDepartment()));
                    boolean matchTitle = selectedTitles.isEmpty() || (t.getTitle() != null && selectedTitles.contains(t.getTitle()));

                    return matchKeyword && matchDept && matchTitle;
                });
            }
            default -> {
                // 默认行为：不改变任何过滤
            }
        }
    }


    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // 加载教师数据
    private void loadAllTeachers() {
        studentAdminService.getAllTeachers();
    }


    // 筛选列（院系/职称）
    private void addFilterToTeacherDepartmentColumn() {
        colTeacherDepartment.setText(null);
        Button filterBtn = new Button("🔍");
        Popup popup = new Popup(); popup.setAutoHide(true);

        filterBtn.setOnAction(e -> {
            if (!popup.isShowing()) {
                VBox box = new VBox(5); box.setStyle("-fx-background-color:white;-fx-padding:10;-fx-border-color:gray");
                List<String> depts = teacherData.stream().map(Teacher::getDepartment).distinct().sorted().toList();
                for (String d : depts) {
                    CheckBox cb = new CheckBox(d);
                    cb.setSelected(selectedDepartments.contains(d));
                    cb.selectedProperty().addListener((obs,oldV,newV)->{
                        if(newV) selectedDepartments.add(d); else selectedDepartments.remove(d);
                        updateFilterBasedOnCurrentTable();
                    });
                    box.getChildren().add(cb);
                }
                popup.getContent().clear(); popup.getContent().add(box);
                popup.show(filterBtn, filterBtn.localToScreen(0,filterBtn.getHeight()).getX(),
                        filterBtn.localToScreen(0,filterBtn.getHeight()).getY());
            } else popup.hide();
        });
        HBox header = new HBox(3,new Label("院系"),filterBtn); header.setAlignment(Pos.CENTER);
        colTeacherDepartment.setGraphic(header);
    }

    private void addFilterToTeacherTitleColumn() {
        colTeacherTitle.setText(null);
        Button filterBtn = new Button("🔍");
        Popup popup = new Popup(); popup.setAutoHide(true);

        filterBtn.setOnAction(e -> {
            if (!popup.isShowing()) {
                VBox box = new VBox(5); box.setStyle("-fx-background-color:white;-fx-padding:10;-fx-border-color:gray");
                List<String> titles = teacherData.stream().map(Teacher::getTitle).distinct().sorted().toList();
                for (String t : titles) {
                    CheckBox cb = new CheckBox(t);
                    cb.setSelected(selectedTitles.contains(t));
                    cb.selectedProperty().addListener((obs,oldV,newV)->{
                        if(newV) selectedTitles.add(t); else selectedTitles.remove(t);
                        updateFilterBasedOnCurrentTable();
                    });
                    box.getChildren().add(cb);
                }
                popup.getContent().clear(); popup.getContent().add(box);
                popup.show(filterBtn, filterBtn.localToScreen(0,filterBtn.getHeight()).getX(),
                        filterBtn.localToScreen(0,filterBtn.getHeight()).getY());
            } else popup.hide();
        });
        HBox header = new HBox(3,new Label("职称"),filterBtn); header.setAlignment(Pos.CENTER);
        colTeacherTitle.setGraphic(header);
    }

    /**
     * 处理获取所有教师的响应
     */
    public void handleAllTeachersResponse(Message response) {
        if (response.isSuccess() && response.getData() instanceof List<?>) {
            List<Teacher> teachers = (List<Teacher>) response.getData();
            Platform.runLater(() -> {
                teacherData.setAll(teachers);
            });
        } else {
            System.err.println("获取所有教师失败: " + response.getMessage());
        }
    }

    /**
     * 弹出修改教师信息对话框（支持所有字段）
     */
    private void showEditTeacherDialog(Teacher teacher) {
        Dialog<Teacher> dialog = new Dialog<>();
        dialog.setTitle("修改教师信息");

        // 按钮
        ButtonType updateButtonType = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        // 表单布局
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // 输入字段
        TextField userIdField = new TextField(teacher.getUserId());
        TextField nameField = new TextField(teacher.getName());
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("男", "女");
        genderBox.setValue(teacher.getGender());

        TextField collegeField = new TextField(teacher.getCollege());
        TextField departmentField = new TextField(teacher.getDepartment());
        ComboBox<String> titleBox = new ComboBox<>();
        titleBox.getItems().addAll("讲师", "副教授", "教授");
        titleBox.setValue(teacher.getTitle());

        TextField phoneField = new TextField(teacher.getPhone());
        TextField emailField = new TextField(teacher.getEmail());
        TextField officeField = new TextField(teacher.getOffice());

        // 表单布局
        grid.add(new Label("工号:"), 0, 0);
        grid.add(userIdField, 1, 0);
        grid.add(new Label("姓名:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("性别:"), 0, 2);
        grid.add(genderBox, 1, 2);
        grid.add(new Label("学院:"), 0, 3);
        grid.add(collegeField, 1, 3);
        grid.add(new Label("院系:"), 0, 4);
        grid.add(departmentField, 1, 4);
        grid.add(new Label("职称:"), 0, 5);
        grid.add(titleBox, 1, 5);
        grid.add(new Label("电话:"), 0, 6);
        grid.add(phoneField, 1, 6);
        grid.add(new Label("邮箱:"), 0, 7);
        grid.add(emailField, 1, 7);
        grid.add(new Label("办公室:"), 0, 8);
        grid.add(officeField, 1, 8);

        dialog.getDialogPane().setContent(grid);

        // 结果转换
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                teacher.setUserId(userIdField.getText());
                teacher.setName(nameField.getText());
                teacher.setGender(genderBox.getValue());
                teacher.setCollege(collegeField.getText());
                teacher.setDepartment(departmentField.getText());
                teacher.setTitle(titleBox.getValue());
                teacher.setPhone(phoneField.getText());
                teacher.setEmail(emailField.getText());
                teacher.setOffice(officeField.getText());
                return teacher;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedTeacher -> {
            studentAdminService.sendRequest(updatedTeacher);
        });
    }


//    /**
//     * 处理模糊搜索教师的响应
//     */
//    public void handleSearchTeachersResponse(Message response) {
//        if (response.isSuccess() && response.getData() instanceof List<?>) {
//            List<Teacher> teachers = (List<Teacher>) response.getData();
//            Platform.runLater(() -> {
//                teacherData.setAll(teachers);
//            });
//        } else {
//            System.err.println("模糊搜索教师失败: " + response.getMessage());
//        }
//    }
//
//    /**
//     * 处理获取单个教师信息的响应
//     */
//    public void handleTeacherInfoResponse(Message response) {
//        if (response.isSuccess() && response.getData() instanceof Teacher teacher) {
//            Platform.runLater(() -> {
//                System.out.println("教师详细信息: " + teacher);
//                // TODO: 如果你有单独的详情面板，可以在这里更新
//            });
//        } else {
//            System.err.println("获取教师信息失败: " + response.getMessage());
//        }
//    }
//
    /**
     * 处理更新单个教师信息的响应
     */
    public void handleUpdateTeacherResponse(Message response) {
        Platform.runLater(() -> {
            if (response.isSuccess()) {
                System.out.println("教师更新成功");
                // 修改完成后刷新表格
                refreshAllData();
            } else {
                System.err.println("教师更新失败: " + response.getMessage());
                // 可选：弹窗提示失败
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("更新失败");
                alert.setHeaderText(null);
                alert.setContentText("教师更新失败: " + response.getMessage());
                alert.showAndWait();
            }
        });
    }

}
