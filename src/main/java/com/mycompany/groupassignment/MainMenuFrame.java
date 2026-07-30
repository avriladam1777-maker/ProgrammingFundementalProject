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
import java.awt.*;
import java.util.List;
 
public class MainMenuFrame extends JFrame {
 
    // Attributes (per UML):
    private VehicleServiceBookingSystem system;
    private FileManager fileManager;
 
    // Constructor:
    public MainMenuFrame(VehicleServiceBookingSystem system, FileManager fileManager) {
        this.system = system;
        this.fileManager = fileManager;
 
        setTitle("Vehicle Service Booking System");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        initComponents();
    }
 
    // ---------------------------------- UI setup -----------------------------------
 
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
 
        JLabel titleLabel = new JLabel("Vehicle Service Booking System", SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
 
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
 
        JButton customerButton = new JButton("Continue as Customer");
        customerButton.addActionListener(e -> openCustomerFrame());
        buttonPanel.add(customerButton);
 
        JButton adminButton = new JButton("Admin Login");
        adminButton.addActionListener(e -> openAdminLogin());
        buttonPanel.add(adminButton);
 
        add(buttonPanel, BorderLayout.CENTER);
    }
 
    // ------------------------------- UML behaviour methods --------------------------
 
    public void loadAllData() {
        // Load order matters: FileManager.loadBookings() resolves each
        // booking's customer/vehicle references by looking them up in
        // already-loaded lists (findUserById/findVehicleById), so users and
        // vehicles must finish loading before bookings start. Admins and
        // announcements have no such dependency.
        List<Vehicle> vehicles = fileManager.loadVehicles();
        system.getVehicleList().addAll(vehicles);
 
        List<User> users = fileManager.loadUsers();
        system.getUserList().addAll(users);
 
        List<Admin> admins = fileManager.loadAdmins();
        system.getAdminList().addAll(admins);
 
        // New: guarantee exactly one main admin exists, then persist it
        // immediately in case this was the very first run (fresh/missing
        // admins.txt) - otherwise the seeded main admin would only live
        // in memory and vanish on the next restart.
        system.ensureMainAdminExists();
        fileManager.saveAdmins(system.getAdminList());
 
        List<Announcement> announcements = fileManager.loadAnnouncements();
        system.getAnnouncementList().addAll(announcements);
        
        // Must come after the vehicle/user loads above.
        List<Booking> bookings = fileManager.loadBookings(system.getUserList(), system.getVehicleList());
        system.getBookingList().addAll(bookings);
    }
 
    public void openCustomerFrame() {
        // A customer session always ends by exiting the whole app (project
        // decision), so there's no real "return to this menu" path - but
        // hide rather than dispose, in case that decision ever changes.
        setVisible(false);
        CustomerFrame customerFrame = new CustomerFrame(system, fileManager);
        customerFrame.setVisible(true);
    }
 
    public void openAdminLogin() {
        // MainMenuFrame is intentionally left visible behind AdminFrame -
        // an admin logging out (DISPOSE_ON_CLOSE on AdminFrame) returns
        // here, unlike a customer session which always exits the whole app.
        AdminFrame adminFrame = new AdminFrame(system, fileManager);
        adminFrame.setVisible(true);
    }
 
    // ------------------------------------- main --------------------------------------
 
    public static void main(String[] args) {
        VehicleServiceBookingSystem system = new VehicleServiceBookingSystem();
        FileManager fileManager = new FileManager();
        MainMenuFrame mainMenu = new MainMenuFrame(system, fileManager);
        mainMenu.loadAllData();
        mainMenu.setVisible(true);
    }
}
