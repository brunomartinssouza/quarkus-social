package io.github.brunomartinssouza.quarkussocial.domain.services;

import io.github.brunomartinssouza.quarkussocial.domain.model.Follower;
import io.github.brunomartinssouza.quarkussocial.domain.repository.FollowerRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.UserRepository;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowerRequest;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowerResponse;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@ApplicationScoped
public class FollowerService {

    @Inject
    FollowerRepository followerRepository;

    @Inject
    UserRepository userRepository;

    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){
        var user = userRepository.findById(userId);

        if (userId.equals(request.getFollowerId())){
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        if(null == user){
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());

        boolean follows = followerRepository.follows(follower, user);

        if(!follows){
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);
            followerRepository.persist(entity);
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    public Response listFollowers(@PathParam("userId") Long userId){

        var user = userRepository.findById(userId);
        if(null == user){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        var list = followerRepository.findByUser(userId);
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowerCount(list.size());
        var followerList = list.stream().map(FollowerResponse::new).collect(Collectors.toList());
        responseObject.setContent(followerList);
        return Response.ok(responseObject).build();
    }

    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){
        var user = userRepository.findById(userId);
        var follower = userRepository.findById(followerId);
        if(null == user || null == follower){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        followerRepository.deleteByFollowerAdUser(followerId, userId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
