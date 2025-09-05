package com.vcampus.client.controller;

import com.vcampus.common.dto.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

/**
 * 学籍管理界面控制器
 * 对应 StudentStatus.fxml
 * 编写人：周蔚钺
 */
public class StudentController implements IClientController {

    // ====== 顶部查询区组件 ======
    @FXML
    private TextField studentIdField;
    @FXML
    private TextField nameField;

    // ====== 表格组件 ======
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TableColumn<Student, String> userIdColumn;
    @FXML
    private TableColumn<Student, String> studentIdColumn;
    @FXML
    private TableColumn<Student, String> cardIdColumn;
    @FXML
    private TableColumn<Student, String> nameColumn;
    @FXML
    private TableColumn<Student, String> genderColumn;
    @FXML
    private TableColumn<Student, String> collegeColumn;
    @FXML
    private TableColumn<Student, String> majorColumn;
    @FXML
    private TableColumn<Student, Integer> gradeColumn;

    // 表格数据源
    private final ObservableList<Student> studentData = FXCollections.observableArrayList();

    /**
     * 初始化方法，FXML 加载后自动调用
     */
    @FXML
    private void initialize() {
        // 绑定表格列到 Student 对象的属性
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        cardIdColumn.setCellValueFactory(new PropertyValueFactory<>("cardId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        collegeColumn.setCellValueFactory(new PropertyValueFactory<>("college"));
        majorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));

        // 设置表格数据
        studentTable.setItems(studentData);

        // 模拟数据（测试用）
        studentData.addAll(
                new Student("1000001", "20230001", "123456789", "张三", "男", "计算机学院", "软件工程", 2023),
                new Student("1000002", "20230002", "987654321", "李四", "女", "经济学院", "金融学", 2022)
        );
    }

    /**
     * 处理查询按钮点击
     */
    @FXML
    private void handleSearch() {
        String studentId = studentIdField.getText().trim();
        String name = nameField.getText().trim();

        // TODO: 这里应该调用后端接口查询
        // 先用简单过滤模拟
        ObservableList<Student> filtered = FXCollections.observableArrayList();
        for (Student s : studentData) {
            boolean matches = true;
            if (!studentId.isEmpty() && !s.getStudentId().contains(studentId)) {
                matches = false;
            }
            if (!name.isEmpty() && !s.getName().contains(name)) {
                matches = false;
            }
            if (matches) {
                filtered.add(s);
            }
        }
        studentTable.setItems(filtered);
    }

    /**
     * 处理新增学生按钮点击
     */
    @FXML
    private void handleAdd() {
        // 简化处理：弹出输入对话框
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("新增学生");
        dialog.setHeaderText("请输入学生姓名（其他字段请补全后端接口）");
        dialog.setContentText("姓名:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Student newStudent = new Student("1000003", "20230003", "111222333", name, "男",
                    "测试学院", "测试专业", 2023);
            studentData.add(newStudent);
        });
    }

    /**
     * 处理编辑学生按钮点击
     */
    @FXML
    private void handleEdit() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请先选择一个学生再编辑", Alert.AlertType.WARNING);
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getName());
        dialog.setTitle("编辑学生");
        dialog.setHeaderText("修改学生姓名");
        dialog.setContentText("姓名:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> selected.setName(newName));

        // 刷新表格
        studentTable.refresh();
    }

    /**
     * 处理删除学生按钮点击
     */
    @FXML
    private void handleDelete() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("请先选择一个学生再删除", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("删除确认");
        confirm.setHeaderText("确认删除学生: " + selected.getName() + " ?");
        confirm.setContentText("此操作不可撤销！");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            studentData.remove(selected);
        }
    }

    /**
     * 工具方法：弹出提示框
     */
    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

