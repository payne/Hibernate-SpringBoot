package com.bookstore.service;

import com.bookstore.entity.Author;
import com.bookstore.repository.AuthorRepository;
import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
public class BookstoreService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public BookstoreService(AuthorRepository authorRepository,
                            BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    public void soutAuthorAge38() {
        Optional<Author> optional = authorRepository.findByAge(38);
        Assert.isTrue(optional.isPresent());
        Author alicia = optional.get();
        System.out.println(alicia);
        alicia.getBooks().forEach(b -> System.out.println(b));
    }

    @Transactional
    public void addSomeMore() {
        Optional<Author> optional = authorRepository.findByAge(38);
        Assert.isTrue(optional.isPresent());
        Author alicia = optional.get();

        Optional<Book> optionalBook = bookRepository.findByIsbn("002-AT-MJ");
        Assert.isTrue(optionalBook.isPresent());
        Book book1 = optionalBook.get();

        alicia.addBook(book1);

        authorRepository.save(alicia);
    }

    @Transactional
    public void insertAuthorsWithBooks() {

        Author alicia = new Author();
        alicia.setName("Alicia Tom");
        alicia.setAge(38);
        alicia.setGenre("Anthology");

        Author mark = new Author();
        mark.setName("Mark Janel");
        mark.setAge(23);
        mark.setGenre("Anthology");

        Book bookOfSwords = new Book();
        bookOfSwords.setIsbn("001-AT-MJ");
        bookOfSwords.setTitle("The book of swords");

        Book oneDay = new Book();
        oneDay.setIsbn("002-AT-MJ");
        oneDay.setTitle("One Day");

        alicia.addBook(bookOfSwords); // use addBook() helper
        mark.addBook(oneDay);

        authorRepository.save(alicia);
        authorRepository.save(mark);
    }
}
