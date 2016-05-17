package com.cloudrti.client.api.snapshots;

import java.util.Map;

/**
 * Created by paulb on 10/05/16.
 */
public interface Snapshot {
    String getName();
    String getDescription();
    String getDataType();
    Map<String, Object> getData();
}
