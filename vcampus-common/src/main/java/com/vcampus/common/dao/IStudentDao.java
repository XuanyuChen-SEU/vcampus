package com.vcampus.common.dao;

import com.vcampus.common.dto.Student;
import java.util.List;

/**
 * 学籍信息数据访问接口
 * 定义对 student 表的增删改查操作
 * 编写人：周蔚钺
 */
public interface IStudentDao {

    /**
     * 通过 userId 查询学籍信息
     */
    Student findByUserId(String userId);


    /**
     * 通过姓名模糊搜索学生
     */
    List<Student> findByNameLike(String name);

    List<Student> findByGrade(Integer grade);

    List<Student> findByMajor(String major);


    /**
     * 插入新学生信息
     */
    boolean insert(Student student);

    /**
     * 更新学生信息
     */
    boolean update(Student student);

    /**
     * 删除学生信息
     */
    boolean deleteByUserId(String userId);

    /**
     * 查询所有学生，返回所有学生的学生信息
     */
    List<Student> findAll();


}
