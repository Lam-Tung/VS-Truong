import org.apache.juli.logging.Log;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class ExternalClient {
    private String name;
    private int timeout = 0;

    // RPC Thrift
    private String hostname;
    private int port;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClient.class);

    public ExternalClient(String name) {
        this.name = name;
    }

    public void performGetHistory(int index) {
        hostname = getRandomHost();
        port = getPortForHost(hostname);

        try {
            TTransport transport;

            transport = new TSocket(hostname, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            ExternalClientThriftService.Client client = new ExternalClientThriftService.Client(protocol);

            try {
                String result = client.getHistory(index);
                LOGGER.info("Get history performed successfully...");
                LOGGER.info(result);
            } catch (TException e) {
                LOGGER.error("Error performing get history...{}\n", e.getMessage());
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }
    }

    public void performGetStatus() {
        hostname = getRandomHost();
        port = getPortForHost(hostname);

        try {
            TTransport transport;

            transport = new TSocket(hostname, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            ExternalClientThriftService.Client client = new ExternalClientThriftService.Client(protocol);

            try {
                String result = client.getStatus();
                LOGGER.info("Get status performed successfully...");
                LOGGER.info(result);
            } catch (TException e) {
                LOGGER.error("Error performing get status...{}\n", e.getMessage());
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }
    }

    public void performGetAllInfo() {
        hostname = getRandomHost();
        port = getPortForHost(hostname);

        try {
            TTransport transport;

            transport = new TSocket(hostname, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            ExternalClientThriftService.Client client = new ExternalClientThriftService.Client(protocol);

            try {
                String status = client.getStatus();
                LOGGER.info("STATUS: " + status);
                if (status.equals("running")) {
                    String result = client.getAllInfo();
                    LOGGER.info("Get all info performed successfully...");
                    LOGGER.info(result);
                } else {
                    while (status != "running") {
                        if (timeout >= 10) {
                            LOGGER.info("TIMEOUT");
                            timeout = 0;
                        }
                        hostname = getRandomHost();
                        port = getPortForHost(hostname);

                        transport = new TSocket(hostname, port);
                        transport.open();

                        protocol = new TBinaryProtocol(transport);
                        client = new ExternalClientThriftService.Client(protocol);
                        status = client.getStatus();

                        timeout++;
                    }
                }
            } catch (TException e) {
                LOGGER.error("Error performing get all info...{}\n", e.getMessage());
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }
    }

    private int getPortForHost(String hostname) {
        int port = 0;
        switch (hostname) {
            case "hq1": port = 9090; break;
            case "hq2": port = 9091; break;
            case "hq3": port = 9092; break;
        }

        return port;
    }

    private String getRandomHost() {
        String hostname = "hq";
        Random random = new Random();
        int i = random.nextInt((3 - 1) + 1) + 1;
        hostname += i;

        return hostname;
    }
}
