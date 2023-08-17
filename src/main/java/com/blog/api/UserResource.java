package com.blog.api;

import com.blog.Exception.BlogException;
import com.blog.Exception.BlogExceptionDTO;
import com.blog.auth.JwtService;
import com.blog.core.Auth;
import com.blog.core.BlogPost;
import com.blog.core.User;
import com.blog.db.UserDAO;
import com.blog.db.AuthDAO;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final UserDAO userDAO;
    private final JwtService jwtService;
    private final AuthDAO authDAO;
    private final BlogPostResource blogPostResource;

    public UserResource(UserDAO userDAO, JwtService jwtService, AuthDAO authDAO, BlogPostResource blogPostResource) {
        this.userDAO = userDAO;
        this.jwtService = jwtService;
        this.authDAO = authDAO;
        this.blogPostResource = blogPostResource;
    }

    @POST
    @Path("/register")
    @UnitOfWork
    public Response registerUser(@Valid User user) {
        // Check if the user already exists
        if (userDAO.findByUsername(user.getName()).isPresent()) {
            throw new BadRequestException("User already exists");
        }

        // Hash the user's password before storing it
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        // Create the user
        if(user.getRole()!=null)
        user.setRole(user.getRole());
        else user.setRole("user");
        User createdUser = userDAO.create(user);
        return Response.status(Response.Status.CREATED)
                .entity("User registered successfully")
                .build();
    }

    @POST
    @Path("/login")
    @UnitOfWork
    public Response loginUser(User user) {
        User existingUser = userDAO.findByUsername(user.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Verify the password
        if (!BCrypt.checkpw(user.getPassword(), existingUser.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        // Generate JWT
        String token = jwtService.generateToken(existingUser);
        Auth auth = new Auth(existingUser.getName(), token);
        authDAO.createOrUpdate(auth);
        return Response.ok()
                .entity(token)
                .build();
    }

    @GET
    @Path("/users")
    @UnitOfWork
    public Response getAllUsers(@NotNull @NotBlank @HeaderParam("Authorization") String token) {

        // Verify if the token matches the admin token in the database
        try {
            Optional<User> authenticatedUser = jwtService.verifyToken(token);
            if (authenticatedUser.isEmpty()) {
//                return Response.status(Response.Status.FORBIDDEN).build();
                throw new BlogException("Invalid token");
            }
            if (!authenticatedUser.get().isAdmin()) {
//                return Response.status(Response.Status.FORBIDDEN).build();
                throw new BlogException("Non-Admin users not allowed");
            }
            List<User> users = userDAO.findAll();

            return Response.ok(users).build();
        } catch (BlogException e) {
            return Response.serverError().status(e.getCode()).entity(new BlogExceptionDTO(e.getCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }

    }

    @GET
    @Path("/user/{username}/blogs")
    @UnitOfWork
    public Response getBlogByUsername(@PathParam("username") String username, @NotNull @NotBlank @HeaderParam("Authorization") String token) {

        try {
            Optional<User> authenticatedUser = jwtService.verifyToken(token);
            if (authenticatedUser.isEmpty()) {
                throw new BlogException("Invalid token for given user");
            }
            if(!authenticatedUser.get().getName().equals(username)){
                throw new BlogException("Request Username does not match with token");
            }
            List<BlogPost> blogposts = blogPostResource.getBlogsByUser(username);
            return Response.ok(blogposts).build();
        } catch (BlogException e) {
            return Response.serverError().status(e.getCode()).entity(new BlogExceptionDTO(e.getCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }

    }

    @POST
    @Path("/user/{username}")
    @UnitOfWork
    public Response CreateBlog(@PathParam("username") String username, @NotNull @NotBlank @HeaderParam("Authorization") String token, @Valid BlogPost blogPost) {

        try {
            Optional<User> authenticatedUser = jwtService.verifyToken(token);
            if (authenticatedUser.isEmpty()) {
                throw new BlogException("Invalid token for given user");
            }
            if(!authenticatedUser.get().getName().equals(username)){
                throw new BlogException("Request Username does not match with token");
            }
            Response response = blogPostResource.createBlogPost(username, blogPost);
            return response;
        } catch (BlogException e) {
            return Response.serverError().status(e.getCode()).entity(new BlogExceptionDTO(e.getCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }

    @PUT

    @Path("/user/{username}/{blogId}")
    @UnitOfWork
    public Response UpdateBlog(@NotNull @NotBlank @HeaderParam("Authorization") String token, @PathParam("username") String username, @PathParam("blogId") String blogId, @Valid BlogPost updatedblogPost) {
        try {
            Optional<User> authenticatedUser = jwtService.verifyToken(token);
            if (authenticatedUser.isEmpty()) {
                throw new BlogException("Invalid token for given user");
            }
            if(!authenticatedUser.get().getName().equals(username)){
                throw new BlogException("Request Username does not match with token");
            }
            Response response = blogPostResource.editBlogPost(blogId, updatedblogPost);
            if(response.getStatus()==500){
                throw  new BlogException("Invalid blogId");
            }
            return response;
        } catch (BlogException e) {
            return Response.serverError().status(e.getCode()).entity(new BlogExceptionDTO(e.getCode(), e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().status(500, e.getMessage()).build();
        }
    }
}
