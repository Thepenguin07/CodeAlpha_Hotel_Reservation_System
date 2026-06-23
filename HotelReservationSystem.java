import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

enum RoomType { STANDARD, DELUXE, SUITE }

class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int roomNumber;
    private final RoomType type;
    private final double pricePerNight;

    public Room(int roomNumber, RoomType type, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
    }

    public int getRoomNumber() { return roomNumber; }
    public RoomType getType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
}

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String id;
    private final String guestName;
    private final int roomNumber;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final double totalAmount;
    private boolean isPaid;
    private boolean isCancelled;

    public Reservation(String guestName, int roomNumber, LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        this.id = "RES" + System.currentTimeMillis();
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.isPaid = false;
        this.isCancelled = false;
    }

    public String getId() { return id; }
    public String getGuestName() { return guestName; }
    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isPaid() { return isPaid; }
    public boolean isCancelled() { return isCancelled; }
    
    public void simulatePayment() { this.isPaid = true; }
    public void cancel() { this.isCancelled = true; }
}

public class HotelReservationSystem extends JFrame {
    private static final String DATA_FILE = "hotel_data.dat";
    private List<Room> rooms = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private JTable roomTable;
    private JTable resTable;
    private DefaultTableModel roomModel;
    private DefaultTableModel resModel;

    public HotelReservationSystem() {
        loadData();
        if (rooms.isEmpty()) initializeRooms();

        setTitle("Hotel Reservation System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Rooms", createRoomsPanel());
        tabbedPane.addTab("Book Room", createBookingPanel());
        tabbedPane.addTab("Manage Reservations", createManagePanel());

        add(tabbedPane);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveData();
            }
        });
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"Room Number", "Type", "Price/Night"};
        roomModel = new DefaultTableModel(columns, 0);
        roomTable = new JTable(roomModel);
        updateRoomTable();
        panel.add(new JScrollPane(roomTable), BorderLayout.CENTER);
        return panel;
    }

    private void updateRoomTable() {
        roomModel.setRowCount(0);
        for (Room r : rooms) {
            roomModel.addRow(new Object[]{r.getRoomNumber(), r.getType(), "$" + r.getPricePerNight()});
        }
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField();
        JTextField roomField = new JTextField();
        JTextField inField = new JTextField();
        JTextField outField = new JTextField();

        panel.add(new JLabel("Guest Name:")); panel.add(nameField);
        panel.add(new JLabel("Room Number:")); panel.add(roomField);
        panel.add(new JLabel("Check-In (dd-MM-yyyy):")); panel.add(inField);
        panel.add(new JLabel("Check-Out (dd-MM-yyyy):")); panel.add(outField);

        JButton bookBtn = new JButton("Book & Pay");
        panel.add(new JLabel()); panel.add(bookBtn);

        bookBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int roomNum = Integer.parseInt(roomField.getText());
                LocalDate in = LocalDate.parse(inField.getText(), formatter);
                LocalDate out = LocalDate.parse(outField.getText(), formatter);

                if (!out.isAfter(in)) {
                    JOptionPane.showMessageDialog(this, "Check-out must be after check-in.");
                    return;
                }

                Room selectedRoom = null;
                for (Room r : rooms) {
                    if (r.getRoomNumber() == roomNum) {
                        selectedRoom = r; break;
                    }
                }

                if (selectedRoom == null) {
                    JOptionPane.showMessageDialog(this, "Room not found.");
                    return;
                }

                if (!isRoomAvailable(roomNum, in, out)) {
                    JOptionPane.showMessageDialog(this, "Room is not available for these dates.");
                    return;
                }

                long nights = ChronoUnit.DAYS.between(in, out);
                double total = nights * selectedRoom.getPricePerNight();

                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Total Amount for " + nights + " nights: $" + total + "\nProceed to payment?", 
                    "Payment Simulation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    Reservation res = new Reservation(name, roomNum, in, out, total);
                    res.simulatePayment();
                    reservations.add(res);
                    saveData();
                    updateResTable();
                    JOptionPane.showMessageDialog(this, "Payment Successful! Booking ID: " + res.getId());
                    nameField.setText(""); roomField.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check your data.");
            }
        });

        return panel;
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"ID", "Guest", "Room", "Check-In", "Check-Out", "Total", "Status"};
        resModel = new DefaultTableModel(columns, 0);
        resTable = new JTable(resModel);
        updateResTable();

        JPanel topPanel = new JPanel();
        JTextField idField = new JTextField(15);
        JButton cancelBtn = new JButton("Cancel Reservation");
        topPanel.add(new JLabel("Reservation ID: "));
        topPanel.add(idField);
        topPanel.add(cancelBtn);

        cancelBtn.addActionListener(e -> {
            String id = idField.getText();
            for (Reservation r : reservations) {
                if (r.getId().equals(id) && !r.isCancelled()) {
                    r.cancel();
                    saveData();
                    updateResTable();
                    JOptionPane.showMessageDialog(this, "Reservation cancelled.");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Active reservation not found.");
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resTable), BorderLayout.CENTER);
        return panel;
    }

    private void updateResTable() {
        resModel.setRowCount(0);
        for (Reservation r : reservations) {
            String status = r.isCancelled() ? "Cancelled" : (r.isPaid() ? "Paid" : "Pending");
            resModel.addRow(new Object[]{
                r.getId(), r.getGuestName(), r.getRoomNumber(), 
                r.getCheckIn(), r.getCheckOut(), "$" + r.getTotalAmount(), status
            });
        }
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate in, LocalDate out) {
        for (Reservation r : reservations) {
            if (r.getRoomNumber() == roomNumber && !r.isCancelled()) {
                if (in.isBefore(r.getCheckOut()) && out.isAfter(r.getCheckIn())) {
                    return false;
                }
            }
        }
        return true;
    }

    private void initializeRooms() {
        rooms.add(new Room(101, RoomType.STANDARD, 100.0));
        rooms.add(new Room(102, RoomType.STANDARD, 100.0));
        rooms.add(new Room(201, RoomType.DELUXE, 200.0));
        rooms.add(new Room(202, RoomType.DELUXE, 200.0));
        rooms.add(new Room(301, RoomType.SUITE, 450.0));
        saveData();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            rooms = (List<Room>) ois.readObject();
            reservations = (List<Reservation>) ois.readObject();
        } catch (Exception e) {
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(rooms);
            oos.writeObject(reservations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HotelReservationSystem().setVisible(true);
        });
    }
}
