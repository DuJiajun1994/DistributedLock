import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public abstract class Server {
    /**
     * key: lock name
     * value: client ID
     */
    protected HashMap<String, String> locks;

    Server() {
        locks = new HashMap<>();
    }

    public void run() {
        int port = 10086;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is started");
            while(true){
                Socket socket = serverSocket.accept();
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                String line = reader.readLine();
                System.out.println("Get request: " + line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean lock(String key, String clientId);

    public abstract boolean unlock(String key, String clientId);

    public boolean ownLock(String key, String clientId) {
        String ownerId = locks.get(key);
        return ownerId != null && ownerId.equals(clientId);
    }
}
