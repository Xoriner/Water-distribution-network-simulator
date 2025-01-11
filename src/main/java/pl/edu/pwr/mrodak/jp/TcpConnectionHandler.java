package pl.edu.pwr.mrodak.jp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TCP connection handler for server and client
public class TcpConnectionHandler {
    private ServerSocket serverSocket;
    private ExecutorService executor;

    public TcpConnectionHandler() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void startServer(int port, RequestHandler requestHandler) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.submit(() -> handleClient(clientSocket, requestHandler));
                } catch (Exception ex) {
                    if (!serverSocket.isClosed()) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("Failed to start server: " + ex.getMessage());
        }
    }

    private void handleClient(Socket clientSocket, RequestHandler requestHandler) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine();
            String response = requestHandler.handleRequest(request);
            out.println(response);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String sendRequest(String host, int port, String request) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(request);
            return in.readLine();
        } catch (IOException e) {
            System.err.println("Error connecting to " + host + ":" + port + " - " + e.getMessage());
            return null;
        }
    }

    public void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executor != null) {
                executor.shutdownNow();
            }
            System.out.println("Server has been shut down.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public interface RequestHandler {
        String handleRequest(String request);
    }
}