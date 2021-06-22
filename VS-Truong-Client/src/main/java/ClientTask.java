import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class ClientTask extends TimerTask {
    private final Client client;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientTask.class);

    public ClientTask(Client client) {
        this.client = client;
    }

    public void run() {
        if (client.shutDown) {
            LOGGER.info("Client " + client.id + " down...");
        } else {
            if (client.setRandomSystemFailure()) {
                return;
            }

            client.power = client.setRandomPower();
            client.sendInfo();
        }
    }
}
