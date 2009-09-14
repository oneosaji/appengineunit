package org.sortedunderbelly.appengineunit.harness.junit3;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.sortedunderbelly.appengineunit.spi.TestRun;
import org.sortedunderbelly.appengineunit.spi.TestHarness;

import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;

/**
 * @author Max Ross <maxr@google.com>
 */
public class JUnit3TestRun implements TestRun {

  private final junit.framework.Test test;

  public JUnit3TestRun(junit.framework.Test test) {
    this.test = test;
  }

  public Iterable<String> getTestIds(long runId) {
    List<String> testIds = new ArrayList<String>();

    if (test instanceof TestSuite) {
      TestSuite suite = (TestSuite) test;
      @SuppressWarnings("unchecked")
      Enumeration<Test> tests = suite.tests();
      while (tests.hasMoreElements()) {
        testIds.add(tests.nextElement().toString());
      }
    } else {
      // probably wrong
      testIds.add(test.toString());
    }
    return testIds;
  }

  public Class<? extends TestHarness> getTestHarnessClass() {
    return JUnit3TestHarness.class;
  }
}
