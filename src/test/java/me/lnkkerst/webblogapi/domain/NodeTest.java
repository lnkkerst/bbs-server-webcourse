package me.lnkkerst.webblogapi.domain;

import static me.lnkkerst.webblogapi.domain.NodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import me.lnkkerst.webblogapi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NodeTest {

  @Test
  void equalsVerifier() throws Exception {
    TestUtil.equalsVerifier(Node.class);
    Node node1 = getNodeSample1();
    Node node2 = new Node();
    assertThat(node1).isNotEqualTo(node2);

    node2.setId(node1.getId());
    assertThat(node1).isEqualTo(node2);

    node2 = getNodeSample2();
    assertThat(node1).isNotEqualTo(node2);
  }
}
