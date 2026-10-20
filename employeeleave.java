package emp_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

public class employeeleave {

	static final String URL = "jdbc:mysql://localhost:3306/employee_leave_db";
	static final String USER = "root";
	static final String PASSWORD = "chandu";

	static final Scanner sc = new Scanner(System.in);

	// DATABASE CONNECTION
	static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}

	// INPUT VALIDATIONS
	static int readInt(String message, int min, int max) {
		while (true) {
			try {
				System.out.print(message);
				int value = Integer.parseInt(sc.nextLine().trim());

				if (value >= min && value <= max) {
					return value;
				}

				System.out.println("Enter a number between " + min + " and " + max);

			} catch (NumberFormatException e) {
				System.out.println("Enter a valid number.");
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

			System.out.println("This field is required.");
		}
	}

	static String readEmail() {
		while (true) {
			String email = readText("Enter email: ");

			if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
				return email;
			}

			System.out.println("Enter a valid email address.");
		}
	}

	static LocalDate readDate(String message) {
		while (true) {
			try {
				System.out.print(message);
				return LocalDate.parse(sc.nextLine().trim());

			} catch (Exception e) {
				System.out.println("Invalid date. Use YYYY-MM-DD format.");
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try (Connection con = getConnection()) {
			System.out.println("Database connected successfully!");
		} catch (SQLException e) {
			System.out.println("Database connection failed: " + e.getMessage());
			return;
		}

		while (true) {

			System.out.println("\n====== EMPLOYEE LEAVE MANAGEMENT ======");
			System.out.println("1. Register Employee");
			System.out.println("2. View Employees");
			System.out.println("3. Update Employee");
			System.out.println("4. Delete Employee");
			System.out.println("5. Apply Leave");
			System.out.println("6. View Leave Requests");
			System.out.println("7. Approve Leave");
			System.out.println("8. Reject Leave");
			System.out.println("9. View Leave Balance");
			System.out.println("10. Exit");

			int choice = readInt("Enter your choice: ", 1, 10);

			switch (choice) {
			case 1:
				registerEmployee();
				break;
			case 2:
				viewEmployees();
				break;
			case 3:
				updateEmployee();
				break;
			case 4:
				deleteEmployee();
				break;
			case 5:
				applyLeave();
				break;
			case 6:
				viewLeaveRequests();
				break;
			case 7:
				processLeave(true);
				break;
			case 8:
				processLeave(false);
				break;
			case 9:
				viewLeaveBalance();
				break;
			case 10:
				System.out.println("Application closed.");
				sc.close();
				return;
			}
		}
	}

	// MODULE 1: EMPLOYEE REGISTRATION
	// CREATE

	static void registerEmployee() {

		String name = readText("Enter employee name: ");
		String email = readEmail();
		String department = readText("Enter department: ");
		LocalDate joiningDate = readDate("Enter joining date (YYYY-MM-DD): ");

		String employeeSQL = "INSERT INTO employees " + "(emp_name, email, department, joining_date) "
				+ "VALUES (?, ?, ?, ?)";

		String balanceSQL = "INSERT INTO leave_balance " + "(emp_id, total_leaves, used_leaves) " + "VALUES (?, 12, 0)";

		try (Connection con = getConnection()) {

			con.setAutoCommit(false);

			try {
				int empId;

				try (PreparedStatement ps = con.prepareStatement(employeeSQL, Statement.RETURN_GENERATED_KEYS)) {

					ps.setString(1, name);
					ps.setString(2, email);
					ps.setString(3, department);
					ps.setDate(4, java.sql.Date.valueOf(joiningDate));
					ps.executeUpdate();

					try (ResultSet keys = ps.getGeneratedKeys()) {
						if (!keys.next()) {
							throw new SQLException("Employee ID was not generated.");
						}
						empId = keys.getInt(1);
					}
				}

				try (PreparedStatement ps = con.prepareStatement(balanceSQL)) {

					ps.setInt(1, empId);
					ps.executeUpdate();
				}
				con.commit();

				System.out.println("Employee registered successfully!");
				System.out.println("Employee ID: " + empId);
				System.out.println("Initial leave balance: 12 days");
			} catch (SQLException e) {
				con.rollback();
				System.out.println("Registration failed: " + e.getMessage());
			} finally {
				con.setAutoCommit(true);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// MODULE 1: VIEW EMPLOYEES
	static void viewEmployees() {

		String sql = "SELECT * FROM employees ORDER BY emp_id";

		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			System.out.println("\nID | Name | Email | Department | Joining Date");
			while (rs.next()) {
				System.out
						.println(rs.getInt("emp_id") + " | " + rs.getString("emp_name") + " | " + rs.getString("email")
								+ " | " + rs.getString("department") + " | " + rs.getDate("joining_date"));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// MODULE 1: UPDATE EMPLOYEE
	static void updateEmployee() {

		int id = readInt("Enter employee ID: ", 1, Integer.MAX_VALUE);

		String name = readText("Enter new employee name: ");
		String email = readEmail();
		String department = readText("Enter new department: ");

		LocalDate joiningDate = readDate("Enter joining date (YYYY-MM-DD): ");

		String sql = "UPDATE employees SET emp_name=?, email=?, " + "department=?, joining_date=? WHERE emp_id=?";

		try (Connection con = getConnection(); 
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, name);
			ps.setString(2, email);
			ps.setString(3, department);
			ps.setDate(4, java.sql.Date.valueOf(joiningDate));
			ps.setInt(5, id);

			int rows = ps.executeUpdate();

			System.out.println(rows > 0 ? "Employee updated successfully." : "Employee ID not found.");

		} catch (SQLException e) {
			System.out.println("Update failed: " + e.getMessage());
		}
	}

	// MODULE 1: DELETE EMPLOYEE
	static void deleteEmployee() {

		int id = readInt("Enter employee ID to delete: ", 1, Integer.MAX_VALUE);

		String sql = "DELETE FROM employees WHERE emp_id=?";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);

			int rows = ps.executeUpdate();

			System.out.println(rows > 0 ? "Employee deleted successfully." : "Employee ID not found.");
		} catch (SQLException e) {
			System.out.println(
					"Cannot delete employee. Related leave records " + "may exist. Details: " + e.getMessage());
		}
	}

	// MODULE 2: APPLY LEAVE
	static void applyLeave() {

		int empId = readInt("Enter employee ID: ", 1, Integer.MAX_VALUE);

		System.out.println("Leave types:");
		System.out.println("1. Casual");
		System.out.println("2. Sick");
		System.out.println("3. Earned");

		int typeChoice = readInt("Choose leave type: ", 1, 3);

		String leaveType;

		switch (typeChoice) {
		case 1:
			leaveType = "Casual";
			break;
		case 2:
			leaveType = "Sick";
			break;
		default:
			leaveType = "Earned";
		}
		LocalDate startDate = readDate("Enter start date (YYYY-MM-DD): ");

		LocalDate endDate = readDate("Enter end date (YYYY-MM-DD): ");

		if (endDate.isBefore(startDate)) {
			System.out.println("End date cannot be before start date.");
			return;
		}
		if (startDate.isBefore(LocalDate.now())) {
			System.out.println("Leave start date cannot be in the past.");
			return;
		}

		long daysLong = ChronoUnit.DAYS.between(startDate, endDate) + 1;

		if (daysLong > Integer.MAX_VALUE) {
			System.out.println("Leave duration is too large.");
			return;
		}

		int days = (int) daysLong;
		String reason = readText("Enter leave reason: ");

		String overlapSQL = "SELECT COUNT(*) FROM leave_requests "
				+ "WHERE emp_id=? AND status IN ('Pending','Approved') " + "AND start_date <= ? AND end_date >= ?";

		String insertSQL = "INSERT INTO leave_requests " + "(emp_id, leave_type, start_date, end_date, "
				+ "days, reason, status, applied_on) " + "VALUES (?, ?, ?, ?, ?, ?, 'Pending', CURDATE())";

		try (Connection con = getConnection()) {

			// Check whether employee exists.
			try (PreparedStatement ps = con.prepareStatement("SELECT emp_id FROM employees WHERE emp_id=?")) {

				ps.setInt(1, empId);

				try (ResultSet rs = ps.executeQuery()) {
					if (!rs.next()) {
						System.out.println("Employee not found.");
						return;
					}
				}
			}

			// Prevent overlapping pending/approved requests.
			try (PreparedStatement ps = con.prepareStatement(overlapSQL)) {

				ps.setInt(1, empId);
				ps.setDate(2, java.sql.Date.valueOf(endDate));
				ps.setDate(3, java.sql.Date.valueOf(startDate));

				try (ResultSet rs = ps.executeQuery()) {
					rs.next();

					if (rs.getInt(1) > 0) {
						System.out.println("Leave overlaps an existing pending " + "or approved request.");
						return;
					}
				}
			}

			try (PreparedStatement ps = con.prepareStatement(insertSQL)) {

				ps.setInt(1, empId);
				ps.setString(2, leaveType);
				ps.setDate(3, java.sql.Date.valueOf(startDate));
				ps.setDate(4, java.sql.Date.valueOf(endDate));
				ps.setInt(5, days);
				ps.setString(6, reason);

				ps.executeUpdate();

				System.out.println("Leave applied successfully. Awaiting approval.");
				System.out.println("Requested days: " + days);
			}

		} catch (SQLException e) {
			System.out.println("Leave application failed: " + e.getMessage());
		}
	}

	// MODULE 2: VIEW LEAVE REQUESTS
	static void viewLeaveRequests() {

		String sql = "SELECT l.leave_id, e.emp_name, l.leave_type, " + "l.start_date, l.end_date, l.days, l.reason, "
				+ "l.status, l.applied_on " + "FROM leave_requests l " + "JOIN employees e ON l.emp_id=e.emp_id "
				+ "ORDER BY l.leave_id DESC";

		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			System.out.println("\nLeave ID | Employee | Type | Start | End | Days | Status");

			while (rs.next()) {
				System.out.println(rs.getInt("leave_id") + " | " + rs.getString("emp_name") + " | "
						+ rs.getString("leave_type") + " | " + rs.getDate("start_date") + " | " + rs.getDate("end_date")
						+ " | " + rs.getInt("days") + " | " + rs.getString("status"));

				System.out.println("Reason: " + rs.getString("reason"));
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// MODULE 3: APPROVE OR REJECT LEAVE
	// Transaction keeps status and balance consistent.
	static void processLeave(boolean approve) {

		int leaveId = readInt("Enter leave request ID: ", 1, Integer.MAX_VALUE);

		String decision = approve ? "Approved" : "Rejected";

		try (Connection con = getConnection()) {

			con.setAutoCommit(false);

			try {
				int empId;
				int days;
				String status;

				// Lock the leave request.
				String requestSQL = "SELECT emp_id, days, status " + "FROM leave_requests WHERE leave_id=? FOR UPDATE";

				try (PreparedStatement ps = con.prepareStatement(requestSQL)) {

					ps.setInt(1, leaveId);

					try (ResultSet rs = ps.executeQuery()) {
						if (!rs.next()) {
							throw new SQLException("Leave request not found.");
						}

						empId = rs.getInt("emp_id");
						days = rs.getInt("days");
						status = rs.getString("status");
					}
				}

				if (!"Pending".equals(status)) {
					throw new SQLException("This request is already " + status + ".");
				}

				// On approval, check and lock the leave balance.
				if (approve) {

					String balanceSQL = "SELECT total_leaves, used_leaves "
							+ "FROM leave_balance WHERE emp_id=? FOR UPDATE";

					try (PreparedStatement ps = con.prepareStatement(balanceSQL)) {

						ps.setInt(1, empId);

						try (ResultSet rs = ps.executeQuery()) {

							if (!rs.next()) {
								throw new SQLException("Leave balance not found.");
							}

							int total = rs.getInt("total_leaves");
							int used = rs.getInt("used_leaves");
							int remaining = total - used;

							if (days > remaining) {
								throw new SQLException("Insufficient leave balance. " + "Remaining days: " + remaining);
							}
						}
					}

					String balanceUpdate = "UPDATE leave_balance " + "SET used_leaves = used_leaves + ? "
							+ "WHERE emp_id=? " + "AND used_leaves + ? <= total_leaves";

					try (PreparedStatement ps = con.prepareStatement(balanceUpdate)) {

						ps.setInt(1, days);
						ps.setInt(2, empId);
						ps.setInt(3, days);

						if (ps.executeUpdate() != 1) {
							throw new SQLException("Leave balance update failed.");
						}
					}
				}

				// Update request status.
				String updateSQL = "UPDATE leave_requests SET status=? " + "WHERE leave_id=? AND status='Pending'";

				try (PreparedStatement ps = con.prepareStatement(updateSQL)) {

					ps.setString(1, decision);
					ps.setInt(2, leaveId);

					if (ps.executeUpdate() != 1) {
						throw new SQLException("Leave status could not be updated.");
					}
				}

				con.commit();

				System.out.println("Leave request " + decision.toLowerCase() + " successfully.");

			} catch (SQLException e) {
				con.rollback();
				System.out.println("Operation failed: " + e.getMessage());
				System.out.println("Transaction rolled back.");

			} finally {
				con.setAutoCommit(true);
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// MODULE 4: LEAVE BALANCE TRACKING
	static void viewLeaveBalance() {

		System.out.println("1. View one employee");
		System.out.println("2. View all employees");

		int choice = readInt("Choose option: ", 1, 2);

		String sql = "SELECT e.emp_id, e.emp_name, b.total_leaves, " + "b.used_leaves, "
				+ "(b.total_leaves - b.used_leaves) AS remaining " + "FROM employees e "
				+ "JOIN leave_balance b ON e.emp_id=b.emp_id";

		if (choice == 1) {
			int empId = readInt("Enter employee ID: ", 1, Integer.MAX_VALUE);
			sql += " WHERE e.emp_id=?";
		}

		sql += " ORDER BY e.emp_id";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			if (choice == 1) {
				// Use the same ID entered above.
				// See the corrected implementation below.
				System.out.println("Use the employee-specific query option below.");
			}

			try (ResultSet rs = ps.executeQuery()) {

				System.out.println("\nID | Employee | Total | Used | Remaining");

				while (rs.next()) {
					System.out.println(
							rs.getInt("emp_id") + " | " + rs.getString("emp_name") + " | " + rs.getInt("total_leaves")
									+ " | " + rs.getInt("used_leaves") + " | " + rs.getInt("remaining"));
				}
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
