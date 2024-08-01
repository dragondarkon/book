package com.example.book;

import com.example.book.entity.Book;
import com.example.book.repository.BookRepository;
import com.example.book.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = BookApplication.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
// @TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureTestDatabase
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private BookRepository repository;

    @Test
    public void shouldReturnListOfBooksWhenTodosEndpointIsCalled() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(
                new Book(1L, "john", "test", "1234", LocalDate.now()),
                new Book(2L, "alex", "test", "5678", LocalDate.now())
        ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2));
        verify(bookService, times(1)).findAll();
    }

    @Test
    public void shouldReturnASingleBookWhenIndividualBookEndpointIsCalled() throws Exception {
        Book book = new Book(1L, "john", "test", "1234", LocalDate.now());
        when(bookService.findById(1L)).thenReturn(book);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()));

        verify(bookService, times(1)).findById(1L);
    }

    @Test
    public void shouldSaveATodoAndReturnSuccessResponseWhenPostedToTodosEndpoint() throws Exception {
        when(bookService.save(any(Book.class))).then((Answer<Book>) invocation -> {
            Book existing = invocation.getArgument(0);
            return new Book(1L, existing.getTitle(), existing.getAuthor(), existing.getIsbn(), existing.getPublishedDate());
        });
        Book book = new Book(1L,  "john", "test", "1234", LocalDate.now());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(JsonUtil.toJson(book))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(bookService, times(1)).save(any());
    }

    @Test
    public void shouldUpdateBookAndReturnASuccessfulUpdatedBookResponseWhenPutRequestIsMadeToIndividualBooksEndpoint() throws Exception {
        Book previous = new Book(1L, "Previous", "john", "1234", LocalDate.now());
        Book updated = new Book( 1L, "Updated", "john", "1234", LocalDate.now());
        when(bookService.update(previous.getId(), updated))
                .then((Answer<Book>) invocation -> {
                    Long bookId = invocation.getArgument(0);
                    Book passed = invocation.getArgument(1);
                    return new Book(bookId, passed.getTitle(), passed.getAuthor(), passed.getIsbn(), passed.getPublishedDate());
                });

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/books/{id}",1L)
                                .content(JsonUtil.toJson(updated))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(bookService, times(1)).update(any(), any());
    }

    @Test
    public void shouldDeleteBookWhenDeleteRequestIsMadeToIndividualBookDeleteEndpoint() throws Exception {
        Book book = new Book(1L, "john", "test", "1234", LocalDate.now());

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/books/{id}", book.getId())
                )
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(book.getId());
    }
}
