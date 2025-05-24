package com.library.model.decorator;

import com.library.model.Book;

public class FavoriteBookDecorator extends BookDecorator {
    public FavoriteBookDecorator(Book decoratedBook) {
        super(decoratedBook);
    }

    public String markAsFavorite() {
        return "Book '" + getTitle() + "' is marked as favorite.";
    }
}