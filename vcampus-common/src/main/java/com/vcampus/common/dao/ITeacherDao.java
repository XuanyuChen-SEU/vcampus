package com.vcampus.common.dao;

import com.vcampus.common.dto.Teacher;

import java.util.List;

public interface ITeacherDao {

    /**
     * 获取所有教师信息
     */
    List<Teacher> getAllTeachers();

    /**
     * 通过 userId 获取教师信息
     */
    Teacher getTeacherByUserId(String userId);

    /**
     * 修改教师信息
     * @param teacher 教师对象
     * @return 是否更新成功
     */
    boolean updateTeacher(Teacher teacher);

    /**
     * 通过院系、职称筛选教师
     */
    List<Teacher> getTeachersByDeptAndTitle(String department, String title);
}

