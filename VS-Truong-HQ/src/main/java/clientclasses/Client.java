package clientclasses;

public class Client {
    private int id;
    private ClientType type;
    private String name;
    private int power;

    public Client(int id, ClientType type, String name, int power) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.power = power;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClientType getType() {
        return type;
    }

    public void setType(ClientType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
}
