/*
 * Copyright (C) 2009 Max Ross.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sortedunderbelly.appengineunit.model;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public class RunStatus {
  private final Run run;
  private final int numTestsStarted;
  private final int numTestsInProgress;
  private final int numTestsFailed;
  private final Iterable<Test> failures;
  private final Iterable<Test> testsInProgress;

  public enum Status { NOT_STARTED, RUNNING, FINISHED }

  public RunStatus(Run run, int numTestsStarted, int numTestsInProgress, int numTestsFailed,
                   Iterable<Test> failures, Iterable<Test> testsInProgress) {
    this.run = run;
    this.numTestsStarted = numTestsStarted;
    this.numTestsInProgress = numTestsInProgress;
    this.numTestsFailed = numTestsFailed;
    this.failures = failures;
    this.testsInProgress = testsInProgress;
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

  public Iterable<Test> getFailures() {
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

  public Iterable<Test> getTestsInProgress() {
    return testsInProgress;
  }

}
