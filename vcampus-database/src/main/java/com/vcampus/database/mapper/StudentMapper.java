package com.vcampus.database.mapper;

import com.vcampus.common.dto.Student;

import java.util.List;
import java.util.Map;

public interface StudentMapper {

    // 创建数据库
    void createDatabase(String dbName);
    // 删除数据库
    void dropDatabase(String dbName);
    // 创建用户表（示例表）
    void createStuTable();
    // 删除用户表
    void dropStuTable(String dbName);
    void InsertTempData();

    void loadStudentsFromCsv(String filePath);


    List<Student> selectAll();
    Student selectById(String userId);
    List<Student> selectByCondition(Map map);
    List<Student> selectBySingleCondition(Student student);
    void add(Student student);
    int update(Student student);
    void deleteById(String userId);
    void deleteByIds(String[] userIds);
}
