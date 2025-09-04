package com.vcampus.database.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 基础数据库管理类（通用化设计）
 * 功能：支持任意实体的通用CRUD、表结构操作（添加/删除列），不绑定具体业务表
 * @param <T> 业务实体类型（如 Employee、Department、Salary）
 */
public class BaseDBManager<T> {
    // 数据库核心配置（通用，不绑定业务）
    private  String dbUrl;
    private  String dbUsername;
    private  String dbPassword;
    // 动态表名：通过构造方法传入，适配不同业务表（如 EMP、DEPT、SALARY）
    private String tableName;
    // 结果集转换器：将ResultSet转为业务实体T（由调用方提供，解耦实体转换逻辑）
    private final Function<ResultSet, T> resultSetConverter;


    public String getDbUrl() { return dbUrl; }
    public String getDbUsername() { return dbUsername; }
    public String getDbPassword() { return dbPassword; }
    /**
     * 设置数据库连接URL
     * @param dbUrl 新的数据库连接URL（如 jdbc:mysql://localhost:3306/db2?useSSL=false）
     * @return 当前实例（支持链式调用）
     */
    public BaseDBManager<T> setDbUrl(String dbUrl) {
        if (dbUrl == null || dbUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库URL不能为空！");
        }
        this.dbUrl = dbUrl;
        return this; // 支持链式调用，如 manager.setDbUrl(...).setDbUsername(...)
    }

    /**
     * 设置数据库用户名
     * @param dbUsername 新的数据库用户名
     * @return 当前实例
     */
    public BaseDBManager<T> setDbUsername(String dbUsername) {
        if (dbUsername == null || dbUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库用户名不能为空！");
        }
        this.dbUsername = dbUsername;
        return this;
    }

    /**
     * 设置数据库密码
     * @param dbPassword 新的数据库密码
     * @return 当前实例
     */
    public BaseDBManager<T> setDbPassword(String dbPassword) {
        if (dbPassword == null) { // 密码允许为空字符串（部分数据库配置）
            dbPassword = "";
        }
        this.dbPassword = dbPassword;
        return this;
    }

    // 设置新表名（需移除tableName的final修饰符）
    public void setTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("表名不能为空！");
        }
        this.tableName = tableName.trim();
    }

    /**
     * 构造方法：初始化数据库配置+表名+结果集转换器
     * @param dbUrl 数据库连接URL（如 jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC）
     * @param dbUsername 数据库用户名
     * @param dbPassword 数据库密码
     * @param tableName 业务表名（如 "EMP"、"DEPT"、"SALARY"）
     * @param resultSetConverter 结果集→实体的转换器（如 ResultSet -> Employee）
     */
    public BaseDBManager(String dbUrl, String dbUsername, String dbPassword,
                         String tableName, Function<ResultSet, T> resultSetConverter) {
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.tableName = tableName;
        this.resultSetConverter = resultSetConverter;

        // 加载MySQL 8.0+驱动（通用，不绑定业务）
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL驱动加载失败！请检查驱动包：" + e.getMessage(), e);
        }
    }

    /**
     * 通用工具方法：获取数据库连接（私有，内部复用）
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    // ====================== 新增功能：获取表字段信息 ======================
    /**
     * 获取当前表的所有字段信息
     * @return 字段名列表
     * @throws SQLException SQL异常
     */
    public List<String> getTableColumns() throws SQLException {
        List<String> columns = new ArrayList<>();
        try (Connection conn = getConnection();
             ResultSet rs = conn.getMetaData().getColumns(
                     null, null, tableName.toUpperCase(), null
             )) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME"));
            }
        }
        return columns;
    }

    /**
     * 获取当前表的主键字段
     * @return 主键字段名
     * @throws SQLException SQL异常
     */
    public String getPrimaryKey() throws SQLException {
        try (Connection conn = getConnection();
             ResultSet rs = conn.getMetaData().getPrimaryKeys(null, null, tableName)) { // 未使用toUpperCase()
            if (rs.next()) {
                return rs.getString("COLUMN_NAME");
            }
        }
        return null;
    }

    /**
     * 打印当前表的结构信息
     */
    public void printTableStructure() {
        try {
            System.out.println("\n=== 表结构信息 ===");
            System.out.println("表名: " + tableName);

            List<String> columns = getTableColumns();
            System.out.println("字段列表 (" + columns.size() + "个):");

            String primaryKey = getPrimaryKey();
            try (Connection conn = getConnection();
                 ResultSet rs = conn.getMetaData().getColumns(
                         null, null, tableName.toUpperCase(), null
                 )) {
                while (rs.next()) {
                    String colName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("TYPE_NAME");
                    int colSize = rs.getInt("COLUMN_SIZE");
                    boolean isNullable = rs.getInt("NULLABLE") == 1;

                    String keyMark = (primaryKey != null && primaryKey.equals(colName)) ? " [主键]" : "";
                    System.out.printf("  %s: %s(%d) %s%s%n",
                            colName, dataType, colSize,
                            isNullable ? "允许为空" : "不允许为空",
                            keyMark);
                }
            }
        } catch (SQLException e) {
            System.err.println("获取表结构失败: " + e.getMessage());
        }
    }

    // ====================== 一、通用CRUD操作（支持任意实体）======================
    /**
     * 新增实体（通用：支持员工、部门等任意实体）
     * @param sql 新增SQL（需与表结构匹配，如 "INSERT INTO EMP (Name, Sex, Age) VALUES (?, ?, ?)"）
     * @param params SQL参数（与SQL中的?顺序对应）
     * @return true=新增成功，false=失败
     */
    public boolean addEntity(String sql, Object... params) {
        // 前置校验：SQL和参数非空
        if (sql == null || sql.trim().isEmpty()) {
            System.err.println("新增失败：SQL语句不能为空！");
            return false;
        }
        if (params == null || params.length == 0) {
            System.err.println("新增失败：SQL参数不能为空！");
            return false;
        }

        // 数据库操作：自动关闭资源
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 动态设置SQL参数（适配任意参数类型和数量）
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]); // PreparedStatement参数索引从1开始
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.printf("新增实体失败（表：%s）：%s%n", tableName, e.getMessage());
            return false;
        }
    }

    /**
     * 根据条件删除实体（通用）
     * @param sql 删除SQL（如 "DELETE FROM EMP WHERE Name = ?"）
     * @param params SQL参数（与?顺序对应）
     * @return true=删除成功，false=失败
     */
    public boolean deleteEntity(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            System.err.println("删除失败：SQL语句不能为空！");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 动态设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.printf("删除实体失败（表：%s）：%s%n", tableName, e.getMessage());
            return false;
        }
    }

    /**
     * 根据条件查询单个实体（通用）
     * @param sql 查询SQL（如 "SELECT * FROM EMP WHERE Name = ?"）
     * @param params SQL参数
     * @return 转换后的实体T（如 Employee），无结果则返回null
     */
    public T getEntity(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            System.err.println("查询失败：SQL语句不能为空！");
            return null;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 动态设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // 调用转换器，将ResultSet转为实体T（由调用方定义转换规则）
                return resultSetConverter.apply(rs);
            }

        } catch (SQLException e) {
            System.err.printf("查询单个实体失败（表：%s）：%s%n", tableName, e.getMessage());
        }
        return null;
    }

    /**
     * 根据条件查询实体列表（通用）
     * @param sql 查询SQL（如 "SELECT * FROM EMP WHERE Age > ?"）
     * @param params SQL参数
     * @return 实体列表（如 List<Employee>），无结果则返回空列表
     */
    public List<T> getEntityList(String sql, Object... params) {
        List<T> entityList = new ArrayList<>();
        if (sql == null || sql.trim().isEmpty()) {
            System.err.println("查询列表失败：SQL语句不能为空！");
            return entityList;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 动态设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // 转换ResultSet为实体，添加到列表
                T entity = resultSetConverter.apply(rs);
                entityList.add(entity);
            }

        } catch (SQLException e) {
            System.err.printf("查询实体列表失败（表：%s）：%s%n", tableName, e.getMessage());
        }
        return entityList;
    }

    /**
     * 更新实体（通用）
     * @param sql 更新SQL（如 "UPDATE EMP SET Age = ? WHERE Name = ?"）
     * @param params SQL参数（与?顺序对应）
     * @return true=更新成功，false=失败
     */
    public boolean updateEntity(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            System.err.println("更新失败：SQL语句不能为空！");
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 动态设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.printf("更新实体失败（表：%s）：%s%n", tableName, e.getMessage());
            return false;
        }
    }

    // ====================== 二、通用表结构操作（添加/删除列）======================
    /**
     * 给当前表添加新列（通用，不绑定具体表）
     * @param columnName 新列名（如 "Department"、"Salary"）
     * @param dataType 数据类型（如 "VARCHAR(30)"、"INT"）
     * @param isNullable 是否允许为空
     * @param defaultValue 默认值（null表示无默认值）
     * @return true=添加成功，false=失败
     */
    public boolean addColumn(String columnName, String dataType, boolean isNullable, String defaultValue) {
        // 前置校验
        if (columnName == null || columnName.trim().isEmpty() || columnName.contains(" ")) {
            System.err.println("添加列失败：列名不能为空且不能含空格！");
            return false;
        }
        if (dataType == null || dataType.trim().isEmpty()) {
            System.err.println("添加列失败：数据类型不能为空！");
            return false;
        }
        if (!isNullable && (defaultValue == null || defaultValue.trim().isEmpty())) {
            System.err.println("添加列失败：不允许为空的列必须设置默认值！");
            return false;
        }

        // 拼接添加列SQL（表名用当前实例的tableName，非硬编码EMP）
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("ALTER TABLE ").append(tableName).append(" ADD COLUMN ");
        sqlBuilder.append(columnName.trim()).append(" ").append(dataType.trim());
        sqlBuilder.append(isNullable ? " NULL" : " NOT NULL");
        if (defaultValue != null && !defaultValue.trim().isEmpty()) {
            sqlBuilder.append(" DEFAULT '").append(defaultValue.trim()).append("'");
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.printf("执行添加列SQL（表：%s）：%s%n", tableName, sqlBuilder.toString());
            stmt.execute(sqlBuilder.toString());
            return true;

        } catch (SQLException e) {
            System.err.printf("添加列失败（表：%s）：%s%n", tableName, e.getMessage());
            return false;
        }
    }

    /**
     * 从当前表删除指定列（通用）
     * @param columnName 要删除的列名
     * @return true=删除成功，false=失败
     */
    public boolean deleteColumn(String columnName) {
        if (columnName == null || columnName.trim().isEmpty()) {
            System.err.println("删除列失败：列名不能为空！");
            return false;
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 先校验列是否存在（表名用当前tableName）
            if (!isColumnExists(tableName, columnName.trim())) {
                System.err.printf("删除列失败（表：%s）：列'%s'不存在！%n", tableName, columnName.trim());
                return false;
            }

            // 执行删除列SQL
            String sql = "ALTER TABLE " + tableName + " DROP COLUMN " + columnName.trim();
            System.out.printf("执行删除列SQL（表：%s）：%s%n", tableName, sql);
            stmt.execute(sql);
            return true;

        } catch (SQLException e) {
            System.err.printf("删除列失败（表：%s）：%s%n", tableName, e.getMessage());
            return false;
        }
    }

    // ====================== 三、通用工具方法（私有）======================
    /**
     * 校验指定表中是否存在指定列（通用）
     */
    private boolean isColumnExists(String tableName, String columnName) throws SQLException {
        try (Connection conn = getConnection();
             ResultSet rs = conn.getMetaData().getColumns(
                     null, null, tableName.toUpperCase(), columnName.toUpperCase()
             )) {
            return rs.next(); // 有结果则列存在
        }
    }

    /**
     * 打印实体列表（通用，方便调试）
     * @param entityList 要打印的实体列表
     * @param printFunc 实体的打印规则（由调用方定义，如打印Employee的Name和Age）
     */
    public void printEntityList(List<T> entityList, Function<T, String> printFunc) {
        if (entityList.isEmpty()) {
            System.out.printf("（表：%s）无匹配实体数据%n", tableName);
            return;
        }
        System.out.printf("=== 表：%s 的实体列表（共%d条）===%n", tableName, entityList.size());
        for (T entity : entityList) {
            System.out.println(printFunc.apply(entity));
        }
    }

    /**
     * 获取数据库中所有表名
     */
    public List<String> getAllTableNames() throws SQLException {
        List<String> tableNames = new ArrayList<>();
        try (Connection conn = getConnection();
             ResultSet rs = conn.getMetaData().getTables(
                     null, null, "%", new String[]{"TABLE"}
             )) {
            while (rs.next()) {
                tableNames.add(rs.getString("TABLE_NAME"));
            }
        }
        return tableNames;
    }

    /**
     * 打印数据库中所有表名
     */
    public void printAllTables() {
        try {
            List<String> tables = getAllTableNames();
            System.out.println("\n=== 数据库中的表列表 ===");
            for (String table : tables) {
                System.out.println("  " + table + (table.equalsIgnoreCase(tableName) ? " [当前表]" : ""));
            }
        } catch (SQLException e) {
            System.err.println("获取表列表失败: " + e.getMessage());
        }
    }

    public String getTableName() {
        return tableName;
    }
    // ====================== 四、测试方法（通用化测试：支持员工表、临时表）======================
    public static void main(String[] args) {
        // -------------------------- 1. 测试：员工表（EMP）操作 --------------------------
        System.out.println("===== 测试1：员工表（EMP）操作 =====");
        // 数据库连接配置
        String dbUrl = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPwd = "Abcd0410";

        // 1.1 创建员工表的BaseDBManager实例（T=String[]，简化实体；结果集转换器：将rs转为String[]存储字段值）
        BaseDBManager<Map<String, Object>> empDBManager = new BaseDBManager<>(
                dbUrl, dbUser, dbPwd,
                "EMP", // 员工表名
                rs -> { // 结果集→实体的转换器（使用Map存储任意字段）
                    try {
                        Map<String, Object> record = new HashMap<>();
                        ResultSetMetaData metaData = rs.getMetaData();
                        int columnCount = metaData.getColumnCount();

                        for (int i = 1; i <= columnCount; i++) {
                            record.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        return record;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
        );

        // 打印表结构
        empDBManager.printTableStructure();

        // 1.2 新增员工（SQL和参数动态传入，不绑定员工字段）
        String addEmpSql = "INSERT INTO EMP (Name, Sex, Age) VALUES (?, ?, ?)";
        empDBManager.addEntity(addEmpSql, "张三", "男", 28);
        empDBManager.addEntity(addEmpSql, "李四", "女", 32);

        // 1.3 查询员工列表（打印规则：自定义如何显示员工信息）
        String listEmpSql = "SELECT * FROM EMP";
        List<Map<String, Object>> empList = empDBManager.getEntityList(listEmpSql);
        empDBManager.printEntityList(empList, emp -> {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : emp.entrySet()) {
                sb.append(entry.getKey()).append("：").append(entry.getValue()).append(" | ");
            }
            return sb.toString();
        });

        // 1.4 给员工表添加列（通用addColumn方法）
        empDBManager.addColumn("Department", "VARCHAR(20)", true, null);

        // 清理测试数据
        empDBManager.deleteEntity("DELETE FROM EMP WHERE Name = ?", "张三");
        empDBManager.deleteEntity("DELETE FROM EMP WHERE Name = ?", "李四");
        System.out.println("\n===== 测试结束，数据已清理 =====");
    }

}
