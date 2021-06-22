import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class Main {
    private static final int INTERVAL = 10000; // milliseconds

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClient.class);

    public static void main(String[] args) {
        if(args.length < 2) {
            LOGGER.error("Missing arguments.");
            System.exit(1);
        }

        ExternalClient xClient = new ExternalClient(args[0], Integer.parseInt(args[1]));

        Timer timer = new Timer();
        timer.schedule(new ExternalClientTask(xClient), 0, INTERVAL);
    }
}
