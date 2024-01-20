package me.lnkkerst.webblogapi.domain;

import static me.lnkkerst.webblogapi.domain.NodeTestSamples.*;
import static me.lnkkerst.webblogapi.domain.PostTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import me.lnkkerst.webblogapi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Post.class);
    Post post1 = getPostSample1();
    Post post2 = new Post();
    assertThat(post1).isNotEqualTo(post2);

    post2.setId(post1.getId());
    assertThat(post1).isEqualTo(post2);

    post2 = getPostSample2();
    assertThat(post1).isNotEqualTo(post2);
  }

  @Test
  void nodeTest() throws Exception {
    Post post = getPostRandomSampleGenerator();
    Node nodeBack = getNodeRandomSampleGenerator();

    post.setNode(nodeBack);
    assertThat(post.getNode()).isEqualTo(nodeBack);

    post.node(null);
    assertThat(post.getNode()).isNull();
  }
}
