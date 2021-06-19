import java.util.TimerTask;

public class InfoTask extends TimerTask {
    private final Client client;

    public InfoTask(Client client) {
        this.client = client;
    }

    public void run() {
        if (client.setRandomSystemFailure()) {
            return;
        }

        client.power = client.setRandomPower();
        client.sendInfo();
    }
}
