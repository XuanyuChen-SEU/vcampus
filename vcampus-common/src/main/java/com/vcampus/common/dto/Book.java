package com.vcampus.common.dto;

import java.io.Serializable; // 【修复1】导入 Serializable

public class Book implements Serializable { // 【修复1】实现 Serializable 接口

    private static final long serialVersionUID = 1L; // 【推荐】添加 serialVersionUID

    private String bookId;
    private String bookName;
    private String author;
    private String ISBN;
    private String publisher;
    private String description;
    private String borrowStatus;

    public Book() {
    }

    // 这个全参数构造函数是正确的
    public Book(String bookId, String bookName, String author, String ISBN, String publisher, String description, String borrowStatus) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.ISBN = ISBN;
        this.publisher = publisher;
        this.description = description;
        this.borrowStatus = borrowStatus;
    }



    // 【修复2】删除了那个错误的构造函数
    // public Book(String b001, String 深入理解Java虚拟机, ...) { ... }

    // --- Getters 和 Setters ---
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 【修复4】修正了方法命名
    public String getBorrowStatus() {
        return borrowStatus;
    }

    // 【修复3 & 4】修正了变量赋值和方法命名
    public void setBorrowStatus(String borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    @Override
    public String toString() {
        // ... (toString 方法是好的，无需修改)
        return "Book{" + "bookId='" + bookId + '\'' + ", bookName='" + bookName + '\'' + '}';
    }
}