package org.sortedunderbelly.appengineunit.spi;

/**
 * @author Max Ross <maxr@google.com>
 */
public interface TestRunListener {
  void onCompletion(String statusURL, long runId);
}
