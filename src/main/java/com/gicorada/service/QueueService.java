package com.gicorada.service;

import com.gicorada.QueueEntry;
import com.gicorada.QueuePosition;
import com.gicorada.QueueWebSocket;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service per la gestione della coda
 */
@ApplicationScoped
public class QueueService {
    private final HashMap<String, List<QueueEntry>> queue = new HashMap<>();
    private final HashMap<String, QueueEntry> lastCalled = new HashMap<>();

    /**
     * Aggiunge un Entry alla coda
     * @param tenant Tenant dell'utente
     * @param name Nome dell'utente a cui appartiene l'entry
     * @return Posizione dell'utente nella coda incapsulata in un QueuePosition
     */
    public QueuePosition join(String tenant, String name) {
        var entry = new QueueEntry(
            UUID.randomUUID().toString(),
            name,
            System.currentTimeMillis()
        );

        queue.computeIfAbsent(tenant, k -> new CopyOnWriteArrayList<>());
        queue.get(tenant).add(entry);

        return position(tenant, entry.id());
    }

    /**
     * Rimuove un Entry dalla coda
     * @param tenant Tenant dell'utente
     * @param id Id dell'entry da rimuovere
     * @return True se l'elemento era presente nella coda, altrimenti False
     */
    public boolean leave(String tenant, String id) {
        QueueWebSocket.notifyUserLeft(tenant);
        return queue.get(tenant).removeIf(e -> e.id().equals(id));
    }

    /**
     * Restituisce la posizione dell'Entry nella coda
     * @param tenant Tenant dell'utente
     * @param id Id dell'Entry da cercare
     * @return Posizione dell'Entry nella coda incapsulata in un QueuePosition
     */
    public QueuePosition position(String tenant, String id) {
        if(lastCalled.get(tenant) != null && lastCalled.get(tenant).id().equals(id)) {
            return new QueuePosition(id, 0);
        }

        if(queue.containsKey(tenant) && queue.get(tenant).isEmpty()) {
            return new QueuePosition(id, -1);
        }

        int index = 0;
        if(queue.containsKey(tenant)) {
            for (QueueEntry entry : queue.get(tenant)) {
                if (entry.id().equals(id)) {
                    return new QueuePosition(id, index+1);
                }
                index++;
            }
        }
        return new QueuePosition(id, -1);
    }

    /**
     * Restituisce l'ultimo Entry chiamato
     * @param tenant Tenant dell'utente
     * @return Ultimo Entry chiamato
     */
    public QueueEntry lastCalled(String tenant) {
        return lastCalled.get(tenant);
    }

    /**
     * Verifica se ci sono altri Entry nella coda
     * @param tenant Tenant dell'utente
     * @return True se ci sono altri Entry nella coda, altrimenti False
     */
    public boolean hasNext(String tenant) {
        return !queue.get(tenant).isEmpty();
    }

    /**
     * Avanza nella coda e restituisce l'Entry successivo
     * @param tenant Tenant dell'utente
     * @return Entry successivo all'attuale
     */
    public QueueEntry next(String tenant) {
        if(queue.get(tenant).isEmpty()) {
            return null;
        }
        lastCalled.put(tenant, queue.get(tenant).removeFirst());
        QueueWebSocket.notifyUserCalled(tenant, lastCalled.get(tenant).id());
        return lastCalled.get(tenant);
    }

    /**
     * Restituisce la lista di Entry nella coda
     * @param tenant Tenant dell'utente
     * @return Tutti gli Entry nella coda
     */
    public List<QueueEntry> all(String tenant) {
        return queue.get(tenant);
    }
}
