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

import com.google.appengine.api.NamespaceManager;

import com.sortedunderbelly.appengineunit.model.Test;

import org.junit.runner.notification.RunListener;

/**
 * A {@link RunListener} that establishes a new namespace before every test
 * and resets it after the test.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
final class NewNamespacePerTestListener extends BaseTestListener {

  private final Test gaeTest;

  public NewNamespacePerTestListener(Test gaeTest) {
    this.gaeTest = gaeTest;
  }

  public void endTest(junit.framework.Test test) {
    NamespaceManager.reset();
  }

  public void startTest(junit.framework.Test test) {
    String namespace = String.format(
        "%s_%d", gaeTest.getRun().getId(), sanitizeTestNameForNamespace(test.toString()));
    NamespaceManager.set(namespace);
  }

  private int sanitizeTestNameForNamespace(String testName) {
    return testName.hashCode();
//    return testName.replace("(", "_").replace(")", "_").replace(".", "_");
  }

}