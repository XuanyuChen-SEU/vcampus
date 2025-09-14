package com.vcampus.database.mapper;

public interface Mapper {
    // 创建数据库
    void createDatabase(String dbName);

    // 删除数据库
    void dropDatabase(String dbName);

    // 创建表
    void createUserTable();
    void createStudentTable();
    void createPasswordResetApplicationTable();
    void createProductTable();
    void createOrderTable();
    void createFavoriteTable();
}
