import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalClient {
    private String name;

    // RPC Thrift
    private static String hostname = "hq";
    private static int tPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClient.class);

    public ExternalClient(String name, int tPort) {
        this.name = name;
        this.tPort = tPort;

        try {
            TTransport transport = new TSocket(hostname, tPort);
        } catch (TTransportException e) {
            LOGGER.error("Failed to create TTransport...{}", e.getMessage());
        }
    }
}
