package lock;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class LeaderServer extends Server {
    private List<String> followerAddress;

    LeaderServer(List<String> address) {
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
        boolean result = dealLock(message);
        String response = String.valueOf(result);
        System.out.println("Response: " + response);
        writer.write(response + "\n");
        writer.flush();
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
            writer.write(message + "\n");
            writer.flush();
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

    public static void main(String [] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("The IP address of follower servers:");
        ArrayList<String> list = new ArrayList<>();
        while(true) {
            String address = scanner.nextLine();
            if(address.equals("stop")) break;
            list.add(address);
        }
        LeaderServer server = new LeaderServer(list);
        server.start();
    }
}
