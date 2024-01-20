package me.lnkkerst.webblogapi.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NodeTestSamples {

  private static final Random random = new Random();
  private static final AtomicLong longCount =
      new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  public static Node getNodeSample1() {
    return new Node().id(1L).name("name1");
  }

  public static Node getNodeSample2() {
    return new Node().id(2L).name("name2");
  }

  public static Node getNodeRandomSampleGenerator() {
    return new Node().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
  }
}
