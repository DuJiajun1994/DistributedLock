package lock;
import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class Client {
    private String id;
    private BufferedReader reader;
    private BufferedWriter writer;

    Client(String address, int port) throws IOException {
        id = getId();
        connect(address, port);
    }

    public boolean lock(String key) throws IOException {
        return request("lock", key);
    }

    public boolean unlock(String key) throws IOException {
        return request("unlock", key);
    }

    public boolean ownLock(String key) throws IOException {
        return request("ownLock", key);
    }

    private String getId() {
        return UUID.randomUUID().toString();
    }

    private void connect(String address, int port) throws IOException {
        Socket socket = new Socket(address, port);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        writer = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    private boolean request(String operation, String key) throws IOException {
        Message message = new Message(operation, id, key);
        writer.write(message.toString());
        String response = reader.readLine();
        return Boolean.getBoolean(response);
    }

    public static void main(String [] args) {

    }
}
