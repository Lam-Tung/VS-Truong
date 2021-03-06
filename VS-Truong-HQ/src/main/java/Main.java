import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class Main {
    private static final int INTERVAL = 5000; // milliseconds

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if(args.length < 2) {
            LOGGER.error("Missing arguments...");
            System.exit(1);
        }

        HQ hq = new HQ(args[0], Integer.parseInt(args[1]));
        LOGGER.info("HQ " + args[0] + " started...");
        Timer timer = new Timer();
        timer.schedule(new HQTask(hq), 10000, INTERVAL);
        hq.run();
    }
}
