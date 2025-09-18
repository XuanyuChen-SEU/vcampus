package com.vcampus.database.mapper;

import com.vcampus.common.dto.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StudentMapper {



    void loadStudentsFromCsv(String filePath);


    List<Student> selectAll();
    Student selectById(String userId);
    List<Student> selectByCondition(Map map);
    List<Student> selectBySingleCondition(Student student);
    void add(Student student);
    int update(Student student);
    void deleteById(String userId);
    void deleteByIds(String[] userIds);
    void updateStudents(@Param("list") List<Student> students);
}
