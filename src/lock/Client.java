package lock;
import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class Client {
    private String serverAddress;
    private int serverPort;
    private String id;

    Client(String address) {
        this(address, 8080);
    }

    Client(String address, int port) {
        serverAddress = address;
        serverPort = port;
        id = UUID.randomUUID().toString();
    }

    public boolean lock(String key) {
        return request("lock", key);
    }

    public boolean unlock(String key) {
        return request("unlock", key);
    }

    public boolean ownLock(String key) {
        return request("ownLock", key);
    }

    private boolean request(String operation, String key) {
        boolean success = false;
        Socket socket = null;
        try {
            socket = new Socket(serverAddress, serverPort);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            Message message = new Message(operation, id, key);
            writer.write(message.toString());
            String response = reader.readLine();
            success = Boolean.getBoolean(response);
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
        return success;
    }

    public static void main(String [] args) {

    }
}
