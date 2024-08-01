package com.example.book;

import com.example.book.entity.Book;
import com.example.book.repository.BookRepository;
import com.example.book.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    public void shouldReturnAllBooksWhenGetAllBooksIsCalled() {
        List<Book> expected = List.of(
                new Book(1L, "john", "test", "1234", LocalDate.now()),
                new Book(2L, "alex", "test", "5678", LocalDate.now())
        );
        when(bookRepository.findAll()).thenReturn(expected);

        List<Book> todos = bookService.findAll();

        verify(bookRepository, times(1)).findAll();
        assertEquals(expected, todos);
    }

    @Test
    public void shouldReturnBookWithId_1_WhenQueriedForBookWithId_1() {
        Book expected = new Book(1L, "john", "test", "1234", LocalDate.now());
        when(bookRepository.findById(anyLong()))
                .thenReturn(Optional.of(expected));

        Book actual = bookService.findById(1L);

        verify(bookRepository, times(1)).findById(1L);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldCreateNewBookWhenAskedBookService() {
        Book book = new Book(1L, "john", "test", "1234", LocalDate.now());
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book actual = bookService.save(book);

        verify(bookRepository, times(1)).save(book);
        assertEquals(book, actual);
    }

    @Test
    public void shouldUpdateExistingBookWhenUpdateIsCalledOnService() {
        Book updated = new Book(1L, "john", "test", "1234", LocalDate.now());
        when(bookRepository.getById(1L)).thenReturn(new Book(1L, "john", "test", "1234", LocalDate.now()));

        bookService.update(1L, updated);

        verify(bookRepository, times(1)).getById(1L);
        updated.setId(1L);
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    public void shouldDeleteBookWhenDeleteIsCalledOnService() {
        Book book = new Book(1L, "john", "test", "1234", LocalDate.now());

        bookService.deleteById(book.getId());

        verify(bookRepository, times(1)).deleteById(book.getId());
    }

}
