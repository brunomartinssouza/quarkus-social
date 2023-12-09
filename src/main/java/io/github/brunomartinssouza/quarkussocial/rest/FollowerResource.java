package io.github.brunomartinssouza.quarkussocial.rest;

import io.github.brunomartinssouza.quarkussocial.domain.model.Follower;
import io.github.brunomartinssouza.quarkussocial.domain.repository.FollowerRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.UserRepository;
import io.github.brunomartinssouza.quarkussocial.domain.services.FollowerService;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowerRequest;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowerResponse;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowersPerUserResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    @Inject
    FollowerService followerService;

    @PUT
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest request){
        return followerService.followUser(userId, request);
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId){
        return followerService.listFollowers(userId);
    }

    @DELETE
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId){
        return followerService.unfollowUser(userId, followerId);
    }


}
