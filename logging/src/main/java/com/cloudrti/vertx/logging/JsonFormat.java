package com.cloudrti.vertx.logging;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonFormat extends LayoutBase<LoggingEvent> {

    private final InfraNaming infraNaming = new InfraNaming();

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String doLayout(LoggingEvent event) {


        LogMessage logMessage = new LogMessage()
                .setMessage(event.getFormattedMessage())
                .setLevel(event.getLevel().levelStr)
                .setLevelAsInt(event.getLevel().levelInt)
                .setNamespace(infraNaming.getNamespace())
                .setApp(infraNaming.getAppName())
                .setVersion(infraNaming.getVersion())
                .setPod(infraNaming.getPodName())
                .setThread(event.getThreadName());

        if (event.getThrowableProxy() != null) {
            logMessage.setException(event.getThrowableProxy().getClassName())
                    .setExceptionMessage(event.getThrowableProxy().getMessage())
                    .setStackTrace(ThrowableProxyUtil.asString(event.getThrowableProxy()));

        }


        try {
            return mapper.writeValueAsString(logMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }
}
