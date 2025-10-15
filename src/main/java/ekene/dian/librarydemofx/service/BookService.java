package ekene.dian.librarydemofx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ekene.dian.librarydemofx.model.Book;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class BookService {
    private static final String API_BASE_URL = "http://localhost:8080/api/books";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BookService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules(); // For Java 8 date/time support
    }

    public List<Book> getAllBooks() {
        try {
            String response = restTemplate.getForObject(API_BASE_URL, String.class);
            return objectMapper.readValue(response, new TypeReference<List<Book>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Book getBookById(Long id) {
        try {
            String response = restTemplate.getForObject(API_BASE_URL + "/" + id, String.class);
            return objectMapper.readValue(response, Book.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Book createBook(Book book) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Book> request = new HttpEntity<>(book, headers);
            
            String response = restTemplate.postForObject(API_BASE_URL, request, String.class);
            return objectMapper.readValue(response, Book.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateBook(Book book) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Book> request = new HttpEntity<>(book, headers);
            
            restTemplate.put(API_BASE_URL + "/" + book.getId(), request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(Long id) {
        try {
            restTemplate.delete(API_BASE_URL + "/" + id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // For search functionality
    public List<Book> searchBooks(String query) {
        try {
            String response = restTemplate.getForObject(API_BASE_URL + "/search?query=" + query, String.class);
            return objectMapper.readValue(response, new TypeReference<List<Book>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
