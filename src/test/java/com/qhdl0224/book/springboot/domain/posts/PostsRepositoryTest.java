package com.qhdl0224.book.springboot.domain.posts;


import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup(){
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_및_불러오기(){
        String title = "title";
        String content = "content";
        Posts posts = postsRepository.save(Posts.builder().title(title).content(content).author("author").build());

        List<Posts> all = postsRepository.findAll();
        assertThat(posts.getId()).isEqualTo(all.get(0).getId());
        assertThat(posts.getTitle()).isEqualTo(all.get(0).getTitle());
        assertThat(posts.getContent()).isEqualTo(all.get(0).getContent());
    }
    @Test
    public void 게시글_삭제(){
        Posts posts = postsRepository.save(Posts.builder().title("title").content("content").author("author").build());
        List<Posts> all = postsRepository.findAll();
        assertThat(all.size()).isGreaterThan(0);

        postsRepository.delete(posts);
        List<Posts> all2 = postsRepository.findAll();
        assertThat(all2.size()).isEqualTo(0);
    }
}
