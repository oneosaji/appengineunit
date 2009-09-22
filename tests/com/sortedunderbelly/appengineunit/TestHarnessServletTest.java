package com.sortedunderbelly.appengineunit;

import junit.framework.TestCase;

/**
 * @author Max Ross <maxr@google.com>
 */
public class TestHarnessServletTest extends TestCase {

  public void testExtractServerURL() {
    assertEquals("http://localhost:8080",
                 TestHarnessServlet.extractServerURL("http://localhost:8080/the/servlet/path?this=that",
                                                     "/the/servlet/path?this=that"));
  }
}
