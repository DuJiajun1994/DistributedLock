import java.io.*;
import java.net.Socket;
import java.util.UUID;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class Client {
    private String id;
    private boolean isConnected;
    private int fd;
    private BufferedReader reader;
    private BufferedWriter writer;

    Client(String addr) {
        id = getId();
        isConnected = connect(addr);
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

    private String getId() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    private boolean connect(String address) {
        int port = 10086;
        try {
            Socket socket = new Socket(address, port);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            isConnected = true;
        } catch(IOException e) {
            e.printStackTrace();
            isConnected = false;
        }
        return isConnected;
    }

    private boolean request(String operation, String key) {
        boolean success = false;
        try {
            writer.write(operation + ":" + key);
            String line = reader.readLine();
            if(line.equals("true")) {
                success = true;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static void main(String [] args) {
        System.out.println(UUID.randomUUID().toString());
    }
}
