package com.qhdl0224.book.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qhdl0224.book.springboot.domain.posts.Posts;
import com.qhdl0224.book.springboot.domain.posts.PostsRepository;
import com.qhdl0224.book.springboot.web.dto.PostsResponseDto;
import com.qhdl0224.book.springboot.web.dto.PostsSaveRequestDto;
import com.qhdl0224.book.springboot.web.dto.PostsUpdateRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles="USER")
    public void posts_등록() throws Exception{
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder().title(title).content(content).author("author").build();
        String url = "http://localhost:"+port+"/api/v1/posts";

//        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url,requestDto,Long.class);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    @WithMockUser(roles="USER")
    public void posts_업데이트() throws Exception{
        Posts savedPosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());
        Long updatedId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder().title(expectedTitle).content(expectedContent).build();
        String url = "http://localhost:"+port+"/api/v1/posts/"+updatedId;

//        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);
//        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,requestEntity,Long.class);
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }
    @Test
    @WithMockUser(roles="USER")
    public void posts_아이디로_조회() throws Exception{
        Posts savedPosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());
        Long searchId = savedPosts.getId();
        System.out.println(">>>>>>>>>>>>>>searchId = "+searchId);
        String url = "http://localhost:"+port+"/api/v1/posts/"+searchId;

        mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsBytes(PostsResponseDto.class)))
                .andExpect(status().isOk());
        //ResponseEntity<PostsResponseDto> responseEntity =  restTemplate.getForEntity(url+"/{id}",PostsResponseDto.class,searchId);
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody().getId()).isEqualTo(searchId);
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
    @Test
    public void posts_삭제() throws Exception{
        Posts savedPosts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());
        Long searchId = savedPosts.getId();
        String url = "http://localhost:"+port+"/api/v1/posts";

        restTemplate.delete(url,searchId);

    }

}
