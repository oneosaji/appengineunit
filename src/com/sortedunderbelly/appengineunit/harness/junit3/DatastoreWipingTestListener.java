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

import com.google.apphosting.api.ApiProxy;

import com.sortedunderbelly.appengineunit.KindTrackingDatastoreDelegate;

import junit.framework.Test;
import junit.framework.TestListener;

/**
 * A {@link TestListener} that wipes all data written by the test when the
 * test finishes running.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class DatastoreWipingTestListener extends BaseTestListener {

  private ApiProxy.Delegate originalDelegate;

  @Override
  public void startTest(Test test) {
    super.startTest(test);
    originalDelegate = ApiProxy.getDelegate();
    ApiProxy.setDelegate(new KindTrackingDatastoreDelegate(originalDelegate));

  }

  @Override
  public void endTest(Test test) {
    KindTrackingDatastoreDelegate
        kindTracker = (KindTrackingDatastoreDelegate) ApiProxy.getDelegate();
    ApiProxy.setDelegate(originalDelegate);
    kindTracker.wipeData();
    super.endTest(test);
  }
}
