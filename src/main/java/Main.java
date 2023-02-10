import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws SQLException {
       mainApp();
    }


    public static void mainApp(){
        while (true) {
            Account acc = login();
            if (acc != null) {
                System.out.println("Jesteś zalogowany!");
                customerMenu();
                break;
            } else {
                System.out.println("Niepoprawne hasło lub login spróbuj ponownie");
            }
        }
    }

    public static void customerMenu(){
        int answer = 0;
        while (answer != 4){
            printMenu();
        try{
        answer = sc.nextInt();
        switch (answer){
            case 1:
                filterCoffee();
                break;
            case 2:
                makeAnOrder();
                break;
            case 3:
                //tu miejsce na funkcję drukującą poprzednie zamówienia
                break;
            case 4:
                exit(0);
        }}catch(Exception ex){
            System.out.println("Podaj liczbę między 1 a 4");
            sc.next();
        }
    }}

    public static void printMenu() {
        System.out.println("Wybierz, co chcesz zrobić: ");
        String[] options = {"1 - przeglądanie  według zadanych parametrów", "2 - złożenie zamówienia",
        "3 - zobaczenie poprzednich zamówień", "4 - wylogowanie"};
        for (String option : options) {
            System.out.println(option);
        }
    }

    public static Account login() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Podaj login:");
        String login = sc.nextLine();
        System.out.println("Podaj haslo:");
        String password = sc.nextLine();
        return DatabaseConnection.checkIfAccountExists(login,password);
    }

    public static void filterCoffee() {
        List<String> conditions = new ArrayList<>();
        List<String> attributes = new ArrayList<>();
        while (true) {
            System.out.println("Wybierz po czym chcesz filtrować kawy: ");
            System.out.println("1 - aromat, 2- kwasowość, 3 - słodycz");
            System.out.println("4 - ocena, 5 - typ, 6 - producent");
            System.out.println("7 - region, 8 - kraj, 0 - zakończ filtrowanie");
            int choice = sc.nextInt();
            if (choice == 0) {
                //System.out.println(DatabaseConnection.filterCoffees(attributes, conditions));
                DatabaseConnection.showFilteredCoffee(attributes, conditions);
                break;
            } else if (choice <= 4 && choice >= 1) {
                sc.nextLine();
                System.out.println("Podaj zakres wartości (w postaci 2-4)");
                String range = sc.nextLine();
                if (range.length() > 4 || !range.contains("-")) {
                    System.out.println("Błędne dane. Spróbuj jeszcze raz.");
                } else {
                    if (choice == 1) {
                        attributes.add("aromat");
                    } else if (choice == 2) {
                        attributes.add("kwasowość");
                    } else if (choice == 3) {
                        attributes.add("słodycz");
                    } else {
                        attributes.add("ocena");
                    }
                    conditions.add(range);
                }

            } else {
                String attribute = switch (choice) {
                    case 5 -> "typy";
                    case 6 -> "producenci";
                    case 7 -> "rejon";
                    case 8 -> "kraj";
                    default -> "";
                };
                sc.nextLine();
                System.out.println("Podaj " + attribute + "-y ");
                System.out.println("Gdy więcej niż jeden, wtedy oddziel je przecinkiem (a,b,c):");
                String condition = sc.nextLine();
                attributes.add(attribute);
                conditions.add(condition);
            }
        }
    }

    public static void makeAnOrder(){
        System.out.print("Podaj numer kawy którą chcesz kupić: ");
        int idCoffee = sc.nextInt();
        sc.nextLine();
        System.out.print("Podaj liczbę sztuk kupowanej kawy: ");
        int cofeeCount = sc.nextInt();
        sc.nextLine();
        System.out.print("Podaj formę dostawy: (kurier/do punktu/paczkomat)");
        String delivery = sc.nextLine();
        System.out.print("Podaj formę zapłaty: (przelew/BLIK/przy odbiorze)");
        String payment = sc.nextLine();
        List<String> deliveries = Arrays.asList(new String[]{"kurier", "do punktu", "paczkomat"});
        List<String> payments = Arrays.asList(new String[]{"przelew", "BLIK", "przy odbiorze"});
        if(deliveries.contains(delivery) && payments.contains(payment)){
            Order order = new Order(LocalDateTime.now(),cofeeCount,delivery,payment,idCoffee);
            DatabaseConnection.addOrder(order);
            System.out.println("Zamówienie zostało złożone.");
        }
        else{
            System.out.println("Niepoprawne dane");
        }

    }
}