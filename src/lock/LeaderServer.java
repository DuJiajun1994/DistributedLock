package lock;

/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class LeaderServer extends Server {
    LeaderServer(int port) {
        super(port);
    }

    @Override
    protected String deal(String request) {
        boolean result = false;
        Message message = new Message(request);
        String operation = message.operation;
        String clientId = message.clientId;
        String key = message.key;
        switch (operation) {
            case "lock": result = lock(clientId, key); break;
            case "unlock": result = unlock(clientId, key); break;
            case "ownLock": result = ownLock(clientId, key); break;
        }
        return String.valueOf(result);
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
