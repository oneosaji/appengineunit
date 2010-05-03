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
public class Failure {

  private String id;
  private String testId;
  private final long runId;
  private final String failureMsg;

  public Failure(String id, String testId, long runId, String failureMsg) {
    this.id = id;
    this.testId = testId;
    this.runId = runId;
    this.failureMsg = failureMsg;
  }

  public String getId() {
    return id;
  }

  public String getTestId() {
    return testId;
  }

  public String getFailureMsg() {
    return failureMsg;
  }

  public long getRunId() {
    return runId;
  }
}
