package com.cloudrti.client.api.snapshots;

public class SnapshotDescription {
    private final String m_name;
    private final String m_description;
    private final String m_dataType;

    public SnapshotDescription(String name, String description, String dataType) {
        m_name = name;
        m_description = description;
        m_dataType = dataType;
    }

    public String getName() {
        return m_name;
    }

    public String getDescription() {
        return m_description;
    }

    public String getDataType() {
        return m_dataType;
    }
}
