package com.sortedunderbelly.appengineunit.harness.junit3;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

/**
 * A base {@link TestListener} with no-op implementations of all methods.
 *
 * @author Max Ross <max.ross@gmail.com>
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