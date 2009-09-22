package com.sortedunderbelly.appengineunit.spi;

import com.google.appengine.api.labs.taskqueue.Queue;

/**
 * @author Max Ross <maxr@google.com>
 */
public interface TestHarnessConfig {

  Queue getQueue(long runId);

  TestHarness getTestHarness();

  TestRun newTestRun();

  String getBaseURL();

  IsolationMechanism getIsolationMechanism();

  TestRunListener getTestRunListener();
}
