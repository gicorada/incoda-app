package com.gicorada;

/**
 * Entry della coda
 * @param id UUID assegnato all'Entry
 * @param name Nome del creatore dell'Entry
 * @param joinedAt Timestamp di creazione dell'Entry
 */
public record QueueEntry(String id, String name, long joinedAt) {
}
