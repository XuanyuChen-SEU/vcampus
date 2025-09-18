package com.vcampus.database.mapper;

import com.vcampus.common.dto.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherMapper {


    void loadTeachersFromCsv(String filePath);
    /**
     * 获取所有教师信息
     */
    List<Teacher> getAllTeachers();

    /**
     * 通过 userId 获取教师信息
     * @param userId 教师的用户ID
     */
    Teacher getTeacherByUserId(@Param("userId") String userId);

    /**
     * 修改教师信息
     * @param teacher 教师对象
     * @return 更新行数
     */
    int updateTeacher(Teacher teacher);

    /**
     * 通过院系、职称筛选教师
     * @param department 院系，可选
     * @param title 职称，可选
     */
    List<Teacher> getTeachersByDeptAndTitle(@Param("department") String department,
                                            @Param("title") String title);
}
