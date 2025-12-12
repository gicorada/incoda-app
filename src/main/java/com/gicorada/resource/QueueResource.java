package com.gicorada.resource;

import com.gicorada.JoinRequest;
import com.gicorada.QueueException;
import com.gicorada.QueuePosition;
import com.gicorada.service.QueueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/queue")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QueueResource {

    @Inject
    QueueService service;

    @POST
    @Path("/join")
    public QueuePosition join(JoinRequest request) {
        if(request.name() == null || request.name().isBlank()) {
            throw new QueueException("BadParams", "È necessario fornire il nome", 422);
        }
        return service.join(request.name());
    }
    
    @GET
    @Path("/{id}/position")
    public QueuePosition position(@PathParam("id") String id) {
        return service.position(id);
    }

    @DELETE
    @Path("/{id}/leave")
    public boolean leave(@PathParam("id") String id) {
        var pos = service.position(id);
        if(pos.position() == -1) {
            throw new QueueException("NotFound", "L'utente non è in coda", 404);
        }
        return service.leave(id);
    }

    @POST
    @Path("/{id}/finished")
    public void finished(@PathParam("id") String id) {
        if(!service.lastCalled().id().equals(id)) {
            throw new QueueException("BadParams", "L'utente non corrisponde all'ultimo utente chiamato", 403);
        }
        service.next();
    }
}
