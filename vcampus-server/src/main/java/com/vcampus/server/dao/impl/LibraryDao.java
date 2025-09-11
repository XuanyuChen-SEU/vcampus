package com.vcampus.server.dao.impl;

import com.vcampus.common.dao.ILibraryDao;
import com.vcampus.common.dto.Book;

import java.util.List;

public class LibraryDao implements ILibraryDao {
    @Override
    public Book getBookByName(String bookName) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Book getBookById(String bookId) {return null;}
    @Override
    public List<Book> getBookByAuthor(String author) {
        return null;
    }
    @Override
    public boolean insertBook(Book book) {
        return false;
    }
    @Override
    public boolean updateBook(Book book) {
        return false;
    }
    @Override
    public boolean deleteBook(Book book) {
        return false;
    }
    @Override
    public List<Book> getAllBooks() {
        return null;
    }
}
