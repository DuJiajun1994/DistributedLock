package lock;

/**
 * Created by M_D_Luffy on 2018/4/30.
 */
public class Message {
    public String operation;
    public String clientId;
    public String key;

    Message(String op, String id, String k) {
        operation = op;
        clientId = id;
        key = k;
    }

    Message(String s) {
        String [] arr = s.split(":");
        operation = arr[0];
        clientId = arr[1];
        key = arr[2];
    }

    @Override
    public String toString() {
        return operation + ":" + clientId + ":" + key;
    }
}
