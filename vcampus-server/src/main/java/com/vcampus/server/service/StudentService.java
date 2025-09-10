package com.vcampus.server.service;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.User;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.dao.impl.StudentDao;

import java.sql.SQLException;

/**
 * 服务端学生信息服务类
 * 负责处理客户端请求、查询数据库并返回学生信息
 */
public class StudentService {

    private final StudentDao studentDao;

    public StudentService() {
        this.studentDao = new StudentDao();
    }

    /**
     * 根据学生ID查询学生信息
     * @param studentId 学生ID（userId）
     * @return Message 消息对象，用于返回给客户端
     */
    /**
    public Message getStudentById(String studentId) {
        Message message = new Message();
        try {
            Student student = studentDao.findStudentById(studentId);
            if (student != null) {
                message.setSuccess(true);
                message.setData(student);
            } else {
                message.setSuccess(false);
                message.setMessage("未找到对应的学生信息");
            }
        } catch (SQLException e) {
            message.setSuccess(false);
            message.setMessage("数据库查询失败：" + e.getMessage());
        }
        return message;
    }
     */

        /**
         * 根据学生ID查询学生信息（模拟数据）
         * @param studentId 学生ID（userId）
         * @return Message 消息对象，用于返回给客户端
         */
        public Student getStudentById(String studentId) {
            if (studentId == null || studentId.isEmpty()) {
                return null;
            }


            // 构建一个固定的学生信息对象
            Student student = new Student();
            student.setUserId(studentId);       // 用户ID
            student.setStudentId("20250001");   // 学号
            student.setCardId("100200300");    // 一卡通号
            student.setName("张三");            // 姓名
            student.setGender("男");            // 性别
            student.setCollege("计算机学院");     // 学院
            student.setMajor("软件工程");        // 专业
            student.setGrade(2025);
            student.setStudent_status("在读");// 学籍状态
            student.setBirth_date("2005-1-1");
            student.setNative_place("江苏南京");
            student.setPolitics_status("共青团员");

            // 使用Message静态方法封装成功消息
            return student;
        }
    }


