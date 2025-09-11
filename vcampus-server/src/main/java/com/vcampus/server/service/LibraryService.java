package com.vcampus.server.service;

import com.vcampus.common.dto.Book;
import com.vcampus.common.dto.Student;
import com.vcampus.server.dao.impl.LibraryDao;

public class LibraryService {

    private LibraryDao libraryDao;

    public LibraryService() {
        this.libraryDao = new LibraryDao();
    }
    public Book getBookById(String bookId) {
        if (bookId == null || bookId.isEmpty()) {
            return null;
        }
        return null;
    }

}