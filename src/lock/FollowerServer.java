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
    protected String deal(String request) {
        Message message = new Message(request);
        if(message.operation.equals("ownLock")) {
            boolean own = ownLock(message.clientId, message.key);
            return String.valueOf(own);
        }
        String response = String.valueOf(false);
        try {
            Socket socket = new Socket(leaderAddress, portToClient);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(request);
            response = reader.readLine();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
