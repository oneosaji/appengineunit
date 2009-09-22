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

import com.google.appengine.api.datastore.Key;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public class Failure {

  private Key key;
  private final long runId;
  private final String failureMsg;

  public Failure(long runId, String failureMsg) {
    this(null, runId, failureMsg);
  }

  public Failure(Key key, long runId, String failureMsg) {
    this.key = key;
    this.runId = runId;
    this.failureMsg = failureMsg;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getFailureMsg() {
    return failureMsg;
  }

  public long getRunId() {
    return runId;
  }
}
