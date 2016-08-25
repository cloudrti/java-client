package com.cloudrti.client.vertx.sensors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class VmStatsVerticle extends AbstractVerticle{

	private final static Logger log = LoggerFactory.getLogger(VmStatsVerticle.class);
	private final ObjectMapper mapper = new ObjectMapper();

	private Producer<byte[], byte[]> producer;

	@Override
	public void stop() throws Exception {
		producer.close();
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		Properties props = new Properties();
		props.put("bootstrap.servers", getProperty("KAFKA"));
		props.put("retries", 0);
		props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

		producer = new KafkaProducer<>(props);

		AtomicLong lastGcCollections = new AtomicLong();
		AtomicLong lastGcTime = new AtomicLong();

		postSensorData(lastGcCollections, lastGcTime);
		vertx.setPeriodic(60000, t -> postSensorData(lastGcCollections, lastGcTime));

		startFuture.complete();
	}

	private void postSensorData(AtomicLong lastGcCollections, AtomicLong lastGcTime) {
		log.info("Posting sensor data");

		java.lang.management.MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

		Metric usedMem = new Metric("memory_used", memoryUsage.getUsed());
		Metric committedMem = new Metric("memory_committed", memoryUsage.getCommitted());
		Metric maxMem = new Metric("memory_max", memoryUsage.getMax());

		CpuStats cpuStats = getTotalCpu();
		Double processLoad = cpuStats.processCpuLoad * 100;
		Metric cpu = new Metric("cpu_processload", processLoad.longValue());

		Metric threads = new Metric("threads", ManagementFactory.getThreadMXBean().getAllThreadIds().length);

		GcStats gcStats = getGcStats();
		Metric gcCollections = new Metric("gc_collections", gcStats.totalCollections - lastGcCollections.getAndSet(gcStats.totalCollections));
		Metric gcTime = new Metric("gc_time", gcStats.totalTime - lastGcTime.getAndSet(gcStats.totalTime));

		writeStats(usedMem, committedMem, maxMem, cpu, threads, gcCollections, gcTime);
	}

	private void writeStats(Metric... stats) {
		Stream.of(stats).forEach(metric -> {
			try {
				producer.send(new ProducerRecord<>(getTopic(), mapper.writeValueAsBytes(metric)));
			} catch (JsonProcessingException e) {
				log.error("Error converting metric to json", e);
			}
		});
	}

	private String getTopic() {
		return "cloudrti.logging." + getProperty("POD_NAMESPACE");
	}

	private CpuStats getTotalCpu() {
		try {
			OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
			
			Method m = os.getClass().getMethod("getProcessCpuTime", (Class<?>[]) null);
			m.setAccessible(true);
			
			Method m2 = os.getClass().getMethod("getProcessCpuLoad", (Class<?>[]) null);
			m2.setAccessible(true);
			long processCpuTime = (Long) m.invoke(os, (Object[]) null);
			double processCpuLoad = (Double) m2.invoke(os, (Object[]) null);
			
			return new CpuStats(processCpuTime, processCpuLoad * 100);
		} catch (Exception e) {
			log.error("Error getting CPU stats", e);
		}
		
		return new CpuStats(0, 0);
	}
	
	private GcStats getGcStats() {
	    long totalGarbageCollections = 0;
	    long garbageCollectionTime = 0;

	    for(GarbageCollectorMXBean gc :
	            ManagementFactory.getGarbageCollectorMXBeans()) {

	        long count = gc.getCollectionCount();

	        if(count >= 0) {
	            totalGarbageCollections += count;
	        }

	        long time = gc.getCollectionTime();

	        if(time >= 0) {
	            garbageCollectionTime += time;
	        }
	    }

	    return new GcStats(totalGarbageCollections, garbageCollectionTime);
	}
	
	private class GcStats {
		final long totalCollections;
		final long totalTime;

		public GcStats(long totalCollections, long totalTime) {
			this.totalCollections = totalCollections;
			this.totalTime = totalTime;
		}
	}

	private class CpuStats {
		final long totalTime;
		final double processCpuLoad;
		public CpuStats(long totalTime, double processCpuLoad) {
			this.totalTime = totalTime;
			this.processCpuLoad = processCpuLoad;
		}
	}

	private String getProperty(String name) {
		String value = System.getProperty(name);
		if(value == null) {
			value = System.getenv(name);
		}

		return value;
	}
}
