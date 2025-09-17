package com.vcampus.common.dto;

import java.io.Serializable;

public class BorrowLog implements Serializable {
    private  final long serialVersionUID = 1L;
    private String logId;
    private String bookId;
    private String bookName;
    private String userId;
    private String username;    // 【新增】借书人姓名
    private String borrowDate;  // 【新增】借书日期
    private String dueDate;     // 应还日期

    // 【更新】构造函数，加入了 username 和 borrowDate
    public BorrowLog(String logId, String bookId, String bookName, String userId, String username, String borrowDate, String dueDate) {
        this.logId=logId;
        this.bookId = bookId;
        this.bookName = bookName;
        this.userId = userId;
        this.username = username;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    // 【新增】这个构造函数用于在服务器端创建一条新的借阅记录时使用
    // 它允许我们先创建一个不含 logId 的对象，之后再为其设置生成的 UUID
    public BorrowLog(String bookId, String bookName, String userId, String username, String borrowDate, String dueDate) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.userId = userId;
        this.username = username;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public BorrowLog() {

    }


    // --- 所有字段的 Getters 和 Setters ---
    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    // 【新增】username 的 Getter/Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // 【新增】borrowDate 的 Getter/Setter
    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}