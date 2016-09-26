package com.cloudrti.client.api.flightcontrols;

import rx.Observable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface FlightControl {
    String getName();
    default List<String> getArgumentNames() { return Collections.emptyList(); };
    Observable<String> execute(Map<String, String> data);
}
