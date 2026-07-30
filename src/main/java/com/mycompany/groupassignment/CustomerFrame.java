/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;
 
/**
 * «boundary» CustomerFrame
 *
 * @author User
 */
 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;
 
public class CustomerFrame extends JFrame {
 
    // Attributes (per UML):
    private VehicleServiceBookingSystem system;
    private User currentUser;
    private FileManager fileManager;
 
    // --- GUI components (not in UML - Swing plumbing) ---
    private JTextField searchKeywordField;
    private JTextField minPriceField;
    private JTextField maxPriceField;
 
    private JTable vehicleTable;
    private DefaultTableModel vehicleTableModel;
 
    private JTextField deliveryAddressField;
    private JTextField rentalDaysField;
 
    private JTextField cancelTokenField;
 
    private JTextField reviewRatingField;
    private JTextArea reviewCommentArea;
 
    private JLabel welcomeLabel;
 
    // Constructor:
    // system and fileManager are handed in by MainMenuFrame - never
    // constructed here, same rule every other class in this project follows.
    public CustomerFrame(VehicleServiceBookingSystem system, FileManager fileManager) {
        this.system = system;
        this.fileManager = fileManager;
 
        setTitle("Vehicle Service Booking - Customer");
        setSize(950, 650);
        setLocationRelativeTo(null);
        // Decision locked in: CustomerFrame is not independently closeable.
        // Closing it (X button or Exit button) always ends the whole app.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        // Resolve who's using the frame BEFORE building the UI, so the
        // welcome label can be built with the right name in one pass.
        this.currentUser = resolveCurrentUser();
 
        initComponents();
        refreshVehicleTable();
    }
 
    // ---------------------------- Login / registration ----------------------------
 
    // Decision locked in (updated): customers log in by NAME, not by ID.
    // The idNumber field still exists on User and is still generated at
    // registration - FileManager still needs a stable key to persist
    // bookings against - but it's now purely internal. The customer is
    // never shown it and never asked for it again after the first visit.
    private User resolveCurrentUser() {
        String name = JOptionPane.showInputDialog(this,
                "Enter your name:", "Customer Login", JOptionPane.QUESTION_MESSAGE);
 
        if (name != null && !name.trim().isEmpty()) {
            User existing = system.findUserByName(name.trim());
            if (existing != null) {
                JOptionPane.showMessageDialog(this, "Welcome back, " + existing.getName() + "!");
                return existing;
            }
        }
 
        // No matching name on file (or they left it blank) - treat as a new customer.
        return registerNewUser(name);
    }
 
    private User registerNewUser(String enteredName) {
        // Re-prompt only if they left the name blank the first time -
        // a name is required either way, returning or brand new.
        String name = (enteredName != null && !enteredName.trim().isEmpty())
                ? enteredName.trim()
                : JOptionPane.showInputDialog(this, "Enter your name:");
 
        String contact = JOptionPane.showInputDialog(this, "Enter your contact number:");
        String email = JOptionPane.showInputDialog(this, "Enter your email:");
        int age = readAge();
 
        // ID is now generated every time and never shown to the customer -
        // findUserByName() is the only thing that needs to find this record
        // again later, so there's no reason to ask the customer for it or
        // let them supply their own.
        String idNumber = UUID.randomUUID().toString();
 
        User newUser = new User(name, idNumber, age, contact, email);
        system.registerUser(newUser);
        fileManager.saveUsers(system.getUserList()); // save immediately, per project convention
        return newUser;
    }
 
    private int readAge() {
        String ageText = JOptionPane.showInputDialog(this, "Enter your age:");
        try {
            return Integer.parseInt(ageText.trim());
        } catch (Exception e) {
            // TODO exception-handling pass: reject/re-prompt on invalid age
            // instead of silently defaulting to 0.
            JOptionPane.showMessageDialog(this, "Invalid age entered - defaulting to 0.");
            return 0;
        }
    }
 
    // ---------------------------------- UI setup -----------------------------------
 
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
 
        // ID number is now purely internal - no reason to show it here.
        welcomeLabel = new JLabel("Logged in as: " + currentUser.getName());
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 10));
        add(welcomeLabel, BorderLayout.PAGE_START);
 
        add(buildSearchPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildActionPanel(), BorderLayout.SOUTH);
    }
 
    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search Vehicles"));
 
        panel.add(new JLabel("Keyword:"));
        searchKeywordField = new JTextField(10);
        panel.add(searchKeywordField);
 
        panel.add(new JLabel("Min Price:"));
        minPriceField = new JTextField(6);
        panel.add(minPriceField);
 
        panel.add(new JLabel("Max Price:"));
        maxPriceField = new JTextField(6);
        panel.add(maxPriceField);
 
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> onSearch());
        panel.add(searchButton);
 
        JButton showAllButton = new JButton("Show All Vehicles");
        showAllButton.addActionListener(e -> refreshVehicleTable());
        panel.add(showAllButton);
 
        return panel;
    }
 
    private JScrollPane buildTablePanel() {
        String[] columns = {"Vehicle ID", "Brand", "Model", "Price/Day", "Available", "Avg Rating"};
        vehicleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only table - editing happens through the action panel
            }
        };
        vehicleTable = new JTable(vehicleTableModel);
        vehicleTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
 
        JScrollPane scrollPane = new JScrollPane(vehicleTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Fleet"));
        return scrollPane;
    }
 
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 4, 4));
 
        panel.add(buildBookRow());
        panel.add(buildCancelRow());
        panel.add(buildReviewRow());
        panel.add(buildExitRow());
 
        return panel;
    }
 
    private JPanel buildBookRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBorder(BorderFactory.createTitledBorder("Book Selected Vehicle"));
 
        row.add(new JLabel("Delivery Address:"));
        deliveryAddressField = new JTextField(16);
        row.add(deliveryAddressField);
 
        row.add(new JLabel("Rental Days:"));
        rentalDaysField = new JTextField(4);
        row.add(rentalDaysField);
 
        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(e -> onBook());
        row.add(bookButton);
 
        return row;
    }
 
    private JPanel buildCancelRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBorder(BorderFactory.createTitledBorder("Cancel a Booking"));
 
        row.add(new JLabel("Booking Token:"));
        cancelTokenField = new JTextField(28);
        row.add(cancelTokenField);
 
        JButton cancelButton = new JButton("Cancel Booking");
        cancelButton.addActionListener(e -> onCancel());
        row.add(cancelButton);
 
        return row;
    }
 
    private JPanel buildReviewRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBorder(BorderFactory.createTitledBorder("Leave a Review on Selected Vehicle"));
 
        row.add(new JLabel("Rating (1-5):"));
        reviewRatingField = new JTextField(3);
        row.add(reviewRatingField);
 
        row.add(new JLabel("Comment:"));
        reviewCommentArea = new JTextArea(1, 20);
        row.add(new JScrollPane(reviewCommentArea));
 
        JButton reviewButton = new JButton("Submit Review");
        reviewButton.addActionListener(e -> onReview());
        row.add(reviewButton);
 
        return row;
    }
 
    private JPanel buildExitRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exitButton = new JButton("Exit Application");
        // Decision locked in: exit always means the whole app closes, not
        // "return to MainMenuFrame" - so this goes straight to System.exit.
        exitButton.addActionListener(e -> System.exit(0));
        row.add(exitButton);
        return row;
    }
 
    // ------------------------------- UML behaviour methods --------------------------
 
    public void onSearch() {
        String keyword = searchKeywordField.getText();
        double minPrice = parseDoubleOrDefault(minPriceField.getText(), 0.0);
        double maxPrice = parseDoubleOrDefault(maxPriceField.getText(), Double.MAX_VALUE);
 
        // Goes through User.searchVehicles(), per the UML, not directly
        // through the system - same pattern leaveReview() already follows.
        List<Vehicle> results = currentUser.searchVehicles(system, keyword, minPrice, maxPrice);
        populateTable(results);
    }
 
    public void onBook() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle from the table first.");
            return;
        }
 
        String vehicleId = (String) vehicleTableModel.getValueAt(selectedRow, 0);
        String deliveryAddress = deliveryAddressField.getText();
        int days = parseIntOrDefault(rentalDaysField.getText(), -1);
 
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a delivery address.");
            return;
        }
        if (days <= 0) {
            JOptionPane.showMessageDialog(this, "Enter a valid number of rental days.");
            return;
        }
 
        Booking booking = currentUser.bookVehicle(system, vehicleId, deliveryAddress, days);
        if (booking == null) {
            // createBooking() returns null on failure - documented stopgap
            // in VehicleServiceBookingSystem, ahead of the exception-handling pass.
            JOptionPane.showMessageDialog(this, "Booking failed - that vehicle may no longer be available.");
            return;
        }
 
        // Booking changes both bookingList AND the vehicle's isAvailable flag,
        // so this needs saveAll(), not just saveBookings().
        fileManager.saveAll(system);
        JOptionPane.showMessageDialog(this, "Booked! Your booking token is:\n" + booking.getBookingToken());
        refreshVehicleTable();
    }
 
    public void onCancel() {
        String token = cancelTokenField.getText();
        if (token == null || token.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a booking token to cancel.");
            return;
        }
 
        boolean cancelled = currentUser.cancelBooking(system, token.trim());
        if (!cancelled) {
            JOptionPane.showMessageDialog(this, "No booking found with that token.");
            return;
        }
 
        // Same reasoning as onBook(): cancel() frees the vehicle too.
        fileManager.saveAll(system);
        JOptionPane.showMessageDialog(this, "Booking cancelled.");
        refreshVehicleTable();
    }
 
    public void onReview() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle from the table first.");
            return;
        }
 
        String vehicleId = (String) vehicleTableModel.getValueAt(selectedRow, 0);
        int rating = parseIntOrDefault(reviewRatingField.getText(), -1);
 
        if (rating < 1 || rating > 5) {
            // TODO exception-handling pass: this validation belongs in
            // Review's constructor (no setters exist) - flagged in the design log.
            JOptionPane.showMessageDialog(this, "Rating must be between 1 and 5.");
            return;
        }
 
        String comment = reviewCommentArea.getText();
 
        // Goes through User.leaveReview(), per the UML, not directly through Vehicle.
        currentUser.leaveReview(system, vehicleId, rating, comment);
 
        // Only vehicleList changed (reviews are nested inside each vehicle's
        // saved line) - saveVehicles() alone is correct here, saveAll() would
        // be wasteful.
        fileManager.saveVehicles(system.getVehicleList());
        JOptionPane.showMessageDialog(this, "Thanks for your review!");
        refreshVehicleTable();
    }
 
    public void refreshVehicleTable() {
        // Deliberately the FULL fleet via getVehicleList(), not the filtered
        // searchVehicles() results - lets the customer see what's unavailable too.
        populateTable(system.getVehicleList());
    }
 
    // ------------------------------------ Helpers -----------------------------------
 
    private void populateTable(List<Vehicle> vehicles) {
        vehicleTableModel.setRowCount(0);
        for (Vehicle v : vehicles) {
            vehicleTableModel.addRow(new Object[] {
                v.getVehicleID(),
                v.getBrand(),
                v.getModel(),
                v.getPrice(),
                v.getIsAvailable() ? "Yes" : "No",
                String.format("%.1f", v.getAverageRating())
            });
        }
    }
 
    private double parseDoubleOrDefault(String text, double defaultValue) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
 
    private int parseIntOrDefault(String text, int defaultValue) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}