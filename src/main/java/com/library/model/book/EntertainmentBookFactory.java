package com.library.model.book;

import com.library.model.Book;

public class EntertainmentBookFactory implements BookFactory {
    @Override
    public Book createBook(int id, String title, String author) {
        return new EBook(id, title, author); // Ví dụ: Sách giải trí là sách điện tử
    }
    private static class EntertainmentBook extends Book {
        public EntertainmentBook(int id, String title, String author) {
            super(id, title, author);
        }

        @Override
        public String getType() {
            return "Entertainment";
        }
    }
}