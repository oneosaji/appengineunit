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
package com.sortedunderbelly.appengineunit.harness.junit3;

import com.sortedunderbelly.appengineunit.spi.TestRun;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public class JUnit3TestRun implements TestRun {

  private final TestSuite testSuite;
  private final Logger logger = Logger.getLogger(getClass().getName());

  public JUnit3TestRun(TestSuite testSuite) {
    this.testSuite = testSuite;
  }

  public Iterable<String> getTestIds(long runId) {
    List<String> testIds = new ArrayList<String>();
    for (Test t : toList(testSuite.tests())) {
      try {
        Class<?> testClass = Class.forName(t.toString());
        if (testClass.isAnonymousClass() || testClass.isLocalClass()) {
          logger.warning(runId + ": Cannot schedule test " + t.getClass().getName()
                         + " for execution because it is an anonymous or local class.");
        } else {
          testIds.add(testClass.getName());
        }
      } catch (ClassNotFoundException e) {
        logger.warning(runId + ": Cannot schedule instance of class " + t.getClass().getName()
                       + "for execution because its String represenation, " + t.toString()
                       + ", is not an available class.");
      }
    }
    return testIds;
  }


  private List<Test> toList(Enumeration e) {
    List<Test> tests = new ArrayList<Test>();
    while (e.hasMoreElements()) {
      tests.add((Test) e.nextElement());
    }
    return tests;
  }

}
