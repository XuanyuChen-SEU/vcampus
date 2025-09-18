package com.vcampus.database.mapper;

public interface Mapper {
    // 创建数据库
    void createDatabase(String dbName);

    // 删除数据库
    void dropDatabase(String dbName);

    // 创建表
    void createUserTable();
    void createStudentTable();
    void createStudentLeaveApplicationTable();
    void createTeacherTable();
    void createBookTable();
    void createBorrowLogTable();
    void createPasswordResetApplicationTable();

    void createProductTable();
    void createOrderTable();
    void createFavoriteTable();
    void createBalanceTable();

    //课程选择模块
    void createCoursesTable();
    void createClassSessionsTable();
    void createCourseSelectionsTable();

    void useDatabase(String dbName);
    //邮件模块
    void createEmailTable();
}
