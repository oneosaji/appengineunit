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
package com.sortedunderbelly.appengineunit;

import com.sortedunderbelly.appengineunit.model.Failure;
import com.sortedunderbelly.appengineunit.model.Run;
import com.sortedunderbelly.appengineunit.model.Test;

/**
 * @author Max Ross <max.ross@gmail.com>
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

  Iterable<Test> getTestsInProgressForRun(long runId);
  Iterable<Test> getFailedTestsForRun(long runId);

  /**
   * @return {@code true} if the completion record was created, {@code false}
   * otherwise.
   */
  boolean createCompletionRecordIfNotAlreadyPresent(long runId);

  Failure getFailure(long runId, String testId, String failureId);
}
