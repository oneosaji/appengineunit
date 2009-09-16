package org.sortedunderbelly.appengineunit;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.TaskHandle;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

import org.sortedunderbelly.appengineunit.model.Failure;
import org.sortedunderbelly.appengineunit.model.Run;
import org.sortedunderbelly.appengineunit.model.RunStatus;
import org.sortedunderbelly.appengineunit.model.Status;
import org.sortedunderbelly.appengineunit.model.Test;
import org.sortedunderbelly.appengineunit.model.TestResult;
import org.sortedunderbelly.appengineunit.model.TestStatus;
import org.sortedunderbelly.appengineunit.spi.TestHarness;
import org.sortedunderbelly.appengineunit.spi.TestHarnessConfig;
import org.sortedunderbelly.appengineunit.spi.TestRun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Max Ross <maxr@google.com>
 */
public class TestHarnessManager {

  private final TestHarnessConfig harnessConfig;
  private final TestHarnessDao dao;
  private final Logger logger = Logger.getLogger(getClass().getName());

  public TestHarnessManager(TestHarnessConfig harnessConfig, TestHarnessDao dao) {
    this.harnessConfig = harnessConfig;
    this.dao = dao;
  }

  public long createNewRun() {
    logger.fine("Creating new run.");
    Run run = dao.newRun();
    TestRun testRun = harnessConfig.newTestRun();
    List<TaskHandle> handles = scheduleExecution(run.getId(), testRun);
    run.setNumTests(handles.size());
    dao.updateRun(run);
    logger.fine("Created new run with id " + run.getId());
    return run.getId();
  }

  public RunStatus getRunStatus(long runId) {
    logger.fine("Getting status for run " + runId);
    Run run = dao.findRunById(runId);

    int numTestsStarted = dao.getNumTestsStartedForRun(runId);
    int numTestsInProgress = dao.getNumTestsInProgressForRun(runId);
    int numTestsFailedForRun = dao.getNumTestsFailedForRun(runId);
    Iterable<Failure> failures = numTestsFailedForRun == 0 ?
                                 Collections.<Failure>emptyList() : dao.getFailuresForRun(runId);
    logger.fine("Retrieved status for run " + runId);
    return new RunStatus(run, numTestsStarted, numTestsInProgress, numTestsFailedForRun, failures);
  }

  private List<TaskHandle> scheduleExecution(long runId, TestRun testRun) {
    logger.fine("Scheduling execution for run " + runId);
    List<TaskHandle> handles = new ArrayList<TaskHandle>();
    Queue q = harnessConfig.getQueue(runId);
    for (String testId : testRun.getTestIds(runId)) {
      handles.add(q.add(buildTaskOptions(runId, testId)));
    }
    logger.fine("Scheduled execution of " + handles.size() + " tests for run " + runId);
    return handles;
  }

  private TaskOptions buildTaskOptions(long runId, String testId) {
    return TaskOptions.Builder.method(TaskOptions.Method.POST)
        .url(harnessConfig.getBaseURL() + runId + "/" + testId + "/run");
  }

  public TestStatus getTestStatus(long runId, String testId) {
    // TODO(maxr) implement
    return new TestStatus(runId, testId);
  }

  public TestResult runTest(long runId, String testId) {
    logger.fine("Running test " + testId + " in run " + runId);
    Run run = dao.findRunById(runId);
    Test test = dao.newTest(run, testId);
    TestHarness harness = harnessConfig.getTestHarness();
    TestResult result = null;
    Throwable thrown = null;
    try {
      result = harness.runTest(harnessConfig, test);
      test.setStatus(result.getStatus());
      logger.fine("Test " + testId + " in run " + runId + " completed with status " + result.getStatus());
    } catch (Throwable t) {
      thrown = t;
    } finally {
      if (thrown != null) {
        // We're doing this handling in the finally block because if we hit a
        // deadline exception in a catch block we'll get interrupted.  This way
        // we don't have to worry about a null result.
        test.setStatus(Status.FAILURE);
        String msg = "Test " + testId + " in run " + runId + " threw an exception of type "
                       + thrown.getClass().getName() + ": " + thrown;
        result = new TestResult(
            runId, testId, Status.FAILURE, -1, Collections.singletonList(msg));
        logger.warning(msg);
      }
      addResultToTest(test, result);
      test.setEndTime(new Date());
      dao.updateTest(test);
    }
    return result;
  }

  private void addResultToTest(Test test, TestResult result) {
    for (String data : result.getFailureData()) {
      test.getFailures().add(new Failure(test.getRun().getId(), data));
    }
  }
}
