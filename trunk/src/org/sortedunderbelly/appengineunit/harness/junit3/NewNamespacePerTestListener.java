package org.sortedunderbelly.appengineunit.harness.junit3;

import com.google.appengine.api.NamespaceManager;

import org.sortedunderbelly.appengineunit.model.Test;

/**
 * @author Max Ross <maxr@google.com>
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

