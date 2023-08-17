package com.blog.Exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BlogExceptionMapper implements ExceptionMapper<BlogException> {
    @Override
    public Response toResponse(BlogException exception) {
        return Response.status(exception.getCode())
                .entity(exception.getBlogExceptionDTO().getMessage())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
