package me.bgmp.lockpicks;

import org.bukkit.Material;

public class LockPick {
    private Material material;
    private double successRatio;

    LockPick(Material material, double successRatio) {
        this.material = material;
        this.successRatio = successRatio;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getSuccessRatio() {
        return successRatio;
    }

    public void setSuccessRatio(double successRatio) {
        this.successRatio = successRatio;
    }
}
