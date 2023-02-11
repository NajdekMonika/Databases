import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    /**
     * Metoda sprawdzająca w bazie danych czy podane przez użytkownika login i hasło istnieją.
     * Tworzone jest account, do którego przypisywane są id konta, login i hasło.
     * @param username - login użytkownika
     * @param password - hasło uzytkownika
     * @return obiekt reperezentujący konto klienta
     */
    public static Account checkIfAccountExists(String username, String password) {
        Account account = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from konta where login='" + username + "' and hasło ='" + password + "'");
            while (resultSet.next()) {
                account = new Account(
                        resultSet.getInt("id_konta"),
                        resultSet.getString("Login"),
                        resultSet.getString("Hasło"));
            }
            connection.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return account;
    }

    /**
     * Metoda zwracająca id klienta na podstawie id konta.
     * @param accountId - id konta
     * @return id klienta, albo jeśli id nie istnieje w systemie zwraca 0.
     */
    public static int getClientIdByAccount(int accountId) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from klienci where konta_id='" + accountId + "'");
            while (resultSet.next()) {
                return resultSet.getInt("id_klienci");
            }
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * Metoda filtrująca kawy według zadanych parametrów
     * @param attributes - lista atrybutów
     * @param conditions - lista warunków
     * @return lista przefiltrowanych kaw.
     */
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
                    String[] conditionArray = conditions.get(i).split(",");
                    sql += attributes.get(i) + " IN ("; //'" + conditions.get(i) + "') ";
                    for (int index = 0; index < conditionArray.length; index++) {
                        sql += "'" + conditionArray[index] + "'";
                        if (index != conditionArray.length - 1) {
                            sql += ',';
                        }
                    }
                    sql += ")";
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
                                resultSet.getString("kraj"),
                                resultSet.getInt("stan_magazynu")
                        ));
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return coffees;
    }

    /**
     * Metoda dodający zamówienie do bazy danych.
     * @param order - Obiekt klasy order
     */
    public static void addOrder(Order order) {
        try {
            int previousAmount = 0;
            int newAmount;
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            PreparedStatement st = connection.prepareStatement("select Stan_magazynu from kawy where id_kawy like ?");
            st.setString(1, String.valueOf(order.getCoffeeId()));
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                previousAmount = resultSet.getInt("Stan_magazynu");
            }
            st = connection.prepareStatement("update kawy set Stan_magazynu = ? where id_kawy like ?");
            newAmount = previousAmount - order.getCoffeeCount();
            if (newAmount > -1) {
                st.setInt(1, newAmount);
                st.setString(2, String.valueOf(order.getCoffeeId()));
                st.executeUpdate();
                String sql = "INSERT INTO zamówienia (Data_Godzina, Liczba_sztuk, Forma_dostawy, Forma_zapłaty, kawy_id, klienci_id) VALUES (?, ?, ?,?,?,?)";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setObject(1, order.getDateTime());
                statement.setObject(2, order.getCoffeeCount());
                statement.setObject(3, order.getDelivery());
                statement.setObject(4, order.getPayment());
                statement.setObject(5, order.getCoffeeId());
                statement.setObject(6, order.getClientId());
                statement.executeUpdate();
                System.out.println("Zamówienie zostało złożone.");
            } else {
                System.out.println("Nie można złożyć zamówienia, za mało paczek na stanie.");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     *
     * @param clientId
     */
    public static void viewYourOrders(String clientId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/kawy", "root", "studia123");
            PreparedStatement st = connection.prepareStatement("create or replace view zamówienia_view as SELECT z.Id_zamówienia," +
                    " z.Data_godzina, z.Liczba_sztuk, z.Forma_dostawy, z.Forma_zapłaty, z.kawy_id," +
                    " k.Cena*z.Liczba_sztuk as całkowita_cena FROM zamówienia z join kawy k" +
                    " on  k.id_kawy = z.kawy_id WHERE z.klienci_id = ?");
            st.setString(1, clientId);
            st.executeUpdate();
            st = connection.prepareStatement("select * from zamówienia_view", ResultSet.TYPE_SCROLL_SENSITIVE,
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
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda wypisująca wyniki zapytań sql.
     * @param resultSet - obiekt reprezentujący odpowiedź na zapytanie.
     * @throws SQLException
     */
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
