package me.lnkkerst.webblogapi.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class PostMapperTest {

  private PostMapper postMapper;

  @BeforeEach
  public void setUp() {
    postMapper = new PostMapperImpl();
  }
}
