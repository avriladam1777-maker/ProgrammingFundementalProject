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
    private VehicleServiceBookingSystem system;
    private Admin currentAdmin;
    private FileManager fileManager;
 
    // --- GUI components (not in UML - Swing plumbing) ---
    private JLabel welcomeLabel;
 
    // Vehicles tab
    private JComboBox<String> vehicleTypeCombo;
    private JTextField vehicleIdField;
    private JTextField brandField;
    private JTextField modelField;
    private JTextField imagePathField;
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
    private JTable announcementTable;
    private DefaultTableModel announcementTableModel;
 
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
 
        add(buildSaveBar(), BorderLayout.SOUTH);
    }
 
    private JPanel buildVehiclesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
 
        panel.add(buildAddVehicleForm(), BorderLayout.NORTH);
        panel.add(buildVehicleStatusTable(), BorderLayout.CENTER);
 
        JButton removeButton = new JButton("Remove Selected Vehicle");
        removeButton.addActionListener(e -> onRemoveVehicle());
        JPanel removeRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removeRow.add(removeButton);
        panel.add(removeRow, BorderLayout.SOUTH);
 
        return panel;
    }
 
    private JPanel buildAddVehicleForm() {
        JPanel form = new JPanel();
        form.setBorder(BorderFactory.createTitledBorder("Add Vehicle"));
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
 
        JPanel imageRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePathField = new JTextField(10);
        imageRow.add(new JLabel("Image Path:"));
        imageRow.add(imagePathField);
        form.add(imageRow);
 
        // Type-specific fields swap via CardLayout, driven by vehicleTypeCombo -
        // this is the one place polymorphism surfaces in the GUI layer: the
        // concrete class constructed in onAddVehicle() depends on this choice.
        typeCardLayout = new CardLayout();
        typeCardPanel = new JPanel(typeCardLayout);
        typeCardPanel.add(buildCarFieldsPanel(), "Car");
        typeCardPanel.add(buildMotoFieldsPanel(), "Motorcycle");
        form.add(typeCardPanel);
 
        vehicleTypeCombo.addActionListener(e ->
                typeCardLayout.show(typeCardPanel, (String) vehicleTypeCombo.getSelectedItem()));
 
        JButton addButton = new JButton("Add Vehicle");
        addButton.addActionListener(e -> onAddVehicle());
        JButton updateButton = new JButton("Update Selected Vehicle");
        updateButton.addActionListener(e -> onUpdateVehicle());
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.add(addButton);
        addRow.add(updateButton);
        form.add(addRow);
 
        return form;
    }
 
    private JPanel buildCarFieldsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

        String[] columns = {"Message ID", "Date", "Content"};
        announcementTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        announcementTable = new JTable(announcementTableModel);
        announcementTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(announcementTable);
        listScroll.setBorder(BorderFactory.createTitledBorder("Posted Announcements"));
        panel.add(listScroll, BorderLayout.CENTER);

        JPanel postRow = new JPanel(new BorderLayout(4, 4));
        postRow.setBorder(BorderFactory.createTitledBorder("Post New Announcement"));
        announcementField = new JTextArea(3, 20);
        postRow.add(new JScrollPane(announcementField), BorderLayout.CENTER);

        JPanel actionCol = new JPanel(new GridLayout(2, 1, 4, 4));
        JButton postButton = new JButton("Post Announcement");
        postButton.addActionListener(e -> onPostAnnouncement());
        JButton deleteButton = new JButton("Delete Selected Announcement");
        deleteButton.addActionListener(e -> onDeleteAnnouncement());
        actionCol.add(postButton);
        actionCol.add(deleteButton);
        postRow.add(actionCol, BorderLayout.EAST);

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
    
    private JPanel buildSaveBar() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save All Changes");
        saveButton.addActionListener(e -> saveChanges());
        row.add(saveButton);
        return row;
    }
 
    // ------------------------------- UML behaviour methods ------------------------
 
    public void onAddVehicle() {
        String vehicleId = vehicleIdField.getText();
        String brand = brandField.getText();
        String model = modelField.getText();
        String imagePath = imagePathField.getText();
        double price = parseDoubleOrDefault(priceField.getText(), -1);
        boolean available = availableCheckBox.isSelected();
 
        if (vehicleId == null || vehicleId.trim().isEmpty() || price <= 0) {
            JOptionPane.showMessageDialog(this, "Enter a valid vehicle ID and a price above 0.");
            return;
        }
 
        // Polymorphism in action: which concrete class gets built depends
        // entirely on the combo box the admin picked.
        Vehicle vehicle;
        String type = (String) vehicleTypeCombo.getSelectedItem();
        if ("Car".equals(type)) {
            int numDoors = parseIntOrDefault(numDoorsField.getText(), 4);
            String transmission = transmissionField.getText();
            int mileage = parseIntOrDefault(carMileageField.getText(), 0);
            vehicle = new Car(vehicleId.trim(), brand, model, imagePath, price, available,
                    numDoors, transmission, mileage);
        } else {
            int engineCC = parseIntOrDefault(engineCCField.getText(), 0);
            int mileage = parseIntOrDefault(motoMileageField.getText(), 0);
            vehicle = new Motorcycle(vehicleId.trim(), brand, model, imagePath, price, available,
                    engineCC, mileage);
        }
 
        currentAdmin.addVehicle(system, vehicle);
        fileManager.saveVehicles(system.getVehicleList()); // only vehicleList changed
        JOptionPane.showMessageDialog(this, "Vehicle added.");
        onViewVehicleStatus();
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
        String imagePath = imagePathField.getText();
        double price = parseDoubleOrDefault(priceField.getText(), -1);
 
        if (price <= 0) {
            JOptionPane.showMessageDialog(this, "Enter a valid price above 0 in the form above, then Update.");
            return;
        }
 
        // Admin.updateVehicle() only touches brand/model/price/imagePath
        // (confirmed against the real method signature) - type-specific
        // fields and availability aren't editable through Update.
        boolean updated = currentAdmin.updateVehicle(system, vehicleId, brand, model, price, imagePath);
        if (!updated) {
            JOptionPane.showMessageDialog(this, "Vehicle not found - nothing was updated.");
            return;
        }
 
        fileManager.saveVehicles(system.getVehicleList());
        JOptionPane.showMessageDialog(this, "Vehicle updated.");
        onViewVehicleStatus();
    }
 
    public void onRemoveVehicle() {
        int row = vehicleStatusTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a vehicle from the table first.");
            return;
        }
 
        String vehicleId = (String) vehicleStatusTableModel.getValueAt(row, 0);
        currentAdmin.removeVehicle(system, vehicleId);
        fileManager.saveVehicles(system.getVehicleList());
        JOptionPane.showMessageDialog(this, "Vehicle removed.");
        onViewVehicleStatus();
    }
 
    public void onPostAnnouncement() {
        String content = announcementField.getText();
        if (content == null || content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter an announcement message first.");
            return;
        }
 
        currentAdmin.postAnnouncement(system, content.trim());
        fileManager.saveAnnouncements(system.getAnnouncementList());
        announcementField.setText("");
        JOptionPane.showMessageDialog(this, "Announcement posted.");
        refreshAnnouncementList();
    }
    
    public void onDeleteAnnouncement() {
        int row = announcementTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an announcement from the table first.");
            return;
        }

        String messageId = (String) announcementTableModel.getValueAt(row, 0);
        boolean deleted = currentAdmin.deleteAnnouncement(system, messageId);
        if (!deleted) {
            JOptionPane.showMessageDialog(this, "Announcement not found - nothing was deleted.");
            return;
        }

        fileManager.saveAnnouncements(system.getAnnouncementList());
        JOptionPane.showMessageDialog(this, "Announcement deleted.");
        refreshAnnouncementList();
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
        if (username == null || username.trim().isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a username and password for the new sub-admin.");
            return;
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
 
        boolean success = currentAdmin.setSubAdminActive(system, subAdminId, newActive);
        if (!success) {
            JOptionPane.showMessageDialog(this, "Could not update that sub-admin's access.");
            refreshSubAdminTable(); // revert the checkbox to the real saved state
            return;
        }
 
        // The checkbox toggle IS the action - save immediately, same
        // convention as every other mutating action in this project.
        fileManager.saveAdmins(system.getAdminList());
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
        announcementTableModel.setRowCount(0);
        for (Announcement a : system.getAnnouncementList()) {
            announcementTableModel.addRow(new Object[]{
                a.getMessageID(),
                a.datePosted,
                a.content
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
