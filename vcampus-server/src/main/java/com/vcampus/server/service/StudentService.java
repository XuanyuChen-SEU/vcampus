package com.vcampus.server.service;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.Student;
import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.common.dto.User;
import com.vcampus.common.dto.Teacher;
import com.vcampus.server.dao.impl.TeacherDao;
import com.vcampus.common.enums.ActionType;
import com.vcampus.server.dao.impl.StudentDao;
import com.vcampus.server.dao.impl.StudentLeaveApplicationDao;
import java.sql.SQLException;
import java.util.List;

/**
 * 服务端学生信息服务类
 * 负责处理客户端请求、查询数据库并返回学生信息
 */
public class StudentService {

    private final StudentDao studentDao;
    private final TeacherDao teacherDao;
    private final StudentLeaveApplicationDao applicationDAO = new StudentLeaveApplicationDao();

    public StudentService() {
        this.studentDao = new StudentDao();
        this.teacherDao = new TeacherDao();
    }

    /**
     * 根据学生ID查询学生信息
     *
     * @param studentId 学生ID（userId）
     * @return Message 消息对象，用于返回给客户端
     */
    public Message getStudentById(String studentId) {
        try {
            Student student = studentDao.findByUserId(studentId);

            if (student == null) {
                return Message.failure(ActionType.INFO_STUDENT, "未找到对应的学生信息");
            }

            // 这里可以考虑对敏感信息处理，比如置空密码字段（如果有的话）
            return Message.success(ActionType.INFO_STUDENT, student, "查询成功");

        } catch (Exception e) {
            System.err.println("查询学生信息过程中发生异常: " + e.getMessage());
            return Message.failure(ActionType.INFO_STUDENT, "服务器内部错误");
        }
    }

    public boolean updateStudent(Student student) {
        try {
            if (student == null || student.getUserId() == null || student.getUserId().isEmpty()) {
                return false;
            }

            return studentDao.update(student);

        } catch (Exception e) {
            System.err.println("更新学生信息时发生异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 保存休学/复学申请
     * @param application 申请对象
     * @return true 成功，false 失败
     */
    public boolean saveApplication(StudentLeaveApplication application) {
        try {
            return applicationDAO.insert(application);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据教师工号/ID获取教师信息
     * @param teacherId 教师ID
     * @return Teacher 对象，如果不存在返回 null
     */
    public Teacher getTeacherById(String teacherId) {
        if (teacherId == null || teacherId.isBlank()) return null;
        try {
            return teacherDao.getTeacherByUserId(teacherId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}



