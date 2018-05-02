package lock;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
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
        boolean result = false;
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
            result = Boolean.getBoolean(response);
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
        return result;
    }

    public static void main(String [] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The IP address of the server to connect:");
        String address = scanner.nextLine();
        Client client = new Client(address);
        System.out.println("Lock command:");
        while(true) {
            String command = scanner.nextLine();
            if(command.equals("stop")) break;
            String [] a = command.split(" ");
            String operation = a[0];
            String key = a[1];
            boolean result = false;
            switch (operation) {
                case "lock": result = client.lock(key); break;
                case "unlock": result = client.unlock(key); break;
                case "ownLock": result = client.ownLock(key); break;
            }
            String response = result ? "success" : "fail";
            System.out.println(response);
        }
    }
}
