import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.TimerTask;

public class ExternalClientTask extends TimerTask {
    private final ExternalClient xClient;
    private int index = 0;
    private int indexCounter = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClientTask.class);

    public ExternalClientTask(ExternalClient xClient) {
        this.xClient = xClient;
    }

    public void run() {
        Random r = new Random();
        boolean task = r.nextBoolean();

        if (task) {
            indexCounter++;
            LOGGER.info("Perform get history...");
            xClient.performGetHistory(index);
            if (indexCounter >= 10) {
                index++;
                LOGGER.info("INDEX INCREASED -> " + index);
                indexCounter = 0;
            }
        } else {
            LOGGER.info("Perform get status...");
            xClient.performGetStatus();
        }
    }
}
