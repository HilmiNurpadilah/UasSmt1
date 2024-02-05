package changescenes;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class PageController implements Initializable {

    @FXML
    private TextField tfId;
    @FXML
    private TextField tfAuthor;
    @FXML
    private TextField tfTitle;
    @FXML
    private TextField tfYear;
    @FXML
    private TextField tfPages;
    @FXML
    private TableView<Books> tvBooks;
    @FXML
    private TableColumn<Books, Integer> colId;
    @FXML
    private TableColumn<Books, String> colTitle;
    @FXML
    private TableColumn<Books, String> colAuthor;
    @FXML
    private TableColumn<Books, Integer> colYear;
    @FXML
    private TableColumn<Books, Integer> colPages;
    @FXML
    private Button btnInsert;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnDelete;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showBooks();
        setupTableListener();
    }

    private void setupTableListener() {
        tvBooks.setOnMouseClicked((MouseEvent event) -> {
            handleButtonAction(event);
        });
    }

    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "");
        } catch (SQLException ex) {
            System.out.println("Error: Unable to establish a connection to the database.");
            ex.printStackTrace();
        }
        return conn;
    }

    public ObservableList<Books> getBooksList() {
        ObservableList<Books> booklist = FXCollections.observableArrayList();
        Connection conn = getConnection();
        String query = "SELECT * FROM books";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            Books books;
            while (rs.next()) {
                books = new Books(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("year"), rs.getInt("pages"));
                booklist.add(books);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return booklist;
    }

    public void showBooks() {
        ObservableList<Books> list = getBooksList();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        colPages.setCellValueFactory(new PropertyValueFactory<>("pages"));
        tvBooks.setItems(list);
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        if (event.getSource() == btnInsert) {
            insertRecord();
        } else if (event.getSource() == btnUpdate) {
            updateRecord();
        } else if (event.getSource() == btnDelete) {
            deleteRecord();
        }
    }

    private void handleButtonAction(MouseEvent event) {
        Books book = tvBooks.getSelectionModel().getSelectedItem();
        if (book != null) {
            tfId.setText(String.valueOf(book.getId()));
            tfTitle.setText(book.getTitle());
            tfAuthor.setText(book.getAuthor());
            tfYear.setText(String.valueOf(book.getYear()));
            tfPages.setText(String.valueOf(book.getPages()));
        }
    }

    private void insertRecord() {
        try {
            Connection conn = getConnection();
            String query = "INSERT INTO books (title, author, year, pages) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, tfTitle.getText());
                pst.setString(2, tfAuthor.getText());
                pst.setInt(3, Integer.parseInt(tfYear.getText()));
                pst.setInt(4, Integer.parseInt(tfPages.getText()));
                pst.executeUpdate();
            }
            showBooks();
            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateRecord() {
        try {
            Connection conn = getConnection();
            String query = "UPDATE books SET title = ?, author = ?, year = ?, pages = ? WHERE id = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, tfTitle.getText());
                pst.setString(2, tfAuthor.getText());
                pst.setInt(3, Integer.parseInt(tfYear.getText()));
                pst.setInt(4, Integer.parseInt(tfPages.getText()));
                pst.setInt(5, Integer.parseInt(tfId.getText()));
                pst.executeUpdate();
            }
            showBooks();
            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteRecord() {
        try {
            Connection conn = getConnection();
            String query = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, Integer.parseInt(tfId.getText()));
                pst.executeUpdate();
            }
            showBooks();
            clearFields();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        tfId.clear();
        tfTitle.clear();
        tfAuthor.clear();
        tfYear.clear();
        tfPages.clear();
    }
}
