package com.sortedunderbelly.appengineunit.harness.junit3;

import com.sortedunderbelly.appengineunit.spi.BaseTestHarnessConfig;
import com.sortedunderbelly.appengineunit.spi.TestHarness;

/**
 * Base config for JUnit 3.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public abstract class JUnit3Config extends BaseTestHarnessConfig {

  public TestHarness getTestHarness() {
    return new JUnit3TestHarness();
  }
}
