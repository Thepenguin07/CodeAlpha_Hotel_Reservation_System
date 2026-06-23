Markdown
# Hotel Reservation System 🏨

A fully functional, desktop-based Hotel Reservation System built with Java. This project was developed as **Task 4** for the Java Programming Internship at **CodeAlpha**. It features a graphical user interface (GUI) and utilizes Object-Oriented Programming (OOP) principles and File I/O for persistent data storage.

## ✨ Features

* **Intuitive Graphical Interface:** Built using Java Swing (`JFrame`, `JTabbedPane`, `JTable`) for a clean and user-friendly experience.
* **Room Management:** Categorizes rooms into Standard, Deluxe, and Suite, each with dynamic pricing.
* **Smart Booking Engine:** Handles real-time date validation, preventing double-bookings and ensuring check-out dates fall after check-in dates.
* **Payment Simulation:** Includes a confirmation workflow that calculates total costs based on the duration of the stay before finalizing the reservation.
* **Data Persistence:** Uses Java Serialization (`ObjectInputStream` / `ObjectOutputStream`) to save room and reservation data to a local `hotel_data.dat` file. No data is lost when the application is closed.
* **Reservation Dashboard:** Allows the management and cancellation of active reservations via a dedicated administrative tab.

## 🛠️ Tech Stack

* **Language:** Java
* **UI Framework:** Java Swing, AWT
* **Core Concepts:** Object-Oriented Programming (OOP), Data Structures (ArrayList), Java Time API (`LocalDate`), File I/O (Serialization)

## 📋 Prerequisites

To run this application on your local machine, you will need:
* **Java Development Kit (JDK):** Version 8 or higher installed on your system.
* Any standard IDE (like IntelliJ IDEA, Eclipse, or VS Code) or terminal for compilation.

## 🚀 How to Run

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/your-username/CodeAlpha_HotelReservationSystem.git](https://github.com/your-username/CodeAlpha_HotelReservationSystem.git)
   cd CodeAlpha_HotelReservationSystem
Compile the Java File:
Open your terminal and compile the main class:
Bash
javac HotelReservationSystem.java
Run the Application:
Execute the compiled class:
Bash
java HotelReservationSystem
(Note: Upon running the application for the first time, it will automatically generate a hotel_data.dat file in the same directory to store your data.)
📁 Project Structure
HotelReservationSystem.java - The main source code containing all classes (Room, Reservation, and HotelReservationSystem).
hotel_data.dat - The serialized data file (auto-generated) that stores active bookings and room statuses.
👤 Author
Shifa Parveen
Role: Java Programming Intern @ CodeAlpha

***

### **Quick Tips for your Repository:**
* **Save this file** exactly as `README.md` in the root folder of your project.
* **Update the GitHub link** under the "How to Run" section with your actual repository URL before pushing it live. 
* It is usually best practice to add `hotel_data.dat` to a `.gitignore` file so you don't commit local test data to your public repository.
