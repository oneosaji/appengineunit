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

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

/**
 * A base {@link RunListener} with no-op implementations of all methods.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class BaseRunListener extends RunListener {

  @Override
  public void testRunStarted(Description description) {
  }

  @Override
  public void testRunFinished(Result result) {
  }

}