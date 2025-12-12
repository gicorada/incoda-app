package com.gicorada;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws/queue")
public class QueueWebSocket {
    public static Set<Session> sessions = ConcurrentHashMap.newKeySet();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    public static void notifyUser(String userId) {
        sessions.forEach(s -> {
            try {
                s.getBasicRemote().sendText("{\"type\":\"called\",\"id\":\"" + userId + "\"}");
            } catch (IOException e) {e.printStackTrace();}
        });
    }
}
