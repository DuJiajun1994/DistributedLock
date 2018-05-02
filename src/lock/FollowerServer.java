package lock;
import java.io.*;
import java.net.Socket;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class FollowerServer extends Server {
    private String leaderAddress;

    FollowerServer(String address) {
        super();
        leaderAddress = address;
    }

    @Override
    protected void dealClientRequest(Socket socket) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String request = reader.readLine();
        Message message = new Message(request);
        boolean result;
        if(message.operation.equals("ownLock")) {
            result = ownLock(message.clientId, message.key);
        } else {
            result = queryLeader(request);
        }
        writer.write(String.valueOf(result));
    }

    private boolean queryLeader(String request) {
        boolean result = false;
        Socket socket = null;
        try {
            socket = new Socket(leaderAddress, portToServer);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(request);
            String response = reader.readLine();
            result = Boolean.getBoolean(response);
        } catch(IOException e) {
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

    @Override
    protected void dealServerRequest(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String response = reader.readLine();
        Message message = new Message(response);
        String operation = message.operation;
        String clientId = message.clientId;
        String key = message.key;
        switch (operation) {
            case "lock": locks.put(key, clientId); break;
            case "unlock": locks.remove(key); break;
        }
    }
}
