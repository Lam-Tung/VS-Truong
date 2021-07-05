import org.apache.juli.logging.Log;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalClient {
    private String name;

    // RPC Thrift
    private static final String hostname = "hq";
    private static final int port = 9090;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClient.class);

    public ExternalClient(String name) {
        this.name = name;
    }

    public void performGetHistory(int index) {
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
}
