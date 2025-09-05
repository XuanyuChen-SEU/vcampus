package com.vcampus.server.service;

import com.vcampus.common.dto.Student;

import java.util.List;

/**
 * 学籍信息业务逻辑层
 * 对 DAO 进行封装，添加必要的业务校验
 * 编写人：周蔚钺
 */
public class StudentService {

    private final StudentDAOImpl studentDAO = new StudentDAOImpl();

    /**
     * 通过 userId 查询学籍
     */
    public Student getStudentByUserId(String userId) {
        if (userId == null || !userId.matches("\\d{7}")) {
            throw new IllegalArgumentException("用户ID必须为7位数字");
        }
        return studentDAO.findByUserId(userId);
    }

    /**
     * 通过学号查询
     */
    public Student getStudentByStudentId(String studentId) {
        if (studentId == null || !studentId.matches("\\d{8}")) {
            throw new IllegalArgumentException("学号必须为8位数字");
        }
        return studentDAO.findByStudentId(studentId);
    }

    /**
     * 模糊搜索学生姓名
     */
    public List<Student> searchStudentsByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        return studentDAO.findByNameLike(name);
    }

    /**
     * 添加新学籍
     */
    public boolean addStudent(Student student) {
        validateStudent(student);
        return studentDAO.insert(student);
    }

    /**
     * 更新学籍信息
     */
    public boolean updateStudent(Student student) {
        validateStudent(student);
        return studentDAO.update(student);
    }

    /**
     * 删除学籍
     */
    public boolean deleteStudent(String userId) {
        if (userId == null || !userId.matches("\\d{7}")) {
            throw new IllegalArgumentException("用户ID必须为7位数字");
        }
        return studentDAO.deleteByUserId(userId);
    }

    /**
     * 获取所有学生
     */
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    /**
     * 基础校验（保证格式合法）
     */
    private void validateStudent(Student student) {
        if (student.getUserId() == null || !student.getUserId().matches("\\d{7}")) {
            throw new IllegalArgumentException("用户ID必须为7位数字");
        }
        if (student.getStudentId() == null || !student.getStudentId().matches("\\d{8}")) {
            throw new IllegalArgumentException("学号必须为8位数字");
        }
        if (student.getCardId() == null || !student.getCardId().matches("\\d{9}")) {
            throw new IllegalArgumentException("一卡通号必须为9位数字");
        }
    }
}
