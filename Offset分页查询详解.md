# Offset 分页查询详解

## 1. Offset 基本概念

**Offset（偏移量）** 是数据库分页查询的核心参数，表示从结果集的第几条记录开始返回数据。

### 计算公式
```
offset = (页码 - 1) × 每页大小
```

## 2. 实际应用示例

### 示例1：每页10条记录的分页

假设数据库中有100条邮件记录，每页显示10条：

| 页码 | Offset 计算 | Offset 值 | 返回记录范围 |
|------|-------------|-----------|-------------|
| 第1页 | (1-1) × 10 | 0 | 第1-10条 |
| 第2页 | (2-1) × 10 | 10 | 第11-20条 |
| 第3页 | (3-1) × 10 | 20 | 第21-30条 |
| 第4页 | (4-1) × 10 | 30 | 第31-40条 |
| ... | ... | ... | ... |
| 第10页 | (10-1) × 10 | 90 | 第91-100条 |

### 示例2：每页20条记录的分页

| 页码 | Offset 计算 | Offset 值 | 返回记录范围 |
|------|-------------|-----------|-------------|
| 第1页 | (1-1) × 20 | 0 | 第1-20条 |
| 第2页 | (2-1) × 20 | 20 | 第21-40条 |
| 第3页 | (3-1) × 20 | 40 | 第41-60条 |

## 3. 在 EmailMapper.xml 中的应用

### 收件箱查询
```xml
<select id="getInboxEmails" resultMap="EmailResultMap">
    SELECT * FROM tb_email 
    WHERE recipientId = #{userId} AND status IN ('SENT', 'READ')
    ORDER BY sendTime DESC 
    LIMIT #{offset}, #{limit}
</select>
```

### 发件箱查询
```xml
<select id="getSentEmails" resultMap="EmailResultMap">
    SELECT * FROM tb_email 
    WHERE senderId = #{userId} AND status IN ('SENT', 'READ')
    ORDER BY sendTime DESC 
    LIMIT #{offset}, #{limit}
</select>
```

### 搜索邮件
```xml
<select id="searchEmails" resultMap="EmailResultMap">
    SELECT * FROM tb_email 
    WHERE (senderId = #{userId} OR recipientId = #{userId}) 
    AND (subject LIKE CONCAT('%', #{keyword}, '%') 
         OR content LIKE CONCAT('%', #{keyword}, '%') 
         OR senderId LIKE CONCAT('%', #{keyword}, '%') 
         OR recipientId LIKE CONCAT('%', #{keyword}, '%'))
    ORDER BY sendTime DESC 
    LIMIT #{offset}, #{limit}
</select>
```

## 4. Java 代码中的 Offset 计算

### EmailMapper.java 接口
```java
/**
 * 获取用户的收件箱邮件
 * @param userId 用户ID
 * @param offset 偏移量
 * @param limit 限制数量
 * @return 邮件列表
 */
List<Email> getInboxEmails(@Param("userId") String userId, 
                          @Param("offset") int offset, 
                          @Param("limit") int limit);
```

### DAO 层实现
```java
public List<Email> getInboxEmails(String userId, int page, int pageSize) {
    try (SqlSession sqlSession = MyBatisUtil.openSession()) {
        EmailMapper mapper = sqlSession.getMapper(EmailMapper.class);
        int offset = (page - 1) * pageSize;  // 计算偏移量
        return mapper.getInboxEmails(userId, offset, pageSize);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}
```

## 5. 完整的调用流程

### 前端请求
```javascript
// 前端请求第2页，每页10条记录
const page = 2;
const pageSize = 10;
```

### 后端处理
```java
// 1. 计算 offset
int offset = (page - 1) * pageSize;  // (2-1) * 10 = 10

// 2. 调用 Mapper
List<Email> emails = mapper.getInboxEmails(userId, offset, pageSize);

// 3. 生成的 SQL
// SELECT * FROM tb_email 
// WHERE recipientId = 'user001' AND status IN ('SENT', 'READ')
// ORDER BY sendTime DESC 
// LIMIT 10, 10
```

## 6. Offset 的优势

### 性能优势
- **减少数据传输**: 只返回需要的数据，不返回全部记录
- **降低内存使用**: 避免一次性加载大量数据
- **提高响应速度**: 用户能更快看到结果

### 用户体验优势
- **分页浏览**: 用户可以逐页查看数据
- **快速定位**: 可以快速跳转到指定页面
- **减少等待**: 每次只加载少量数据

## 7. Offset 的注意事项

### 边界情况处理
```java
// 确保页码和页面大小有效
if (page < 1) page = 1;
if (pageSize < 1) pageSize = 10;
if (pageSize > 100) pageSize = 100;  // 限制最大页面大小
```

### 总数计算
```java
// 需要提供总数用于计算总页数
public int getEmailCount(String userId) {
    // 返回该用户的邮件总数
    // 用于计算: 总页数 = (总数 + 页面大小 - 1) / 页面大小
}
```

## 8. 实际使用示例

### 服务层调用
```java
public class EmailService {
    private EmailDao emailDao = new EmailDao();
    
    public List<Email> getInbox(String userId, int page, int pageSize) {
        // 参数验证
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;
        
        // 调用 DAO
        return emailDao.getInboxEmails(userId, page, pageSize);
    }
    
    public int getTotalPages(String userId, int pageSize) {
        int totalCount = emailDao.getEmailCount(userId);
        return (totalCount + pageSize - 1) / pageSize;
    }
}
```

### 控制器层调用
```java
@RestController
public class EmailController {
    
    @GetMapping("/emails/inbox")
    public ResponseEntity<List<Email>> getInbox(
            @RequestParam String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        List<Email> emails = emailService.getInbox(userId, page, pageSize);
        return ResponseEntity.ok(emails);
    }
}
```

## 9. 总结

Offset 是分页查询的核心概念：

1. **计算公式**: `offset = (页码 - 1) × 每页大小`
2. **SQL 语法**: `LIMIT offset, limit`
3. **性能优势**: 减少数据传输，提高响应速度
4. **用户体验**: 支持分页浏览，快速定位数据
5. **注意事项**: 需要处理边界情况和提供总数统计

在您的邮件系统中，offset 确保了用户能够高效地浏览大量邮件数据，提供了良好的用户体验。


