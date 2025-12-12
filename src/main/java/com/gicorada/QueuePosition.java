package com.gicorada;

/**
 * QueuePosition
 * @param id Id dell'Entry
 * @param position Posizione dell'Entry nella coda. 0 se attualmente chiamato, -1 se non in coda
 */
public record QueuePosition(String id, int position) {
}
