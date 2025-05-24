package com.library.model.book;

import com.library.model.Book;

public class AcademicBookFactory implements BookFactory {
    @Override
    public Book createBook(int id, String title, String author) {
        return new PrintedBook(id, title, author); // Ví dụ: Sách học thuật là sách in
    }
}