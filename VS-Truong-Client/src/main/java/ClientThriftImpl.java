import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientThriftImpl implements ClientThriftService.Iface{
    Client client;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientThriftImpl.class);

    public ClientThriftImpl(Client client) {
        this.client = client;
    }

    @Override
    public boolean shutDown(boolean shutDown) throws TException {
        LOGGER.info("Power on/off request received...");
        if (shutDown) {
            client.shutDown = true;
            LOGGER.info("Client " + client.id + " power off...");
        } else {
            client.shutDown = false;
            LOGGER.info("Client " + client.id + " power on...");
        }
        return client.shutDown;
    }

    @Override
    public boolean changePower(int amount) throws TException {
        LOGGER.info("Change power request received...");
        client.setPower(amount);
        LOGGER.info("Client " + client.id + " power set to " + amount + "...");
        return true;
    }
}
