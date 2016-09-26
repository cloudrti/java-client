package com.cloudrti.client.api.flightcontrols;

import java.util.List;

public class FlightControlDescription {
	private final String m_name;
	private final List<String> m_arguments;

	public FlightControlDescription(String name, List<String> arguments) {
		m_name = name;
		m_arguments = arguments;
	}
	
	public String getName() {
		return m_name;
	}



	public List<String> getArguments() {
		return m_arguments;
	}



	public static FlightControlDescription fromFlightControl(FlightControl control) {
		return new FlightControlDescription(control.getName(), control.getArgumentNames());
	}
}
