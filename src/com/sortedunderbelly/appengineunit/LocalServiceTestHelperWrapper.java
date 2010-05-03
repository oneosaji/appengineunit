/*
 * Copyright (C) 2010 Google Inc
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
package com.sortedunderbelly.appengineunit;

import com.google.appengine.tools.development.testing.LocalServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.apphosting.api.ApiProxy;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public class LocalServiceTestHelperWrapper {

  private final Object helper;
  private static ThreadLocalDelegate THREAD_LOCAL_DELEGATE;

  public LocalServiceTestHelperWrapper(LocalServiceTestConfig... configs) {
    if (ApiProxy.getCurrentEnvironment() == null) {
      helper = new LocalServiceTestHelper(configs);
    } else {
      helper = null;
    }
  }

  public void setUp() {
    if (helper != null) {
      ((LocalServiceTestHelper) helper).setUp();
    } else {
      synchronized (getClass()) {
        if (!(ApiProxy.getDelegate() instanceof ThreadLocalDelegate)) {
          THREAD_LOCAL_DELEGATE = new ThreadLocalDelegate(ApiProxy.getDelegate());
          ApiProxy.setDelegate(THREAD_LOCAL_DELEGATE);
        }
      }
    }
  }

  public void tearDown() {
    if (helper != null) {
      ((LocalServiceTestHelper) helper).tearDown();
    }
    synchronized (getClass()) {
      if (THREAD_LOCAL_DELEGATE != null) {
        THREAD_LOCAL_DELEGATE.clearDelegateForThread();
      }
    }
  }

  public static synchronized void setDelegate(ApiProxy.Delegate delegate) {
    if (THREAD_LOCAL_DELEGATE == null) {
      ApiProxy.setDelegate(delegate);
    } else {
      LocalServiceTestHelperWrapper.THREAD_LOCAL_DELEGATE.setDelegateForThread(delegate);
    }
  }

  public static synchronized ApiProxy.Delegate getDelegate() {
    if (THREAD_LOCAL_DELEGATE == null) {
      return ApiProxy.getDelegate();
    }
    return THREAD_LOCAL_DELEGATE.getDelegate();
  }
}
