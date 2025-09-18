package com.vcampus.server.service;

import com.vcampus.common.dto.Teacher;
import com.vcampus.database.mapper.TeacherMapper;
import com.vcampus.database.utils.MyBatisUtil;
import com.vcampus.server.dao.impl.StudentDao;
import com.vcampus.server.dao.impl.TeacherDao;

public class TeacherService {

    private final TeacherDao teacherDao;

    public TeacherService() {
        this.teacherDao = new TeacherDao();
    }


    /**
     * 根据教师工号获取教师信息
     * @param teacherId 工号
     * @return Teacher对象，如果不存在返回null
     */
    public Teacher getTeacherById(String teacherId) {
        return teacherDao.getTeacherByUserId(teacherId);
    }

    /** 更新教师信息 */
    public boolean updateTeacher(Teacher teacher) {
        return teacherDao.updateTeacher(teacher);
    }
}