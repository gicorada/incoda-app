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
    // TODO è possibile intercettare called e left di altri tenant, da implementare divisione per subset

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    public static void notifyUserCalled(String tenant, String userId) {
        sessions.forEach(s -> {
            try {
                String announce = "{\"type\":\"called\",\"tenant\":\"" + tenant + "\",\"id\":\"" + userId + "\"}";
                s.getBasicRemote().sendText(announce);
            } catch (IOException e) {e.printStackTrace();}
        });
    }

    public static void notifyUserLeft(String tenant) {
        sessions.forEach(s -> {
            try {
                String announce = "{\"type\":\"left\",\"tenant\":\"" + tenant + "\"}";
                s.getBasicRemote().sendText(announce);
            } catch(IOException e) {e.printStackTrace();}
        });
    }
}
