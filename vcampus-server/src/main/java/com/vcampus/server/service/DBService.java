package com.vcampus.server.service;
import com.vcampus.database.service.BaseDBManager;
import com.vcampus.common.dto.User;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;
import java.sql.*;
import java.util.*;

import static com.vcampus.database.service.Test_DBManager.deleteRecordInteractive;

public class DBService {
    public BaseDBManager<Map<String, Object>> dbManager;
    //这个是测试样例  可以试一试

//    public static void main(String[] args) {
//        try {
//            DBService dbService = new DBService();
//            dbService.initialize();
//            // 传入dbManager参数
//            showInteractiveMenu(dbService.dbManager);
//        } catch (RuntimeException e) {
//            System.err.println("系统启动失败：" + e.getMessage());
//        }
//    }
// 需在DBService中新增的initialize重载方法（用于指定数据库名）
//public void initialize(String dbName) {
//    String dbUrl = "jdbc:mysql://localhost:3306/" + dbName + "?useSSL=false&serverTimezone=UTC";
//    String dbUser = "root";
//    String dbPwd = "Abcd0410";
//    String initialTable = "";
//    Map<String, String> dbConfig = initializeDatabase(dbUrl, dbUser, dbPwd, initialTable);
//    this.dbManager = new BaseDBManager<>(
//            dbUrl, dbUser, dbPwd,
//            initialTable,
//            rs -> {
//                try {
//                    Map<String, Object> record = new HashMap<>();
//                    ResultSetMetaData metaData = rs.getMetaData();
//                    int columnCount = metaData.getColumnCount();
//                    for (int i = 1; i <= columnCount; i++) {
//                        String columnName = metaData.getColumnName(i);
//                        Object value = rs.getObject(i);
//                        record.put(columnName, value);
//                    }
//                    return record;
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//    );
//}
public void initialize() {
    // 1. 定义数据库配置参数（保持硬编码，与你的需求一致）
    String dbUrl = "jdbc:mysql://localhost:3306/vcampus?useSSL=false&serverTimezone=UTC";
    String dbUser = "root";
    String dbPwd = "1ssyzjkl";
    String initialTable = "user";

    // 2. 调用initializeDatabase()，执行“创建数据库→创建表→插入初始数据”的一条龙操作
    // （如果数据库/表已存在，会自动跳过创建，仅连接）
    Map<String, String> dbConfig = initializeDatabase(dbUrl, dbUser, dbPwd, initialTable);

    // 3. 使用验证后的配置创建数据库管理实例（确保使用的是实际生效的配置）
    this.dbManager = new BaseDBManager<>(
            dbConfig.get("dbUrl"),   // 从配置中获取URL（与传入一致，但经过验证）
            dbConfig.get("dbUser"),  // 从配置中获取用户名
            dbConfig.get("dbPwd"),   // 从配置中获取密码
            dbConfig.get("initialTable"),  // 从配置中获取表名
            // 结果集转换器（保持原有逻辑不变）
            rs -> {
                try {
                    Map<String, Object> record = new HashMap<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        record.put(columnName, value);
                    }
                    return record;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
    );

    System.out.println("✅ 初始化完成，已连接到数据库：" + dbConfig.get("dbUrl"));
}
    private static Map<String, String> initializeDatabase(
            String dbUrl,        // 数据库URL（如jdbc:mysql://localhost:3306/test...）
            String dbUser,       // 用户名
            String dbPwd,        // 密码
            String initialTable  // 初始表名
    ) {
        Map<String, String> dbConfig = new HashMap<>();

        try {
            // 1. 提取数据库名（从URL中解析）
            String dbName = extractDatabaseName(dbUrl);

            if (dbName == null || dbName.isEmpty()) {
                throw new IllegalArgumentException("数据库URL格式错误，无法提取数据库名");
            }

            // 2. 先连接到MySQL服务器（不指定数据库），用于创建数据库
            String serverUrl = dbUrl.replace("/" + dbName, ""); // 去除URL中的数据库名部分
            BaseDBManager<Map<String, Object>> serverManager = new BaseDBManager<>(
                    serverUrl,
                    dbUser,
                    (dbPwd != null) ? dbPwd : "",
                    "", // 临时表名（无意义）
                    rs -> new HashMap<>()
            );

            // 3. 创建数据库（如果不存在）
            try (Connection serverConn = serverManager.getConnection()) {
                String createDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName;
                try (Statement stmt = serverConn.createStatement()) {
                    stmt.execute(createDbSql);
                    System.out.println("✅ 数据库【" + dbName + "】检查/创建完成");
                }
            }

            // 4. 连接到目标数据库，检查并创建表
            BaseDBManager<Map<String, Object>> TestingManager = new BaseDBManager<>(
                    dbUrl,
                    dbUser,
                    (dbPwd != null) ? dbPwd : "",
                    initialTable,
                    rs -> new HashMap<>()
            );

            try (Connection conn = TestingManager.getConnection()) {
                // 5. 检查并创建表（仅包含userId和password字段）
                if (!isTableExists(TestingManager, initialTable)) {
                    String createTableSql = getCreateTableSql(initialTable);
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(createTableSql);
                        System.out.println("✅ 表【" + initialTable + "】创建完成（仅包含userId和password字段）");

                        // 6. 插入初始数据（只包含userId和password）
                        insertInitialData(conn, initialTable);
                        System.out.println("✅ 初始数据插入完成");
                    }
                } else {
                    System.out.println("✅ 表【" + initialTable + "】已存在，无需创建");
                }

                // 7. 保存配置
                dbConfig.put("dbUrl", dbUrl);
                dbConfig.put("dbUser", dbUser);
                dbConfig.put("dbPwd", (dbPwd != null) ? dbPwd : "");
                dbConfig.put("initialTable", initialTable);
                System.out.println("✅ 数据库初始化完成");
            }

        } catch (IllegalArgumentException e) {
            System.err.println("❌ 配置参数错误：" + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("❌ 数据库操作失败：" + e.getMessage());
            throw new RuntimeException("数据库初始化失败", e);
        }

        return dbConfig;
    }

    // 辅助方法：从URL中提取数据库名（不变）
    private static String extractDatabaseName(String dbUrl) {
        // 例如从"jdbc:mysql://localhost:3306/test?..."中提取"test"

        String[] parts = dbUrl.split("/");

        for (String part : parts) {
            if (part.contains("?")) {
                return part.split("\\?")[0];
            }
            // 新增：空字符串直接跳过，继续下一次循环
            if (part.isEmpty()) {
                continue;
            }
            if (!part.startsWith("jdbc:mysql:") && !part.contains(":")) {
                return part;
            }
        }
        return null;
    }

    // 辅助方法：检查表是否存在（不变）
    private static boolean isTableExists(BaseDBManager<Map<String, Object>> dbManager, String tableName) throws SQLException
    {
        List<String> allTables = dbManager.getAllTableNames();
        return allTables.stream().anyMatch(table -> table.equalsIgnoreCase(tableName));
    }

    // 辅助方法：获取创建表的SQL（简化为只包含userId和password）
    private static String getCreateTableSql(String tableName) {
        // 表结构仅包含：userId（唯一）、password
        return "CREATE TABLE " + tableName + " (" +
                "userId VARCHAR(20) NOT NULL PRIMARY KEY COMMENT '用户唯一标识（主键）'," +  // 用户ID作为主键（唯一不可重复）
                "password VARCHAR(50) NOT NULL COMMENT '用户密码'" +  // 密码字段
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";  // 字符集设置
    }

    // 辅助方法：插入初始数据（只包含userId和password）
    private static void insertInitialData(Connection conn, String tableName) throws SQLException {
        // 仅插入userId和password两个字段的值
        String insertSql = "INSERT INTO " + tableName + " (userId, password) VALUES " +
                "('1234321', '1234567')," +  // 管理员账号
                "('4321123', '1234568')," +  // 学生账号
                "('2132131', '1234569')";  // 教师账号

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertSql);
        }
    }
    /**
     * /**
     * 初始化数据库配置：循环获取用户输入，验证连接和表存在性，直到成功
     *
     * @return 有效的数据库配置（URL、用户名、密码、初始表名）
     */
    private static Map<String, String> initializeDatabase() {
        Scanner scanner = new Scanner(System.in);
        Map<String, String> dbConfig = new HashMap<>();

        while (true) {
            try {
                // 提示用户输入数据库连接信息
                System.out.print("请输入数据库URL（例如：jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC）：");
                String dbUrl = scanner.nextLine().trim();

                System.out.print("请输入用户名：");
                String dbUser = scanner.nextLine().trim();

                System.out.print("请输入数据库密码：");
                String dbPwd = scanner.nextLine().trim();

                System.out.print("请输入初始表名：");
                String initialTable = scanner.nextLine().trim();

                // 基本输入校验
                if (dbUrl.isEmpty() || dbUser.isEmpty() || initialTable.isEmpty()) {
                    System.out.println("❌ 数据库URL、用户名和初始表名不能为空，请重新输入！");
                    continue;
                }

                // 尝试创建临时管理器验证连接
                System.out.println("正在测试数据库连接...");
                BaseDBManager<Map<String, Object>> tempManager = new BaseDBManager<>(
                        dbUrl, dbUser, dbPwd,
                        initialTable,
                        rs -> new HashMap<>() // 临时转换器，仅用于验证
                );

                // 验证连接有效性
                try (Connection conn = tempManager.getConnection()) {
                    // 验证表是否存在
                    List<String> allTables = tempManager.getAllTableNames();
                    boolean tableExists = allTables.stream()
                            .anyMatch(table -> table.equalsIgnoreCase(initialTable));

                    if (!tableExists) {
                        System.out.println("❌ 初始表名不存在，请重新输入！");
                        continue;
                    }

                    // 验证通过，保存配置
                    dbConfig.put("dbUrl", dbUrl);
                    dbConfig.put("dbUser", dbUser);
                    dbConfig.put("dbPwd", dbPwd);
                    dbConfig.put("initialTable", initialTable);
                    System.out.println("✅ 数据库连接成功！");
                    break;
                }

            } catch (SQLException e) {
                System.out.println("❌ 数据库连接失败：" + e.getMessage() + "，请重新输入！");
            } catch (RuntimeException e) {
                System.out.println("❌ 配置错误：" + e.getMessage() + "，请重新输入！");
            }
        }

        //scanner.close();
        return dbConfig;
    }
    public static User searchRecordByField(
            BaseDBManager<Map<String, Object>> dbManager,
            String targetField,
            Object fieldValue) {

        try {
            List<String> columnNames = getColumnNames(dbManager);
            if (columnNames.isEmpty()) {
                System.out.println("❌ 表结构为空，无法搜索！");
                return null;
            }

            // 1. 验证字段是否存在
            String finalTargetField = columnNames.stream()
                    .filter(col -> col.equalsIgnoreCase(targetField))
                    .findFirst()
                    .orElse(null);
            if (finalTargetField == null) {
                System.out.println("❌ 字段名不存在！可用字段：" + String.join("、", columnNames));
                return null;
            }

            // 2. 打印搜索信息
            String fieldType = getColumnType(dbManager, finalTargetField);
            System.out.printf("搜索字段：%s（类型：%s），搜索值：%s%n",
                    finalTargetField, fieldType, fieldValue);

            // 3. 执行查询
            String sql = String.format(
                    "SELECT * FROM `%s` WHERE `%s` = ?",
                    dbManager.getTableName(),
                    finalTargetField
            );
            Object[] params = {fieldValue};
            List<Map<String, Object>> results = dbManager.getEntityList(sql, params);

            // 4. 处理结果并转换为User对象
            if (results.isEmpty()) {
                System.out.println("❌ 未找到匹配的记录！");
                return null;
            } else {

                // 取第一条记录（若有多个结果，按业务需求选择，这里默认取第一条）
                Map<String, Object> firstRecord = results.get(0);

                // 从数据库记录中提取userId和password（注意：数据库字段名需与这里的key一致）
                // 例如：若数据库字段是user_id，则需改为firstRecord.get("user_id")
                String userIdFromDB = firstRecord.get("userId") != null ? firstRecord.get("userId").toString() : null;
                String passwordFromDB = firstRecord.get("password") != null ? firstRecord.get("password").toString() : null;

                // 校验并创建User对象（处理userId格式校验异常）
                try {
                    User user = new User();
                    // 设置userId（会触发7位数字校验）
                    if (userIdFromDB != null) {
                        user.setUserId(userIdFromDB);
                    }
                    // 设置密码（注意：数据库中应存储密文，这里直接赋值）
                    user.setPassword(passwordFromDB);
                    return user;
                } catch (IllegalArgumentException e) {
                    System.err.println("❌ 用户ID格式错误：" + e.getMessage());
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ 搜索失败：" + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("❌ 操作出错：" + e.getMessage());
            return null;
        }
    }
    /**
     * 根据指定条件字段修改目标字段的内容
     * @param dbManager 数据库管理器实例
     * @param targetField 要修改的目标字段名
     * @param newValueStr 目标字段的新值（字符串形式，将自动转换类型）
     * @param conditionField 条件字段名（用于定位要修改的记录）
     * @param conditionValueStr 条件字段的值（字符串形式，将自动转换类型）
     * @return 是否修改成功
     */
    public static boolean updateRecordByField(
            BaseDBManager<Map<String, Object>> dbManager,
            String targetField,
            String newValueStr,
            String conditionField,
            String conditionValueStr) {

        try {
            // 1. 获取表结构字段列表
            List<String> columnNames = getColumnNames(dbManager);
            if (columnNames.isEmpty()) {
                System.out.println("❌ 表结构为空，无法执行修改操作！");
                return false;
            }

            // 2. 验证目标字段是否存在
            String finalTargetField = columnNames.stream()
                    .filter(col -> col.equalsIgnoreCase(targetField))
                    .findFirst()
                    .orElse(null);
            if (finalTargetField == null) {
                System.out.println("❌ 目标字段不存在！可用字段：" + String.join("、", columnNames));
                return false;
            }

            // 3. 验证条件字段是否存在
            String finalConditionField = columnNames.stream()
                    .filter(col -> col.equalsIgnoreCase(conditionField))
                    .findFirst()
                    .orElse(null);
            if (finalConditionField == null) {
                System.out.println("❌ 条件字段不存在！可用字段：" + String.join("、", columnNames));
                return false;
            }

            // 4. 转换值类型（根据字段类型进行转换）
            Object newValue;
            Object conditionValue;
            try {
                newValue = convertValueByColumnType(dbManager, finalTargetField, newValueStr);
                conditionValue = convertValueByColumnType(dbManager, finalConditionField, conditionValueStr);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ 数值转换失败：" + e.getMessage());
                return false;
            }

            // 5. 构建更新SQL
            String sql = String.format(
                    "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?",
                    dbManager.getTableName(),
                    finalTargetField,
                    finalConditionField
            );
            Object[] params = {newValue, conditionValue};

            // 6. 执行更新操作
            boolean isSuccess = dbManager.updateEntity(sql, params);

            // 7. 输出结果信息
            if (isSuccess) {
                System.out.printf("✅ 成功将字段【%s】的值修改为【%s】（条件：%s = %s）%n",
                        finalTargetField, newValue, finalConditionField, conditionValue);
            } else {
                System.out.printf("❌ 修改失败，未找到匹配条件【%s = %s】的记录或未发生变更%n",
                        finalConditionField, conditionValue);
            }
            return isSuccess;

        } catch (SQLException e) {
            System.err.println("❌ 修改操作失败：" + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("❌ 操作出错：" + e.getMessage());
            return false;
        }
    }
    /**
     * 显示交互式菜单（支持所有操作）
     */
    private static void showInteractiveMenu(BaseDBManager<Map<String, Object>> dbManager) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== 操作菜单 =====");
            System.out.println("1. 添加记录       2. 删除记录       3. 搜索记录");
            System.out.println("4. 查看所有记录   5. 表添加列       6. 表删除列");
            System.out.println("7. 切换数据库配置 8. 切换操作表名   9. 查看表结构");
            System.out.println("10. 显示所有表    11. 退出系统");
            System.out.print("请选择操作（1-11）：");

            // 处理用户输入
            int choice;
            if (!scanner.hasNextInt()) {
                System.out.println("❌ 输入无效！请输入数字1-11");
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            // 执行对应操作
            switch (choice) {
                case 1:
                    addRecordInteractive(dbManager, scanner);
                    break;
                case 2:
                    deleteRecordInteractive(dbManager, scanner);
                    break;
                case 3:
                    searchRecordInteractive(dbManager, scanner);
                    break;
                case 4:
                    viewAllRecords(dbManager);
                    break;
                case 5:
                    addColumnInteractive(dbManager, scanner);
                    break;
                case 6:
                    deleteColumnInteractive(dbManager, scanner);
                    break;
                case 7:
                    switchDatabaseConfig(dbManager, scanner);
                    break;
                case 8:
                    switchTableName(dbManager, scanner);
                    break;
                case 9:
                    dbManager.printTableStructure();
                    break;
                case 10:
                    dbManager.printAllTables();
                    break;
                case 11:
                    System.out.println("✅ 系统已退出，再见！");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ 无效选择！请输入1-11");
            }
        }
    }

    /**
     * 新增：切换操作表名
     */
    private static void switchTableName(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：切换操作表名 ===");
        System.out.printf("当前操作表：%s%n", dbManager.getTableName());

        // 显示数据库中所有表
        dbManager.printAllTables();

        System.out.print("请输入新的表名：");
        String newTableName = scanner.nextLine().trim();

        if (newTableName.isEmpty()) {
            System.out.println("❌ 表名不能为空！");
            return;
        }

        try {
            // 调用BaseDBManager的setter方法切换表名
            dbManager.setTableName(newTableName);
            // 验证表是否存在
            String checkSql = "SELECT 1 FROM " + newTableName + " LIMIT 1";
            dbManager.getEntityList(checkSql);

            // 显示新表结构
            System.out.println("✅ 表名切换成功！");
            dbManager.printTableStructure();

        } catch (Exception e) {
            System.err.println("❌ 切换失败：" + e.getMessage());
        }
    }

    /**
     * 切换数据库配置
     */
    private static void switchDatabaseConfig(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：切换数据库配置 ===");
        System.out.println("📌 提示：直接回车保留当前配置");

        // 1. 输入新的数据库URL
        System.out.printf("当前数据库URL：%s%n", dbManager.getDbUrl());
        System.out.print("请输入新的数据库URL：");
        String newUrl = scanner.nextLine().trim();

        // 2. 输入新的用户名
        System.out.printf("当前用户名：%s%n", dbManager.getDbUsername());
        System.out.print("请输入新的用户名：");
        String newUser = scanner.nextLine().trim();

        // 3. 输入新的密码
        System.out.print("请输入新的密码（直接回车保留当前密码）：");
        String newPwd = scanner.nextLine().trim();

        try {
            // 更新数据库配置
            if (!newUrl.isEmpty()) dbManager.setDbUrl(newUrl);
            if (!newUser.isEmpty()) dbManager.setDbUsername(newUser);
            if (!newPwd.isEmpty()) dbManager.setDbPassword(newPwd);

            // 验证连接有效性
            dbManager.getConnection().close();

            System.out.println("✅ 数据库配置切换成功！");
            System.out.printf("当前配置：URL=%s，用户=%s%n",
                    dbManager.getDbUrl(), dbManager.getDbUsername());

            // 显示数据库中的表
            dbManager.printAllTables();
        } catch (IllegalArgumentException e) {
            System.err.println("❌ 配置无效：" + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ 连接失败：" + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ 切换失败：" + e.getMessage());
        }
    }

    /**
     * 交互式：添加记录（通用化，适配当前表结构）
     */
    private static void addRecordInteractive(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：添加记录 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        try {
            // 获取表字段列表
            List<String> columns = dbManager.getTableColumns();
            if (columns.isEmpty()) {
                System.out.println("❌ 表结构为空，无法添加记录！");
                return;
            }

            // 显示需要输入的字段
            System.out.println("请输入以下字段的值：");
            List<Object> params = new java.util.ArrayList<>();
            StringBuilder columnsSql = new StringBuilder();
            StringBuilder valuesSql = new StringBuilder();

            // 构建SQL和参数
            for (String column : columns) {
                // 跳过自增主键
                if (column.equalsIgnoreCase(dbManager.getPrimaryKey()) &&
                        "INT".equalsIgnoreCase(getColumnType(dbManager, column))) {
                    System.out.println("跳过自增主键：" + column);
                    continue;
                }

                System.out.print(column + "：");
                String value = scanner.nextLine().trim();

                // 处理空值
                if (value.equalsIgnoreCase("null")) {
                    params.add(null);
                } else {
                    // 根据字段类型转换值
                    String columnType = getColumnType(dbManager, column);
                    if (columnType != null && columnType.contains("INT")) {
                        try {
                            params.add(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ 输入不是有效的整数，将按字符串处理");
                            params.add(value);
                        }
                    } else if (columnType != null && (columnType.contains("DECIMAL") || columnType.contains("DOUBLE") || columnType.contains("FLOAT"))) {
                        try {
                            params.add(Double.parseDouble(value));
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ 输入不是有效的数字，将按字符串处理");
                            params.add(value);
                        }
                    } else {
                        params.add(value);
                    }
                }

                if (columnsSql.length() > 0) {
                    columnsSql.append(", ");
                    valuesSql.append(", ");
                }
                columnsSql.append(column);
                valuesSql.append("?");
            }

            // 构建插入SQL
            String addSql = String.format(
                    "INSERT INTO %s (%s) VALUES (%s)",
                    dbManager.getTableName(),
                    columnsSql.toString(),
                    valuesSql.toString()
            );

            boolean success = dbManager.addEntity(addSql, params.toArray());
            System.out.println(success ? "✅ 添加成功！" : "❌ 添加失败！");

        } catch (SQLException e) {
            System.err.println("❌ 添加记录失败：" + e.getMessage());
        }
    }

    /**
     * 交互式：删除记录
     */
    private static void searchRecordInteractive(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：按字段搜索记录 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        try {
            List<String> columnNames = getColumnNames(dbManager);
            if (columnNames.isEmpty()) {
                System.out.println("❌ 表结构为空，无法搜索！");
                return;
            }

            System.out.println("可用字段：" + String.join("、", columnNames));

            // 声明时直接赋初始值（解决“可能尚未赋值”的错误）
            String targetField = "";  // 关键修正：给初始值
            while (true) {
                System.out.print("请输入要搜索的字段名：");
                targetField = scanner.nextLine().trim();  // 循环内重新赋值

                if (targetField.isEmpty()) {
                    System.out.println("❌ 字段名不能为空，请重新输入！");
                    continue;
                }

                // 检查字段是否存在（忽略大小写）
                String finalTargetField1 = targetField;
                boolean fieldExists = columnNames.stream()
                        .anyMatch(col -> col.equalsIgnoreCase(finalTargetField1));
                if (fieldExists) {
                    // 转换为表中实际的字段名（保持大小写一致）
                    String finalTargetField2 = targetField;
                    targetField = columnNames.stream()
                            .filter(col -> col.equalsIgnoreCase(finalTargetField2))
                            .findFirst()
                            .get();
                    break;  // 跳出循环，此时targetField已被正确赋值
                } else {
                    System.out.println("❌ 字段名不存在，请从可用字段中选择！");
                }
            }

            // 错误2修复：将targetField转为final变量后再引用
            final String finalTargetField = targetField;
            String fieldType = getColumnType(dbManager, finalTargetField);
            System.out.printf("请输入%s（类型：%s）的值：", finalTargetField, fieldType);
            String fieldValueStr = scanner.nextLine().trim();

            if (fieldValueStr.isEmpty()) {
                System.out.println("❌ 字段值不能为空！");
                return;
            }

            Object fieldValue;
            try {
                fieldValue = convertValueByColumnType(dbManager, finalTargetField, fieldValueStr);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ " + e.getMessage());
                return;
            }

            String sql = String.format(
                    "SELECT * FROM `%s` WHERE `%s` = ?",
                    dbManager.getTableName(),
                    finalTargetField
            );
            Object[] params = {fieldValue};

            List<Map<String, Object>> results = dbManager.getEntityList(sql, params);

            // 获取查询结果后添加：
            if (results.isEmpty()) {
                System.out.println("❌ 未找到匹配的记录！");
            } else {
                System.out.println("✅ 找到 " + results.size() + " 条匹配记录：");
                // 打印表头
                if (!results.isEmpty()) {
                    StringBuilder header = new StringBuilder();
                    for (String column : results.get(0).keySet()) {
                        header.append(String.format("%-15s", column));
                    }
                    System.out.println(header.toString());
                    // 打印每条记录
                    for (Map<String, Object> record : results) {
                        StringBuilder row = new StringBuilder();
                        for (Object value : record.values()) {
                            String valueStr = value != null ? value.toString() : "NULL";
                            row.append(String.format("%-15s", valueStr));
                        }
                        System.out.println(row.toString());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ 搜索失败：" + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ 操作出错：" + e.getMessage());
        }
    }

    /**
     * 交互式：搜索记录
     */
    /**
     * 交互式：搜索记录（优化主键搜索）
     */

    // 辅助方法：获取表的所有字段名
    private static List<String> getColumnNames(BaseDBManager<Map<String, Object>> dbManager) throws SQLException {
        List<String> columns = new ArrayList<>();
        // 查询一条记录获取字段名（LIMIT 1提高效率）
        String sql = "SELECT * FROM `" + dbManager.getTableName() + "` LIMIT 1";
        List<Map<String, Object>> records = dbManager.getEntityList(sql);
        if (!records.isEmpty()) {
            columns.addAll(records.get(0).keySet());
        }
        return columns;
    }
    /**
     * 查看所有记录
     */
    private static void viewAllRecords(BaseDBManager<Map<String, Object>> dbManager) {
        System.out.println("\n=== 操作：查看所有记录 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        // 先获取记录总数
        String countSql = "SELECT COUNT(*) AS total FROM " + dbManager.getTableName();
        Map<String, Object> countResult = dbManager.getEntity(countSql);
        int total = countResult != null ? Integer.parseInt(countResult.get("total").toString()) : 0;
        System.out.println("表中共有 " + total + " 条记录");

        if (total == 0) {
            return;
        }

        // 分页查询
        Scanner scanner = new Scanner(System.in);
        int pageSize = 10;
        int pageNum = 1;
        int totalPages = (total + pageSize - 1) / pageSize;

        while (true) {
            int offset = (pageNum - 1) * pageSize;
            String sql = "SELECT * FROM " + dbManager.getTableName() +
                    " LIMIT " + pageSize + " OFFSET " + offset;
            List<Map<String, Object>> records = dbManager.getEntityList(sql);

            System.out.println("\n=== 第 " + pageNum + "/" + totalPages + " 页 ===");

            // 打印表头
            if (!records.isEmpty()) {
                StringBuilder header = new StringBuilder();
                for (String column : records.get(0).keySet()) {
                    header.append(String.format("%-15s", column));
                }
                System.out.println(header.toString());

                // 打印记录
                for (Map<String, Object> record : records) {
                    StringBuilder row = new StringBuilder();
                    for (Object value : record.values()) {
                        String valueStr = value != null ? value.toString() : "NULL";
                        row.append(String.format("%-15s", valueStr.length() > 12 ? valueStr.substring(0, 12) + "..." : valueStr));
                    }
                    System.out.println(row.toString());
                }
            }

            // 分页控制
            System.out.print("\n分页操作：(上一页[p]/下一页[n]/首页[f]/末页[l]/退出[q])：");
            String action = scanner.nextLine().trim().toLowerCase();

            if ("q".equals(action)) {
                break;
            } else if ("p".equals(action) && pageNum > 1) {
                pageNum--;
            } else if ("n".equals(action) && pageNum < totalPages) {
                pageNum++;
            } else if ("f".equals(action)) {
                pageNum = 1;
            } else if ("l".equals(action)) {
                pageNum = totalPages;
            } else {
                System.out.println("无效操作，保持当前页");
            }
        }

    }

    /**
     * 交互式：为表添加列
     */
    private static void addColumnInteractive(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：为表添加列 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        try {
            // 获取当前表字段，避免重复添加
            List<String> existingColumns = dbManager.getTableColumns();

            // 输入新列名
            System.out.print("请输入新列名：");
            String columnName = scanner.nextLine().trim();

            if (columnName.isEmpty()) {
                System.out.println("❌ 列名不能为空！");
                return;
            }

            if (existingColumns.contains(columnName.toUpperCase())) {
                System.out.println("❌ 列名已存在！");
                return;
            }

            // 输入数据类型
            System.out.println("常用数据类型：INT, VARCHAR(长度), DECIMAL(长度,小数位), DATE, DATETIME");
            System.out.print("请输入数据类型（例如：VARCHAR(50)）：");
            String dataType = scanner.nextLine().trim();

            if (dataType.isEmpty()) {
                System.out.println("❌ 数据类型不能为空！");
                return;
            }

            // 是否允许为空
            System.out.print("是否允许为空？(y/n)：");
            String nullableInput = scanner.nextLine().trim();
            boolean isNullable = "y".equalsIgnoreCase(nullableInput);

            // 默认值
            String defaultValue = null;
            if (!isNullable) {
                System.out.print("请输入默认值：");
                defaultValue = scanner.nextLine().trim();
            } else {
                System.out.print("是否设置默认值？(y/n)：");
                String setDefault = scanner.nextLine().trim();
                if ("y".equalsIgnoreCase(setDefault)) {
                    System.out.print("请输入默认值：");
                    defaultValue = scanner.nextLine().trim();
                }
            }

            // 执行添加列操作
            boolean success = dbManager.addColumn(columnName, dataType, isNullable, defaultValue);
            if (success) {
                System.out.println("✅ 列添加成功！");
                System.out.println("更新后的表结构：");
                dbManager.printTableStructure();
            } else {
                System.out.println("❌ 列添加失败！");
            }

        } catch (SQLException e) {
            System.err.println("❌ 添加列失败：" + e.getMessage());
        }
    }

    /**
     * 交互式：从表中删除列
     */
    private static void deleteColumnInteractive(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：从表中删除列 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        try {
            // 显示当前表的所有列
            List<String> columns = dbManager.getTableColumns();
            System.out.println("当前表的列：");
            for (int i = 0; i < columns.size(); i++) {
                System.out.println((i + 1) + ". " + columns.get(i) +
                        (columns.get(i).equalsIgnoreCase(dbManager.getPrimaryKey()) ? " [主键]" : ""));
            }

            // 输入要删除的列名
            System.out.print("请输入要删除的列名：");
            String columnName = scanner.nextLine().trim();

            if (columnName.isEmpty()) {
                System.out.println("❌ 列名不能为空！");
                return;
            }

            // 检查是否为主键
            if (columnName.equalsIgnoreCase(dbManager.getPrimaryKey())) {
                System.out.println("❌ 不能删除主键列！");
                return;
            }

            // 二次确认
            System.out.print("确认要删除列 '" + columnName + "' 吗？(y/n)：");
            String confirm = scanner.nextLine().trim();
            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("操作已取消");
                return;
            }

            // 执行删除列操作
            boolean success = dbManager.deleteColumn(columnName);
            if (success) {
                System.out.println("✅ 列删除成功！");
                System.out.println("更新后的表结构：");
                dbManager.printTableStructure();
            } else {
                System.out.println("❌ 列删除失败！");
            }

        } catch (SQLException e) {
            System.err.println("❌ 删除列失败：" + e.getMessage());
        }
    }

    // 辅助方法：获取字段类型
    private static String getColumnType(BaseDBManager<Map<String, Object>> dbManager, String columnName) throws SQLException {
        try (Connection conn = dbManager.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            // 查询字段元数据
            try (ResultSet rs = metaData.getColumns(
                    null, null, dbManager.getTableName(), columnName)) {
                if (rs.next()) {
                    return rs.getString("TYPE_NAME"); // 返回字段类型（如VARCHAR、INT）
                }
            }
        }
        return "未知类型";
    }

    /**
     * 辅助方法：根据列类型转换值
     */
    /**
     * 辅助方法：根据列类型转换值（强化校验）
     */
    private static Object convertValueByColumnType(BaseDBManager<Map<String, Object>> dbManager,
                                                   String columnName, String valueStr) throws SQLException {
        String columnType = getColumnType(dbManager, columnName);

        if (columnType != null && columnType.contains("INT")) {
            try {
                return Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                // 转换失败直接抛异常，由调用方处理提示
                throw new IllegalArgumentException("主键值必须是整数！");
            }
        } else if (columnType != null && (columnType.contains("DECIMAL") || columnType.contains("DOUBLE") || columnType.contains("FLOAT"))) {
            try {
                return Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("主键值必须是数字！");
            }
        } else if (columnType != null && (columnType.contains("VARCHAR") || columnType.contains("CHAR"))) {
            // 字符串类型无需转换，但可根据需求添加长度校验
            return valueStr;
        }

        // 其他类型（如日期）可根据实际需求添加转换逻辑
        return valueStr;
    }

    //------------------------------------------------------------------
    /**
     * 创建指定名称的数据库（若不存在）
     * @param dbUrl 数据库连接基础URL（如：jdbc:mysql://localhost:3306/）
     * @param username 数据库用户名
     * @param password 数据库密码
     * @param dbName 要创建的数据库名称
     * @return 是否创建成功
     */
    public static boolean createDatabase(String dbUrl, String username, String password, String dbName) {
        if (dbName == null || dbName.trim().isEmpty()) {
            System.out.println("❌ 数据库名称不能为空！");
            return false;
        }
        // 移除URL中可能包含的数据库名，连接到默认数据库（如mysql）
        String baseUrl = dbUrl.replaceAll("/[^/]+\\?", "?"); // 处理格式：jdbc:mysql://host:port/db?params → jdbc:mysql://host:port/?params
        if (!baseUrl.contains("?")) {
            baseUrl += "/";
        }

        // 临时连接管理器（用于执行创建数据库操作）
        BaseDBManager<Map<String, Object>> tempManager = new BaseDBManager<>(
                baseUrl, username, password,
                "", // 无需指定表名
                rs -> new HashMap<>()
        );

        try (Connection conn = tempManager.getConnection()) {
            // 执行创建数据库SQL（避免重复创建）
            String sql = String.format("CREATE DATABASE IF NOT EXISTS `%s` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", dbName);
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.printf("✅ 数据库【%s】创建成功（或已存在）%n", dbName);
                return true;
            }
        } catch (SQLException e) {
            System.err.printf("❌ 创建数据库【%s】失败：%s%n", dbName, e.getMessage());
            return false;
        }
    }
    /**
     * 在指定数据库中创建表（需先切换到目标数据库）
     * @param dbManager 数据库管理器（需已连接到目标数据库）
     * @param tableName 表名
     * @param columns 字段定义列表（格式："字段名 类型 约束"，如："id INT PRIMARY KEY AUTO_INCREMENT", "name VARCHAR(50) NOT NULL"）
     * @return 是否创建成功
     */
    public static boolean createTable(BaseDBManager<Map<String, Object>> dbManager, String tableName, List<String> columns) {
        if (tableName == null || tableName.trim().isEmpty()) {
            System.out.println("❌ 表名不能为空！");
            return false;
        }
        if (columns == null || columns.isEmpty()) {
            System.out.println("❌ 字段定义不能为空！");
            return false;
        }

        try {
            // 检查表是否已存在
            List<String> allTables = dbManager.getAllTableNames();
            if (allTables.stream().anyMatch(t -> t.equalsIgnoreCase(tableName))) {
                System.out.printf("⚠️ 表【%s】已存在，无需重复创建%n", tableName);
                return true;
            }

            // 构建CREATE TABLE语句
            String columnsSql = String.join(", ", columns);
            String sql = String.format("CREATE TABLE `%s` (%s)", tableName, columnsSql);

            try (Connection conn = dbManager.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.printf("✅ 表【%s】创建成功%n", tableName);
                // 切换当前管理器的操作表
                dbManager.setTableName(tableName);
                return true;
            }
        } catch (SQLException e) {
            System.err.printf("❌ 创建表【%s】失败：%s%n", tableName, e.getMessage());
            return false;
        }
    }
    /**
     * 批量插入记录
     * @param dbManager 数据库管理器实例
     * @param batchData 批量数据（外层List为多条记录，内层List为单条记录的字段值）
     * @return 成功插入的记录数
     */
    public static int batchInsertRecords(
            BaseDBManager<Map<String, Object>> dbManager,
            List<List<String>> batchData) {

        if (batchData == null || batchData.isEmpty()) {
            System.out.println("❌ 批量数据为空，无需插入！");
            return 0;
        }

        try {
            // 1. 获取表字段列表（排除自增主键）
            List<String> columns = new ArrayList<>();
            List<String> allColumns = dbManager.getTableColumns();
            String primaryKey = dbManager.getPrimaryKey();

            for (String column : allColumns) {
                // 跳过自增主键（假设INT类型主键为自增）
                if (column.equalsIgnoreCase(primaryKey) &&
                        "INT".equalsIgnoreCase(getColumnType(dbManager, column))) {
                    System.out.println("跳过自增主键：" + column);
                    continue;
                }
                columns.add(column);
            }

            if (columns.isEmpty()) {
                System.out.println("❌ 表中无可用插入字段（可能全为自增主键）！");
                return 0;
            }

            // 2. 校验每条记录的字段数量是否匹配
            int fieldCount = columns.size();
            for (int i = 0; i < batchData.size(); i++) {
                if (batchData.get(i).size() != fieldCount) {
                    throw new IllegalArgumentException(
                            "第" + (i + 1) + "条记录字段数不匹配（预期：" + fieldCount + "，实际：" + batchData.get(i).size() + "）");
                }
            }

            // 3. 构建批量插入SQL
            StringBuilder columnsSql = new StringBuilder();
            StringBuilder valuesSql = new StringBuilder();
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    columnsSql.append(", ");
                    valuesSql.append(", ");
                }
                columnsSql.append("`").append(columns.get(i)).append("`");
                valuesSql.append("?");
            }

            String sql = String.format(
                    "INSERT INTO `%s` (%s) VALUES (%s)",
                    dbManager.getTableName(),
                    columnsSql.toString(),
                    valuesSql.toString()
            );

            // 4. 执行批量插入（使用PreparedStatement批处理）
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                // 关闭自动提交，手动控制事务
                conn.setAutoCommit(false);

                // 添加批量参数
                for (List<String> record : batchData) {
                    for (int i = 0; i < record.size(); i++) {
                        String column = columns.get(i);
                        String valueStr = record.get(i).trim();

                        // 转换值类型（参考addRecordInteractive的逻辑）
                        Object value;
                        if (valueStr.equalsIgnoreCase("null")) {
                            value = null;
                        } else {
                            String columnType = getColumnType(dbManager, column);
                            if (columnType != null && columnType.contains("INT")) {
                                try {
                                    value = Integer.parseInt(valueStr);
                                } catch (NumberFormatException e) {
                                    System.out.println("⚠️ 第" + (batchData.indexOf(record) + 1) + "条记录的" + column + "不是整数，按字符串处理");
                                    value = valueStr;
                                }
                            } else if (columnType != null && (columnType.contains("DECIMAL") || columnType.contains("DOUBLE") || columnType.contains("FLOAT"))) {
                                try {
                                    value = Double.parseDouble(valueStr);
                                } catch (NumberFormatException e) {
                                    System.out.println("⚠️ 第" + (batchData.indexOf(record) + 1) + "条记录的" + column + "不是数字，按字符串处理");
                                    value = valueStr;
                                }
                            } else {
                                value = valueStr;
                            }
                        }

                        pstmt.setObject(i + 1, value);
                    }
                    pstmt.addBatch(); // 添加到批处理
                }

                // 执行批处理并获取结果
                int[] results = pstmt.executeBatch();
                conn.commit(); // 提交事务

                // 统计成功插入的记录数
                int successCount = 0;
                for (int result : results) {
                    if (result > 0) {
                        successCount++;
                    }
                }

                System.out.printf("✅ 批量插入完成，共处理%d条记录，成功%d条%n", batchData.size(), successCount);
                return successCount;

            } catch (SQLException e) {
                System.err.println("❌ 批量插入失败：" + e.getMessage());
                throw e;
            }

        } catch (SQLException e) {
            System.err.println("❌ 表结构获取失败：" + e.getMessage());
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("❌ 数据校验失败：" + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("❌ 批量插入出错：" + e.getMessage());
            return 0;
        }
    }

//    // 补充getColumnType方法（现有代码中可能未显式实现，用于获取字段类型）
//    private static String getColumnType(BaseDBManager<Map<String, Object>> dbManager, String columnName) throws SQLException {
//        String sql = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
//                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
//        Object[] params = {dbManager.getTableName(), columnName};
//        Map<String, Object> result = dbManager.getEntity(sql, params);
//        return result != null ? result.get("DATA_TYPE").toString() : null;
//    }

























}






