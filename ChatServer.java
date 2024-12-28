import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());
    private static Connection dbConnection;

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        connectToDatabase();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("New client connected: " + clientSocket);
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void connectToDatabase() {
        try {
            // Database connection setup

            String url = "jdbc:mysql://localhost:3306/ChatApp";
            String user = "root"; // Replace with your DB username
            String password = "gil@123"; // Replace with your DB password

            dbConnection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.exit(1);
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private String username;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Authentication
                while (!authenticate()) {
                    out.println("Invalid credentials. Please try again.");
                }

                out.println("Welcome to the chat, " + username + "!");
                broadcast(username + " has joined the chat.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("bye")) {
                        break;
                    }
                    storeMessageInDatabase(username, message);
                    broadcast(username + ": " + message);
                }
            } catch (IOException e) {
                System.err.println("Error with client: " + e.getMessage());
            } finally {
                try {
                    clientSockets.remove(socket);
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
                broadcast(username + " has left the chat.");
                System.out.println(username + " disconnected.");
            }
        }

        private boolean authenticate() {
            try {
                out.println("Enter username: ");
                String user = in.readLine();
                out.println("Enter password: ");
                String pass = in.readLine();

                String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
                PreparedStatement stmt = dbConnection.prepareStatement(query);
                stmt.setString(1, user);
                stmt.setString(2, pass);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    username = user;
                    return true;
                }
            } catch (IOException | SQLException e) {
                System.err.println("Authentication error: " + e.getMessage());
            }
            return false;
        }

        private void storeMessageInDatabase(String user, String message) {
            String query = "INSERT INTO Messages (username, message) VALUES (?, ?)";
            try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
                stmt.setString(1, user);
                stmt.setString(2, message);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving message: " + e.getMessage());
            }
        }

        private void broadcast(String message) {
            synchronized (clientSockets) {
                for (Socket client : clientSockets) {
                    try {
                        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                        writer.println(message);
                    } catch (IOException e) {
                        System.err.println("Error broadcasting message: " + e.getMessage());
                    }
                }
            }
        }
    }
}
