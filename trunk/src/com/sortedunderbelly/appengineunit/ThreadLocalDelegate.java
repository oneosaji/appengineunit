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

import com.google.apphosting.api.ApiProxy;

import java.util.concurrent.Future;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public class ThreadLocalDelegate implements ApiProxy.Delegate {

  private final ApiProxy.Delegate globalDelegate;
  private final ThreadLocal<ApiProxy.Delegate>
      threadLocalDelegate = new ThreadLocal<ApiProxy.Delegate>();

  public ThreadLocalDelegate(ApiProxy.Delegate globalDelegate) {
    this.globalDelegate = globalDelegate;
  }

  ApiProxy.Delegate getDelegate() {
    ApiProxy.Delegate result = threadLocalDelegate.get();
    if (result == null) {
      result = globalDelegate;
    }
    return result;
  }

  public byte[] makeSyncCall(ApiProxy.Environment environment, String pkg, String method, byte[] bytes)
      throws ApiProxy.ApiProxyException {
    return getDelegate().makeSyncCall(environment, pkg, method, bytes);
  }

  public Future makeAsyncCall(ApiProxy.Environment environment, String pkg, String method, byte[] bytes,
                              ApiProxy.ApiConfig apiConfig) {
    return getDelegate().makeAsyncCall(environment, pkg, method, bytes, apiConfig);
  }

  public void log(ApiProxy.Environment environment, ApiProxy.LogRecord logRecord) {
    getDelegate().log(environment, logRecord);
  }

  public void setDelegateForThread(ApiProxy.Delegate delegate) {
    threadLocalDelegate.set(delegate);
  }

  public void clearDelegateForThread() {
    threadLocalDelegate.remove();
  }
}
