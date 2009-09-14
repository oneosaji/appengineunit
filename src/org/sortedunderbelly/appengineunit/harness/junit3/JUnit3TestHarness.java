package org.sortedunderbelly.appengineunit.harness.junit3;

import com.google.appengine.api.NamespaceManager;

import junit.framework.AssertionFailedError;
import junit.framework.TestFailure;
import junit.framework.TestListener;
import junit.framework.TestSuite;

import org.sortedunderbelly.appengineunit.model.Status;
import org.sortedunderbelly.appengineunit.model.Test;
import org.sortedunderbelly.appengineunit.model.TestResult;
import org.sortedunderbelly.appengineunit.spi.TestHarness;
import org.sortedunderbelly.appengineunit.spi.TestHarnessConfig;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Max Ross <maxr@google.com>
 */
public class JUnit3TestHarness implements TestHarness {

  public TestResult runTest(TestHarnessConfig config, Test test) {
    junit.framework.TestResult result = newJUnitTestResult(config, test);
    try {
      // TODO(maxr): Check for a suite() method.
      TestSuite testSuite = new TestSuite(Class.forName(test.getId()));
      testSuite.run(result);
      return translateResult(test.getRun().getId(), test.getId(), testSuite.countTestCases(), result);
    } catch (RuntimeException rte) {
      throw rte;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  protected junit.framework.TestResult newJUnitTestResult(TestHarnessConfig config, Test test) {
    junit.framework.TestResult testResult = new junit.framework.TestResult();
    if (config.separateNamespacePerTest()) {
      testResult.addListener(new NewNamespacePerTestListener(test));
    }
    return testResult;
  }

  private TestResult translateResult(long runId, String testId, int numTests, junit.framework.TestResult result) {
    Status status = Status.SUCCESS;
    List<String> failureData = new ArrayList<String>();
    if (result.errorCount() != 0) {
      status = Status.FAILURE;
      @SuppressWarnings("unchecked")
      Enumeration<TestFailure> errorEnum = result.errors();
      while (errorEnum.hasMoreElements()) {
        failureData.add(testFailureToString(errorEnum.nextElement()));
      }
    }
    if (result.failureCount() != 0) {
      status = Status.FAILURE;
      @SuppressWarnings("unchecked")
      Enumeration<TestFailure> failureEnum = result.failures();
      while (failureEnum.hasMoreElements()) {
        failureData.add(testFailureToString(failureEnum.nextElement()));
      }
    }
    return new TestResult(runId, testId, status, numTests, failureData);
  }

  private String testFailureToString(TestFailure failure) {
    return failure.exceptionMessage() + "<b>" + failure.trace();
  }

  private static final class NewNamespacePerTestListener implements TestListener {

    private final Test gaeTest;

    public NewNamespacePerTestListener(Test gaeTest) {
      this.gaeTest = gaeTest;
    }

    public void addError(junit.framework.Test test, Throwable t) { }

    public void addFailure(junit.framework.Test test, AssertionFailedError t) { }

    public void endTest(junit.framework.Test test) {
      NamespaceManager.reset();
    }

    public void startTest(junit.framework.Test test) {
      String namespace = String.format("%s_%s", gaeTest.getRun().getId(), test.toString());
      NamespaceManager.set(namespace);
    }
  }

}
