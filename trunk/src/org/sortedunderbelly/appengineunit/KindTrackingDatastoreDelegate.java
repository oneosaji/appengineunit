package org.sortedunderbelly.appengineunit;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.DatastorePb;
import com.google.storage.onestore.v3.OnestoreEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ApiProxy.Delegate} implementation that keeps track of the kinds of
 * all {@link Entity Entities} that get written to the datastore.
 *
 * @author Max Ross <maxr@google.com>
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
