package com.vcampus.common.dao;

import com.vcampus.common.dto.Book;

import java.util.List;

public interface ILibraryDao {
    /*
     * 通过书名查询书籍信息
     *
     * */
    Book getBookByName(String bookName);

    /*
     * 通过书名查询书籍信息
     *
     * */
    Book getBookById(String bookId);

    /*
     * 通过作者模糊搜索书籍信息
     * */
    List<Book> getBookByAuthor(String author);

    /*
     * 插入新的书籍信息
     * */
    boolean insertBook(Book book);

    /*
     *
     * 更新书籍信息
     * */
    boolean updateBook(Book book);

    /*
    *
    删除书籍信息
    * */
    boolean deleteBook(Book book);

    /*
     *
     * 查询所有书籍信息
     * */
    List<Book> getAllBooks();
}
