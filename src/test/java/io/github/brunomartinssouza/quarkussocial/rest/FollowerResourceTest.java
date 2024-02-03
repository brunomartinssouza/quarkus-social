package io.github.brunomartinssouza.quarkussocial.rest;

import io.github.brunomartinssouza.quarkussocial.domain.model.Follower;
import io.github.brunomartinssouza.quarkussocial.domain.model.User;
import io.github.brunomartinssouza.quarkussocial.domain.repository.FollowerRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.UserRepository;
import io.github.brunomartinssouza.quarkussocial.rest.dto.FollowerRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;


    @BeforeEach
    @Transactional
    void setUp(){
        //usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //usuário que segue
        var follower = new User();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();

        //criando um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);

    }

    @Test
    @DisplayName("should return 409 when Follower id is equal to User id")
    public void sameUserAsFollowTest(){

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .body(body)
        .when()
            .put()
        .then()
            .statusCode(409)
            .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when User id doesn't exist")
    public void userNotFoundWhenTryingToFollowTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
            .body(body)
        .when()
            .put()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest() {

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .body(body)
        .when()
            .put()
        .then()
            .statusCode(204);
    }

    @Test
    @DisplayName("should return 404 on list user followers and User id doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {

        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return list user's followers")
    public void listFollowersTest() {

        var response =
                given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
                .when()
                    .get()
                .then()
                    .extract().response();

        var followerCount = response.jsonPath().get("followerCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(200, response.statusCode());
        assertEquals(1, followerCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("should return 404 on unfollow user and User id doesn't exist")
    public void userNotFoundWhenUnfollowingAUserTest() {

        var inexistentUserId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should Unfollow an user")
    public void unfollowUserTest() {

        given()
            .pathParam("userId", userId)
            .queryParam("followerId", followerId)
        .when()
            .delete()
        .then()
            .statusCode(204);
    }
}