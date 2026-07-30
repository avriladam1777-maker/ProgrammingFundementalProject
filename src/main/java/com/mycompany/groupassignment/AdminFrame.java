/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.groupassignment;

/**
 *
 * @author User
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
 
public class AdminFrame extends JFrame {
 
    // Attributes (per UML):
    private final VehicleServiceBookingSystem system;
    private final Admin currentAdmin;
    private final FileManager fileManager;
 
    // --- GUI components (not in UML - Swing plumbing) ---
    private JLabel welcomeLabel;
 
    // Vehicles tab
    private JComboBox<String> vehicleTypeCombo;
    private JTextField vehicleIdField;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField priceField;
    private JCheckBox availableCheckBox;
 
    private JTextField numDoorsField;
    private JTextField transmissionField;
    private JTextField carMileageField;
 
    private JTextField engineCCField;
    private JTextField motoMileageField;
 
    private CardLayout typeCardLayout;
    private JPanel typeCardPanel;
 
    private JTable vehicleStatusTable;
    private DefaultTableModel vehicleStatusTableModel;
 
    // Bookings tab
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;
 
    // Announcements tab
    private JTextArea announcementField;
    private JTextArea announcementListArea;
 
    // Manage Sub-Admins tab (main admin only)
    private JTable subAdminTable;
    private DefaultTableModel subAdminTableModel;
    private JTextField newSubAdminUsernameField;
    private JPasswordField newSubAdminPasswordField;
    
    // Constructor:
    // system and fileManager are handed in by MainMenuFrame - same rule
    // every other class in this project follows.
    public AdminFrame(VehicleServiceBookingSystem system, FileManager fileManager) {
        this.system = system;
        this.fileManager = fileManager;
 
        setTitle("Vehicle Service Booking - Admin");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        // Decision locked in: unlike CustomerFrame, closing AdminFrame only
        // ends the admin's session. MainMenuFrame is never hidden behind it
        // (see MainMenuFrame.openAdminLogin()), so it's still there to
        // return to - EXIT_ON_CLOSE would throw that away for no reason.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
 
        this.currentAdmin = resolveCurrentAdmin();
        if (this.currentAdmin == null) {
            // Login was cancelled - nothing to show. Dispose immediately
            // and hand control back to MainMenuFrame.
            dispose();
            return;
        }
 
        initComponents();
        onViewVehicleStatus();
        // Bookings, announcements, and sub-admin management are main-admin
        // only - the components backing them don't even exist for a
        // sub-admin session (see initComponents()), so guard the refresh
        // calls the same way.
        if (currentAdmin.isMainAdmin()) {
            onViewBookings();
            refreshAnnouncementList();
            refreshSubAdminTable();
        }
    }
 
    public boolean isLoginSuccessful() {
        return currentAdmin != null;
    }

    // ---------------------------------- Login -----------------------------------
 
    private Admin resolveCurrentAdmin() {
        while (true) {
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
            };
            int option = JOptionPane.showConfirmDialog(this, message,
                    "Admin Login", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) {
                return null; // admin backed out of the login dialog
            }
 
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            // A deactivated sub-admin's credentials will still be correct,
            // but authenticateAdmin() returns null for them anyway - same
            // message as a wrong password, deliberately not revealing why.
            Admin admin = system.authenticateAdmin(username, password);
 
            if (admin != null) {
                JOptionPane.showMessageDialog(this, "Welcome, " + admin.getAdminUsername() + "!");
                return admin;
            }
            JOptionPane.showMessageDialog(this, "Invalid credentials. Try again.");
        }
    }
 
    // ---------------------------------- UI setup ---------------------------------
 
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
 
        welcomeLabel = new JLabel("Logged in as: " + currentAdmin.getAdminUsername());
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(6, 10, 0, 10));
        add(welcomeLabel, BorderLayout.PAGE_START);
 
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Vehicles", buildVehiclesTab());
        
        // Hard rule from the updated requirements: sub-admins only get
        // vehicle management. Everything else - bookings, announcements,
        // managing other admins - is main-admin only, enforced simply by
        // never building those tabs for a sub-admin session.
        if (currentAdmin.isMainAdmin()) {
            tabs.addTab("Bookings", buildBookingsTab());
            tabs.addTab("Announcements", buildAnnouncementsTab());
            tabs.addTab("Manage Sub-Admins", buildManageAdminsTab());
        }
        
        add(tabs, BorderLayout.CENTER);
 
        add(buildNavigationBar(), BorderLayout.SOUTH);
    }
 
    private JPanel buildVehiclesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
 
        panel.add(buildAddVehicleForm(), BorderLayout.NORTH);
        panel.add(buildVehicleStatusTable(), BorderLayout.CENTER);
 
        // Update reuses the Add Vehicle form fields (brand/model/price/imagePath)
        // against whichever row is selected below - onUpdateVehicle() already
        // expected this ("...in the form above, then Update"), it just had no
        // button wired to it yet.
        JButton updateButton = new JButton("Update Selected Vehicle");
        updateButton.setPreferredSize(new Dimension(200, 30));
        updateButton.addActionListener(e -> onUpdateVehicle());
 
        JButton removeButton = new JButton("Remove Selected Vehicle");
        removeButton.setPreferredSize(new Dimension(200, 30));
        removeButton.addActionListener(e -> onRemoveVehicle());
 
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionRow.add(updateButton);
        actionRow.add(removeButton);
        panel.add(actionRow, BorderLayout.SOUTH);
 
        return panel;
    }
 
    private JPanel buildAddVehicleForm() {
        JPanel form = new JPanel();
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Add Vehicle"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
 
        JPanel commonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        vehicleTypeCombo = new JComboBox<>(new String[]{"Car", "Motorcycle"});
        vehicleIdField = new JTextField(8);
        brandField = new JTextField(8);
        modelField = new JTextField(8);
        priceField = new JTextField(6);
        availableCheckBox = new JCheckBox("Available", true);
 
        commonRow.add(new JLabel("Type:"));
        commonRow.add(vehicleTypeCombo);
        commonRow.add(new JLabel("ID:"));
        commonRow.add(vehicleIdField);
        commonRow.add(new JLabel("Brand:"));
        commonRow.add(brandField);
        commonRow.add(new JLabel("Model:"));
        commonRow.add(modelField);
        commonRow.add(new JLabel("Price/Day:"));
        commonRow.add(priceField);
        commonRow.add(availableCheckBox);
        form.add(commonRow);
 
        // Type-specific fields swap via CardLayout, driven by vehicleTypeCombo -
        // this is the one place polymorphism surfaces in the GUI layer: the
        // concrete class constructed in onAddVehicle() depends on this choice.
        typeCardLayout = new CardLayout();
        typeCardPanel = new JPanel(typeCardLayout);
        typeCardPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        typeCardPanel.add(buildCarFieldsPanel(), "Car");
        typeCardPanel.add(buildMotoFieldsPanel(), "Motorcycle");
        form.add(typeCardPanel);
 
        vehicleTypeCombo.addActionListener(e ->
                typeCardLayout.show(typeCardPanel, (String) vehicleTypeCombo.getSelectedItem()));
 
        JButton addButton = new JButton("Add Vehicle");
        addButton.setPreferredSize(new Dimension(150, 30));
        addButton.addActionListener(e -> onAddVehicle());
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.add(addButton);
        form.add(addRow);
 
        return form;
    }
 
    private JPanel buildCarFieldsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        numDoorsField = new JTextField(3);
        transmissionField = new JTextField(8);
        carMileageField = new JTextField(6);
 
        panel.add(new JLabel("Doors:"));
        panel.add(numDoorsField);
        panel.add(new JLabel("Transmission:"));
        panel.add(transmissionField);
        panel.add(new JLabel("Mileage:"));
        panel.add(carMileageField);
        return panel;
    }
 
    private JPanel buildMotoFieldsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        engineCCField = new JTextField(6);
        motoMileageField = new JTextField(6);
 
        panel.add(new JLabel("Engine CC:"));
        panel.add(engineCCField);
        panel.add(new JLabel("Mileage:"));
        panel.add(motoMileageField);
        return panel;
    }
 
    private JScrollPane buildVehicleStatusTable() {
        String[] columns = {"Vehicle ID", "Brand", "Model", "Price/Day", "Available", "Avg Rating"};
        vehicleStatusTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vehicleStatusTable = new JTable(vehicleStatusTableModel);
        vehicleStatusTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
 
        JScrollPane scrollPane = new JScrollPane(vehicleStatusTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Current Fleet"));
        return scrollPane;
    }
 
    private JScrollPane buildBookingsTab() {
        String[] columns = {"Token", "Customer", "Vehicle", "Days", "Date", "Status", "Total Cost"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(bookingsTableModel);
 
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Bookings"));
        return scrollPane;
    }
 
    private JPanel buildAnnouncementsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
 
        announcementListArea = new JTextArea();
        announcementListArea.setEditable(false);
        JScrollPane listScroll = new JScrollPane(announcementListArea);
        listScroll.setBorder(BorderFactory.createTitledBorder("Posted Announcements"));
        panel.add(listScroll, BorderLayout.CENTER);
 
        JPanel postRow = new JPanel(new BorderLayout(4, 4));
        postRow.setBorder(BorderFactory.createTitledBorder("Post New Announcement"));
        announcementField = new JTextArea(3, 20);
        postRow.add(new JScrollPane(announcementField), BorderLayout.CENTER);
 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove Latest Announcement");
        removeButton.setPreferredSize(new Dimension(220, 30));
        removeButton.addActionListener(e -> onRemoveLatestAnnouncement());
        buttonPanel.add(removeButton);

        JButton postButton = new JButton("Post Announcement");
        postButton.setPreferredSize(new Dimension(180, 30));
        postButton.addActionListener(e -> onPostAnnouncement());
        buttonPanel.add(postButton);
        
        postRow.add(buttonPanel, BorderLayout.EAST);
 
        panel.add(postRow, BorderLayout.SOUTH);
        return panel;
    }
 
    private JPanel buildManageAdminsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
 
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.setBorder(BorderFactory.createTitledBorder("Add Sub-Admin"));
        newSubAdminUsernameField = new JTextField(10);
        newSubAdminPasswordField = new JPasswordField(10);
        addRow.add(new JLabel("Username:"));
        addRow.add(newSubAdminUsernameField);
        addRow.add(new JLabel("Password:"));
        addRow.add(newSubAdminPasswordField);
        JButton addSubAdminButton = new JButton("Add Sub-Admin");
        addSubAdminButton.setPreferredSize(new Dimension(150, 30));
        addSubAdminButton.addActionListener(e -> onAddSubAdmin());
        addRow.add(addSubAdminButton);
        panel.add(addRow, BorderLayout.NORTH);
 
        String[] columns = {"Admin ID", "Username", "Active"};
        subAdminTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Returning Boolean.class for the "Active" column is the
                // entire trick - Swing renders Boolean columns as checkboxes
                // automatically. This is what makes it "as simple as a checkbox".
                return columnIndex == 2 ? Boolean.class : String.class;
            }
 
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // only the checkbox is clickable
            }
        };
        subAdminTable = new JTable(subAdminTableModel);
        subAdminTableModel.addTableModelListener(e -> {
            if (e.getColumn() == 2) {
                onToggleSubAdminActive(e.getFirstRow());
            }
        });
 
        JScrollPane scrollPane = new JScrollPane(subAdminTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                "Sub-Admins  (uncheck Active to block their access)"));
        panel.add(scrollPane, BorderLayout.CENTER);
 
        return panel;
    }
    
    private JPanel buildNavigationBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JButton backButton = new JButton("Log Out / Back");
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.addActionListener(e -> dispose());
        leftPanel.add(backButton);
        
        JButton exitButton = new JButton("Exit Application");
        exitButton.setPreferredSize(new Dimension(150, 35));
        exitButton.addActionListener(e -> System.exit(0));
        leftPanel.add(exitButton);
        
        navBar.add(leftPanel, BorderLayout.WEST);
        
        JButton saveButton = new JButton("Save All Changes");
        saveButton.setPreferredSize(new Dimension(200, 35));
        saveButton.addActionListener(e -> saveChanges());
        navBar.add(saveButton, BorderLayout.EAST);
        
        return navBar;
    }
 
    // ------------------------------- UML behaviour methods ------------------------
 
    public void onAddVehicle() {
        String vehicleId = vehicleIdField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        boolean available = availableCheckBox.isSelected();
 
        if (vehicleId == null || vehicleId.trim().isEmpty() || brand == null || brand.trim().isEmpty() || model == null || model.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter valid non-empty values for ID, Brand, and Model.");
            return;
        }
 
        for (Vehicle v : system.getVehicleList()) {
            if (v.getVehicleID().equals(vehicleId.trim())) {
                JOptionPane.showMessageDialog(this, "A vehicle with this ID already exists.");
                return;
            }
        }
 
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be strictly positive.");
                return;
            }
            
            Vehicle vehicle;
            String type = (String) vehicleTypeCombo.getSelectedItem();
            if ("Car".equals(type)) {
                int numDoors = Integer.parseInt(numDoorsField.getText().trim());
                if (numDoors < 2 || numDoors > 6) {
                    JOptionPane.showMessageDialog(this, "Doors must be between 2 and 6.");
                    return;
                }
                String transmission = transmissionField.getText();
                if (transmission == null || transmission.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Enter a valid transmission type.");
                    return;
                }
                int mileage = Integer.parseInt(carMileageField.getText().trim());
                if (mileage < 0) {
                    JOptionPane.showMessageDialog(this, "Mileage cannot be negative.");
                    return;
                }
                vehicle = new Car(vehicleId.trim(), brand.trim(), model.trim(), price, available,
                        numDoors, transmission.trim(), mileage);
            } else {
                int engineCC = Integer.parseInt(engineCCField.getText().trim());
                if (engineCC <= 50) {
                    JOptionPane.showMessageDialog(this, "Engine CC must be greater than 50.");
                    return;
                }
                int mileage = Integer.parseInt(motoMileageField.getText().trim());
                if (mileage < 0) {
                    JOptionPane.showMessageDialog(this, "Mileage cannot be negative.");
                    return;
                }
                vehicle = new Motorcycle(vehicleId.trim(), brand.trim(), model.trim(), price, available,
                        engineCC, mileage);
            }
 
            currentAdmin.addVehicle(system, vehicle);
            fileManager.saveVehicles(system.getVehicleList()); // only vehicleList changed
            JOptionPane.showMessageDialog(this, "Vehicle added.");
            onViewVehicleStatus();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price, Doors, CC, and Mileage must be valid numbers.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save vehicle: " + e.getMessage());
        }
    }
    
    public void onUpdateVehicle() {
        int row = vehicleStatusTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle from the table first.");
            return;
        }
 
        String vehicleId = (String) vehicleStatusTableModel.getValueAt(row, 0);
        String brand = brandField.getText();
        String model = modelField.getText();
        
        if (brand == null || brand.trim().isEmpty() || model == null || model.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter valid non-empty values for Brand and Model.");
            return;
        }
 
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be strictly positive.");
                return;
            }
 
            // Admin.updateVehicle() only touches brand/model/price
            // (confirmed against the real method signature) - type-specific
            // fields and availability aren't editable through Update.
            boolean updated = currentAdmin.updateVehicle(system, vehicleId, brand.trim(), model.trim(), price);
            if (!updated) {
                JOptionPane.showMessageDialog(this, "Vehicle not found - nothing was updated.");
                return;
            }
 
            fileManager.saveVehicles(system.getVehicleList());
            JOptionPane.showMessageDialog(this, "Vehicle updated.");
            onViewVehicleStatus();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to update vehicle: " + e.getMessage());
        }
    }
 
    public void onRemoveVehicle() {
        int row = vehicleStatusTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle from the table first.");
            return;
        }
 
        try {
            String vehicleId = (String) vehicleStatusTableModel.getValueAt(row, 0);
            currentAdmin.removeVehicle(system, vehicleId);
            fileManager.saveVehicles(system.getVehicleList());
            JOptionPane.showMessageDialog(this, "Vehicle removed.");
            onViewVehicleStatus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to remove vehicle: " + e.getMessage());
        }
    }
 
    public void onPostAnnouncement() {
        String content = announcementField.getText();
        if (content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an announcement message first.");
            return;
        }
 
        try {
            currentAdmin.postAnnouncement(system, content.trim());
            fileManager.saveAnnouncements(system.getAnnouncementList());
            announcementField.setText("");
            JOptionPane.showMessageDialog(this, "Announcement posted.");
            refreshAnnouncementList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to post announcement: " + e.getMessage());
        }
    }
 
    public void onRemoveLatestAnnouncement() {
        List<Announcement> announcements = system.getAnnouncementList();
        if (announcements.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No announcements to remove.");
            return;
        }
        
        announcements.remove(announcements.size() - 1);
        try {
            fileManager.saveAnnouncements(announcements);
            refreshAnnouncementList();
            JOptionPane.showMessageDialog(this, "Latest announcement removed.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to remove announcement: " + e.getMessage());
        }
    }
 
    public void onViewBookings() {
        bookingsTableModel.setRowCount(0);
        List<Booking> bookings = currentAdmin.viewAllBookings(system);
        for (Booking b : bookings) {
            bookingsTableModel.addRow(new Object[]{
                b.getBookingToken(),
                b.getCustomer().getName(),
                b.getVehicle().getBrand() + " " + b.getVehicle().getModel(),
                b.rentalDurationDays,
                b.getBookingDate(),
                b.getStatus(),
                String.format("%.2f", b.calculateTotalCost())
            });
        }
    }
 
    public void onViewVehicleStatus() {
        vehicleStatusTableModel.setRowCount(0);
        List<Vehicle> vehicles = currentAdmin.viewVehicleStatus(system);
        for (Vehicle v : vehicles) {
            vehicleStatusTableModel.addRow(new Object[]{
                v.getVehicleID(),
                v.getBrand(),
                v.getModel(),
                v.getPrice(),
                v.getIsAvailable() ? "Yes" : "No",
                String.format("%.1f", v.getAverageRating())
            });
        }
    }
    
    // --- New: sub-admin management (main admin only) ---
 
    public void onAddSubAdmin() {
        String username = newSubAdminUsernameField.getText();
        String password = new String(newSubAdminPasswordField.getPassword());
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a valid non-empty username and password for the new sub-admin.");
            return;
        }
        
        for (Admin a : system.getAdminList()) {
            if (a.getAdminUsername().equals(username.trim())) {
                JOptionPane.showMessageDialog(this, "This username is already taken.");
                return;
            }
        }
 
        // currentAdmin.createSubAdmin() itself refuses and returns null if
        // this admin isn't the main admin - this check is redundant since
        // this tab only exists for the main admin, but cheap insurance.
        Admin subAdmin = currentAdmin.createSubAdmin(system, username.trim(), password);
        if (subAdmin == null) {
            JOptionPane.showMessageDialog(this, "Only the main admin can add sub-admins.");
            return;
        }
 
        fileManager.saveAdmins(system.getAdminList());
        newSubAdminUsernameField.setText("");
        newSubAdminPasswordField.setText("");
        JOptionPane.showMessageDialog(this, "Sub-admin added.");
        refreshSubAdminTable();
    }
 
    private void onToggleSubAdminActive(int row) {
        String subAdminId = (String) subAdminTableModel.getValueAt(row, 0);
        boolean newActive = (Boolean) subAdminTableModel.getValueAt(row, 2);
 
        try {
            boolean success = currentAdmin.setSubAdminActive(system, subAdminId, newActive);
            if (!success) {
                JOptionPane.showMessageDialog(this, "Could not update that sub-admin's access.");
                refreshSubAdminTable(); // revert the checkbox to the real saved state
                return;
            }
 
            // The checkbox toggle IS the action - save immediately, same
            // convention as every other mutating action in this project.
            fileManager.saveAdmins(system.getAdminList());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save: " + ex.getMessage());
            refreshSubAdminTable();
        }
    }
 
    private void refreshSubAdminTable() {
        subAdminTableModel.setRowCount(0);
        for (Admin a : system.getAdminList()) {
            if (!a.isMainAdmin()) { // the main admin itself is never listed here - nothing to toggle
                subAdminTableModel.addRow(new Object[]{a.getAdminID(), a.getAdminUsername(), a.isActive()});
            }
        }
    }
 
    public void saveChanges() {
        // The one explicit, admin-triggered save. Every other action
        // (add/remove/post) already saves its own precise slice immediately,
        // per project convention - this is the "save everything, just to be
        // safe" button the UML's saveChanges() method implies.
        fileManager.saveAll(system);
        JOptionPane.showMessageDialog(this, "All changes saved.");
    }
 
    // ------------------------------------ Helpers ---------------------------------
 
    private void refreshAnnouncementList() {
        StringBuilder sb = new StringBuilder();
        for (Announcement a : system.getAnnouncementList()) {
            sb.append("[").append(a.datePosted).append("] ").append(a.content).append("\n\n");
        }
        announcementListArea.setText(sb.toString());
    }
}