package com.vcampus.server.service;

import com.vcampus.common.dto.Message;
import com.vcampus.common.dto.StudentLeaveApplication;
import com.vcampus.common.dto.Teacher;
import com.vcampus.server.dao.impl.StudentDao;
import com.vcampus.server.dao.impl.StudentLeaveApplicationDao;
import com.vcampus.server.dao.impl.TeacherDao;
import com.vcampus.common.dto.Student;

import java.util.List;

/**
 * 服务端管理员端学生信息业务类
 */
public class StudentAdminService {

    private final StudentDao studentDao = new StudentDao();
    private final TeacherDao teacherDao=new TeacherDao();
    private final StudentLeaveApplicationDao studentleaveapplicationDao = new StudentLeaveApplicationDao();

    public List<Student> findAll() {
        return studentDao.findAll();
    }

    public List<Student> findByNameLike(String nameKeyword) {
        if (nameKeyword == null || nameKeyword.trim().isEmpty()) {
            return findAll();
        }
        return studentDao.findByNameLike("%" + nameKeyword + "%");
    }

    public Student getStudentById(String userId) {
        return studentDao.findByUserId(userId);
    }

    public boolean updateStudentInfo(Student student) {
        return studentDao.update(student) ; // update 返回受影响行数
    }

    /**
     * 获取所有请假申请
     */
    public List<StudentLeaveApplication> getAllApplications() {
        return studentleaveapplicationDao.selectAllApplications(); // 从数据库中查询所有申请
    }

    /**
     * 更新请假申请状态
     */
    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        try {
            boolean ok = studentleaveapplicationDao.updateStatus(applicationId, newStatus);
            if (!ok) return false;

            // 2. 如果通过了，就联动修改学生的学籍状态
            if ("已通过".equals(newStatus)) {
                StudentLeaveApplication app = studentleaveapplicationDao.findById(applicationId);
                if (app != null) {
                    Student stu = studentDao.findByUserId(app.getStudentId());
                    if (stu != null) {
                        String currentStatus = stu.getStudent_status();
                        if ("在读".equals(currentStatus)) {
                            stu.setStudent_status("休学");
                        } else if ("休学".equals(currentStatus)) {
                            stu.setStudent_status("在读");
                        }
                        studentDao.update(stu);

                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public StudentLeaveApplication getApplicationById(String applicationId) {
        return studentleaveapplicationDao.findById(applicationId);
    }

    public boolean updateStudents(List<Student> students) {
        try {
            // 直接调用 DAO 的批量更新方法
            studentDao.updateStudents(students);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 获取所有教师 */
    public List<Teacher> getAllTeachers() {
        return teacherDao.getAllTeachers();
    }

//    /** 根据姓名模糊搜索教师 */
//    public List<Teacher> searchTeachersByName(String nameKeyword) {
//        return teacherDao.findByName(nameKeyword);
//    }
//
//    /** 根据工号获取教师 */
//    public Teacher getTeacherById(String userId) {
//        return teacherDao.findById(userId);
//    }

    /** 更新教师信息 */
    public boolean updateTeacher(Teacher teacher) {
        return teacherDao.updateTeacher(teacher);
    }
}

