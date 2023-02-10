import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    public static Account checkIfAccountExists(String username, String password){
        Account account = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from konta where login='" + username + "' and hasło ='" + password + "'");
            while(resultSet.next()){
                account = new Account(
                        resultSet.getInt("id_konta"),
                        resultSet.getString("Login"),
                        resultSet.getString("Hasło"));
            }
            connection.close();

        }
        catch(SQLException | ClassNotFoundException e){e.printStackTrace();
        }
        return account;
    }

    public static int getClientIdByAccount(int accountId){
        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from klienci where konta_id='"+ accountId + "'");
            while(resultSet.next()){
                return resultSet.getInt("id_klienci");
            }
            connection.close();

        }
        catch(SQLException e){e.printStackTrace();
        }
        return 0;
    }



    public static List<Coffee> filterCoffees(List<String> attributes, List<String> conditions) {
        List<Coffee> coffees = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            Statement statement = connection.createStatement();
            String sql = "select * from kawa_view where ";
            for (int i = 0; i < attributes.size(); i++) {
                if (conditions.get(i).contains("-")) {
                    String[] range = conditions.get(i).split("-");
                    sql += attributes.get(i) + " >= " + range[0] + " AND " + attributes.get(i) + " <= " + range[1];
                } else {
                    sql += attributes.get(i) + " IN ('" + conditions.get(i) + "') ";
                }
                if (i != attributes.size() - 1) {
                    sql += " and ";
                }
            }
            System.out.println(sql);
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                coffees.add(
                        new Coffee(
                                resultSet.getInt("id_kawy"),
                                resultSet.getDouble("aromat"),
                                resultSet.getDouble("kwasowość"),
                                resultSet.getDouble("słodycz"),
                                resultSet.getDouble("ocena"),
                                resultSet.getDouble("cena"),
                                resultSet.getString("typy"),
                                resultSet.getString("producenci"),
                                resultSet.getString("rejon"),
                                resultSet.getString("kraj")
                        ));
            }
        }
        catch(SQLException | ClassNotFoundException e){e.printStackTrace();
        }

        return coffees;
    }

    public static void addOrder(Order order){
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            String sql = "INSERT INTO zamówienia (Data_Godzina, Liczba_sztuk, Forma_dostawy, Forma_zapłaty, kawy_id, klienci_id) VALUES (?, ?, ?,?,?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, order.getDateTime());
            statement.setObject(2, order.getCoffeeCount());
            statement.setObject(3, order.getDelivery());
            statement.setObject(4, order.getPayment());
            statement.setObject(5, order.getCoffeeId());
            statement.setObject(6, order.getClientId());
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void viewYourOrders(String clientId) {
        try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
        PreparedStatement st = connection.prepareStatement("create or replace view zamówienia_view as SELECT Id_zamówienia, Data_godzina, Liczba_sztuk, " +
                        "Forma_dostawy, Forma_zapłaty, kawy_id FROM zamówienia WHERE klienci_id = ?");
        st.setString(1, clientId);
        st.executeUpdate();
        st = connection.prepareStatement("select * from zamówienia_view",ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        ResultSet resultSet = st.executeQuery();
        int row = 0;
        if (resultSet.last()) {
            row = resultSet.getRow();
            resultSet.beforeFirst();
        }
        if (row == 0) {
            System.out.println("Nie masz żadnych zamówień.");
        } else {
            printResultSet(st.executeQuery());
        }}catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    public static void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        int columnsNumber = rsmd.getColumnCount(); // liczba kolumn
        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1)
                    System.out.print(", ");
                String columnValue = resultSet.getString(i);
                System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
            }
            System.out.println("");
        }
        System.out.println("");

    }


}
