package com.sortedunderbelly.appengineunit.harness.junit3;

import com.google.appengine.api.NamespaceManager;

import com.sortedunderbelly.appengineunit.model.Test;

/**
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
    String namespace = String.format("%s_%s", gaeTest.getRun().getId(), test.toString());
    NamespaceManager.set(namespace);
  }
}

