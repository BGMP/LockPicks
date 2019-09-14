package me.bgmp.lockpicks.Utils;

public enum Permission {
    APARTMENTS_OVERRIDE("lockpicks.apartments.override", "Overrides access to apartments, providing full permissions over them."),
    APARTMENT_CREATE("lockpicks.apartments.create", "Allows access to create apartment doors."),
    APARTMENT_DESTROY("lockpicks.apartments.destroy", "Allows access to destroy existing apartment doors."),
    APARTMENT_RENT("lockpicks.apartments.rent", "Allows access to rent apartment doors.");

    private String node;
    private String description;

    Permission(String node, String description) {
        this.node = node;
        this.description = description;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
