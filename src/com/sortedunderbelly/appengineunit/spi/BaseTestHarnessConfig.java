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
package com.sortedunderbelly.appengineunit.spi;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public abstract class BaseTestHarnessConfig implements TestHarnessConfig {

  public Queue getQueue(long runId) {
    return QueueFactory.getDefaultQueue();
  }

  public String getBaseURL() {
    return "/testharness/";
  }

  public IsolationMechanism getIsolationMechanism() {
    return IsolationMechanism.ONE_NAMESPACE_PER_TEST;
  }

  public TestRunListener getTestRunListener() {
    return new TestRunListener() {
      public void onCompletion(String statusURL, long runId) {
        // by default we do nothing
      }
    };
  }
}
