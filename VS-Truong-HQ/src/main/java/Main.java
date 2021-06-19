import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        if(args.length < 1) {
            LOGGER.error("Missing arguments...");
            System.exit(1);
        }

        HQ hq = new HQ(args[0]);
        LOGGER.info("HQ started...");
        hq.run();
    }
}
