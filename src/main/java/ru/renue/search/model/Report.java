package ru.renue.search.model;

public class Report {
    private final String guid;
    private final String codeName;
    private final String description;

    public Report(String guid, String codeName, String description) {
        this.guid = guid;
        this.codeName = codeName;
        this.description = description;
    }

    public String getGuid() {
        return guid;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Report{" +
                "guid='" + guid + '\'' +
                ", codeName='" + codeName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
