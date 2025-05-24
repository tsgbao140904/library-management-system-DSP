package com.library.model.book;

import com.library.model.Book;

public class EBook extends Book {
    public EBook(int id, String title, String author) {
        super(id, title, author);
    }

    @Override
    public String getType() {
        return "EBook";
    }
}