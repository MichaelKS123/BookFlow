// BookFlow - DatabaseManager.java
// Created by Michael Semera
// Handles all database operations with MySQL via JDBC

package com.michaelsemera.bookflow;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "bookflow";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password"; // Change this
    
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        connectToDatabase();
    }
    
    private void connectToDatabase() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // First connect without database to create it if needed
            Connection tempConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = tempConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            tempConn.close();
            
            // Now connect to the database
            connection = DriverManager.getConnection(DB_URL + DB_NAME, DB_USER, DB_PASSWORD);
            System.out.println("✅ Connected to database: " + DB_NAME);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }
    
    public void initializeDatabase() throws SQLException {
        createTables();
        insertSampleData();
        System.out.println("✅ Database initialized successfully");
    }
    
    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        
        // Books table
        String createBooksTable = 
            "CREATE TABLE IF NOT EXISTS books (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "title VARCHAR(255) NOT NULL," +
            "author VARCHAR(255) NOT NULL," +
            "isbn VARCHAR(20) UNIQUE," +
            "publisher VARCHAR(255)," +
            "publication_year INT," +
            "category VARCHAR(100)," +
            "total_copies INT DEFAULT 1," +
            "available_copies INT DEFAULT 1," +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")";
        stmt.executeUpdate(createBooksTable);
        
        // Users table
        String createUsersTable = 
            "CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "name VARCHAR(255) NOT NULL," +
            "email VARCHAR(255) UNIQUE NOT NULL," +
            "phone VARCHAR(20)," +
            "address TEXT," +
            "membership_type ENUM('Basic', 'Premium', 'Student') DEFAULT 'Basic'," +
            "registration_date DATE," +
            "status ENUM('Active', 'Suspended', 'Inactive') DEFAULT 'Active'" +
            ")";
        stmt.executeUpdate(createUsersTable);
        
        // Loans table
        String createLoansTable = 
            "CREATE TABLE IF NOT EXISTS loans (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "book_id INT NOT NULL," +
            "user_id INT NOT NULL," +
            "issue_date DATE NOT NULL," +
            "due_date DATE NOT NULL," +
            "return_date DATE," +
            "status ENUM('Active', 'Returned', 'Overdue') DEFAULT 'Active'," +
            "fine DECIMAL(10,2) DEFAULT 0.00," +
            "FOREIGN KEY (book_id) REFERENCES books(id)," +
            "FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")";
        stmt.executeUpdate(createLoansTable);
        
        // Reservations table
        String createReservationsTable = 
            "CREATE TABLE IF NOT EXISTS reservations (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "book_id INT NOT NULL," +
            "user_id INT NOT NULL," +
            "reservation_date DATE NOT NULL," +
            "status ENUM('Active', 'Fulfilled', 'Cancelled') DEFAULT 'Active'," +
            "FOREIGN KEY (book_id) REFERENCES books(id)," +
            "FOREIGN KEY (user_id) REFERENCES users(id)" +
            ")";
        stmt.executeUpdate(createReservationsTable);
        
        stmt.close();
    }
    
    private void insertSampleData() throws SQLException {
        // Check if data already exists
        Statement checkStmt = connection.createStatement();
        ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM books");
        rs.next();
        if (rs.getInt(1) > 0) {
            checkStmt.close();
            return; // Data already exists
        }
        checkStmt.close();
        
        // Insert sample books
        String[] sampleBooks = {
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 'Scribner', 1925, 'Fiction', 3, 3)",
            
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 'Harper Perennial', 1960, 'Fiction', 2, 2)",
            
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('1984', 'George Orwell', '9780451524935', 'Signet Classic', 1949, 'Science Fiction', 4, 4)",
            
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('Pride and Prejudice', 'Jane Austen', '9780141439518', 'Penguin Classics', 1813, 'Fiction', 2, 2)",
            
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('The Catcher in the Rye', 'J.D. Salinger', '9780316769488', 'Little, Brown', 1951, 'Fiction', 3, 2)",
            
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('A Brief History of Time', 'Stephen Hawking', '9780553380163', 'Bantam', 1988, 'Science', 2, 2)",
            
            "INSERT INTO books (title, author, isbn, publisher, publication_year, category, total_copies, available_copies) " +
            "VALUES ('Sapiens', 'Yuval Noah Harari', '9780062316097', 'Harper', 2015, 'History', 3, 3)"
        };
        
        // Insert sample users
        String[] sampleUsers = {
            "INSERT INTO users (name, email, phone, membership_type, registration_date) " +
            "VALUES ('John Smith', 'john.smith@email.com', '555-0101', 'Premium', '2024-01-15')",
            
            "INSERT INTO users (name, email, phone, membership_type, registration_date) " +
            "VALUES ('Emma Johnson', 'emma.j@email.com', '555-0102', 'Basic', '2024-02-20')",
            
            "INSERT INTO users (name, email, phone, membership_type, registration_date) " +
            "VALUES ('Michael Brown', 'michael.b@email.com', '555-0103', 'Student', '2024-03-10')",
            
            "INSERT INTO users (name, email, phone, membership_type, registration_date) " +
            "VALUES ('Sarah Davis', 'sarah.d@email.com', '555-0104', 'Basic', '2024-04-05')"
        };
        
        // Insert sample loans
        String[] sampleLoans = {
            "INSERT INTO loans (book_id, user_id, issue_date, due_date, status) " +
            "VALUES (5, 1, '2024-10-01', '2024-10-15', 'Active')"
        };
        
        Statement stmt = connection.createStatement();
        
        for (String sql : sampleBooks) {
            stmt.executeUpdate(sql);
        }
        
        for (String sql : sampleUsers) {
            stmt.executeUpdate(sql);
        }
        
        for (String sql : sampleLoans) {
            stmt.executeUpdate(sql);
        }
        
        stmt.close();
        System.out.println("✅ Sample data inserted");
    }
    
    // ==================== BOOK OPERATIONS ====================
    
    public void addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, publisher, publication_year, " +
                    "category, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, book.getTitle());
        pstmt.setString(2, book.getAuthor());
        pstmt.setString(3, book.getIsbn());
        pstmt.setString(4, book.getPublisher());
        pstmt.setInt(5, book.getPublicationYear());
        pstmt.setString(6, book.getCategory());
        pstmt.setInt(7, book.getTotalCopies());
        pstmt.setInt(8, book.getAvailableCopies());
        
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            books.add(extractBookFromResultSet(rs));
        }
        
        rs.close();
        stmt.close();
        
        return books;
    }
    
    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        ResultSet rs = pstmt.executeQuery();
        Book book = null;
        
        if (rs.next()) {
            book = extractBookFromResultSet(rs);
        }
        
        rs.close();
        pstmt.close();
        
        return book;
    }
    
    public List<Book> searchBooks(String query) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        String searchPattern = "%" + query + "%";
        pstmt.setString(1, searchPattern);
        pstmt.setString(2, searchPattern);
        pstmt.setString(3, searchPattern);
        
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            books.add(extractBookFromResultSet(rs));
        }
        
        rs.close();
        pstmt.close();
        
        return books;
    }
    
    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publisher = ?, " +
                    "publication_year = ?, category = ?, total_copies = ?, available_copies = ? " +
                    "WHERE id = ?";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, book.getTitle());
        pstmt.setString(2, book.getAuthor());
        pstmt.setString(3, book.getIsbn());
        pstmt.setString(4, book.getPublisher());
        pstmt.setInt(5, book.getPublicationYear());
        pstmt.setString(6, book.getCategory());
        pstmt.setInt(7, book.getTotalCopies());
        pstmt.setInt(8, book.getAvailableCopies());
        pstmt.setInt(9, book.getId());
        
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    public void deleteBook(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("isbn"),
            rs.getString("publisher"),
            rs.getInt("publication_year"),
            rs.getString("category"),
            rs.getInt("total_copies"),
            rs.getInt("available_copies")
        );
    }
    
    // ==================== USER OPERATIONS ====================
    
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, phone, address, membership_type, registration_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getEmail());
        pstmt.setString(3, user.getPhone());
        pstmt.setString(4, user.getAddress());
        pstmt.setString(5, user.getMembershipType());
        pstmt.setDate(6, Date.valueOf(user.getRegistrationDate()));
        
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            users.add(extractUserFromResultSet(rs));
        }
        
        rs.close();
        stmt.close();
        
        return users;
    }
    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getString("membership_type"),
            rs.getDate("registration_date").toLocalDate(),
            rs.getString("status")
        );
    }
    
    // ==================== LOAN OPERATIONS ====================
    
    public void issueLoan(int bookId, int userId, LocalDate issueDate, LocalDate dueDate) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // Insert loan record
            String insertLoan = "INSERT INTO loans (book_id, user_id, issue_date, due_date, status) " +
                               "VALUES (?, ?, ?, ?, 'Active')";
            PreparedStatement pstmt1 = connection.prepareStatement(insertLoan);
            pstmt1.setInt(1, bookId);
            pstmt1.setInt(2, userId);
            pstmt1.setDate(3, Date.valueOf(issueDate));
            pstmt1.setDate(4, Date.valueOf(dueDate));
            pstmt1.executeUpdate();
            pstmt1.close();
            
            // Decrease available copies
            String updateBook = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
            PreparedStatement pstmt2 = connection.prepareStatement(updateBook);
            pstmt2.setInt(1, bookId);
            pstmt2.executeUpdate();
            pstmt2.close();
            
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void returnBook(int loanId) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // Get book ID from loan
            String getLoan = "SELECT book_id FROM loans WHERE id = ?";
            PreparedStatement pstmt1 = connection.prepareStatement(getLoan);
            pstmt1.setInt(1, loanId);
            ResultSet rs = pstmt1.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("Loan not found");
            }
            
            int bookId = rs.getInt("book_id");
            rs.close();
            pstmt1.close();
            
            // Update loan record
            String updateLoan = "UPDATE loans SET return_date = CURDATE(), status = 'Returned' WHERE id = ?";
            PreparedStatement pstmt2 = connection.prepareStatement(updateLoan);
            pstmt2.setInt(1, loanId);
            pstmt2.executeUpdate();
            pstmt2.close();
            
            // Increase available copies
            String updateBook = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
            PreparedStatement pstmt3 = connection.prepareStatement(updateBook);
            pstmt3.setInt(1, bookId);
            pstmt3.executeUpdate();
            pstmt3.close();
            
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public List<Loan> getAllLoans() throws SQLException {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT l.*, b.title, u.name FROM loans l " +
                    "JOIN books b ON l.book_id = b.id " +
                    "JOIN users u ON l.user_id = u.id " +
                    "ORDER BY l.issue_date DESC";
        
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            loans.add(extractLoanFromResultSet(rs));
        }
        
        rs.close();
        stmt.close();
        
        return loans;
    }
    
    private Loan extractLoanFromResultSet(ResultSet rs) throws SQLException {
        return new Loan(
            rs.getInt("id"),
            rs.getInt("book_id"),
            rs.getInt("user_id"),
            rs.getString("title"),
            rs.getString("name"),
            rs.getDate("issue_date").toLocalDate(),
            rs.getDate("due_date").toLocalDate(),
            rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null,
            rs.getString("status"),
            rs.getDouble("fine")
        );
    }
    
    // ==================== STATISTICS ====================
    
    public int getTotalBooks() throws SQLException {
        return getCount("SELECT COUNT(*) FROM books");
    }
    
    public int getAvailableBooks() throws SQLException {
        return getCount("SELECT SUM(available_copies) FROM books");
    }
    
    public int getTotalUsers() throws SQLException {
        return getCount("SELECT COUNT(*) FROM users");
    }
    
    public int getActiveLoans() throws SQLException {
        return getCount("SELECT COUNT(*) FROM loans WHERE status = 'Active'");
    }
    
    private int getCount(String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int count = rs.getInt(1);
        rs.close();
        stmt.close();
        return count;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}