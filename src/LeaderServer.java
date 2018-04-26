/**
 * Created by M_D_Luffy on 2018/4/26.
 */
public class LeaderServer extends Server {
    @Override
    public boolean lock(String key, String clientId) {
        if(locks.containsKey(key)) return false;
        locks.put(key, clientId);
        return true;
    }

    @Override
    public boolean unlock(String key, String clientId) {
        String ownerId = locks.get(key);
        if(ownerId == null || !ownerId.equals(clientId)) return false;
        locks.remove(key);
        return true;
    }
}
