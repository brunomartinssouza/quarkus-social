package io.github.brunomartinssouza.quarkussocial.domain.services;

import io.github.brunomartinssouza.quarkussocial.domain.model.Post;
import io.github.brunomartinssouza.quarkussocial.domain.model.User;
import io.github.brunomartinssouza.quarkussocial.domain.repository.FollowerRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.PostRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.UserRepository;
import io.github.brunomartinssouza.quarkussocial.rest.dto.CreatePostRequest;
import io.github.brunomartinssouza.quarkussocial.rest.dto.PostResponse;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@ApplicationScoped
public class PostService {

    @Inject
    UserRepository userRepository;

    @Inject
    PostRepository postRepository;

    @Inject
    FollowerRepository followerRepository;

    @Transactional
    public Response savePost(Long userId, CreatePostRequest request){
        User user = userRepository.findById(userId);

        if (user == null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);
        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    public Response listPost(Long userId, Long followerId){

        User user = userRepository.findById(userId);
        if (null == user){
            return Response.status(Response.Status.NOT_FOUND).entity("Inexistent user").build();
        }

        if (null == followerId){
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        User follower = userRepository.findById(followerId);

        if (null == follower){
            return Response.status(Response.Status.NOT_FOUND).entity("Inexistent Follower").build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if (!follows){
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }

        var query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending), user);
        var list = query.list();

        var postResponseList = list.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }

}
