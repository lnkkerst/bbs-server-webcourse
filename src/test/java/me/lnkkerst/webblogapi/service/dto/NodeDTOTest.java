package me.lnkkerst.webblogapi.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.lnkkerst.webblogapi.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NodeDTOTest {

  @Test
  void dtoEqualsVerifier() throws Exception {
    TestUtil.equalsVerifier(NodeDTO.class);
    NodeDTO nodeDTO1 = new NodeDTO();
    nodeDTO1.setId(1L);
    NodeDTO nodeDTO2 = new NodeDTO();
    assertThat(nodeDTO1).isNotEqualTo(nodeDTO2);
    nodeDTO2.setId(nodeDTO1.getId());
    assertThat(nodeDTO1).isEqualTo(nodeDTO2);
    nodeDTO2.setId(2L);
    assertThat(nodeDTO1).isNotEqualTo(nodeDTO2);
    nodeDTO1.setId(null);
    assertThat(nodeDTO1).isNotEqualTo(nodeDTO2);
  }
}
