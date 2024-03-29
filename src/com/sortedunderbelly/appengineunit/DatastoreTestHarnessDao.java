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
package com.sortedunderbelly.appengineunit;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;

import com.sortedunderbelly.appengineunit.model.Failure;
import com.sortedunderbelly.appengineunit.model.Run;
import com.sortedunderbelly.appengineunit.model.Status;
import com.sortedunderbelly.appengineunit.model.Test;
import com.sortedunderbelly.appengineunit.spi.TestHarness;
import com.sortedunderbelly.appengineunit.spi.TestHarnessConfig;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Max Ross <max.ross@gmail.com>
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
      throw new RuntimeException("Run with id " + runId + " not found (namespaace is " + NamespaceManager.get() + ").");
    }
  }

  public int getNumTestsStartedForRun(long runId) {
    Query numStartedQuery = new Query(getTestEntityKind());
    numStartedQuery.addFilter("runKey", Query.FilterOperator.EQUAL, buildRunKey(runId));
    return ds.prepare(numStartedQuery).countEntities();
  }

  private PreparedQuery getNumInProgressForRunQuery(long runId) {
    Query numInProgressQuery = new Query(getTestEntityKind());
    numInProgressQuery.addFilter("runKey", Query.FilterOperator.EQUAL, buildRunKey(runId));
    numInProgressQuery.addFilter("status", Query.FilterOperator.EQUAL, Status.STARTED.name());
    return ds.prepare(numInProgressQuery);
  }
  public int getNumTestsInProgressForRun(long runId) {
    return getNumInProgressForRunQuery(runId).countEntities();
  }

  public Iterable<Test> getTestsInProgressForRun(final long runId) {
    PreparedQuery query = getNumInProgressForRunQuery(runId);
    final Iterable<Entity> inner = query.asIterable();
    EntityFunction<Test> func = new EntityFunction<Test>() {
      public Test apply(Entity from) {
        return entityToTest(from, runId);
      }
    };
    return new EntityTransformer(inner, func);
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
      }
      ds.put(txn, testToEntity(test));
      txn.commit();
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
  }

  private interface EntityFunction<T> {
    T apply(Entity from);
  }

  private static final class EntityTransformer<T> implements Iterable<T> {
    private final Iterable<Entity> inner;
    private final EntityFunction<T> func;

    private EntityTransformer(Iterable<Entity> inner, EntityFunction<T> func) {
      this.inner = inner;
      this.func = func;
    }

    public Iterator<T> iterator() {
      final Iterator<Entity> innerIter = inner.iterator();
      return new Iterator<T>() {
        public boolean hasNext() {
          return innerIter.hasNext();
        }

        public T next() {
          return func.apply(innerIter.next());
        }

        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    }
  }

  public Iterable<Test> getFailedTestsForRun(final long runId) {
    Query failureForRunQuery = new Query(getTestEntityKind());
    failureForRunQuery.addFilter(
        Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.GREATER_THAN, KeyFactory.createKey(getTestEntityKind(), "Run " + runId));
    failureForRunQuery.addFilter(
        Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.LESS_THAN, KeyFactory.createKey(getTestEntityKind(), "Run " + (runId + 1)));
    failureForRunQuery.addFilter("hasFailures", Query.FilterOperator.EQUAL, true);
    final Iterable<Entity> inner = ds.prepare(failureForRunQuery).asIterable();
    EntityFunction<Test> func = new EntityFunction<Test>() {
      public Test apply(Entity from) {
        return entityToTest(from, runId);
      }
    };
    return new EntityTransformer(inner, func);
  }

  private List<Entity> failuresToEntities(Test test) {
    List<Entity> entities = new ArrayList<Entity>();
    for (Failure f : test.getFailures()) {
      entities.add(failureToEntity(test, f));
    }
    return entities;
  }

  private Key buildFailureKey(Test test, Failure f) {
    Key testKey = buildTestKey(test);
    return buildFailureKey(testKey, f.getId());
  }

  private Key buildFailureKey(Key testKey, String failureId) {
    return KeyFactory.createKey(testKey, getFailureEntityKind(), failureId);
  }

  private Entity failureToEntity(Test test, Failure f) {
    Key failureKey = buildFailureKey(test, f);
    Entity e = new Entity(failureKey);
    String failureMsg = f.getFailureMsg().length() > 500 ?
                        f.getFailureMsg().substring(0, 500) : f.getFailureMsg();
    e.setProperty("failureMsgShort", failureMsg);
    e.setProperty("failureMsgFull", new Text(f.getFailureMsg()));
    e.setProperty("runKey", buildRunKey(test.getRun().getId()));
    return e;
  }

//  private Failure entityToFailure(Entity failureEntity) {
//    Key runKey = (Key) failureEntity.getProperty("runKey");
//    Text failureMsg = (Text) failureEntity.getProperty("failureMsgFull");
//    return new Failure(failureEntity.getKey(), runKey.getId(), failureMsg.getValue());
//  }

  private Key buildTestKey(Test test) {
    return buildTestKey(test.getRun().getId(), test.getId());
  }

  private Key buildTestKey(long runId, String testId) {
    return KeyFactory.createKey(getTestEntityKind(), "Run " + runId + ":" + testId);
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

  private Test entityToTest(Entity e, long runId) {
    Test t = new Test(e.getKey().getName(), null);
    Iterable<Key> failureKeys = (Iterable<Key>) e.getProperty("failureKeys");
    if (failureKeys != null) {
      for (Key failureKey : failureKeys) {
        Failure f = new Failure(failureKey.getName(), failureKey.getParent().getName(), runId, null);
        t.getFailures().add(f);
      }
    }
    return t;
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
      testEntity.setProperty("hasFailures", false);
    } else {
      List<Key> failureKeys = new ArrayList<Key>();
      for (Failure f : t.getFailures()) {
        failureKeys.add(buildFailureKey(t, f));
      }
      testEntity.setProperty("failureKeys", failureKeys);
      testEntity.setProperty("hasFailures", true);
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

  public Failure getFailure(long runId, String testId, String failureId) {
    Key testKey = KeyFactory.createKey(getTestEntityKind(), testId);
    try {
      Entity e = ds.get(buildFailureKey(testKey, failureId));
      return entityToFailure(e);
    } catch (EntityNotFoundException e1) {
      throw new RuntimeException(e1);
    }
  }

  private Failure entityToFailure(Entity e) {
    Key runKey = (Key) e.getProperty("runKey");
    String failureMsg = ((Text) e.getProperty("failureMsgFull")).getValue();
    return new Failure(e.getKey().getName(), e.getParent().getName(), runKey.getId(), failureMsg);
  }
}
