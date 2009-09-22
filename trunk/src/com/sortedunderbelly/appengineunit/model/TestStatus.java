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
