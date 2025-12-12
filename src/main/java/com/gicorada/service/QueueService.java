package com.gicorada.service;

import com.gicorada.QueueEntry;
import com.gicorada.QueuePosition;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service per la gestione della coda
 */
@ApplicationScoped
public class QueueService {
    private final List<QueueEntry> queue = new CopyOnWriteArrayList<>();
    private QueueEntry lastCalled = null;

    /**
     * Aggiunge un Entry alla coda
     * @param name Nome dell'utente a cui appartiene l'entry
     * @return Posizione dell'utente nella coda incapsulata in un QueuePosition
     */
    public QueuePosition join(String name) {
        var entry = new QueueEntry(
            UUID.randomUUID().toString(),
            name,
            System.currentTimeMillis()
        );

        queue.add(entry);

        return position(entry.id());
    }

    /**
     * Rimuove un Entry dalla coda
     * @param id Id dell'entry da rimuovere
     * @return True se l'elemento era presente nella coda, altrimenti False
     */
    public boolean leave(String id) {
        return queue.removeIf(e -> e.id().equals(id));
    }

    /**
     * Restituisce la posizione dell'Entry nella coda
     * @param id Id dell'Entry da cercare
     * @return Posizione dell'Entry nella coda incapsulata in un QueuePosition
     */
    public QueuePosition position(String id) {
        if(lastCalled != null && lastCalled.id().equals(id)) {
            return new QueuePosition(id, 0);
        }

        if(queue.isEmpty()) {
            return new QueuePosition(id, -1);
        }

        int index = 0;
        for (QueueEntry entry : queue) {
            if (entry.id().equals(id)) {
                return new QueuePosition(id, index+1);
            }
            index++;
        }
        return new QueuePosition(id, -1);
    }

    /**
     * Restituisce l'ultimo Entry chiamato
     * @return Ultimo Entry chiamato
     */
    public QueueEntry lastCalled() {
        return lastCalled;
    }

    /**
     * Verifica se ci sono altri Entry nella coda
     * @return True se ci sono altri Entry nella coda, altrimenti False
     */
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    /**
     * Avanza nella coda e restituisce l'Entry successivo
     * @return Entry successivo all'attuale
     */
    public QueueEntry next() {
        if(queue.isEmpty()) {
            return null;
        }
        lastCalled = queue.removeFirst();
        return lastCalled;
    }

    /**
     * Restituisce la lista di Entry nella coda
     * @return Tutti gli Entry nella coda
     */
    public List<QueueEntry> all() {
        return queue;
    }
}
