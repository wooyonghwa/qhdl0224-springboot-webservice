package com.qhdl0224.book.springboot;

import com.qhdl0224.book.springboot.domain.posts.Posts;
import com.qhdl0224.book.springboot.domain.posts.PostsRepository;
import com.qhdl0224.book.springboot.web.dto.PostsResponseDto;
import com.qhdl0224.book.springboot.web.dto.PostsSaveRequestDto;
import com.qhdl0224.book.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void posts_등록() throws Exception{
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder().title(title).content(content).author("author").build();
        String url = "http://localhost:"+port+"/api/v1/posts";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url,requestDto,Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void posts_업데이트() throws Exception{
        Posts savedPosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());
        Long updatedId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder().title(expectedTitle).content(expectedContent).build();
        String url = "http://localhost:"+port+"/api/v1/posts/"+updatedId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,requestEntity,Long.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);

    }
    @Test
    public void posts_아이디로_조회() throws Exception{
        Posts savedPosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());
        Long searchId = savedPosts.getId();
        System.out.println(">>>>>>>>>>>>>>searchId = "+searchId);
        String url = "http://localhost:"+port+"/api/v1/posts";

        ResponseEntity<PostsResponseDto> responseEntity =  restTemplate.getForEntity(url+"/{id}",PostsResponseDto.class,searchId);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getId()).isEqualTo(searchId);
    }

    @Test
    public void baseTimeEntity_확인() throws Exception{
        LocalDateTime now = LocalDateTime.of(2020,7,4,0,0,0);
        postsRepository.save(Posts.builder().title("title").content("content").author("author").build());

        List<Posts> all = postsRepository.findAll();
        Posts posts = all.get(0);
        System.out.println(">>>>>>>>>>>>>>>>>> created Date = "+posts.getCreateDate()+", modified Date = "+posts.getModifiedDate());

        assertThat(posts.getCreateDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);
    }

}
