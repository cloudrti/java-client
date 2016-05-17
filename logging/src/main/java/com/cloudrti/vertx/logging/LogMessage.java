package com.cloudrti.vertx.logging;

/**
 * Created by paulb on 12/05/16.
 */
public class LogMessage {
    private String message;
    private String level;
    private int levelAsInt;
    private String namespace;
    private String app;
    private String version;
    private String pod;
    private String thread;
    private String exception;
    private String exceptionMessage;
    private String stackTrace;

    public String getMessage() {
        return message;
    }

    public LogMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getLevel() {
        return level;
    }

    public LogMessage setLevel(String level) {
        this.level = level;
        return this;
    }

    public int getLevelAsInt() {
        return levelAsInt;
    }

    public LogMessage setLevelAsInt(int levelAsInt) {
        this.levelAsInt = levelAsInt;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public LogMessage setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getApp() {
        return app;
    }

    public LogMessage setApp(String app) {
        this.app = app;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public LogMessage setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getPod() {
        return pod;
    }

    public LogMessage setPod(String pod) {
        this.pod = pod;
        return this;
    }

    public String getThread() {
        return thread;
    }

    public LogMessage setThread(String thread) {
        this.thread = thread;
        return this;
    }

    public String getException() {
        return exception;
    }

    public LogMessage setException(String exception) {
        this.exception = exception;
        return this;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public LogMessage setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public LogMessage setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }
}
