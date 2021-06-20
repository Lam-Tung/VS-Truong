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

    protected InetAddress host;
    protected DatagramSocket udpSocket;

    protected static String hostname = "hq";
    protected static int HOST_PORT = 6543;
    protected final int MIN_POWER = 50;
    protected final int MAX_POWER = 1000;
    protected final int BUFFER_SIZE = 512;
    protected final int FAILURE_CHANCE = 5;
    protected final int FAILURE_DURATION = 5000;

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public Client(int id, String name, ClientType type) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.power = setRandomPower();
        this.buffer = new byte[BUFFER_SIZE];

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
    }

    protected int setRandomPower() {
        Random r = new Random();
        return r.nextInt((MAX_POWER - MIN_POWER) + 1) + MIN_POWER;
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
}
