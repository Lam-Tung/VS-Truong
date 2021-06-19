import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

public class HQ {
    private static  String hostname = "hq";
    private static final int HOST_PORT = 6543;
    private static final int BUFFER_SIZE = 512;

    private final String name;
    private byte[] buffer;
    private DatagramSocket udpSocket;

    private static final Logger LOGGER = LoggerFactory.getLogger(HQ.class);

    public HQ(String name) {
        this.name = name;
        this.buffer = new byte[BUFFER_SIZE];

        try {
            this.udpSocket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(hostname , HOST_PORT);
            udpSocket.bind(address);
            LOGGER.info("UDP socket created... " + "Listening on port " + HOST_PORT + "...");
        } catch (SocketException e) {
            LOGGER.error("Failed to create UDP socket...{}\n", e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        while (true) {
            DatagramPacket udpPacket = new DatagramPacket(buffer, BUFFER_SIZE);
            try {
                udpSocket.receive(udpPacket);
                printUdpPacket(udpPacket);

            } catch (IOException e) {
                LOGGER.error("Error receiving packet...{}\n", e.getMessage());
            }
        }
    }

    private void printUdpPacket(DatagramPacket udpPacket) {
        InetAddress address = udpPacket.getAddress();
        int port = udpPacket.getPort();
        String payload = new String(udpPacket.getData(), 0, udpPacket.getLength());

        /*JSONObject json = new JSONObject(payload);
        int id = json.getInt("id");
        ClientType type = (ClientType) json.get("type");
        String name = json.getString("name");
        int power = json.getInt("power");*/

        LOGGER.info("Received UDP packet from " + address + ":" + port + payload);
    }
}
