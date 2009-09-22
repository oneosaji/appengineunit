package com.sortedunderbelly.appengineunit.model;

/**
 * @author Max Ross <maxr@google.com>
 */
public class RunStatus {
  private final Run run;
  private final int numTestsStarted;
  private final int numTestsInProgress;
  private final int numTestsFailed;
  private final Iterable<Failure> failures;

  public enum Status { NOT_STARTED, RUNNING, FINISHED }

  public RunStatus(Run run, int numTestsStarted, int numTestsInProgress, int numTestsFailed,
                   Iterable<Failure> failures) {
    this.run = run;
    this.numTestsStarted = numTestsStarted;
    this.numTestsInProgress = numTestsInProgress;
    this.numTestsFailed = numTestsFailed;
    this.failures = failures;
  }

  public Run getRun() {
    return run;
  }

  public int getNumTestsStarted() {
    return numTestsStarted;
  }

  public int getNumTestsInProgress() {
    return numTestsInProgress;
  }

  public int getNumTestsFailed() {
    return numTestsFailed;
  }

  public Iterable<Failure> getFailures() {
    return failures;
  }

  public int getNumTestsSucceeded() {
    return numTestsStarted - numTestsInProgress - numTestsFailed;
  }

  public Status getStatus() {
    if (numTestsStarted == 0) {
      return Status.NOT_STARTED;
    } else if ((numTestsFailed + getNumTestsSucceeded()) != run.getNumTests() ) {
      return Status.RUNNING;
    } else {
      return Status.FINISHED;
    }
  }
}