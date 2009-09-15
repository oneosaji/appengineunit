package org.sortedunderbelly.appengineunit.harness.junit3;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * @author Max Ross <maxr@google.com>
 */
public class BaseTestListener implements TestListener {

  public void addError(Test test, Throwable throwable) {
  }

  public void addFailure(Test test, AssertionFailedError assertionFailedError) {
  }

  public void endTest(Test test) {
  }

  public void startTest(Test test) {
  }
}