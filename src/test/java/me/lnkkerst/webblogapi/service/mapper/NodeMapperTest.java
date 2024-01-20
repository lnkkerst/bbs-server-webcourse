package me.lnkkerst.webblogapi.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class NodeMapperTest {

  private NodeMapper nodeMapper;

  @BeforeEach
  public void setUp() {
    nodeMapper = new NodeMapperImpl();
  }
}
