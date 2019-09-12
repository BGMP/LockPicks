package me.bgmp.lockpicks.Utils;

import org.bukkit.ChatColor;

public enum ChatConstant {
    /* Success constants */
    RELOADED_CONFIG("Configuration reloaded successfully."),
    CREATED_APARTMENT("Apartment successfully created"),
    DESTROYED_APARTMENT("Apartment successfully destroyed"),
    APARTMENT_PURCHASED("Apartment successfully purchased!"),
    /* Exception constants */
    NO_PERMISSION("You do not have permission."),
    NUMBER_STRING_EXCEPTION("Expected a number, string received instead."),
    UNKNOWN_ERROR("An unknown error has occurred."),
    NOT_ENOUGH_MONEY("You do not have enough money."),
    ALREADY_OWNED("This apartment is already owned by ");

    private String message;

    ChatConstant(String message) {
        this.message = message;
    }

    public String formatAsSuccess() {
        return ChatColor.GREEN + message;
    }

    public String formatAsException() {
        return ChatColor.YELLOW + "⚠ " + ChatColor.RED + message;
    }
}
