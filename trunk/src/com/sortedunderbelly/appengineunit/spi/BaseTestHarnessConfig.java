package com.sortedunderbelly.appengineunit.spi;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public abstract class BaseTestHarnessConfig implements TestHarnessConfig {

  public Queue getQueue(long runId) {
    return QueueFactory.getDefaultQueue();
  }

  public String getBaseURL() {
    return "/testharness/";
  }

  public IsolationMechanism getIsolationMechanism() {
    return IsolationMechanism.NONE;
  }

  public TestRunListener getTestRunListener() {
    return new TestRunListener() {
      public void onCompletion(String statusURL, long runId) {
        // by default we do nothing
      }
    };
  }
}
