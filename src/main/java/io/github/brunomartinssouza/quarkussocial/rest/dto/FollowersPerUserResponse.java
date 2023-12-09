package io.github.brunomartinssouza.quarkussocial.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class FollowersPerUserResponse {

    private Integer followerCount;
    private List<FollowerResponse> content;

}
