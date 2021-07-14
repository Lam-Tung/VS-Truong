import clientclasses.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.TimerTask;

public class HQTask extends TimerTask {
    private final HQ hq;
    private int counter = 0;
    private final int DATA_SETS = 10;
    private int index = 0;
    private int indexCounter = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(HQTask.class);

    public HQTask(HQ hq) {
        this.hq = hq;
    }

    public void run() {
        if (hq.status != "running") {
            LOGGER.info(hq.getName() + " is down...");
        } else {
            String allStatus = "";

            switch (hq.getName()) {
                case "hq1": {
                    allStatus += hq.getOwnStatus(index, DATA_SETS);
                    allStatus += hq.performGetAllInfo("hq2", 9091 - 100, index);
                    allStatus += hq.performGetAllInfo("hq3", 9092 - 100, index);
                    break;
                }
                case "hq2": {
                    allStatus += hq.performGetAllInfo("hq1", 9090-100, index);
                    allStatus += hq.getOwnStatus(index, DATA_SETS);
                    allStatus += hq.performGetAllInfo("hq3", 9092-100, index);
                    break;
                }
                case "hq3": {
                    allStatus += hq.performGetAllInfo("hq1", 9090 - 100, index);
                    allStatus += hq.performGetAllInfo("hq2", 9091 - 100, index);
                    allStatus += hq.getOwnStatus(index, DATA_SETS);
                    break;
                }
            }

            hq.setAllStatus(allStatus);

            // increase index counter
            indexCounter++;

            if (indexCounter >= 10) {
                index++;
                indexCounter = 0;
            }

            // random task
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

        counter++;
        if (counter == 5) {
            Random random = new Random();
            boolean hqShutDown = random.nextBoolean();
            if (hqShutDown) {
                hq.status = "down";
                LOGGER.info("HQ DOWWWWWWWWWWWN");
            } else {
                hq.status = "running";
                LOGGER.info("HQ UPPPPPPPPPPPPPPPPP");
                //on reboot get all info
                String allStatus = "";

                switch (hq.getName()) {
                    case "hq1": {
                        allStatus += hq.getOwnStatus(index, 0);
                        allStatus += hq.performGetAllInfo("hq2", 9091 - 100, index);
                        allStatus += hq.performGetAllInfo("hq3", 9092 - 100, index);
                        break;
                    }
                    case "hq2": {
                        allStatus += hq.performGetAllInfo("hq1", 9090-100, index);
                        allStatus += hq.getOwnStatus(index, 0);
                        allStatus += hq.performGetAllInfo("hq3", 9092-100, index);
                        break;
                    }
                    case "hq3": {
                        allStatus += hq.performGetAllInfo("hq1", 9090 - 100, index);
                        allStatus += hq.performGetAllInfo("hq2", 9091 - 100, index);
                        allStatus += hq.getOwnStatus(index, 0);
                        break;
                    }
                }

                hq.setAllStatus(allStatus);
            }
            counter = 0;
        }
    }

    private String getRandomClientHostname() {
        String hostname = hq.getName();
        String clientHostname = "";
        Random r = new Random();
        int amountClients = hq.getClients().size();

        while (amountClients == 0) {
            amountClients = hq.getClients().size();
        }

        int clientFirstNumber = r.nextInt((3 - 1) + 1) + 1;
        String id = "";
        switch (hostname) {
            case "hq1": {
                id = clientFirstNumber + "1";
                break;
            }
            case "hq2": {
                id = clientFirstNumber + "2";
                break;
            }
            case "hq3": {
                id = clientFirstNumber + "3";
                break;
            }
        }

        Client client = hq.getClients().get(Integer.parseInt(id));

        clientHostname += client.getType().toString();
        clientHostname += client.getId();

        return clientHostname;
    }

    private int getPort(String hostname) {
        int port = 0 ;

        if (hostname.startsWith("PRODUCER")) {
            switch (hostname) {
                case "PRODUCER11": port = 9011; break;
                case "PRODUCER12": port = 9012; break;
                case "PRODUCER13": port = 9013; break;
                case "PRODUCER21": port = 9021; break;
                case "PRODUCER22": port = 9022; break;
                case "PRODUCER23": port = 9023; break;
                case "PRODUCER31": port = 9031; break;
                case "PRODUCER32": port = 9032; break;
                case "PRODUCER33": port = 9033; break;
            }
        } else if (hostname.startsWith("CONSUMER")) {
            switch (hostname) {
                case "CONSUMER11": port = 9041; break;
                case "CONSUMER12": port = 9042; break;
                case "CONSUMER13": port = 9043; break;
            }
        }

        return port;
    }
}
