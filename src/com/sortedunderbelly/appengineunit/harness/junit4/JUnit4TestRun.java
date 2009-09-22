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
package com.sortedunderbelly.appengineunit.harness.junit4;

import com.sortedunderbelly.appengineunit.harness.junitx.JUnitTestRun;

import junit.framework.TestSuite;

/**
 * JUnit 4 specialization of a {@link JUnitTestRun}.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class JUnit4TestRun extends JUnitTestRun {

  public JUnit4TestRun(TestSuite testSuite) {
    super(testSuite);
  }
}
