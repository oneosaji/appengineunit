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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DatastorePb;
import com.google.storage.onestore.v3.OnestoreEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * A {@link ApiProxy.Delegate} implementation that keeps track of the kinds of
 * all {@link Entity Entities} that get written to the datastore.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class KindTrackingDatastoreDelegate implements ApiProxy.Delegate {

  private final ApiProxy.Delegate delegate;
  private final Set<String> kinds = new HashSet<String>();

  public KindTrackingDatastoreDelegate(ApiProxy.Delegate delegate) {
    this.delegate = delegate;
  }

  public byte[] makeSyncCall(ApiProxy.Environment environment, String pkg, String method, byte[] bytes)
      throws ApiProxy.ApiProxyException {
    // Only interested in datastore calls
    if (pkg.equals("datastore_v3")) {
      sniffKinds(method, bytes);
    }
    return delegate.makeSyncCall(environment, pkg, method, bytes);
  }

  public Future makeAsyncCall(ApiProxy.Environment environment, String pkg, String method, byte[] bytes,
                              ApiProxy.ApiConfig apiConfig) {
    if (pkg.equals("datastore_v3")) {
      sniffKinds(method, bytes);
    }
    return delegate.makeAsyncCall(environment, pkg, method, bytes, apiConfig);
  }

  private void sniffKinds(OnestoreEntity.Reference ref) {
    OnestoreEntity.Path path = ref.getPath();
    for (OnestoreEntity.Path.Element ele : path.elements()) {
      kinds.add(ele.getType());
    }
  }

  private void sniffKinds(String method, byte[] bytes) {
    // Put is the only RPC that writes entity data so that's
    // the only one we need to sniff kinds from
    if (method.equals("Put")) {
      DatastorePb.PutRequest req = new DatastorePb.PutRequest();
      req.mergeFrom(bytes);
      for (OnestoreEntity.EntityProto e : req.entitys()) {
        sniffKinds(e.getKey());
      }
    }
  }

  public void log(ApiProxy.Environment environment, ApiProxy.LogRecord logRecord) {
    delegate.log(environment, logRecord);
  }

  /**
   * Clear out all entities of all kinds that have been written.
   */
  public void wipeData() {
    for (String kind : kinds) {
      DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
      for (Entity e : ds.prepare(new Query(kind).setKeysOnly()).asIterable()) {
        ds.delete(e.getKey());
      }
    }
  }
}
