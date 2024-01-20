package me.lnkkerst.webblogapi.domain;

import static me.lnkkerst.webblogapi.domain.FavoriteTestSamples.*;
import static me.lnkkerst.webblogapi.domain.NodeTestSamples.*;
import static me.lnkkerst.webblogapi.domain.PostTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import me.lnkkerst.webblogapi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FavoriteTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Favorite.class);
    Favorite favorite1 = getFavoriteSample1();
    Favorite favorite2 = new Favorite();
    assertThat(favorite1).isNotEqualTo(favorite2);

    favorite2.setId(favorite1.getId());
    assertThat(favorite1).isEqualTo(favorite2);

    favorite2 = getFavoriteSample2();
    assertThat(favorite1).isNotEqualTo(favorite2);
  }

  @Test
  void nodeTest() throws Exception {
    Favorite favorite = getFavoriteRandomSampleGenerator();
    Node nodeBack = getNodeRandomSampleGenerator();

    favorite.setNode(nodeBack);
    assertThat(favorite.getNode()).isEqualTo(nodeBack);

    favorite.node(null);
    assertThat(favorite.getNode()).isNull();
  }

  @Test
  void postTest() throws Exception {
    Favorite favorite = getFavoriteRandomSampleGenerator();
    Post postBack = getPostRandomSampleGenerator();

    favorite.setPost(postBack);
    assertThat(favorite.getPost()).isEqualTo(postBack);

    favorite.post(null);
    assertThat(favorite.getPost()).isNull();
  }
}
