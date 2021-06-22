import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public abstract class Client {
    protected int id;
    protected String name;
    protected ClientType type;
    protected int power;
    protected byte[] buffer;

    protected final int BASE_POWER = 200;
    protected final int MIN_POWER = -100;
    protected final int MAX_POWER = 100;
    protected final int BUFFER_SIZE = 512;
    protected final int FAILURE_CHANCE = 5;
    protected final int FAILURE_DURATION = 5000;

    // UDP
    protected InetAddress host;
    protected DatagramSocket udpSocket;
    protected static String hostname = "hq";
    protected static int HOST_PORT = 6543;

    // RPC Thrift
    protected boolean shutDown = false;
    protected ClientThriftImpl clientThriftImpl;
    protected ClientThriftService.Processor processor;
    protected int tPort;

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public Client(int id, String name, ClientType type, int tPort) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.power = BASE_POWER;
        this.buffer = new byte[BUFFER_SIZE];
        this.tPort = tPort;

        LOGGER.info("Created Client: " + this.name + " of type " + this.type.name());

        try {
            host = InetAddress.getByName(hostname);
            udpSocket = new DatagramSocket();
            LOGGER.info("Started the UDP socket that connects to {}", host.getHostAddress());
        } catch (UnknownHostException e) {
            LOGGER.error("Can not find the destination host address...\n{}", e.getMessage());
        } catch (SocketException e) {
            LOGGER.error("Failed to create UDP socket...\n{}", e.getLocalizedMessage());
        }

        try {
            clientThriftImpl = new ClientThriftImpl(this);
            processor = new ClientThriftService.Processor(clientThriftImpl);

            Runnable tServer = () -> createThriftServer(processor);
            Thread thriftServerThread = new Thread(tServer);
            thriftServerThread.start();
        } catch (Exception e) {
            LOGGER.error("Failed to create Thrift-Server...{}", e.getMessage());
        }
    }

    protected int setRandomPower() {
        Random r = new Random();
        return power + r.nextInt((MAX_POWER - MIN_POWER) + 1) + MIN_POWER;
    }

    protected void setPower(int power) {
        this.power = power;
    }

    protected void sendInfo() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("type", this.type);
        json.put("name", this.name);
        json.put("power", this.power);
        String jsonString = json.toString();

        try {
            buffer = jsonString.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, HOST_PORT);
            LOGGER.info("Sending UDP packet... " + "Destination: " + host + " Port: " + HOST_PORT);
            udpSocket.send(packet);
        } catch (IOException e) {
            LOGGER.error("Failed to send UDP packet...{}", e.getMessage());
        }
    }

    public boolean setRandomSystemFailure() {
        Random r = new Random();
        int success = r.nextInt(100) + 1;

        if (success < FAILURE_CHANCE) {
            LOGGER.info("Client failure...");
            try {
                Thread.sleep(FAILURE_DURATION);
            } catch (InterruptedException e) {
                LOGGER.error("Sleep failed...{}", e.getMessage());
            }
            return true;
        }

        return false;
    }

    public void createThriftServer(ClientThriftService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(tPort);
            TServer tServer = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            LOGGER.info("Starting thrift-server... Listening on port " + tPort + "...");
            tServer.serve();
        } catch (TTransportException e) {
            LOGGER.error("Failed to create TTransport...\n{}", e.getMessage());
        }
    }
}
