package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ITeacherDao;
import com.vcampus.common.dto.Teacher;
import com.vcampus.database.mapper.TeacherMapper;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherDao implements ITeacherDao {

    @Override
    public List<Teacher> getAllTeachers() {
        try (SqlSession session = MyBatisUtil.openSession()) {
            TeacherMapper mapper = session.getMapper(TeacherMapper.class);
            return mapper.getAllTeachers();
        }
    }

    @Override
    public Teacher getTeacherByUserId(String userId) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            TeacherMapper mapper = session.getMapper(TeacherMapper.class);
            return mapper.getTeacherByUserId(userId);
        }
    }

    @Override
    public boolean updateTeacher(Teacher teacher) {
        int count = 0;
        try (SqlSession session = MyBatisUtil.openSession()) {
            TeacherMapper mapper = session.getMapper(TeacherMapper.class);
            count = mapper.updateTeacher(teacher);
            session.commit();
        }
        return count > 0;
    }

    @Override
    public List<Teacher> getTeachersByDeptAndTitle(String department, String title) {
        try (SqlSession session = MyBatisUtil.openSession()) {
            TeacherMapper mapper = session.getMapper(TeacherMapper.class);

            Map<String, Object> params = new HashMap<>();
            params.put("department", department);
            params.put("title", title);

            return mapper.getTeachersByDeptAndTitle(department, title);
        }
    }
}
