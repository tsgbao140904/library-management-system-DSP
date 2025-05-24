package com.library.model.decorator;

import com.library.model.Book;

public abstract class BookDecorator extends Book {
    protected Book decoratedBook;

    public BookDecorator(Book decoratedBook) {
        super(decoratedBook.getId(), decoratedBook.getTitle(), decoratedBook.getAuthor());
        this.decoratedBook = decoratedBook;
    }

    @Override
    public String getType() {
        return decoratedBook.getType();
    }
}