package http;

import clientclasses.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HTTPServer implements Runnable {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Map<Integer, Client> clients;
    private Map<Integer, List<Integer>> history;

    private static final int HTTP_PORT = 8080;

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPServer.class);

    public HTTPServer(Map<Integer, Client> clients, Map<Integer, List<Integer>> history) {
        try {
            this.clients = clients;
            this.history = history;
            threadPool = Executors.newCachedThreadPool();
            serverSocket = new ServerSocket(HTTP_PORT);

            LOGGER.info("HTTP socket created... " + "Listening on port " + HTTP_PORT + "...");

        } catch (IOException e) {
            LOGGER.error("Failed to create HTTP socket...{}\n", e.getMessage());
            System.exit(1);
        }

    }

    @Override
    public void run() {
        Socket clientSocket = null;
        while (true)  {
            try {
                if ((clientSocket = serverSocket.accept()) == null) break;
            } catch (IOException e) {
                LOGGER.error("HTTP-error in start()...{}\n", e.getMessage());
            }

            assert clientSocket != null;
            LOGGER.info("Received connection from " + clientSocket.getRemoteSocketAddress().toString() + "...");

            HTTPHandler handler = new HTTPHandler(clientSocket, clients, history);
            threadPool.execute(handler);
        }
    }
}

