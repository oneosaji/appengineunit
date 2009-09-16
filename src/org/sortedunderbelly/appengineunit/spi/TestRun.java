package org.sortedunderbelly.appengineunit.spi;

/**
 * @author Max Ross <maxr@google.com>
 */
public interface TestRun {

  Iterable<String> getTestIds(long runId);
}
