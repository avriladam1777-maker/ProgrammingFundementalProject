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
 
    private JTable myBookingsTable;
    private DefaultTableModel myBookingsTableModel;
 
    private JTextField deliveryAddressField;
    private JTextField rentalDaysField;
 
    private JTextField cancelTokenField;
 
    private JTextField reviewRatingField;
    private JTextArea reviewCommentArea;
 
    private JLabel welcomeLabel;
 
    private JTextArea announcementListArea;
 
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
        if (this.currentUser == null) {
            System.exit(0);
        }
 
        initComponents();
        refreshVehicleTable();
        refreshMyBookingsTable();
        refreshAnnouncementList();
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
 
        if (name == null) {
            return null;
        }

        if (!name.trim().isEmpty()) {
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
        String name = enteredName;
        while (name == null || name.trim().isEmpty()) {
            name = JOptionPane.showInputDialog(this, "Enter your name:");
            if (name == null) {
                return null;
            }
        }
        name = name.trim();
 
        String contact = JOptionPane.showInputDialog(this, "Enter your contact number:");
        if (contact == null) return null;
        
        String email = JOptionPane.showInputDialog(this, "Enter your email:");
        if (email == null) return null;
        
        for (User u : system.getUserList()) {
            if (u.getContactNum().equals(contact.trim()) || u.getUserEmail().equals(email.trim())) {
                JOptionPane.showMessageDialog(this, "This contact number or email is already registered.");
                return null;
            }
        }
        
        int age = readAge();
        if (age == -1) return null;
 
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
        if (ageText == null) {
            return -1;
        }
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
 
        // New: everything that used to sit directly on the frame now lives
        // inside a "Book a Vehicle" tab, alongside a new read-only
        // Announcements tab. Chosen over a login-time pop-up because it lets
        // the customer check for new posts anytime during their session via
        // Refresh, rather than only ever seeing a one-time snapshot at login.
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Book a Vehicle", buildBookingTab());
        tabs.addTab("Announcements", buildAnnouncementsTab());
        tabs.addTab("My Bookings", buildMyBookingsTab());
        add(tabs, BorderLayout.CENTER);
    }
 
    private JPanel buildBookingTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.add(buildSearchPanel(), BorderLayout.NORTH);
        panel.add(buildTablePanel(), BorderLayout.CENTER);
        panel.add(buildActionPanel(), BorderLayout.SOUTH);
        return panel;
    }
 
    private JPanel buildMyBookingsTab() {
        String[] columns = {"Token", "Vehicle", "Days", "Date", "Status", "Total Cost"};
        myBookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myBookingsTable = new JTable(myBookingsTableModel);
        myBookingsTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        
        JScrollPane scrollPane = new JScrollPane(myBookingsTable);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton copyButton = new JButton("Copy Selected Token");
        copyButton.addActionListener(e -> {
            int selectedRow = myBookingsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select a booking from the table first.");
                return;
            }
            String token = (String) myBookingsTableModel.getValueAt(selectedRow, 0);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new java.awt.datatransfer.StringSelection(token), null);
            JOptionPane.showMessageDialog(this, "Token copied to clipboard!");
        });
        copyPanel.add(copyButton);
        panel.add(copyPanel, BorderLayout.SOUTH);
        
        return panel;
    }
 
    private JPanel buildAnnouncementsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
 
        announcementListArea = new JTextArea();
        announcementListArea.setEditable(false);
        JScrollPane listScroll = new JScrollPane(announcementListArea);
        listScroll.setBorder(BorderFactory.createTitledBorder("Announcements"));
        panel.add(listScroll, BorderLayout.CENTER);
 
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(150, 30));
        refreshButton.addActionListener(e -> refreshAnnouncementList());
        JPanel refreshRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshRow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        refreshRow.add(refreshButton);
        panel.add(refreshRow, BorderLayout.SOUTH);
 
        return panel;
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
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.addActionListener(e -> onSearch());
        panel.add(searchButton);
 
        JButton showAllButton = new JButton("Show All Vehicles");
        showAllButton.setPreferredSize(new Dimension(150, 30));
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
        JPanel panel = new JPanel(new GridLayout(4, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
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
        bookButton.setPreferredSize(new Dimension(100, 30));
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
        cancelButton.setPreferredSize(new Dimension(150, 30));
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
        reviewButton.setPreferredSize(new Dimension(150, 30));
        reviewButton.addActionListener(e -> onReview());
        row.add(reviewButton);
 
        return row;
    }
 
    private JPanel buildExitRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exitButton = new JButton("Exit Application");
        exitButton.setPreferredSize(new Dimension(150, 30));
        // Decision locked in: exit always means the whole app closes, not
        // "return to MainMenuFrame" - so this goes straight to System.exit.
        exitButton.addActionListener(e -> System.exit(0));
        row.add(exitButton);
        return row;
    }
 
    // ------------------------------- UML behaviour methods --------------------------
 
    public void onSearch() {
        String keyword = searchKeywordField.getText();
        double minPrice = 0.0;
        double maxPrice = Double.MAX_VALUE;
        try {
            if (!minPriceField.getText().trim().isEmpty()) {
                minPrice = Double.parseDouble(minPriceField.getText().trim());
            }
            if (!maxPriceField.getText().trim().isEmpty()) {
                maxPrice = Double.parseDouble(maxPriceField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for prices.");
            return;
        }
 
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
 
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a delivery address.");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(rentalDaysField.getText().trim());
            if (days <= 0) {
                JOptionPane.showMessageDialog(this, "Enter a valid number of rental days.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for rental days.");
            return;
        }
 
        try {
            Booking booking = currentUser.bookVehicle(system, vehicleId, deliveryAddress.trim(), days);
            if (booking == null) {
                // createBooking() returns null on failure - documented stopgap
                // in VehicleServiceBookingSystem, ahead of the exception-handling pass.
                JOptionPane.showMessageDialog(this, "Booking failed - that vehicle may no longer be available.");
                return;
            }
            fileManager.saveAll(system);
            JOptionPane.showMessageDialog(this, "Booked! Your booking token is:\n" + booking.getBookingToken());
            refreshVehicleTable();
            refreshMyBookingsTable();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save booking: " + e.getMessage());
        }
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
 
        try {
            fileManager.saveAll(system);
            JOptionPane.showMessageDialog(this, "Booking cancelled.");
            refreshVehicleTable();
            refreshMyBookingsTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save cancellation: " + e.getMessage());
        }
    }
 
    public void onReview() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle from the table first.");
            return;
        }
 
        String vehicleId = (String) vehicleTableModel.getValueAt(selectedRow, 0);
        
        boolean hasBooked = false;
        for (Booking b : system.getBookingList()) {
            if (b.getCustomer().getName().equals(currentUser.getName()) && b.getVehicle().getVehicleID().equals(vehicleId)) {
                hasBooked = true;
                break;
            }
        }
        
        if (!hasBooked) {
            JOptionPane.showMessageDialog(this, "You can only review vehicles you have booked.");
            return;
        }
        
        int rating;
        try {
            rating = Integer.parseInt(reviewRatingField.getText().trim());
            if (rating < 1 || rating > 5) {
                JOptionPane.showMessageDialog(this, "Rating must be between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for rating.");
            return;
        }
 
        String comment = reviewCommentArea.getText();
        if (comment == null || comment.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a comment.");
            return;
        }
 
        try {
            currentUser.leaveReview(system, vehicleId, rating, comment.trim());
            fileManager.saveVehicles(system.getVehicleList());
            JOptionPane.showMessageDialog(this, "Thanks for your review!");
            refreshVehicleTable();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save review: " + e.getMessage());
        }
    }
 
    public void refreshVehicleTable() {
        // Deliberately the FULL fleet via getVehicleList(), not the filtered
        // searchVehicles() results - lets the customer see what's unavailable too.
        populateTable(system.getVehicleList());
    }
 
    // ------------------------------------ Helpers -----------------------------------
 
    public void refreshMyBookingsTable() {
        myBookingsTableModel.setRowCount(0);
        for (Booking booking : system.getBookingList()) {
            if (booking.getCustomer().getName().equals(currentUser.getName())) {
                myBookingsTableModel.addRow(new Object[]{
                    booking.getBookingToken(),
                    booking.getVehicle().getBrand() + " " + booking.getVehicle().getModel(),
                    booking.rentalDurationDays,
                    booking.getBookingDate(),
                    booking.getStatus(),
                    String.format("%.2f", booking.calculateTotalCost())
                });
            }
        }
    }
 
    private void refreshAnnouncementList() {
        StringBuilder sb = new StringBuilder();
        for (Announcement a : system.getAnnouncementList()) {
            sb.append("[").append(a.datePosted).append("] ").append(a.content).append("\n\n");
        }
        if (sb.length() == 0) {
            sb.append("No announcements yet.");
        }
        announcementListArea.setText(sb.toString());
    }
 
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
}