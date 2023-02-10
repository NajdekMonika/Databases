public class Client {
    private int id;
    private String name;
    private String surname;
    private String phoneNumber;
    private String email;
    private String Address;

    private Account account;

    public Client(int id, String name, String surname, String phoneNumber, String email, String address, Account account) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        Address = address;
        this.account = account;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", Address='" + Address + '\'' +
                ", account=" + account +
                '}';
    }
}
