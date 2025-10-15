package ekene.dian.librarydemofx;

import ekene.dian.librarydemofx.model.Book;
import ekene.dian.librarydemofx.service.BookService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private final BookService bookService = new BookService();
    private final ObservableList<Book> bookList = FXCollections.observableArrayList();
    private Book selectedBook = null;

    @FXML private TableView<Book> bookTableView;
    @FXML private TableColumn<Book, Long> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, LocalDate> publishedDateColumn;
    
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField isbnField;
    @FXML private DatePicker publishedDatePicker;
    @FXML private TextField searchField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupTableColumns();
            setupTableSelection();
            
            SortedList<Book> sortedData = new SortedList<>(bookList);
            sortedData.comparatorProperty().bind(bookTableView.comparatorProperty());
            bookTableView.setItems(sortedData);
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.trim().isEmpty() ||
                    (oldValue != null && !newValue.equals(oldValue) && newValue.length() >= 3)) {
                    Platform.runLater(this::loadBooks);
                }
            });
            
            loadBooks();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Initialization Error", "Failed to initialize the application: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        publishedDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getPublishedDate()));
        
        publishedDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toString());
            }
        });
    }
    
    private void loadBooks() {
        try {
            bookList.clear();
            
            // If search field has text, use server-side search, otherwise get all books
            String searchText = searchField.getText();
            List<Book> books;
            
            if (searchText != null && !searchText.trim().isEmpty()) {
                // Use server-side search
                books = bookService.searchBooks(searchText);
            } else {
                // Get all books
                books = bookService.getAllBooks();
            }
            
            if (books != null) {
                bookList.setAll(books);
            } else {
                showAlert("Error", "Failed to load books. Please check your connection to the server.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while loading books: " + e.getMessage());
        } finally {
            // Ensure the table is updated even if there's an error
            bookTableView.refresh();
        }
    }
    
    @FXML
    private void refreshBooks() {
        loadBooks();
        clearForm();
        selectedBook = null;
    }
    
    private void setupTableSelection() {
        bookTableView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    // Fetch the latest data from the server when a book is selected
                    Book updatedBook = bookService.getBookById(newValue.getId());
                    if (updatedBook != null) {
                        selectedBook = updatedBook;
                        titleField.setText(updatedBook.getTitle());
                        authorField.setText(updatedBook.getAuthor());
                        isbnField.setText(updatedBook.getIsbn());
                        publishedDatePicker.setValue(updatedBook.getPublishedDate());
                    } else {
                        showAlert("Error", "Could not fetch book details. Please try again.");
                    }
                }
            });
    }
    
    @FXML
    private void handleAddBook() {
        if (validateInput()) {
            Book book = new Book(
                titleField.getText(),
                authorField.getText(),
                isbnField.getText(),
                publishedDatePicker.getValue()
            );
            
            Book createdBook = bookService.createBook(book);
            if (createdBook != null) {
                bookList.add(createdBook);
                clearForm();
                showAlert("Success", "Book added successfully!");
            } else {
                showAlert("Error", "Failed to add book.");
            }
        }
    }
    
    @FXML
    private void handleUpdateBook() {
        if (selectedBook != null && validateInput()) {
            selectedBook.setTitle(titleField.getText());
            selectedBook.setAuthor(authorField.getText());
            selectedBook.setIsbn(isbnField.getText());
            selectedBook.setPublishedDate(publishedDatePicker.getValue());
            
            if (bookService.updateBook(selectedBook)) {
                bookTableView.refresh();
                clearForm();
                selectedBook = null;
                showAlert("Success", "Book updated successfully!");
            } else {
                showAlert("Error", "Failed to update book.");
            }
        } else {
            showAlert("No Selection", "Please select a book to update.");
        }
    }
    
    @FXML
    private void handleDeleteBook() {
        if (selectedBook != null) {
            if (bookService.deleteBook(selectedBook.getId())) {
                bookList.remove(selectedBook);
                clearForm();
                selectedBook = null;
                showAlert("Success", "Book deleted successfully!");
            } else {
                showAlert("Error", "Failed to delete book.");
            }
        } else {
            showAlert("No Selection", "Please select a book to delete.");
        }
    }
    
    @FXML
    private void handleRefresh() {
        refreshBooks();
    }
    
    private boolean validateInput() {
        String errorMessage = "";
        
        if (titleField.getText() == null || titleField.getText().trim().isEmpty()) {
            errorMessage += "Title is required!\n";
        }
        if (authorField.getText() == null || authorField.getText().trim().isEmpty()) {
            errorMessage += "Author is required!\n";
        }
        if (isbnField.getText() == null || isbnField.getText().trim().isEmpty()) {
            errorMessage += "ISBN is required!\n";
        }
        if (publishedDatePicker.getValue() == null) {
            errorMessage += "Published date is required!\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Invalid Fields", errorMessage);
            return false;
        }
    }
    
    private void clearForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        publishedDatePicker.setValue(null);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}