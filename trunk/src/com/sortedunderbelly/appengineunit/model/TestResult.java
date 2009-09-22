package com.sortedunderbelly.appengineunit.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Max Ross <maxr@google.com>
 */
public final class TestResult {

  private final long runId;
  private final String testId;
  private final Status status;
  private final int numTests;
  private final List<String> failureData = new ArrayList<String>();

  public TestResult(long runId, String testId, Status status, int numTests, List<String> failureData) {
    this.runId = runId;
    this.testId = testId;
    this.status = status;
    this.numTests = numTests;
    this.failureData.addAll(failureData);
  }

  public Status getStatus() {
    return status;
  }

  public List<String> getFailureData() {
    return failureData;
  }

  public int getNumTests() {
    return numTests;
  }

  public long getRunId() {
    return runId;
  }

  public String getTestId() {
    return testId;
  }
}
