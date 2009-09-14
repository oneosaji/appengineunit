package org.sortedunderbelly.appengineunit.spi;

import org.sortedunderbelly.appengineunit.model.TestResult;
import org.sortedunderbelly.appengineunit.model.Test;

/**
 * @author Max Ross <maxr@google.com>
 */
public interface TestHarness {

  TestResult runTest(TestHarnessConfig config, Test test);
}
