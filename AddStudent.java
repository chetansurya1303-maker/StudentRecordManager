import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddStudent {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/student_management";
        String username = "root";
        String password = "MySql@12345";

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        System.out.print("Enter student age: ");
        int age = sc.nextInt();

        String query = "INSERT INTO students (name, age) VALUES (?, ?)";

        try (
                Connection con = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = con.prepareStatement(query)
        ) {
            ps.setString(1, name);
            ps.setInt(2, age);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student added successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error adding student.");
            e.printStackTrace();
        }

        sc.close();
    }
}
