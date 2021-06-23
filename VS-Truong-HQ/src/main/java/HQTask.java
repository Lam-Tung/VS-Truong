import clientclasses.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;

public class HQTask extends TimerTask {
    private final HQ hq;

    private static final Logger LOGGER = LoggerFactory.getLogger(HQTask.class);

    public HQTask(HQ hq) {
        this.hq = hq;
    }

    public void run() {
        Random r = new Random();
        boolean task = r.nextBoolean();

        String hostname = getRandomClientHostname();
        int port = getPort(hostname);

        if (task) {
                boolean value = r.nextBoolean();
                LOGGER.info("Perform power on/off for " + hostname + "...");
                hq.performShutDown(hostname, port, value);
        } else {
                int amount = 400;
                LOGGER.info("Perform power change for " + hostname + " to " + amount + "...");
                hq.performPowerChange(hostname, port, amount);
        }
    }

    private String getRandomClientHostname() {
        String hostname = "";
        Random r = new Random();
        int amountClients = hq.getClients().size();
        int index = 0;
        while (amountClients == 0) {
            amountClients = hq.getClients().size();
        }
        while (index == 0) {
            index = r.nextInt(amountClients);
        }
        Client client = hq.getClients().get(index);
        hostname += client.getType().toString();
        hostname += client.getId();
        LOGGER.info(hostname);

        return hostname;
    }

    private int getPort(String hostname) {
        int port = 0 ;

        if (hostname.startsWith("PRODUCER")) {
            switch (hostname) {
                case "PRODUCER1": port = 9091; break;
                case "PRODUCER2": port = 9092; break;
                case "PRODUCER3": port = 9093; break;
            }
        } else if (hostname.startsWith("CONSUMER")) {
            port = 9094;
        }

        return port;
    }
}
