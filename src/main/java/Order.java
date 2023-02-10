import java.time.LocalDateTime;

public class Order {
    private LocalDateTime dateTime;
    private int coffeeCount;
    private String delivery;
    private String payment;
    private int coffeeId;

    public Order(LocalDateTime dateTime, int coffeeCount, String delivery, String payment, int coffeeId) {
        this.dateTime = dateTime;
        this.coffeeCount = coffeeCount;
        this.delivery = delivery;
        this.payment = payment;
        this.coffeeId = coffeeId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getCoffeeCount() {
        return coffeeCount;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getPayment() {
        return payment;
    }

    public int getCoffeeId() {
        return coffeeId;
    }

    @Override
    public String toString() {
        return "Zamówienie{" +
                " data i godzina zamówienia=" + dateTime +
                ", liczba sztuk=" + coffeeCount +
                ", forma dostawy='" + delivery + '\'' +
                ", forma zaplaty='" + payment + '\'' +
                ", kawa=" + coffeeId +
                '}';
    }
}
