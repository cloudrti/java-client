package com.cloudrti.client.vertx.sensors;

import com.cloudrti.client.api.env.InfraNaming;

public class Metric  {
	private String metricType;
	private long value;
	protected String appVersion;
	protected String namespace;
	protected String app;
	protected String pod;

	public Metric() {
	}
	
	public Metric(String type, long value) {
		this.metricType = type;
		this.value = value;

		InfraNaming infraNaming = new InfraNaming();
		this.namespace = infraNaming.getNamespace();
		this.app = infraNaming.getAppName();
		this.appVersion = infraNaming.getVersion();
		this.pod = infraNaming.getPodName();
	}

	public String getMetricType() {
		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getPod() {
		return pod;
	}

	public void setPod(String pod) {
		this.pod = pod;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
