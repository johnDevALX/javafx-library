package ekene.dian.librarydemofx.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ekene.dian.librarydemofx.model.Book;
import ekene.dian.librarydemofx.model.PageResponse;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class BookService {
    private static final String API_BASE_URL = "http://localhost:8080/api/books";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BookService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<Book> getAllBooks() {
        try {
            String response = restTemplate.getForObject(API_BASE_URL, String.class);
            if (response != null && !response.trim().isEmpty()) {
                System.out.println("API Response: " + response); // Debug log
                
                objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                PageResponse<Book> pageResponse = objectMapper.readValue(
                    response, 
                    new TypeReference<PageResponse<Book>>() {}
                );
                
                return pageResponse.getContent() != null ? pageResponse.getContent() : Collections.emptyList();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error fetching all books: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Book getBookById(Long id) {
        try {
            String response = restTemplate.getForObject(API_BASE_URL + "/" + id, String.class);
            if (response != null && !response.trim().isEmpty()) {
                return objectMapper.readValue(response, Book.class);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error fetching book with id " + id + ": " + e.getMessage());
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
            if (response != null && !response.trim().isEmpty()) {
                return objectMapper.readValue(response, Book.class);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error creating book: " + e.getMessage());
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
            System.err.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(Long id) {
        try {
            restTemplate.delete(API_BASE_URL + "/" + id);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> searchBooks(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = API_BASE_URL + "/search?query=" + encodedQuery;
            System.out.println("Search URL: " + url); // Debug log
            
            String response = restTemplate.getForObject(url, String.class);
            
            if (response != null && !response.trim().isEmpty()) {
                System.out.println("Search Response: " + response); // Debug log
                
                objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                
                PageResponse<Book> pageResponse = objectMapper.readValue(
                    response, 
                    new TypeReference<PageResponse<Book>>() {}
                );
                
                return pageResponse.getContent() != null ? pageResponse.getContent() : Collections.emptyList();
            }
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error searching books: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
