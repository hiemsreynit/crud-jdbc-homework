import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.Table;

import javax.crypto.spec.PSource;
import java.sql.*;
import java.util.Scanner;

public class CrudOperation {

    private static final Scanner sc = new Scanner(System.in);
    public static final Table table = new Table(
            3, BorderStyle.UNICODE_ROUND_BOX_WIDE
    );

    private static final String URL = "jdbc:postgresql://localhost:5432/test_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "172772";

    public void createUser() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        System.out.print("Enter id: ");
        int id = sc.nextInt(); sc.nextLine();

        if(existById(id)) {
            System.out.println("User doesn't exist.");
        }

        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter age: ");
        Integer age = Integer.parseInt(sc.nextLine());

        User user = new User(id, name, age);
        String sql = """
                INSERT INTO users (id, name, age)
                       VALUES (?, ?, ?)
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, user.getId());
        ps.setString(2, user.getName());
        ps.setInt(3, user.getAge());

        int rowEffected = ps.executeUpdate();
        if (rowEffected > 0) {
            System.out.println("User created successfully");
        } else {
            System.out.println("Failed to create user");
        }

        connection.close();
        ps.close();
    }

    public void readUserById() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        System.out.print("Enter id to find: ");
        int findId = sc.nextInt();

        if(!existById(findId)) {
            System.out.println("User doesn't exist.");
        }

        String sql = """
                SELECT * FROM users 
                         WHERE id = ?
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, findId);

        ResultSet rs = ps.executeQuery();

        table.addCell("ID");
        table.addCell("NAME");
        table.addCell("AGE");
        if (rs.next()) {
            User user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("age")
            );
            table.addCell(rs.getString("id"));
            table.addCell(rs.getString("name"));
            table.addCell(rs.getString("age"));
            System.out.println(table.render());
        }

    }

    public static boolean existById(Integer id) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        String sql = """
                SELECT 1 FROM users WHERE id = ?
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        return rs.next();
    }

    public void updateUserById() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        System.out.print("Enter id to find: ");
        int findId = sc.nextInt(); sc.nextLine();

        if(!existById(findId)) {
            System.out.println("User doesn't exist.");
        }

        System.out.print("Enter new name: ");
        String name = sc.nextLine();
        System.out.print("Enter new age: ");
        int age = sc.nextInt();

        String sql = """
                UPDATE users SET name = ?, age = ? WHERE id = ?
                """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, name);
        ps.setInt(2, age);
        ps.setInt(3, findId);
        int rowEffected = ps.executeUpdate();
        if (rowEffected > 0) {
            System.out.println("User updated successfully");
        }else {
            System.out.println("Failed to update user");
        }
    }

    public void deleteUserById() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        System.out.print("Enter id: ");
        int id = sc.nextInt(); sc.nextLine();

        if(!existById(id)) {
            System.out.println("User doesn't exist.");
        }

        String sql = """
               DELETE FROM users WHERE id = ?
               """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int rowEffected = ps.executeUpdate();
        if (rowEffected > 0) {
            System.out.println("User deleted successfully");
        }else {
            System.out.println("Failed to delete user");
        }
    }

    public void readAllUsers() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        String sql = """
                SELECT * FROM users
                """;
        PreparedStatement ps = connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        table.addCell("ID");
        table.addCell("NAME");
        table.addCell("AGE");

        while(rs.next()) {
            rs.getInt("id");
            rs.getString("name");
            rs.getInt("age");

            table.addCell(rs.getString("id"));
            table.addCell(rs.getString("name"));
            table.addCell(rs.getString("age"));
        }
        System.out.println(table.render());

    }

    public static void main(String[] args) {
        CrudOperation user = new CrudOperation();

        while (true) {
            String menu = """
                    1. Add user
                    2. Read User By Id
                    3. Update User By Id
                    4. Delete user By Id
                    5. Read All Users
                    0. Exit program
                    """;
            System.out.print(menu);
            System.out.print("Choose on option: ");
            int op = sc.nextInt();

            if(op == 0) break;

            try {
                switch (op) {
                    case 1 -> user.createUser();
                    case 2 -> user.readUserById();
                    case 3 -> user.updateUserById();
                    case 4 -> user.deleteUserById();
                    case 5 -> user.readAllUsers();
                    default -> System.out.println("Invalid option!");
                }
            }catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }


    }

}
