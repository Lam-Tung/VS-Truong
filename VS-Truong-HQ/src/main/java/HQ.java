import clientclasses.Client;
import clientclasses.ClientType;
import http.HTTPServer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.*;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HQ {
    public String getName() {
        return name;
    }

    private final String name;
    private final Map<Integer, Client> clients;
    private final Map<Integer, List<Integer>> history;
    private final Map<Integer, Boolean> statusClients;
    private String allStatus;

    // UDP
    private final byte[] buffer;
    private DatagramSocket udpSocket;
    private static String hostname;
    private static final int HOST_PORT = 6543;
    private static final int BUFFER_SIZE = 512;
    private static final boolean UDP_TRANSFER = false;

    // RPC Thrift
    private ExternalClientThriftImpl externalClientThriftImpl;
    private ExternalClientThriftService.Processor processor;
    private HqThriftServiceImpl hqThriftImpl;
    private HqThriftService.Processor processorHq;
    int tPort;
    String status = "running";

    // Mqtt
    private String subscriberId;
    private IMqttClient subscriber;

    private static final Logger LOGGER = LoggerFactory.getLogger(HQ.class);

    public HQ(String name, int tPort) {
        this.name = name;
        this.hostname = name;
        this.buffer = new byte[BUFFER_SIZE];
        this.clients = new HashMap<>();
        this.history = new HashMap<>();
        this.statusClients = new HashMap<>();
        this.tPort = tPort;
        this.subscriberId = UUID.randomUUID().toString();
        this.allStatus = "";

        // UDP
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

        // RPC Thrift
        try {
            externalClientThriftImpl = new ExternalClientThriftImpl(this);
            processor = new ExternalClientThriftService.Processor(externalClientThriftImpl);

            Runnable tServer = () -> createThriftServer(processor);
            Thread thriftServerThread = new Thread(tServer);
            thriftServerThread.start();
        } catch (Exception e) {
            LOGGER.error("Failed to create Thrift-Server...{}", e.getMessage());
        }

        try {
            hqThriftImpl = new HqThriftServiceImpl(this);
            processorHq = new HqThriftService.Processor(hqThriftImpl);

            Runnable tServer = () -> createThriftServer(processorHq);
            Thread thriftServerThread = new Thread(tServer);
            thriftServerThread.start();
        } catch (Exception e) {
            LOGGER.error("Failed to create Thrift-Server...{}", e.getMessage());
        }

        // MQTT
        try {
            subscriber = new MqttClient("tcp://mqtt.eclipseprojects.io:1883", subscriberId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            subscriber.connect(options);
            LOGGER.info("Mqtt connection to server established...");
        } catch (MqttException e) {
            LOGGER.error("Failed to create Mqtt-Client...{}", e.getMessage());
        }
    }

    public void run() {
        // UDP
        if (UDP_TRANSFER) {
            while (true) {
                DatagramPacket udpPacket = new DatagramPacket(buffer, BUFFER_SIZE);
                try {
                    udpSocket.receive(udpPacket);
                    byte[] payload = Arrays.copyOfRange(udpPacket.getData(), 0, udpPacket.getLength());
                    updateClients(payload);
                    printUdpPacket(udpPacket);
                } catch (IOException e) {
                    LOGGER.error("Error receiving packet...{}\n", e.getMessage());
                }
            }
        // MQTT
        } else {
            try {
                String topics = this.hostname;
                CountDownLatch receivedSignal = new CountDownLatch(10);
            subscriber.subscribe(topics, (topic, msg) -> {
                    byte[] payload = msg.getPayload();
                    updateClients(payload);
                    printMqttData(payload);
                    receivedSignal.countDown();
                });
                receivedSignal.await(15, TimeUnit.SECONDS);
            } catch (MqttException e) {
                LOGGER.error("Error subscribing...{}\n", e.getMessage());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printUdpPacket(DatagramPacket udpPacket) {
        InetAddress address = udpPacket.getAddress();
        int port = udpPacket.getPort();
        String payload = new String(udpPacket.getData(), 0, udpPacket.getLength());

        LOGGER.info("Received UDP packet from " + address + ":" + port + payload);
    }

    private void printMqttData(byte[] payload) {
        String jsonString = new JSONObject(new String(payload)).toString();

        LOGGER.info("Received Mqtt data: " + jsonString);
    }

    private void updateClients(byte[] payload) {
        JSONObject jsonObject = new JSONObject(new String(payload));

        int id = jsonObject.getInt("id");
        ClientType type = ClientType.valueOf(jsonObject.getString("type"));
        String name = jsonObject.getString("name");
        int power = jsonObject.getInt("power");
        boolean shutdownStatus = jsonObject.getBoolean("shutdown-status");

        // add to clients
        Client client = new Client(id, type, name, power, shutdownStatus);
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

        // update status of clients
        if (statusClients.containsKey(id)) {
            statusClients.replace(id, shutdownStatus);
        } else {
            statusClients.put(id, shutdownStatus);
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
                LOGGER.info("Power on/off performed successfully...");
            } catch (TException e) {
                LOGGER.error("Error performing shut down...{}\n", e.getMessage());
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }
    }

    public void performPowerChange(String hostname, int port, int amount) {
        try {
            TTransport transport;

            transport = new TSocket(hostname, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            ClientThriftService.Client client = new ClientThriftService.Client(protocol);

            try {
                client.changePower(amount);
                LOGGER.info("Change power performed successfully...");
            } catch (TException e) {
                LOGGER.error("Error changing power...{}\n", e.getMessage());
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }
    }

    public String performGetAllInfo(String hostname, int port, int index) {
        String result = "";
        try {
            TTransport transport;

            transport = new TSocket(hostname, port);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            HqThriftService.Client client = new HqThriftService.Client(protocol);

            try {
                result = client.getOtherHqStatus(index);
                LOGGER.info(" Get info from " + hostname + "...");
            } catch (TException e) {
                LOGGER.error("Error getting info...{}\n", e.getMessage());
                e.printStackTrace();
            }

            transport.close();
        } catch (TException e) {
            LOGGER.error("Error creating TSocket...{}\n", e.getMessage());
        }

        return result;
    }

    public void createThriftServer(ExternalClientThriftService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(tPort);
            TServer tServer = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            LOGGER.info("Starting thrift-server... Listening on port " + tPort + "...");
            tServer.serve();
        } catch (TTransportException e) {
            LOGGER.error("Failed to create TTransport...\n{}", e.getMessage());
        }
    }

    public void createThriftServer(HqThriftService.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(tPort-100);
            TServer tServer = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
            LOGGER.info("Starting thrift-server... Listening on port " + (tPort-100) + "...");
            tServer.serve();
        } catch (TTransportException e) {
            LOGGER.error("Failed to create TTransport...\n{}", e.getMessage());
        }
    }

    public String getOwnStatus(int index, int DATA_SETS) {
        StringBuilder result = new StringBuilder();
        // history
        Map<Integer, List<Integer>> history = getHistory();

        for (Map.Entry<Integer, List<Integer>> entry : history.entrySet()) {
            result.append("Client ").append(entry.getKey()).append(":\n");
            List<Integer> powerHistory = entry.getValue();
            int startIndex = index * DATA_SETS;

            if (startIndex > powerHistory.size()) {
                startIndex--;
            }

            for (int i = startIndex; i < powerHistory.size(); i++) {
                result.append(powerHistory.get(i)).append(", ");
            }

            result.append("\n");
        }

        result.append("Client status: \n");

        // client status
        Map<Integer, Boolean> statusClients = getStatusClients();
        for (Map.Entry<Integer, Boolean> entry : statusClients.entrySet()) {
            result.append("Client ").append(entry.getKey()).append(":\n");
            boolean value = entry.getValue();
            if (value) {
                result.append("down").append(", ");
            } else {
                result.append("up").append(", ");
            }
            result.append("\n");
        }

        return result.toString();
    }

    public Map<Integer, Client> getClients() {
        return clients;
    }

    public Map<Integer, List<Integer>> getHistory() {
        return history;
    }

    public Map<Integer, Boolean> getStatusClients() {
        return statusClients;
    }

    public String getAllStatus() {
        return allStatus;
    }

    public void setAllStatus(String allStatus) {
        this.allStatus = allStatus;
    }
}
