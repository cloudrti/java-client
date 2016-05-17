package com.cloudrti.client.vertx.snapshots;

import com.cloudrti.client.api.snapshots.Snapshot;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by paulb on 10/05/16.
 */
public class ThreadDumpSnapshot implements Snapshot {

    private static final String NEWLINE = System.getProperty("line.separator");

    @Override
    public String getName() {
        return "threaddump";
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String, Object> details = new HashMap<>();

        StringWriter sw = new StringWriter();
        try {
            dumpStack(sw);
        } catch (IOException e) {
            e.printStackTrace();
        }

        details.put("threads", sw.toString());

        return details;
    }

    public void dumpStack(Writer writer) throws IOException {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
        Map<Long, ThreadInfo> threadInfoMap = new HashMap<Long, ThreadInfo>();
        for (ThreadInfo threadInfo : threadInfos) {
            threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
        }

        try {
            Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
            writer.write("Dump of " + stacks.size() + " threads at "
                    + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(System.currentTimeMillis()))
                    + NEWLINE + NEWLINE);
            for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
                Thread thread = entry.getKey();
                writer.write("\"" + thread.getName() + "\" prio=" + thread.getPriority() + " tid=" + thread.getId()
                        + " " + thread.getState() + " " + (thread.isDaemon() ? "deamon" : "worker") + NEWLINE);
                ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
                if (threadInfo != null) {
                    writer.write("    native=" + threadInfo.isInNative() + ", suspended=" + threadInfo.isSuspended()
                            + ", block=" + threadInfo.getBlockedCount() + ", wait=" + threadInfo.getWaitedCount()
                            + NEWLINE);
                    writer.write("    lock="
                            + threadInfo.getLockName()
                            + " owned by "
                            + threadInfo.getLockOwnerName()
                            + " ("
                            + threadInfo.getLockOwnerId()
                            + "), cpu="
                            + mxBean.getThreadCpuTime(threadInfo.getThreadId())
                            + ", user="
                            + mxBean.getThreadUserTime(threadInfo.getThreadId())
                            + NEWLINE);
                }
                for (StackTraceElement element : entry.getValue()) {
                    writer.write("    ");
                    String eleStr = element.toString();
                    if (eleStr.startsWith("com.mprew")) {
                        writer.write(">>  ");
                    } else {
                        writer.write("    ");
                    }
                    writer.write(eleStr);
                    writer.write(NEWLINE);
                }
                writer.write(NEWLINE);
            }
            writer.write("------------------------------------------------------");
            writer.write(NEWLINE);
            writer.write("Non-daemon threads: ");
            for (Thread thread : stacks.keySet()) {
                if (!thread.isDaemon()) {
                    writer.write("\"" + thread.getName() + "\", ");
                }
            }
            writer.write(NEWLINE);
            writer.write("------------------------------------------------------");
            writer.write(NEWLINE);
            writer.write("Blocked threads: ");
            for (Thread thread : stacks.keySet()) {
                if (thread.getState() == Thread.State.BLOCKED) {
                    writer.write("\"" + thread.getName() + "\", ");
                }
            }
            writer.write(NEWLINE);
        } finally {
            writer.close();
        }
    }

    @Override
    public String getDescription() {
        return "Creates a full thread dump";
    }

    @Override
    public String getDataType() {
        return "threaddump";
    }
}
