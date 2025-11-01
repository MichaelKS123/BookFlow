// BookFlow - Library Management System
// Created by Michael Semera
// Main.java

package com.michaelsemera.bookflow;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    
    private DatabaseManager dbManager;
    private BorderPane mainLayout;
    private TableView<Book> bookTable;
    private TableView<User> userTable;
    private TableView<Loan> loanTable;
    private Label statusLabel;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            dbManager = new DatabaseManager();
            dbManager.initializeDatabase();
            
            primaryStage.setTitle("BookFlow - Library Management System");
            
            mainLayout = new BorderPane();
            mainLayout.setPadding(new Insets(10));
            
            // Top navigation
            HBox topNav = createTopNavigation();
            mainLayout.setTop(topNav);
            
            // Bottom status bar
            HBox statusBar = createStatusBar();
            mainLayout.setBottom(statusBar);
            
            // Show dashboard by default
            showDashboard();
            
            Scene scene = new Scene(mainLayout, 1200, 700);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            updateStatus("BookFlow started successfully");
            
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to initialize database: " + e.getMessage());
            Platform.exit();
        }
    }
    
    private HBox createTopNavigation() {
        HBox nav = new HBox(15);
        nav.setPadding(new Insets(10));
        nav.setAlignment(Pos.CENTER_LEFT);
        nav.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 5;");
        
        Label title = new Label("📚 BookFlow");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);
        
        Button dashboardBtn = createNavButton("Dashboard");
        Button booksBtn = createNavButton("Books");
        Button usersBtn = createNavButton("Users");
        Button loansBtn = createNavButton("Loans");
        Button reportsBtn = createNavButton("Reports");
        
        dashboardBtn.setOnAction(e -> showDashboard());
        booksBtn.setOnAction(e -> showBooks());
        usersBtn.setOnAction(e -> showUsers());
        loansBtn.setOnAction(e -> showLoans());
        reportsBtn.setOnAction(e -> showReports());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label authorLabel = new Label("by Michael Semera");
        authorLabel.setTextFill(Color.web("#95a5a6"));
        authorLabel.setFont(Font.font("Arial", 12));
        
        nav.getChildren().addAll(title, dashboardBtn, booksBtn, usersBtn, 
                                  loansBtn, reportsBtn, spacer, authorLabel);
        
        return nav;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #34495e;");
        
        statusLabel = new Label("Ready");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Arial", 12));
        
        statusBar.getChildren().add(statusLabel);
        
        return statusBar;
    }
    
    private Button createNavButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; " +
                     "-fx-font-size: 14px; -fx-padding: 10 20; " +
                     "-fx-background-radius: 5; -fx-cursor: hand;");
        
        btn.setOnMouseEntered(e -> 
            btn.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 10 20; " +
                        "-fx-background-radius: 5; -fx-cursor: hand;"));
        
        btn.setOnMouseExited(e -> 
            btn.setStyle("-fx-background-color: #34495e; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 10 20; " +
                        "-fx-background-radius: 5; -fx-cursor: hand;"));
        
        return btn;
    }
    
    private void showDashboard() {
        VBox dashboard = new VBox(20);
        dashboard.setPadding(new Insets(20));
        
        Label title = new Label("Dashboard");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Statistics cards
        HBox statsCards = createStatsCards();
        
        // Recent activity
        Label recentLabel = new Label("Recent Loans");
        recentLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        loanTable = createLoanTable();
        updateLoanTable();
        
        dashboard.getChildren().addAll(title, statsCards, recentLabel, loanTable);
        mainLayout.setCenter(dashboard);
        
        updateStatus("Dashboard loaded");
    }
    
    private HBox createStatsCards() {
        HBox cards = new HBox(20);
        cards.setAlignment(Pos.CENTER);
        
        try {
            int totalBooks = dbManager.getTotalBooks();
            int availableBooks = dbManager.getAvailableBooks();
            int totalUsers = dbManager.getTotalUsers();
            int activeLoans = dbManager.getActiveLoans();
            
            VBox booksCard = createStatCard("Total Books", String.valueOf(totalBooks), "#3498db");
            VBox availCard = createStatCard("Available", String.valueOf(availableBooks), "#27ae60");
            VBox usersCard = createStatCard("Users", String.valueOf(totalUsers), "#9b59b6");
            VBox loansCard = createStatCard("Active Loans", String.valueOf(activeLoans), "#e74c3c");
            
            cards.getChildren().addAll(booksCard, availCard, usersCard, loansCard);
            
        } catch (SQLException e) {
            showAlert("Error", "Failed to load statistics: " + e.getMessage());
        }
        
        return cards;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; " +
                     "-fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        card.setPrefWidth(250);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.WHITE);
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.WHITE);
        
        card.getChildren().addAll(titleLabel, valueLabel);
        
        return card;
    }
    
    private void showBooks() {
        VBox booksView = new VBox(20);
        booksView.setPadding(new Insets(20));
        
        Label title = new Label("Book Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        // Action buttons
        HBox actionBar = new HBox(10);
        
        Button addBtn = new Button("➕ Add Book");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                       "-fx-font-size: 14px; -fx-padding: 10 20;");
        addBtn.setOnAction(e -> showAddBookDialog());
        
        Button editBtn = new Button("✏️ Edit Book");
        editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                        "-fx-font-size: 14px; -fx-padding: 10 20;");
        editBtn.setOnAction(e -> showEditBookDialog());
        
        Button deleteBtn = new Button("🗑️ Delete Book");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                          "-fx-font-size: 14px; -fx-padding: 10 20;");
        deleteBtn.setOnAction(e -> deleteSelectedBook());
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search books...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> searchBooks(newVal));
        
        actionBar.getChildren().addAll(addBtn, editBtn, deleteBtn, searchField);
        
        // Books table
        bookTable = createBookTable();
        updateBookTable();
        
        booksView.getChildren().addAll(title, actionBar, bookTable);
        mainLayout.setCenter(booksView);
        
        updateStatus("Book management loaded");
    }
    
    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();
        
        TableColumn<Book, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        idCol.setPrefWidth(50);
        
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        titleCol.setPrefWidth(250);
        
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getAuthor()));
        authorCol.setPrefWidth(200);
        
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getIsbn()));
        isbnCol.setPrefWidth(150);
        
        TableColumn<Book, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        categoryCol.setPrefWidth(120);
        
        TableColumn<Book, Integer> totalCol = new TableColumn<>("Total Copies");
        totalCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getTotalCopies()).asObject());
        totalCol.setPrefWidth(100);
        
        TableColumn<Book, Integer> availCol = new TableColumn<>("Available");
        availCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getAvailableCopies()).asObject());
        availCol.setPrefWidth(100);
        
        table.getColumns().addAll(idCol, titleCol, authorCol, isbnCol, 
                                   categoryCol, totalCol, availCol);
        table.setPrefHeight(500);
        
        return table;
    }
    
    private void updateBookTable() {
        try {
            List<Book> books = dbManager.getAllBooks();
            ObservableList<Book> bookData = FXCollections.observableArrayList(books);
            bookTable.setItems(bookData);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load books: " + e.getMessage());
        }
    }
    
    private void searchBooks(String query) {
        try {
            List<Book> books = dbManager.searchBooks(query);
            ObservableList<Book> bookData = FXCollections.observableArrayList(books);
            bookTable.setItems(bookData);
        } catch (SQLException e) {
            showAlert("Error", "Search failed: " + e.getMessage());
        }
    }
    
    private void showAddBookDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField isbnField = new TextField();
        TextField publisherField = new TextField();
        TextField yearField = new TextField();
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Fiction", "Non-Fiction", "Science", "History", 
                                      "Technology", "Biography", "Other");
        TextField copiesField = new TextField();
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Author:"), 0, 1);
        grid.add(authorField, 1, 1);
        grid.add(new Label("ISBN:"), 0, 2);
        grid.add(isbnField, 1, 2);
        grid.add(new Label("Publisher:"), 0, 3);
        grid.add(publisherField, 1, 3);
        grid.add(new Label("Year:"), 0, 4);
        grid.add(yearField, 1, 4);
        grid.add(new Label("Category:"), 0, 5);
        grid.add(categoryBox, 1, 5);
        grid.add(new Label("Copies:"), 0, 6);
        grid.add(copiesField, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Book book = new Book(
                    0,
                    titleField.getText(),
                    authorField.getText(),
                    isbnField.getText(),
                    publisherField.getText(),
                    Integer.parseInt(yearField.getText()),
                    categoryBox.getValue(),
                    Integer.parseInt(copiesField.getText()),
                    Integer.parseInt(copiesField.getText())
                );
                
                dbManager.addBook(book);
                updateBookTable();
                updateStatus("Book added successfully");
                
            } catch (Exception e) {
                showAlert("Error", "Failed to add book: " + e.getMessage());
            }
        }
    }
    
    private void showEditBookDialog() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a book to edit");
            return;
        }
        
        // Similar to add dialog but pre-filled with existing data
        // Implementation details omitted for brevity
        updateStatus("Edit functionality available");
    }
    
    private void deleteSelectedBook() {
        Book selected = bookTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a book to delete");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete book: " + selected.getTitle());
        confirm.setContentText("Are you sure?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                dbManager.deleteBook(selected.getId());
                updateBookTable();
                updateStatus("Book deleted successfully");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete book: " + e.getMessage());
            }
        }
    }
    
    private void showUsers() {
        VBox usersView = new VBox(20);
        usersView.setPadding(new Insets(20));
        
        Label title = new Label("User Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        HBox actionBar = new HBox(10);
        
        Button addBtn = new Button("➕ Add User");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                       "-fx-font-size: 14px; -fx-padding: 10 20;");
        addBtn.setOnAction(e -> showAddUserDialog());
        
        actionBar.getChildren().add(addBtn);
        
        userTable = createUserTable();
        updateUserTable();
        
        usersView.getChildren().addAll(title, actionBar, userTable);
        mainLayout.setCenter(usersView);
        
        updateStatus("User management loaded");
    }
    
    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        
        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        
        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        nameCol.setPrefWidth(250);
        
        TableColumn<User, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        
        TableColumn<User, String> membershipCol = new TableColumn<>("Membership");
        membershipCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getMembershipType()));
        
        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, membershipCol);
        table.setPrefHeight(500);
        
        return table;
    }
    
    private void updateUserTable() {
        try {
            List<User> users = dbManager.getAllUsers();
            ObservableList<User> userData = FXCollections.observableArrayList(users);
            userTable.setItems(userData);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }
    
    private void showAddUserDialog() {
        // Implementation similar to add book
        updateStatus("Add user dialog");
    }
    
    private void showLoans() {
        VBox loansView = new VBox(20);
        loansView.setPadding(new Insets(20));
        
        Label title = new Label("Loan Management");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        HBox actionBar = new HBox(10);
        
        Button issueBtn = new Button("📤 Issue Book");
        issueBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                         "-fx-font-size: 14px; -fx-padding: 10 20;");
        issueBtn.setOnAction(e -> showIssueLoanDialog());
        
        Button returnBtn = new Button("📥 Return Book");
        returnBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                          "-fx-font-size: 14px; -fx-padding: 10 20;");
        returnBtn.setOnAction(e -> returnSelectedLoan());
        
        actionBar.getChildren().addAll(issueBtn, returnBtn);
        
        loanTable = createLoanTable();
        updateLoanTable();
        
        loansView.getChildren().addAll(title, actionBar, loanTable);
        mainLayout.setCenter(loansView);
        
        updateStatus("Loan management loaded");
    }
    
    private TableView<Loan> createLoanTable() {
        TableView<Loan> table = new TableView<>();
        
        TableColumn<Loan, Integer> idCol = new TableColumn<>("Loan ID");
        idCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        
        TableColumn<Loan, String> bookCol = new TableColumn<>("Book");
        bookCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getBookTitle()));
        bookCol.setPrefWidth(250);
        
        TableColumn<Loan, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getUserName()));
        userCol.setPrefWidth(200);
        
        TableColumn<Loan, String> issueDateCol = new TableColumn<>("Issue Date");
        issueDateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getIssueDate().toString()));
        
        TableColumn<Loan, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDueDate().toString()));
        
        TableColumn<Loan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
        
        table.getColumns().addAll(idCol, bookCol, userCol, issueDateCol, dueDateCol, statusCol);
        table.setPrefHeight(500);
        
        return table;
    }
    
    private void updateLoanTable() {
        try {
            List<Loan> loans = dbManager.getAllLoans();
            ObservableList<Loan> loanData = FXCollections.observableArrayList(loans);
            loanTable.setItems(loanData);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load loans: " + e.getMessage());
        }
    }
    
    private void showIssueLoanDialog() {
        updateStatus("Issue loan dialog");
    }
    
    private void returnSelectedLoan() {
        Loan selected = loanTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a loan to return");
            return;
        }
        
        if (selected.getStatus().equals("Returned")) {
            showAlert("Already Returned", "This book has already been returned");
            return;
        }
        
        try {
            dbManager.returnBook(selected.getId());
            updateLoanTable();
            updateStatus("Book returned successfully");
        } catch (SQLException e) {
            showAlert("Error", "Failed to return book: " + e.getMessage());
        }
    }
    
    private void showReports() {
        VBox reportsView = new VBox(20);
        reportsView.setPadding(new Insets(20));
        
        Label title = new Label("Reports & Analytics");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label info = new Label("Generate reports: Most borrowed books, overdue items, user activity");
        
        reportsView.getChildren().addAll(title, info);
        mainLayout.setCenter(reportsView);
        
        updateStatus("Reports loaded");
    }
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void stop() {
        if (dbManager != null) {
            dbManager.closeConnection();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}