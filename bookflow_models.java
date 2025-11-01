// BookFlow - Model Classes
// Created by Michael Semera
// Book.java, User.java, Loan.java

package com.michaelsemera.bookflow;

import java.time.LocalDate;

// ==================== Book.java ====================
public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private int publicationYear;
    private String category;
    private int totalCopies;
    private int availableCopies;
    
    public Book(int id, String title, String author, String isbn, String publisher,
                int publicationYear, String category, int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }
    
    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getPublisher() { return publisher; }
    public int getPublicationYear() { return publicationYear; }
    public String getCategory() { return category; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setPublicationYear(int year) { this.publicationYear = year; }
    public void setCategory(String category) { this.category = category; }
    public void setTotalCopies(int total) { this.totalCopies = total; }
    public void setAvailableCopies(int available) { this.availableCopies = available; }
    
    // Utility methods
    public boolean isAvailable() {
        return availableCopies > 0;
    }
    
    public int getBorrowedCopies() {
        return totalCopies - availableCopies;
    }
    
    @Override
    public String toString() {
        return String.format("%s by %s (%d) - %d/%d available", 
            title, author, publicationYear, availableCopies, totalCopies);
    }
}

// ==================== User.java ====================
class User {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String membershipType; // Basic, Premium, Student
    private LocalDate registrationDate;
    private String status; // Active, Suspended, Inactive
    
    public User(int id, String name, String email, String phone, String address,
                String membershipType, LocalDate registrationDate, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.membershipType = membershipType;
        this.registrationDate = registrationDate;
        this.status = status;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getMembershipType() { return membershipType; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public String getStatus() { return status; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setMembershipType(String type) { this.membershipType = type; }
    public void setRegistrationDate(LocalDate date) { this.registrationDate = date; }
    public void setStatus(String status) { this.status = status; }
    
    // Utility methods
    public boolean isActive() {
        return "Active".equals(status);
    }
    
    public int getMembershipDays() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(registrationDate, LocalDate.now());
    }
    
    public int getMaxBooksAllowed() {
        switch (membershipType) {
            case "Premium": return 10;
            case "Student": return 5;
            case "Basic":
            default: return 3;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, email, membershipType);
    }
}

// ==================== Loan.java ====================
class Loan {
    private int id;
    private int bookId;
    private int userId;
    private String bookTitle;
    private String userName;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // Active, Returned, Overdue
    private double fine;
    
    public Loan(int id, int bookId, int userId, String bookTitle, String userName,
                LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                String status, double fine) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.userName = userName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
    }
    
    // Getters
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public String getBookTitle() { return bookTitle; }
    public String getUserName() { return userName; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getStatus() { return status; }
    public double getFine() { return fine; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setBookTitle(String title) { this.bookTitle = title; }
    public void setUserName(String name) { this.userName = name; }
    public void setIssueDate(LocalDate date) { this.issueDate = date; }
    public void setDueDate(LocalDate date) { this.dueDate = date; }
    public void setReturnDate(LocalDate date) { this.returnDate = date; }
    public void setStatus(String status) { this.status = status; }
    public void setFine(double fine) { this.fine = fine; }
    
    // Utility methods
    public boolean isOverdue() {
        if ("Returned".equals(status)) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
    }
    
    public int getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    public double calculateFine(double finePerDay) {
        return getDaysOverdue() * finePerDay;
    }
    
    public int getLoanDuration() {
        LocalDate endDate = (returnDate != null) ? returnDate : LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(issueDate, endDate);
    }
    
    @Override
    public String toString() {
        return String.format("Loan #%d: %s -> %s (Due: %s, Status: %s)", 
            id, bookTitle, userName, dueDate, status);
    }
}