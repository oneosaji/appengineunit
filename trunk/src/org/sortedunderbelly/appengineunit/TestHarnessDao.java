package org.sortedunderbelly.appengineunit;

import org.sortedunderbelly.appengineunit.model.Failure;
import org.sortedunderbelly.appengineunit.model.Run;
import org.sortedunderbelly.appengineunit.model.Test;

/**
 * @author Max Ross <maxr@google.com>
 */
public interface TestHarnessDao {
  Run newRun();

  void updateRun(Run run);

  Run findRunById(long runId);

  int getNumTestsStartedForRun(long runId);

  int getNumTestsInProgressForRun(long runId);

  int getNumTestsFailedForRun(long runId);

  Test newTest(Run run, String testId);

  void updateTest(Test test);

  Iterable<Failure> getFailuresForRun(long runId);

  /**
   * @return {@code true} if the completion record was created, {@code false}
   * otherwise.
   */
  boolean createCompletionRecordIfNotAlreadyPresent(long runId);
}
