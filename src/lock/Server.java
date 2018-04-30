package lock;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public abstract class Server {
    private int port;
    /**
     * key: lock name
     * value: client ID
     */
    protected HashMap<String, String> locks;

    Server(int p) {
        port = p;
        locks = new HashMap<>();
    }

    public void run() {
        try {
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
                    String response = deal(request);
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

    protected abstract String deal(String request);

    protected boolean ownLock(String clientId, String key) {
        String ownerId = locks.get(key);
        return ownerId != null && ownerId.equals(clientId);
    }
}
