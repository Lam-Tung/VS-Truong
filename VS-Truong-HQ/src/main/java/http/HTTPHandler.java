package http;

import clientclasses.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class HTTPHandler implements Runnable {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Map<Integer, Client> clients;
    private Map<Integer, List<Integer>> history;

    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPHandler.class);

    public HTTPHandler(Socket clientSocket, Map<Integer, Client> clients, Map<Integer, List<Integer>> history) {
        this.clientSocket = clientSocket;
        this.clients = clients;
        this.history = history;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            boolean isRestCall = false;
            /*
            GET /rest-api/history HTTP/1.1
            User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:89.0) Gecko/20100101 Firefox/89.0
            */
            String method = null;
            String uri = null;
            String currentLine;
            int lineCounter = 0;
            while((currentLine = in.readLine()) != null) {
                //LOGGER.info(currentLine + "\n");
                if(lineCounter == 0) {
                    String[] params = currentLine.split(" ");
                    method = params[0];
                    uri = params[1];

                    if(uri.startsWith("/rest-api/")) {
                        isRestCall = true;
                    }
                }

                if(currentLine.startsWith("User-Agent:")) {
                    LOGGER.info("Received request from " + currentLine + "...\n");

                }

                if(currentLine.isEmpty()) {
                    break;
                }

                lineCounter++;
            }

            if(method.equals("GET")) {
                if (!isRestCall) {
                /*
                HTTP/1.1 200 OK
                Content-Length: 88
                Content-Type: text/html
                Connection: Closed

                <html>
                    <body>
                        <div>This is HQ!</div>
                    </body>
                </html>
                */

                    String response =
                            "<html>" +
                                "<body>" +
                                    "<div>This is HQ!</div>" +
                                "</body>" +
                            "</html>"
                            ;

                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Length: " + response.length());
                    out.println("Content-Type: text/html");
                    out.println("Connection: Closed");
                    out.println();
                    sendResponse(response);
                } else {
                    processRestCall(uri);
                }
            } else {
                sendBadRequestResponse("Cannot handle HTTP-Request...");
            }
        } catch (IOException e) {
            LOGGER.error("Cannot handle HTTP-Request...{}\n", e.getMessage());
        }

    }

    private void sendResponse(String response) {
        try {
            out.println(response);
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.error("Failed toi close clientSocket...{}\n", e.getMessage());
        }
    }

    private void sendBadRequestResponse(String message) {
        String response =
                "<html>" +
                    "<body>" +
                        "<div>" + message + "</div>" +
                    "</body>" +
                "</html>"
                ;

        out.println("HTTP/1.1 400 Bad Request");
        out.println("Content-Length: " + response.length());
        out.println("Content-Type: text/html");
        out.println("Connection: Closed");
        out.println();
        sendResponse(response);
    }

    private void processRestCall(String uri) {
        // /rest-api/clients
        String[] uriParts = uri.split("/");
        if (uriParts.length == 3) {
            String endpoint = uriParts[2];
            String response = getRestResponse(endpoint);

            if (response == null) {
                sendBadRequestResponse("Cannot handle rest call...");
                return;
            }

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Length: " + response.length());
            out.println("Content-Type: text/html");
            out.println("Connection: Closed");
            out.println();
            sendResponse(response);
        } else {
            sendBadRequestResponse("Cannot handle rest call...");
        }
    }

    private String getRestResponse(String endpoint) {
        String response = null;
        switch (endpoint) {
            case "history": {
                String output = "History: \n";
                for (Map.Entry<Integer, List<Integer>> entry : history.entrySet()) {
                    output +=
                            "<div>" +
                                    "ID: " + entry.getKey() +
                                    "</div>"
                    ;
                    output += "<div>";
                    List<Integer> powerHistory = entry.getValue();
                    for (int i = 0; i < powerHistory.size(); i++) {
                        if (i == (powerHistory.size() - 1)) {
                            output += powerHistory.get(i);
                        } else {
                            output += powerHistory.get(i) + ", ";
                        }
                    }
                    output += "</div>";
                }

                response =
                        "<html>" +
                                "<body>" +
                                "<div>" + output + "</div>" +
                                "</body>" +
                                "</html>"
                ;
                break;
            }
            case "clients": {
                String output = "Clients: \n";
                for (Map.Entry<Integer, Client> entry : clients.entrySet()) {
                    output +=
                            "<div>" +
                                    "ID: " + entry.getKey() + " / " +
                                    "TYPE: " + entry.getValue().getType() + " / " +
                                    "NAME: " + entry.getValue().getName() + " / " +
                                    "POWER: " + entry.getValue().getPower() + " / " +
                                    "</div>"
                    ;
                }

                response =
                        "<html>" +
                                "<body>" +
                                "<div>" + output + "</div>" +
                                "</body>" +
                                "</html>"
                ;
                break;
            }
        }
        return response;
    }
}
