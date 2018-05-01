package lock;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public abstract class Server {
    protected int portToClient;
    protected int portToServer;
    /**
     * key: lock name
     * value: client ID
     */
    protected HashMap<String, String> locks;

    Server() {
        this(8080, 8081);
    }

    Server(int ptc, int pts) {
        portToClient = ptc;
        portToServer = pts;
        locks = new HashMap<>();
    }

    public void run() {
        startServer(true);
        startServer(false);
    }

    private void startServer(boolean toClient) {
        try {
            int port = toClient ? portToClient: portToServer;
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is started");
            while(true){
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    String request = reader.readLine();
                    System.out.println("Request: " + request);
                    String response;
                    if(toClient) {
                        response = dealClientRequest(request);
                    } else {
                        response = dealServerRequest(request);
                    }
                    System.out.println("Response: " + response);
                    writer.write(response);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String dealClientRequest(String request);

    protected abstract String dealServerRequest(String request);

    protected boolean ownLock(String clientId, String key) {
        String ownerId = locks.get(key);
        return ownerId != null && ownerId.equals(clientId);
    }
}
