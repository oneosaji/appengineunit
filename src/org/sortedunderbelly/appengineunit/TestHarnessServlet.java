package org.sortedunderbelly.appengineunit;

import org.sortedunderbelly.appengineunit.model.Failure;
import org.sortedunderbelly.appengineunit.model.TestResult;
import org.sortedunderbelly.appengineunit.model.RunStatus;
import org.sortedunderbelly.appengineunit.model.TestStatus;
import org.sortedunderbelly.appengineunit.spi.TestHarnessConfig;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Max Ross <maxr@google.com>
 */
public class TestHarnessServlet extends HttpServlet {

  private static final String CONFIG_CLASS_PROPERTY = "testharness.config.class";
  private static final String DAO_CLASS_PROPERTY = "testharness.dao.class";

  private TestHarnessManager harnessManager;
  private String harnessBaseURL;

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    harnessManager = createTestHarnessManager(servletConfig);
    super.init(servletConfig);
  }

  private TestHarnessConfig createTestHarnessConfig(ServletConfig servletConfig)
      throws ServletException {
    String configClass = servletConfig.getInitParameter(CONFIG_CLASS_PROPERTY);
    if (configClass == null) {
      throw new ServletException(CONFIG_CLASS_PROPERTY + " is a required init parameter");
    }
    try {
      return (TestHarnessConfig) Class.forName(configClass).newInstance();
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private TestHarnessManager createTestHarnessManager(ServletConfig servletConfig) throws ServletException {
    TestHarnessConfig harnessConfig = createTestHarnessConfig(servletConfig);
    harnessBaseURL = harnessConfig.getBaseURL();
    String daoClass = servletConfig.getInitParameter(DAO_CLASS_PROPERTY);
    TestHarnessDao dao;
    if (daoClass == null) {
      dao = new DatastoreTestHarnessDao(harnessConfig);
    } else {
      try {
        dao = (TestHarnessDao) Class.forName(daoClass).newInstance();
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
    return new TestHarnessManager(harnessConfig, dao);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      doPost(req, resp);
    } catch (NoClassDefFoundError e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("text/html");
    String[] components = req.getRequestURI().substring(1).split("/");
    if (components.length < 2) {
      throw new ServletException("invalid url");
    }
    if (components[1].equals("new")) {
      long runId = harnessManager.createNewRun();
      resp.sendRedirect(harnessBaseURL + runId);
    } else {
      long runId = Long.parseLong(components[1]);
      if (components.length == 2) {
        displayRunStatus(harnessManager.getRunStatus(runId), resp);
      } else  {
        String testId = components[2];
        if (components.length == 3) {
          displayTestStatus(harnessManager.getTestStatus(runId, testId), resp);
        } else if (components[3].equals("run")){
          TestResult result = harnessManager.runTest(runId, testId);
          displayTestResult(result, resp);
        } else {
          throw new ServletException("Invalid action for test " + testId + " in run " + runId);
        }
      }
    }
  }

  private void displayTestResult(TestResult result, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.getWriter().println(
        "Test " + result.getTestId() + " in run " + result.getRunId()
        + " completed with status: " + result.getStatus());
  }

  private void displayTestStatus(TestStatus testStatus, HttpServletResponse resp)
      throws IOException {
    // TODO(maxr) Implement
    resp.getWriter().println("This is the status for test " + testStatus.getTestId() + " in run " + testStatus.getRunId());
  }

  private void displayRunStatus(RunStatus runStatus, HttpServletResponse resp) throws IOException {
    resp.getWriter().println("Details For Run " + runStatus.getRun().getId());
    resp.getWriter().println("<br>Status: " + runStatus.getStatus());
    resp.getWriter().println("<br>Num Tests: " + runStatus.getRun().getNumTests());
    resp.getWriter().println("<br>Num Started: " + runStatus.getNumTestsStarted());
    resp.getWriter().println("<br>Num In Progress: " + runStatus.getNumTestsInProgress());
    resp.getWriter().println("<br>Num Succeeded: " + runStatus.getNumTestsSucceeded());
    resp.getWriter().println("<br>Num Failed: " + runStatus.getNumTestsFailed());

    boolean first = true;
    for (Failure f : runStatus.getFailures()) {
      if (first) {
        first = false;
        resp.getWriter().println("<br>");
        resp.getWriter().println("<br>Failures");
      }
      resp.getWriter().println("<br>TestId: " + f.getKey().getParent().toString());
      resp.getWriter().println("<br>FailureMsg: " + f.getFailureMsg());
      resp.getWriter().println("<br>");
    }
  }
}