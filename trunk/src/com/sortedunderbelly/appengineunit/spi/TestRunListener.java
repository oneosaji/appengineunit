package com.sortedunderbelly.appengineunit.spi;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public interface TestRunListener {
  void onCompletion(String statusURL, long runId);
}
