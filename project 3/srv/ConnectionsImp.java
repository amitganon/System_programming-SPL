package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImp<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connections = new ConcurrentHashMap<>();

    private static class ConnectionHolder{
        private static ConnectionsImp connectionsInstance = new ConnectionsImp();
    }

    public static ConnectionsImp getInstance() {
        return ConnectionHolder.connectionsInstance;
    }

    public void connect(int connectionId, ConnectionHandler con){
        if(!connections.containsKey(connectionId))
            connections.put(connectionId,con);
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(connections.containsKey(connectionId)) {
            connections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler<T> client : connections.values()){
            client.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId);
    }
}
