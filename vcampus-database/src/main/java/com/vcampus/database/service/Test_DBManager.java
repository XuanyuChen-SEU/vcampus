package com.vcampus.database.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 基于BaseDBManager的交互式测试类
 * 支持数据库配置和表名动态切换，自动扫描表字段
 */
public class Test_DBManager {
    public static void main(String[] args) {
        // 1. 初始配置
        String dbUrl = "jdbc:mysql://localhost:3306/test?useSSL=false&serverTimezone=UTC";
        String dbUser = "root";
        String dbPwd = "Abcd0410";
        String initialTable = "EMP"; // 初始表名

        try {
            // 2. 创建数据库管理实例（使用Map存储记录，支持任意表结构）
            BaseDBManager<Map<String, Object>> dbManager = new BaseDBManager<>(
                    dbUrl, dbUser, dbPwd,
                    initialTable,
                    // 结果集转换器（动态适配任意表结构）
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

            System.out.println("=== 数据库管理系统（支持多表操作）===");
            System.out.println("初始连接：" + dbUrl);
            System.out.println("初始操作表：" + initialTable + "\n");

            // 显示当前表结构
            dbManager.printTableStructure();

            // 3. 进入交互式菜单
            showInteractiveMenu(dbManager);

        } catch (RuntimeException e) {
            System.err.println("系统启动失败：" + e.getMessage());
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
                case 1: addRecordInteractive(dbManager, scanner); break;
                case 2: deleteRecordInteractive(dbManager, scanner); break;
                case 3: searchRecordInteractive(dbManager, scanner); break;
                case 4: viewAllRecords(dbManager); break;
                case 5: addColumnInteractive(dbManager, scanner); break;
                case 6: deleteColumnInteractive(dbManager, scanner); break;
                case 7: switchDatabaseConfig(dbManager, scanner); break;
                case 8: switchTableName(dbManager, scanner); break;
                case 9: dbManager.printTableStructure(); break;
                case 10: dbManager.printAllTables(); break;
                case 11:
                    System.out.println("✅ 系统已退出，再见！");
                    scanner.close();
                    return;
                default: System.out.println("❌ 无效选择！请输入1-11");
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
    public static void deleteRecordInteractive(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：删除记录 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        try {
            // 获取主键信息
            String primaryKey = dbManager.getPrimaryKey();
            if (primaryKey == null) {
                System.out.println("⚠️ 未找到主键，将使用条件删除");

                // 让用户输入删除条件
                System.out.print("请输入删除条件（例如：name = '张三'）：");
                String condition = scanner.nextLine().trim();

                if (condition.isEmpty()) {
                    System.out.println("❌ 删除条件不能为空！");
                    return;
                }

                // 二次确认
                System.out.print("确认删除符合条件的所有记录？(y/n)：");
                String confirm = scanner.nextLine().trim();
                if (!"y".equalsIgnoreCase(confirm)) {
                    System.out.println("操作已取消");
                    return;
                }

                String deleteSql = "DELETE FROM " + dbManager.getTableName() + " WHERE " + condition;
                boolean success = dbManager.deleteEntity(deleteSql);
                System.out.println(success ? "✅ 删除成功！" : "❌ 删除失败！");
            } else {
                // 使用主键删除
                System.out.println("将按主键删除记录，主键字段：" + primaryKey);
                System.out.print("请输入要删除的" + primaryKey + "值：");
                String pkValue = scanner.nextLine().trim();

                if (pkValue.isEmpty()) {
                    System.out.println("❌ 主键值不能为空！");
                    return;
                }

                String deleteSql = "DELETE FROM " + dbManager.getTableName() + " WHERE " + primaryKey + " = ?";
                boolean success = dbManager.deleteEntity(deleteSql, convertValueByColumnType(dbManager, primaryKey, pkValue));
                System.out.println(success ? "✅ 删除成功！" : "❌ 删除失败或记录不存在！");
            }

        } catch (SQLException e) {
            System.err.println("❌ 删除记录失败：" + e.getMessage());
        }
    }

    /**
     * 交互式：搜索记录
     */
    private static void searchRecordInteractive(BaseDBManager<Map<String, Object>> dbManager, Scanner scanner) {
        System.out.println("\n=== 操作：搜索记录 ===");
        System.out.println("当前操作表：" + dbManager.getTableName());

        try {
            // 让用户选择搜索方式
            System.out.println("1. 按条件搜索   2. 按主键搜索");
            System.out.print("请选择搜索方式（1-2）：");
            int searchType;
            if (!scanner.hasNextInt()) {
                System.out.println("❌ 输入无效！");
                scanner.nextLine();
                return;
            }
            searchType = scanner.nextInt();
            scanner.nextLine();

            String sql;
            Object[] params = null;

            if (searchType == 1) {
                // 按条件搜索
                System.out.print("请输入查询条件（例如：age > 30，直接回车查询所有）：");
                String condition = scanner.nextLine().trim();

                if (condition.isEmpty()) {
                    sql = "SELECT * FROM " + dbManager.getTableName();
                } else {
                    sql = "SELECT * FROM " + dbManager.getTableName() + " WHERE " + condition;
                }
            } else {
                // 按主键搜索
                String primaryKey = dbManager.getPrimaryKey();
                if (primaryKey == null) {
                    System.out.println("❌ 未找到主键，无法按主键搜索！");
                    return;
                }

                System.out.print("请输入" + primaryKey + "值：");
                String pkValue = scanner.nextLine().trim();

                if (pkValue.isEmpty()) {
                    System.out.println("❌ 主键值不能为空！");
                    return;
                }

                sql = "SELECT * FROM " + dbManager.getTableName() + " WHERE " + primaryKey + " = ?";
                params = new Object[]{convertValueByColumnType(dbManager, primaryKey, pkValue)};
            }

            // 执行查询
            List<Map<String, Object>> results;
            if (params == null) {
                results = dbManager.getEntityList(sql);
            } else {
                results = dbManager.getEntityList(sql, params);
            }

            // 显示结果
            System.out.println("\n=== 搜索结果（共" + results.size() + "条） ===");
            if (results.isEmpty()) {
                System.out.println("未找到匹配的记录");
                return;
            }

            // 打印表头
            StringBuilder header = new StringBuilder();
            for (String column : results.get(0).keySet()) {
                header.append(String.format("%-15s", column));
            }
            System.out.println(header.toString());

            // 打印记录
            for (Map<String, Object> record : results) {
                StringBuilder row = new StringBuilder();
                for (Object value : record.values()) {
                    String valueStr = value != null ? value.toString() : "NULL";
                    row.append(String.format("%-15s", valueStr.length() > 12 ? valueStr.substring(0, 12) + "..." : valueStr));
                }
                System.out.println(row.toString());
            }

        } catch (SQLException e) {
            System.err.println("❌ 搜索记录失败：" + e.getMessage());
        }
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

    /**
     * 辅助方法：获取列的数据类型
     */
    private static String getColumnType(BaseDBManager<Map<String, Object>> dbManager, String columnName) throws SQLException {
        try (Connection conn = dbManager.getConnection();
             ResultSet rs = conn.getMetaData().getColumns(
                     null, null, dbManager.getTableName().toUpperCase(), columnName.toUpperCase()
             )) {
            if (rs.next()) {
                return rs.getString("TYPE_NAME");
            }
        }
        return null;
    }

    /**
     * 辅助方法：根据列类型转换值
     */
    private static Object convertValueByColumnType(BaseDBManager<Map<String, Object>> dbManager,
                                                   String columnName, String valueStr) throws SQLException {
        String columnType = getColumnType(dbManager, columnName);

        if (columnType != null && columnType.contains("INT")) {
            try {
                return Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                // 转换失败则返回原始字符串
                return valueStr;
            }
        } else if (columnType != null && (columnType.contains("DECIMAL") || columnType.contains("DOUBLE") || columnType.contains("FLOAT"))) {
            try {
                return Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                return valueStr;
            }
        }

        // 其他类型直接返回字符串
        return valueStr;
    }
}
