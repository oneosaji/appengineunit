package com.sortedunderbelly.appengineunit.harness.junit3;

import com.sortedunderbelly.appengineunit.model.Status;
import com.sortedunderbelly.appengineunit.model.Test;
import com.sortedunderbelly.appengineunit.model.TestResult;
import com.sortedunderbelly.appengineunit.spi.IsolationMechanism;
import com.sortedunderbelly.appengineunit.spi.TestHarness;
import com.sortedunderbelly.appengineunit.spi.TestHarnessConfig;

import junit.framework.TestFailure;
import junit.framework.TestSuite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A JUnit3 {@link TestHarness}.  The id of each {@link Test} is a class name,
 * so all tests belonging to that class are run.
 *
 * @author Max Ross <maxr@google.com>
 */
public class JUnit3TestHarness implements TestHarness {

  public TestResult runTest(TestHarnessConfig config, Test test) {
    junit.framework.TestResult result = newJUnitTestResult(config, test);
    try {
      Class<?> cls = Class.forName(test.getId());
      TestSuite testSuite;
      try {
        Method m = cls.getMethod("suite");
        testSuite = (TestSuite) m.invoke(null);
      } catch (NoSuchMethodException nsme) {
        testSuite = new TestSuite(cls);
      }
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
    if (config.getIsolationMechanism() == IsolationMechanism.ONE_NAMESPACE_PER_TEST) {
      testResult.addListener(new NewNamespacePerTestListener(test));
    } else if (config.getIsolationMechanism() == IsolationMechanism.WIPE_STORAGE_AFTER_EACH_TEST){
      testResult.addListener(new DatastoreWipingTestListener());
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

  private static final String FIVE_SPACES = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

  private String testFailureToString(TestFailure failure) {
    return failure.exceptionMessage().replace("\n", "<br>") + "<br>" + FIVE_SPACES
           + failure.trace().replace("\n", "<br>" + FIVE_SPACES);
  }
}
