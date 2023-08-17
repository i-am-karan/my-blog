package com.blog.api;


import com.blog.Exception.BlogException;
import com.blog.core.BlogPost;
import com.blog.db.BlogPostDAO;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.*;

@Produces("application/json")
@Consumes("application/json")
@Path("/blogs")
public class BlogPostResource {

    private final BlogPostDAO blogPostDAO;

    public BlogPostResource(BlogPostDAO blogPostDAO) {
        this.blogPostDAO = blogPostDAO;
    }

    @GET
    @UnitOfWork
    public List<BlogPost> getAllPosts() {
        return blogPostDAO.findAll();
    }

    public List<BlogPost> getBlogsByUser(String username) {
        return blogPostDAO.findAllByUsername(username);
    }

    public Response createBlogPost(String userName, BlogPost blogPost) {
        // Generate a unique ID for the blog post
        blogPost.setUsername(userName);
        // Add the blog post to the list
        blogPostDAO.create(blogPost);

        return Response.ok(blogPost).build();
    }

    public Response editBlogPost(String blogId,BlogPost updatedBlogPost) {
        // Find the blog post by ID

        Optional<BlogPost> blogPost =null;
        try{
            blogPost=blogPostDAO.findById(blogId);
           if(blogPost.isEmpty()){
//               System.out.println("hello karan");
               throw new BlogException("Blog with given id does not exist");
           }
            blogPost.get().setTitle(updatedBlogPost.getTitle());
            blogPost.get().setContent(updatedBlogPost.getContent());

            blogPostDAO.update(blogPost.get());

            return Response.ok(blogPost).build();
        }catch (Exception e){
            return Response.serverError().status(500, e.getMessage()).build();
        }

    }

}
