import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.TimerTask;

public class ExternalClientTask extends TimerTask {
    private final ExternalClient xClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClientTask.class);

    public ExternalClientTask(ExternalClient xClient) {
        this.xClient = xClient;
    }

    public void run() {
        Random r = new Random();
        boolean task = r.nextBoolean();

        if (task) {
            LOGGER.info("Perform get history...");
            xClient.performGetHistory();
        } else {
            LOGGER.info("Perform get status...");
            xClient.performGetStatus();
        }
    }
}
