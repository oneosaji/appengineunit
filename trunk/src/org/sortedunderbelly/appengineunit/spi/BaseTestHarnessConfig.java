package org.sortedunderbelly.appengineunit.spi;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

/**
 * @author Max Ross <maxr@google.com>
 */
public abstract class BaseTestHarnessConfig implements TestHarnessConfig {

  public Queue getQueue(long runId) {
    return QueueFactory.getDefaultQueue();
  }

  public String getBaseURL() {
    return "/testharness/";
  }

  public boolean separateNamespacePerTest() {
    return false;
  }
}
