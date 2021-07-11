package clientclasses;

public class Client {
    private int id;
    private ClientType type;
    private String name;
    private int power;
    private boolean shutdownStatus;

    public Client(int id, ClientType type, String name, int power, boolean shutdownStatus) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.power = power;
        this.shutdownStatus = shutdownStatus;
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

    public boolean isShutdownStatus() {
        return shutdownStatus;
    }

    public void setShutdownStatus(boolean shutdownStatus) {
        this.shutdownStatus = shutdownStatus;
    }
}
