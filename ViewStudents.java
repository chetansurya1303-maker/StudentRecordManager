
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class ViewStudents {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/student_management";
        String username = "root";
        String password = "MySql@12345";

        String query = "SELECT * FROM students";

        try (
                Connection con = DriverManager.getConnection(url, username, password);
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                System.out.println(
                        rs.getInt("id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getInt("age")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
