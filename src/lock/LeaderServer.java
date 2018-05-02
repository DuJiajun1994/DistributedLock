package lock;

import java.io.*;
import java.net.Socket;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class LeaderServer extends Server {
    private String [] followerAddress;

    LeaderServer(String [] address) {
        super();
        followerAddress = address;
    }

    @Override
    protected void dealClientRequest(Socket socket) throws IOException {
        dealRequest(socket);
    }

    @Override
    protected void dealServerRequest(Socket socket) throws IOException {
        dealRequest(socket);
    }

    private void dealRequest(Socket socket)  throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        String request = reader.readLine();
        System.out.println("Request: " + request);
        Message message = new Message(request);
        String operation = message.operation;
        String clientId = message.clientId;
        String key = message.key;
        boolean result = false;
        switch (operation) {
            case "lock": result = lock(clientId, key); break;
            case "unlock": result = unlock(clientId, key); break;
            case "ownLock": result = ownLock(clientId, key); break;
        }
        writer.write(String.valueOf(result));
        if(result && (operation.equals("lock") || operation.equals("unlock"))) {
            for(String address: followerAddress) {
                notifyFollowers(address, request);
            }
        }
    }

    private void notifyFollowers(String address, String message) {
        Socket socket = null;
        try {
            socket = new Socket(address, portToServer);
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(message);
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

    private boolean lock(String key, String clientId) {
        if(locks.containsKey(key)) return false;
        locks.put(key, clientId);
        return true;
    }

    private boolean unlock(String key, String clientId) {
        String ownerId = locks.get(key);
        if(ownerId == null || !ownerId.equals(clientId)) return false;
        locks.remove(key);
        return true;
    }
}
