import clientclasses.Client;
import clientclasses.ClientType;
import http.HTTPServer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class HQ {
    private static  String hostname = "hq";
    private static final int HOST_PORT = 6543;
    private static final int BUFFER_SIZE = 512;

    private final String name;
    private byte[] buffer;
    private DatagramSocket udpSocket;
    private Map<Integer, Client> clients;
    private Map<Integer, List<Integer>> history;

    private static final Logger LOGGER = LoggerFactory.getLogger(HQ.class);

    public HQ(String name) {
        this.name = name;
        this.buffer = new byte[BUFFER_SIZE];
        this.clients = new HashMap<>();
        this.history = new HashMap<>();

        try {
            udpSocket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(hostname , HOST_PORT);
            udpSocket.bind(address);
            LOGGER.info("UDP socket created... " + "Listening on port " + HOST_PORT + "...");
        } catch (SocketException e) {
            LOGGER.error("Failed to create UDP socket...{}\n", e.getMessage());
            System.exit(1);
        }

        // HTTP-Server
        Runnable HTTPServer = new HTTPServer(clients, history);
        Thread HTTPServerThread = new Thread(HTTPServer);
        HTTPServerThread.start();
    }

    public void run() {
        while (true) {
            DatagramPacket udpPacket = new DatagramPacket(buffer, BUFFER_SIZE);
            try {
                udpSocket.receive(udpPacket);
                updateClients(udpPacket);
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

        LOGGER.info("Received UDP packet from " + address + ":" + port + payload);
    }

    private void updateClients(DatagramPacket udpPacket) {
        byte[] payload = Arrays.copyOfRange(udpPacket.getData(), 0, udpPacket.getLength());
        JSONObject jsonObject = new JSONObject(new String(payload));

        int id = jsonObject.getInt("id");
        ClientType type = ClientType.valueOf(jsonObject.getString("type"));
        String name = jsonObject.getString("name");
        int power = jsonObject.getInt("power");

        // add to clients
        Client client = new Client(id, type, name, power);
        clients.put(id, client);

        // add to history
        if (history.containsKey(id)) {
            List<Integer> list = history.get(id);
            list.add(power);
        } else {
            List<Integer> powerHistory = new ArrayList<>();
            powerHistory.add(power);
            history.put(id, powerHistory);
        }
    }

    public void performShutDown(String hostname, int port, boolean value) {
        try {
            TTransport transport;

            transport = new TSocket(hostname, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            ClientThriftService.Client client = new ClientThriftService.Client(protocol);

            try {
                client.shutDown(value);
                LOGGER.info("Shut down performed successfully...");
            } catch (TException e) {
                LOGGER.error("Error performing shut down...{}\n", e.getMessage());
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }
    }

    public Map<Integer, Client> getClients() {
        return clients;
    }

    public Map<Integer, List<Integer>> getHistory() {
        return history;
    }
}
