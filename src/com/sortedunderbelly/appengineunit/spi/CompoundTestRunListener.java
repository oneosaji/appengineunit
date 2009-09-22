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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link TestRunListener} implementation that cycles through 1 or more
 * {@link TestRunListener TestRunListeners} for each event method.  The event
 * method on each contained listener will be called even if the event methods
 * on other contained listeners throw exceptions.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class CompoundTestRunListener implements TestRunListener {

  private final Logger logger = Logger.getLogger(getClass().getName());

  private final List<TestRunListener> listeners;

  public CompoundTestRunListener(List<TestRunListener> listeners) {
    this.listeners = listeners;
  }

  public void onCompletion(String statusURL, long runId) {
    RuntimeException thrown = null;
    for (TestRunListener trl : listeners) {
      // We're going to power through these listeners even
      // if one of them throws an exception.
      try {
        trl.onCompletion(statusURL, runId);
      } catch (RuntimeException rte) {
        logger.log(Level.SEVERE, "TestRunListener of type " + trl.getClass().getName() + " threw an exception.", rte);
        thrown = rte;
      }
    }

    // If one or more of the listeners threw an exception we'll just throw the
    // most recent one.
    if (thrown != null) {
      throw thrown;
    }
  }
}
