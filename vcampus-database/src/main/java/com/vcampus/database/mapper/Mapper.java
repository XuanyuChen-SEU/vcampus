package com.vcampus.database.mapper;

public interface Mapper {

    // 创建数据库
    void createDatabase(String dbName);
    // 删除数据库
    void dropDatabase(String dbName);
    // 创建用户表（示例表）
    void createUserTable();
    void createStudentTable();


    //加载数据
    void loadStudentsFromCsv(String filePath);
    void loadUsersFromCsv(String filePath);


    // 删除所有表
    void dropTables(String dbName);
    void InsertTempData();

}
