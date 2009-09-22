package com.sortedunderbelly.appengineunit.spi;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public interface TestRun {

  Iterable<String> getTestIds(long runId);
}
