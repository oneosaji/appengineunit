package org.sortedunderbelly.appengineunit;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;

import org.sortedunderbelly.appengineunit.model.Failure;
import org.sortedunderbelly.appengineunit.model.Run;
import org.sortedunderbelly.appengineunit.model.Status;
import org.sortedunderbelly.appengineunit.model.Test;
import org.sortedunderbelly.appengineunit.spi.TestHarness;
import org.sortedunderbelly.appengineunit.spi.TestHarnessConfig;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Max Ross <maxr@google.com>
 */
public class DatastoreTestHarnessDao implements TestHarnessDao {

  private final DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

  private final TestHarnessConfig config;

  public DatastoreTestHarnessDao(TestHarnessConfig config) {
    this.config = config;
  }

  public Run newRun() {
    Entity runEntity = new Entity(getRunEntityKind());
    TestHarness harness = config.getTestHarness();
    runEntity.setProperty("testRunnerClass", harness.getClass().getName());
    Date created = new Date();
    runEntity.setProperty("created", created);
    // -1 is an indication that we don't know the number of tests yet
    runEntity.setProperty("numTests", -1);
    Key key = ds.put(runEntity);
    return new Run(key.getId(), harness.getClass(), created);
  }

  public void updateRun(Run run) {
    try {
      Entity runEntity = ds.get(KeyFactory.createKey(getRunEntityKind(), run.getId()));
      // num tests is the only mutable field
      runEntity.setProperty("numTests", run.getNumTests());
      ds.put(runEntity);
    } catch (EntityNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public Run findRunById(long runId) {
    try {
      return entityToRun(ds.get(buildRunKey(runId)));
    } catch (EntityNotFoundException e) {
      throw new RuntimeException("Run with id " + runId + " not found.");
    }
  }

  public int getNumTestsStartedForRun(long runId) {
    Query numStartedQuery = new Query(getTestEntityKind());
    numStartedQuery.addFilter("runKey", Query.FilterOperator.EQUAL, buildRunKey(runId));
    return ds.prepare(numStartedQuery).countEntities();
  }

  public int getNumTestsInProgressForRun(long runId) {
    Query numInProgressQuery = new Query(getTestEntityKind());
    numInProgressQuery.addFilter("runKey", Query.FilterOperator.EQUAL, buildRunKey(runId));
    numInProgressQuery.addFilter("status", Query.FilterOperator.EQUAL, Status.STARTED.name());
    return ds.prepare(numInProgressQuery).countEntities();
  }

  public int getNumTestsFailedForRun(long runId) {
    Query numFailedQuery = new Query(getTestEntityKind());
    numFailedQuery.addFilter("runKey", Query.FilterOperator.EQUAL, buildRunKey(runId));
    numFailedQuery.addFilter("status", Query.FilterOperator.EQUAL, Status.FAILURE.name());
    return  ds.prepare(numFailedQuery).countEntities();
  }

  public Test newTest(Run run, String testId) {
    Test test = new Test(testId, run);
    ds.put(testToEntity(test));
    return test;
  }

  public void updateTest(Test test) {
    Transaction txn = ds.beginTransaction();
    try {
      if (!test.getFailures().isEmpty()) {
        List<Entity> failureEntities = failuresToEntities(test);
        ds.put(txn, failureEntities);
        // update the Failure model objects with the Keys
        Iterator<Failure> failureIter = test.getFailures().iterator();
        for (Entity e : failureEntities) {
          failureIter.next().setKey(e.getKey());
        }
      }
      ds.put(txn, testToEntity(test));
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  public Iterable<Failure> getFailuresForRun(final long runId) {
    return new Iterable<Failure>() {
      public Iterator<Failure> iterator() {
        Query failureForRunQuery = new Query(getFailureEntityKind());
        failureForRunQuery.addFilter("runKey", Query.FilterOperator.EQUAL, buildRunKey(runId));
        final Iterator<Entity> inner = ds.prepare(failureForRunQuery).asIterator();
        return new Iterator<Failure>() {
          public boolean hasNext() {
            return inner.hasNext();
          }

          public Failure next() {
            return entityToFailure(inner.next());
          }

          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }

  private List<Entity> failuresToEntities(Test test) {
    List<Entity> entities = new ArrayList<Entity>();
    for (Failure f : test.getFailures()) {
      entities.add(failureToEntity(test, f));
    }
    return entities;
  }

  private Entity failureToEntity(Test test, Failure f) {
    Entity e = new Entity(getFailureEntityKind(), buildTestKey(test));
    String failureMsg = f.getFailureMsg().length() > 500 ?
                        f.getFailureMsg().substring(0, 500) : f.getFailureMsg();
    e.setProperty("failureMsgShort", failureMsg);
    e.setProperty("failureMsgFull", new Text(f.getFailureMsg()));
    e.setProperty("runKey", buildRunKey(test.getRun().getId()));
    return e;
  }

  private Failure entityToFailure(Entity failureEntity) {
    Key runKey = (Key) failureEntity.getProperty("runKey");
    Text failureMsg = (Text) failureEntity.getProperty("failureMsgFull");
    return new Failure(failureEntity.getKey(), runKey.getId(), failureMsg.getValue());  
  }

  private Key buildTestKey(Test test) {
    return KeyFactory.createKey(getTestEntityKind(), "_" + test.getRun().getId() + ":" + test.getId());
  }

  private Key buildRunKey(long runId) {
    return KeyFactory.createKey(getRunEntityKind(), runId);
  }

  private static Run entityToRun(Entity e) {
    Date created = (Date) e.getProperty("created");
    try {
      Run run = new Run(e.getKey().getId(), Class.forName((String) e.getProperty("testRunnerClass")), created);
      run.setNumTests((Long) e.getProperty("numTests"));
      return run;
    } catch (ClassNotFoundException e1) {
      throw new RuntimeException(e1);
    }
  }

  private Entity testToEntity(Test t) {
    Entity testEntity = new Entity(buildTestKey(t));
    testEntity.setProperty("startTime", t.getStartTime());
    if (t.getEndTime() == null) {
      testEntity.removeProperty("endTime");
      testEntity.removeProperty("durationInMs");
    } else {
      testEntity.setProperty("endTime", t.getEndTime());
      testEntity.setProperty("durationInMs", t.getEndTime().getTime() - t.getStartTime().getTime());
    }
    testEntity.setProperty("status", t.getStatus().name());
    testEntity.setProperty("numTests", t.getNumTests());
    if (t.getFailures().isEmpty()) {
      testEntity.removeProperty("failureKeys");
    } else {
      List<Key> failureKeys = new ArrayList<Key>();
      for (Failure f : t.getFailures()) {
        failureKeys.add(f.getKey());
      }
      testEntity.setProperty("failureKeys", failureKeys);
    }
    testEntity.setProperty("runKey", buildRunKey(t.getRun().getId()));
    return testEntity;
  }

  protected String getRunEntityKind() {
    return "HarnessRun";
  }

  protected String getTestEntityKind() {
    return "HarnessTest";
  }

  protected String getFailureEntityKind() {
    return "HarnessFailure";
  }

  protected String getCompletionNotificationEntityKind() {
    return "CompletionNotification";
  }

  public boolean createCompletionRecordIfNotAlreadyPresent(long runId) {
    Key key = KeyFactory.createKey(getCompletionNotificationEntityKind(), Long.valueOf(runId).toString());
    // We'll do a fetch by Key in a txn so we can guarantee that
    Transaction txn = ds.beginTransaction();
    try {
      ds.get(key);
      // Entity already exists otherwise there would have been an exception so
      // return false to indicate that nothing needs to be done.
      return false;
    } catch (EntityNotFoundException enfe) {
      // Entity doesn't already exist so create it.
      // The Entity doesn't have any data, we just use it as a lock that
      // prevents more than one completion notification from being sent.
      Entity entity = new Entity(key);
      ds.put(entity);
      try {
        txn.commit();
      } catch (ConcurrentModificationException cme) {
        // Somebody create the entity during our txn.
        // That's fine, it just means we return false;
        return false;
      }
      return true;
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }
}
