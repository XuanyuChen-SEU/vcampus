package com.vcampus.server.dao.impl;
import com.vcampus.common.dao.IStudentDao;
import com.vcampus.common.dto.Student;
import com.vcampus.database.mapper.StudentMapper;
import com.vcampus.database.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.Map;
import java.util.HashMap;
import java.util.List;


public class StudentDao implements IStudentDao{
    @Override
    public Student findByUserId(String userId)
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.selectById(userId);
        }
    }
    @Override
    public List<Student> findByNameLike(String name)
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Student student = new Student();
            student.setName(name);
            return studentMapper.selectBySingleCondition(student);
        }
    }
    @Override
    public boolean insert(Student student)
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            studentMapper.add(student);
            sqlSession.commit();
        }
       return true;
    }
    @Override
    public boolean update(Student student)
    {
        int count=0;
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
                    StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
                    count=studentMapper.update(student);
                    sqlSession.commit();
        }
        return count>0;
    }
    @Override
    public boolean deleteByUserId(String userId)
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            studentMapper.deleteById(userId);
            sqlSession.commit();
        }
            return true;
    }
    @Override
    public List<Student> findAll()
    {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            return studentMapper.selectAll();
        }
    }

    @Override
    public List<Student> findByGrade(Integer grade) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Map<String, Object> params = new HashMap<>();
            params.put("grade", grade);

            return studentMapper.selectByCondition(params);
        }
    }

    @Override
    public List<Student> findByMajor(String major) {
        try (SqlSession sqlSession = MyBatisUtil.openSession()) {
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);

            Map<String, Object> params = new HashMap<>();
            params.put("major", major);

            return studentMapper.selectByCondition(params);
        }
    }

}
