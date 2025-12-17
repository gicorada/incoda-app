package com.gicorada.resource;

import com.gicorada.QueueEntry;
import com.gicorada.QueueException;
import com.gicorada.service.QueueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {

    @Inject
    QueueService service;

    @POST
    @Path("/{tenant}/next")
    public QueueEntry next(@PathParam("tenant") String tenant) {
        if(!service.hasNext(tenant)) {
            throw new QueueException("QueueEmpty", "Non ci sono più persone in coda", 400);
        }

        return service.next(tenant);
    }

    @GET
    @Path("/{tenant}/queue/")
    public List<QueueEntry> queue(@PathParam("tenant") String tenant) {
        return service.all(tenant);
    }
}
