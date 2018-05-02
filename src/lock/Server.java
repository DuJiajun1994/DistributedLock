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
    private HashMap<String, String> locks;

    Server() {
        this(8080, 8081);
    }

    Server(int ptc, int pts) {
        portToClient = ptc;
        portToServer = pts;
        locks = new HashMap<>();
    }

    public void start() {
        new Thread() {
            @Override
            public void run() {
                startServer(true);
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                startServer(false);
            }
        }.start();
    }

    private void startServer(boolean toClient) {
        int port = toClient ? portToClient: portToServer;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while(true){
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    if(toClient) {
                        dealClientRequest(socket);
                    } else {
                        dealServerRequest(socket);
                    }
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
        } finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract void dealClientRequest(Socket socket) throws IOException;

    protected abstract void dealServerRequest(Socket socket) throws IOException;

    protected synchronized boolean dealLock(Message message) {
        boolean result = false;
        String operation = message.operation;
        String clientId = message.clientId;
        String key = message.key;
        switch (operation) {
            case "lock": result = lock(clientId, key); break;
            case "unlock": result = unlock(clientId, key); break;
            case "ownLock": result = ownLock(clientId, key); break;
        }
        return result;
    }

    private boolean lock(String clientId, String key) {
        if(locks.containsKey(key)) return false;
        locks.put(key, clientId);
        return true;
    }

    private boolean unlock(String clientId, String key) {
        if(!ownLock(clientId, key)) return false;
        locks.remove(key);
        return true;
    }

    private boolean ownLock(String clientId, String key) {
        String ownerId = locks.get(key);
        return ownerId != null && ownerId.equals(clientId);
    }
}
