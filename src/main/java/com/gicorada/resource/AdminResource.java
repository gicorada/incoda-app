package com.gicorada.resource;

import com.gicorada.QueueEntry;
import com.gicorada.QueueException;
import com.gicorada.service.QueueService;
import com.gicorada.QueueWebSocket;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    QueueService service;

    @POST
    @Path("/next")
    public QueueEntry next() {
        if(!service.hasNext()) {
            throw new QueueException("QueueEmpty", "Non ci sono più persone in coda", 400);
        }

        var next = service.next();
        QueueWebSocket.notifyUser(next.id());
        return next;
    }

    @GET
    @Path("/queue")
    public List<QueueEntry> queue() {
        return service.all();
    }
}
