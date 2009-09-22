package com.sortedunderbelly.appengineunit.harness.junit3;

import com.google.apphosting.api.ApiProxy;

import com.sortedunderbelly.appengineunit.KindTrackingDatastoreDelegate;

import junit.framework.Test;

/**
 * A {@link junit.framework.TestListener} that wipes all data written by
 * the test when the test finishes running.
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
