package com.vcampus.common.dao;

import com.vcampus.common.dto.Student;
import java.util.List;

/**
 * 学籍信息数据访问接口
 * 定义对 student 表的增删改查操作
 * 编写人：周蔚钺
 */
public interface StudentDAO {

    /**
     * 通过 userId 查询学籍信息
     */
    Student findByUserId(String userId);

    /**
     * 通过 studentId 查询学籍信息
     */
    Student findByStudentId(String studentId);

    /**
     * 通过姓名模糊搜索学生
     */
    List<Student> findByNameLike(String name);

    /**
     * 插入新学籍
     */
    boolean insert(Student student);

    /**
     * 更新学籍（全量更新）
     */
    boolean update(Student student);

    /**
     * 删除学籍
     */
    boolean deleteByUserId(String userId);

    /**
     * 查询所有学生
     */
    List<Student> findAll();
}
