package student_management;

import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

public class DBConnection {

    static final String URL ="jdbc:mysql://localhost:3306/student_management";
    static final String USER = "root";
    static final String PASSWORD = "chandu";
    
    static Scanner sc = new Scanner(System.in);

    // DATABASE CONNECTION
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
        
    }
    
    // INPUT VALIDATION
    static int readInt(String message, int min, int max) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(sc.nextLine().trim());

                if (value >= min && value <= max) {
                    return value;
                }

                System.out.println(
                    "Enter a value between " + min + " and " + max
                );
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    static String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty.");
        }
    }
    
    static String readEmail() {
        while (true) {
            String email = readText("Enter email: ");

            if (email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }

            System.out.println("Invalid email format.");
        }
    }
    
    static LocalDate readDate(String message) {
        while (true) {
            try {
                System.out.print(message);
                return LocalDate.parse(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println(
                    "Invalid date. Use YYYY-MM-DD format."
                );
            }
        }
    }
    
    // MAIN MENU
    public static void main(String[] args) {

        try (Connection con = getConnection()) {
            System.out.println("MySQL connected successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: "
                + e.getMessage());
            System.out.println(
                "Check MySQL, database name, password and Connector/J."
            );
            return;
        }
        
        while (true) {
            System.out.println("\n========== STUDENT MANAGEMENT SYSTEM ==========");
            System.out.println("1.  Register Student");
            System.out.println("2.  View All Students");
            System.out.println("3.  Update Student");
            System.out.println("4.  Delete Student");
            System.out.println("5.  View All Courses");
            System.out.println("6.  Enroll Student in Course");
            System.out.println("7.  View Enrollments");
            System.out.println("8.  Mark Attendance");
            System.out.println("9.  View Attendance Percentage");
            System.out.println("10. Enter or Update Marks");
            System.out.println("11. Generate Results");
            System.out.println("12. Exit");
            
            int choice = readInt("Enter your choice: ", 1, 12);

            switch (choice) {
                case 1:
                    registerStudent();
                    break;
                case 2:
                    viewStudents();
                    break;
                case 3:
                    updateStudent();
                    break;
                case 4:
                    deleteStudent();
                    break;
                    
                case 5:
                    viewCourses();
                    break;
                case 6:
                    enrollStudent();
                    break;
                case 7:
                    viewEnrollments();
                    break;
                case 8:
                    markAttendance();
                    break;
                case 9:
                    viewAttendance();
                    break;
                case 10:
                    enterMarks();
                    break;
                case 11:
                    generateResults();
                    break;
                case 12:
                    System.out.println("Application closed.");
                    sc.close();
                    return;
            }
        }
    }
    
    // MODULE 1: STUDENT REGISTRATION (CREATE)
    static void registerStudent() {

        String name = readText("Enter student name: ");
        String email = readEmail();
        System.out.print("Enter phone: ");
        String phone = sc.nextLine().trim();
        int age = readInt("Enter age (16-100): ", 16, 100);
        String city = readText("Enter city: ");

        String sql = "INSERT INTO students "
            + "(student_name, email, phone, age, city) "
            + "VALUES (?, ?, ?, ?, ?)"; 
        
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

               ps.setString(1, name);
               ps.setString(2, email);
               ps.setString(3, phone);
               ps.setInt(4, age);
               ps.setString(5, city);

               ps.executeUpdate();
               System.out.println("Student registered successfully!");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Email already exists.");
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
    
    // MODULE 1: VIEW STUDENTS (READ)
    static void viewStudents() {

        String sql = "SELECT * FROM students ORDER BY student_id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nID | Name | Email | Phone | Age | City");
            while (rs.next()) {
                System.out.println(
                    rs.getInt("student_id") + " | "
                    + rs.getString("student_name") + " | "
                    + rs.getString("email") + " | "
                    + rs.getString("phone") + " | "
                    + rs.getInt("age") + " | "
                    + rs.getString("city")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 1: UPDATE STUDENT
    static void updateStudent() {

        int id = readInt("Enter student ID: ", 1, Integer.MAX_VALUE);
        String name = readText("Enter new name: ");
        String email = readEmail();
        System.out.print("Enter new phone: ");
        String phone = sc.nextLine().trim();
        int age = readInt("Enter age (16-100): ", 16, 100);
        String city = readText("Enter new city: ");

        String sql = "UPDATE students SET student_name=?, email=?, "
            + "phone=?, age=?, city=? WHERE student_id=?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

               ps.setString(1, name);
               ps.setString(2, email);
               ps.setString(3, phone);
               ps.setInt(4, age);
               ps.setString(5, city);
               ps.setInt(6, id);
               int rows = ps.executeUpdate();

               System.out.println(rows > 0
                   ? "Student updated successfully."
                   : "Student ID not found.");

           } catch (SQLIntegrityConstraintViolationException e) {
               System.out.println("That email is already registered.");
           } catch (SQLException e) {
               System.out.println(e.getMessage());
           }
       }
    // MODULE 1: DELETE STUDENT
    static void deleteStudent() {

        int id = readInt("Enter student ID to delete: ",
            1, Integer.MAX_VALUE);

        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

               ps.setInt(1, id);

               int rows = ps.executeUpdate();

               System.out.println(rows > 0
                   ? "Student deleted successfully."
                   : "Student ID not found.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(
                "Cannot delete this student because enrollments exist."
            );
            System.out.println(
                "Delete related records first, or deactivate the student."
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 2: VIEW COURSES
    static void viewCourses() {

        String sql = "SELECT * FROM courses ORDER BY course_id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nID | Course | Duration | Fee");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("course_id") + " | "
                    + rs.getString("course_name") + " | "
                    + rs.getInt("duration_months") + " months | "
                    + rs.getBigDecimal("fee")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 2: COURSE ENROLLMENT + TRANSACTION
    static void enrollStudent() {

        int studentId = readInt(
            "Enter student ID: ", 1, Integer.MAX_VALUE);

        int courseId = readInt(
            "Enter course ID: ", 1, Integer.MAX_VALUE);

        String sql = "INSERT INTO enrollments "
            + "(student_id, course_id, enrollment_date) "
            + "VALUES (?, ?, CURDATE())";

        try (Connection con = getConnection()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, studentId);
                ps.setInt(2, courseId);

                ps.executeUpdate();
                con.commit();

                System.out.println("Enrollment successful!");

            } catch (SQLException e) {
                con.rollback();
                System.out.println(
                    "Enrollment failed: " + e.getMessage()
                );
                System.out.println(
                    "Check student/course IDs and duplicate enrollment."
                );
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 2: VIEW ENROLLMENTS USING JOINS
    static void viewEnrollments() {

        String sql = "SELECT e.enrollment_id, s.student_name, "
            + "c.course_name, e.enrollment_date "
            + "FROM enrollments e "
            + "JOIN students s ON e.student_id=s.student_id "
            + "JOIN courses c ON e.course_id=c.course_id "
            + "ORDER BY e.enrollment_id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nEnrollment ID | Student | Course | Date");

            while (rs.next()) {
                System.out.println(
                    rs.getInt("enrollment_id") + " | "
                    + rs.getString("student_name") + " | "
                    + rs.getString("course_name") + " | "
                    + rs.getDate("enrollment_date")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 3: MARK ATTENDANCE
    static void markAttendance() {

        int enrollmentId = readInt(
            "Enter enrollment ID: ", 1, Integer.MAX_VALUE);

        LocalDate date = readDate(
            "Enter attendance date (YYYY-MM-DD): ");

        System.out.println("1. Present");
        System.out.println("2. Absent");

        int option = readInt("Select status: ", 1, 2);
        String status = option == 1 ? "Present" : "Absent";

        String sql = "INSERT INTO attendance "
            + "(enrollment_id, attendance_date, status) "
            + "VALUES (?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setDate(2, Date.valueOf(date));
            ps.setString(3, status);

            ps.executeUpdate();
            System.out.println("Attendance saved successfully!");

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println(
                "Invalid enrollment ID or attendance already exists for this date."
            );
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    

    // MODULE 3: ATTENDANCE REPORT
    static void viewAttendance() {

        String sql = "SELECT e.enrollment_id, s.student_name, "
            + "c.course_name, "
            + "COUNT(a.attendance_id) AS total_days, "
            + "COALESCE(SUM(a.status='Present'), 0) AS present_days, "
            + "ROUND(100.0 * "
            + "COALESCE(SUM(a.status='Present'), 0) / "
            + "NULLIF(COUNT(a.attendance_id), 0), 2) AS percentage "
            + "FROM enrollments e "
            + "JOIN students s ON e.student_id=s.student_id "
            + "JOIN courses c ON e.course_id=c.course_id "
            + "LEFT JOIN attendance a "
            + "ON e.enrollment_id=a.enrollment_id "
            + "GROUP BY e.enrollment_id, s.student_name, c.course_name "
            + "ORDER BY e.enrollment_id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println(
                "\nEnrollment | Student | Course | Total | Present | Percentage"
            );

            while (rs.next()) {
                int total = rs.getInt("total_days");

                String percentage = total == 0
                    ? "No attendance recorded"
                    : rs.getBigDecimal("percentage") + "%";

                System.out.println(
                    rs.getInt("enrollment_id") + " | "
                    + rs.getString("student_name") + " | "
                    + rs.getString("course_name") + " | "
                    + total + " | "
                    + rs.getInt("present_days") + " | "
                    + percentage
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 4: ENTER OR UPDATE MARKS
    static void enterMarks() {

        int enrollmentId = readInt(
            "Enter enrollment ID: ", 1, Integer.MAX_VALUE);

        String subject = readText("Enter subject name: ");

        int total = readInt(
            "Enter maximum marks: ", 1, Integer.MAX_VALUE);

        int obtained = readInt(
            "Enter obtained marks: ", 0, total);

        String sql = "INSERT INTO marks "
            + "(enrollment_id, subject_name, total_marks, obtained_marks) "
            + "VALUES (?, ?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "total_marks=VALUES(total_marks), "
            + "obtained_marks=VALUES(obtained_marks)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setString(2, subject);
            ps.setInt(3, total);
            ps.setInt(4, obtained);

            ps.executeUpdate();

            System.out.println("Marks saved or updated successfully!");

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Invalid enrollment ID.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // MODULE 5: RESULT GENERATION
    static void generateResults() {

        String sql = "SELECT s.student_name, c.course_name, "
            + "ROUND(100.0 * SUM(m.obtained_marks) / "
            + "SUM(m.total_marks), 2) AS percentage, "
            + "CASE WHEN MIN(100.0 * m.obtained_marks / "
            + "m.total_marks) >= 40 THEN 'PASS' ELSE 'FAIL' END "
            + "AS result "
            + "FROM enrollments e "
            + "JOIN students s ON e.student_id=s.student_id "
            + "JOIN courses c ON e.course_id=c.course_id "
            + "JOIN marks m ON e.enrollment_id=m.enrollment_id "
            + "GROUP BY e.enrollment_id, s.student_name, c.course_name "
            + "ORDER BY s.student_name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println(
                "\nStudent | Course | Percentage | Result"
            );

            while (rs.next()) {
                System.out.println(
                    rs.getString("student_name") + " | "
                    + rs.getString("course_name") + " | "
                    + rs.getBigDecimal("percentage") + "% | "
                    + rs.getString("result")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
        
            
    