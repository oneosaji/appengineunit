package com.sortedunderbelly.appengineunit.model;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public class TestStatus {

  private final long runId;
  private final String testId;

  public TestStatus(long runId, String testId) {
    this.runId = runId;
    this.testId = testId;
  }

  public long getRunId() {
    return runId;
  }

  public String getTestId() {
    return testId;
  }
}
