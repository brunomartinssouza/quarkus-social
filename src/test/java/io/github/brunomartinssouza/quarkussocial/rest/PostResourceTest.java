package io.github.brunomartinssouza.quarkussocial.rest;

import io.github.brunomartinssouza.quarkussocial.domain.model.Follower;
import io.github.brunomartinssouza.quarkussocial.domain.model.Post;
import io.github.brunomartinssouza.quarkussocial.domain.model.User;
import io.github.brunomartinssouza.quarkussocial.domain.repository.FollowerRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.PostRepository;
import io.github.brunomartinssouza.quarkussocial.domain.repository.UserRepository;
import io.github.brunomartinssouza.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;


    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;
    Long inexistentUserId = 999l;

    @BeforeEach
    @Transactional
    public void steUP(){
        //usuário padrão dos testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //usuário que não segue
        var userNotFollower = new User();
        userNotFollower.setAge(33);
        userNotFollower.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //usuário que segue
        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("Terceiro");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

        //cirada a postagem para o usuário
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

    }


    @Test
    @DisplayName("should create a post for a user")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some test");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", userId)
        .when()
            .post()
        .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some test");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParam("userId", inexistentUserId)
        .when()
            .post()
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", inexistentUserId)
        .when()
            .get()
        .then()
            .statusCode(404);

    }

   @Test
    @DisplayName("should return 400 when followerId Header is not present")
    public void listPostFollowerHeaderNotSendTest(){

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
        .when()
            .get()
        .then()
            .statusCode(400)
            .body(Matchers.is("You forgot the header followerId"));

    }

    @Test
    @DisplayName("should return 404 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 999;

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .header("followerId",inexistentFollowerId)
        .when()
            .get()
        .then()
            .statusCode(404)
            .body(Matchers.is("Inexistent Follower"));

    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest(){

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .header("followerId",userNotFollowerId)
        .when()
            .get()
        .then()
            .statusCode(403)
            .body(Matchers.is("You can't see these posts"));

    }

    @Test
    @DisplayName("should return posts")
    public void listPostTest(){

        given()
            .contentType(ContentType.JSON)
            .pathParam("userId", userId)
            .header("followerId",userFollowerId)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }

}