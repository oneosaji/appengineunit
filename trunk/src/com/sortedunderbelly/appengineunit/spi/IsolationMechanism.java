package com.sortedunderbelly.appengineunit.spi;

/**
 * Mechanisms by which tests are isolated from one another.
 *
 * @author Max Ross <maxr@google.com>
 */
public enum IsolationMechanism {
  NONE,
  ONE_NAMESPACE_PER_TEST,
  WIPE_STORAGE_AFTER_EACH_TEST,
}
