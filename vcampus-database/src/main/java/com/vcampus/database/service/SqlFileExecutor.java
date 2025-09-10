package com.vcampus.database.service;
import org.apache.ibatis.session.SqlSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlFileExecutor {

    /**
     * 执行 SQL 文件
     * @param sqlSession MyBatis 的 SqlSession
     * @param sqlFilePath SQL 文件在 resources 下的路径（如 "init-data.sql"）
     * @throws Exception 可能抛出 IO 异常或 SQL 异常
     */
    public static void executeSqlFile(SqlSession sqlSession, String sqlFilePath) throws Exception {
        // 1. 读取 SQL 文件内容
        String sqlContent = readSqlFile(sqlFilePath);

        // 2. 分割 SQL 语句（处理分号、换行、注释）
        List<String> sqlStatements = splitSqlStatements(sqlContent);

        // 3. 获取数据库连接，执行每个 SQL 语句
        Connection connection = sqlSession.getConnection();
        Statement statement = connection.createStatement();

        try {
            // 关闭自动提交，批量执行后统一提交
            connection.setAutoCommit(false);

            for (String sql : sqlStatements) {
                if (sql.trim().isEmpty()) continue; // 跳过空行
                statement.execute(sql);
            }

            connection.commit(); // 提交事务
            System.out.println("SQL 文件执行成功，共执行 " + sqlStatements.size() + " 条语句");
        } catch (Exception e) {
            connection.rollback(); // 出错时回滚
            throw new Exception("SQL 文件执行失败：" + e.getMessage(), e);
        } finally {
            statement.close();
            // 注意：不要关闭 connection，由 MyBatis 管理
        }
    }

    /**
     * 读取 resources 目录下的 SQL 文件
     */
    private static String readSqlFile(String filePath) throws IOException {
        try (InputStream is = SqlFileExecutor.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            if (is == null) {
                throw new IOException("SQL 文件不存在：" + filePath);
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                // 过滤注释行（简单处理 -- 注释）
                if (!line.trim().startsWith("--")) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString();
        }
    }

    /**
     * 分割 SQL 语句（按分号分割，处理分号在字符串中的情况）
     */
    private static List<String> splitSqlStatements(String sqlContent) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentSql = new StringBuilder();
        boolean inQuotes = false; // 标记是否在单引号中（避免分割字符串内的分号）

        for (char c : sqlContent.toCharArray()) {
            if (c == '\'') {
                inQuotes = !inQuotes; // 切换引号状态
            }

            if (c == ';' && !inQuotes) {
                // 遇到分号且不在引号中，分割语句
                statements.add(currentSql.toString().trim());
                currentSql.setLength(0); // 重置
            } else {
                currentSql.append(c);
            }
        }

        // 处理最后一条没有分号结尾的语句
        if (currentSql.length() > 0) {
            statements.add(currentSql.toString().trim());
        }

        return statements;
    }
}
