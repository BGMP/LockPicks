package me.bgmp.lockpicks;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LockPick {
    private Material material = Material.getMaterial(LockPicks.getPlugin.getConfig().getString("lockpick.item.material"));
    private String name = ChatColor.translateAlternateColorCodes('&', LockPicks.getPlugin.getConfig().getString("lockpick.item.name"));
    private List<String> lore = LockPicks.getPlugin.getConfig().getStringList("lockpick.item.lore");
    private double successRatio = LockPicks.getPlugin.getConfig().getDouble("lockpick.ratio");
    private double damage = LockPicks.getPlugin.getConfig().getDouble("lockpick.damage");
    private String onDoorCrackMessage = ChatColor.translateAlternateColorCodes('&', LockPicks.getPlugin.getConfig().getString("lockpick.successMessage"));
    private String onDamageMessage = ChatColor.translateAlternateColorCodes('&', LockPicks.getPlugin.getConfig().getString("lockpick.failMessage"));
    private Sound crackSound = Sound.valueOf(LockPicks.getPlugin.getConfig().getString("lockpick.crackSound.effect"));
    private int crackSoundv = LockPicks.getPlugin.getConfig().getInt("lockpick.crackSound.v");
    private int crackSoundv1 = LockPicks.getPlugin.getConfig().getInt("lockpick.crackSound.v1");
    private Sound damageSound = Sound.valueOf(LockPicks.getPlugin.getConfig().getString("lockpick.damageSound.effect"));
    private int damageSoundv = LockPicks.getPlugin.getConfig().getInt("lockpick.damageSound.v");
    private int damageSoundv1 = LockPicks.getPlugin.getConfig().getInt("lockpick.damageSound.v1");

    LockPick() {
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public double getSuccessRatio() {
        return successRatio;
    }

    public void setSuccessRatio(double successRatio) {
        this.successRatio = successRatio;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public String getOnDoorCrackMessage() {
        return onDoorCrackMessage;
    }

    public void setOnDoorCrackMessage(String onDoorCrackMessage) {
        this.onDoorCrackMessage = onDoorCrackMessage;
    }

    public String getOnDamageMessage() {
        return onDamageMessage;
    }

    public void setOnDamageMessage(String onDamageMessage) {
        this.onDamageMessage = onDamageMessage;
    }

    public Sound getCrackSound() {
        return crackSound;
    }

    public void setCrackSound(Sound crackSound) {
        this.crackSound = crackSound;
    }

    public int getCrackSoundv() {
        return crackSoundv;
    }

    public void setCrackSoundv(int crackSoundv) {
        this.crackSoundv = crackSoundv;
    }

    public int getCrackSoundv1() {
        return crackSoundv1;
    }

    public void setCrackSoundv1(int crackSoundv1) {
        this.crackSoundv1 = crackSoundv1;
    }

    public Sound getDamageSound() {
        return damageSound;
    }

    public void setDamageSound(Sound damageSound) {
        this.damageSound = damageSound;
    }

    public int getDamageSoundv() {
        return damageSoundv;
    }

    public void setDamageSoundv(int damageSoundv) {
        this.damageSoundv = damageSoundv;
    }

    public int getDamageSoundv1() {
        return damageSoundv1;
    }

    public void setDamageSoundv1(int damageSoundv1) {
        this.damageSoundv1 = damageSoundv1;
    }

    public ItemStack buildItemStack() {
       ItemStack lockPickItem = new ItemStack(material);
       ItemMeta lockPickItemMeta = lockPickItem.getItemMeta();

       lockPickItemMeta.setDisplayName(name);
       lockPickItemMeta.setLore(lore);

       lockPickItem.setItemMeta(lockPickItemMeta);

       return lockPickItem;
    }
}
