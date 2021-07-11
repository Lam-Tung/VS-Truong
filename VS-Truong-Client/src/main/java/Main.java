import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;


public class Main {
    private static final int INTERVAL = 5000; // milliseconds

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if(args.length < 6) {
            LOGGER.error("Missing arguments.");
            System.exit(1);
        }

        Client client = null;

        if(args[0].equalsIgnoreCase("producer")) {
            client = new Producer(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
        } else if(args[0].equalsIgnoreCase("consumer")) {
            client = new Consumer(Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
        }

        Timer timer = new Timer();
        timer.schedule(new ClientTask(client), 0, INTERVAL);
    }
}
