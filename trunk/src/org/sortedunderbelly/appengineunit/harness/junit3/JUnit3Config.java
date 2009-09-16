package org.sortedunderbelly.appengineunit.harness.junit3;

import org.sortedunderbelly.appengineunit.spi.BaseTestHarnessConfig;
import org.sortedunderbelly.appengineunit.spi.TestHarness;

/**
 * Base config for JUnit 3.
 *
 * @author Max Ross <maxr@google.com>
 */
public abstract class JUnit3Config extends BaseTestHarnessConfig {

  public TestHarness getTestHarness() {
    return new JUnit3TestHarness();
  }
}
