package me.lnkkerst.webblogapi.service.mapper;

import org.junit.jupiter.api.BeforeEach;

class FavoriteMapperTest {

  private FavoriteMapper favoriteMapper;

  @BeforeEach
  public void setUp() {
    favoriteMapper = new FavoriteMapperImpl();
  }
}
