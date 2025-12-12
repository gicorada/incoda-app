package com.gicorada;

import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;

@Provider
public class QueueExceptionMapper implements ExceptionMapper<QueueException> {

    @Override
    public Response toResponse(QueueException e) {
        ErrorResponse err = new ErrorResponse();
        err.error = e.code;
        err.message = e.getMessage();

        return Response
            .status(e.status)
            .entity(err)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
