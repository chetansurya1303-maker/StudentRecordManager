
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/student_management";

    private static final String USER = "root";

    private static final String PASSWORD =
            System.getenv("DB_PASSWORD");

    public static void main(String[] args) {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            System.out.println(
                    "Database password not configured!"
            );
            return;
        }

        try (Connection con = DriverManager.getConnection(
                URL, USER, PASSWORD)) {

            System.out.println(
                    "Database connected successfully!"
            );

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
