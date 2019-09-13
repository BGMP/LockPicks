package me.bgmp.lockpicks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ApartmentDoor {
    private UUID id;
    private Player owner = null;
    private boolean rented = false;
    private Block door;
    private Block sign;
    private List<String> signForRentContent = LockPicks.getPlugin.getConfig().getStringList("apartment.forRentSignContent");
    private List<String> signRentedContent = LockPicks.getPlugin.getConfig().getStringList("apartment.rentedSignContent");
    private double price;

    public ApartmentDoor(UUID id, Block door, Block sign, double price) {
        this.id = id;
        this.door = door;
        this.sign = sign;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public Block getDoor() {
        return door;
    }

    public void setDoor(Block door) {
        this.door = door;
    }

    public Block getSign() {
        return sign;
    }

    public void setSign(Block sign) {
        this.sign = sign;
    }

    public List<String> getSignForRentContent() {
        return signForRentContent;
    }

    public void setSignForRentContent(List<String> signForRentContent) {
        this.signForRentContent = signForRentContent;
    }

    public List<String> getSignRentedContent() {
        return signRentedContent;
    }

    public void setSignRentedContent(List<String> signRentedContent) {
        this.signRentedContent = signRentedContent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public static boolean isApartmentDoor(Location signLocation) {
        return LockPicks.getApartmentDoorsRegistry.getApartmentBySignLocation(signLocation) != null;
    }

    public void setForRentSignContent() {
        new BukkitRunnable() {
            @Override
            public void run() {
                AtomicInteger lineCount = new AtomicInteger(signForRentContent.size() - 4);
                Sign signInstance = (Sign) sign.getState();
                signForRentContent.forEach(contentLine -> {
                    signInstance.setLine(lineCount.get(), ChatColor.translateAlternateColorCodes('&', contentLine.replaceAll("%price%", String.valueOf(price))));
                    lineCount.getAndIncrement();
                });
                signInstance.update();
                this.cancel();
            }
        }.runTaskTimer(LockPicks.getPlugin, 0L, 0L);
    }

    public void setRentedSignContent() {
        Sign signInstance = (Sign) sign.getState();
        AtomicInteger lineCount = new AtomicInteger(signForRentContent.size() - 4);
        signRentedContent.forEach(contentLine -> {
            signInstance.setLine(lineCount.get(), ChatColor.translateAlternateColorCodes('&', contentLine.replaceAll("%owner%", owner.getDisplayName())));
            signInstance.update();
            lineCount.getAndIncrement();
        });
        signInstance.update();
    }

    public void evict() {
        setOwner(null);
        setRented(false);
        setForRentSignContent();
        touchRegistry();
    }

    public void touchRegistry() {
        String key = id.toString();

        if (LockPicks.getApartmentDoorsRegistry.getApartmentBySignLocation(sign.getLocation()) == null) {
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().add(new ApartmentDoor(id, door, sign, price));
        }

        if (owner == null) LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".owner", "none");
        else LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".owner", owner.getName());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".rented", rented);
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".door.meta", door.toString());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".door.location.x", door.getLocation().getX());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".door.location.y", door.getLocation().getY());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".door.location.z", door.getLocation().getZ());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".sign.meta", sign.toString());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".sign.location.x", sign.getLocation().getX());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".sign.location.y", sign.getLocation().getY());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".sign.location.z", sign.getLocation().getZ());
        LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key + ".price", price);
        LockPicks.getApartmentDoorsRegistry.saveRegistry();
    }

    public static class ApartmentDoorsRegistry {
        private List<ApartmentDoor> apartmentDoors;
        private File registryFile = new File(LockPicks.getPlugin.getDataFolder(), "registry.yml");
        private FileConfiguration registryFileConfiguration = YamlConfiguration.loadConfiguration(registryFile);

        ApartmentDoorsRegistry(List<ApartmentDoor> apartmentDoors) {
            this.apartmentDoors = apartmentDoors;
        }

        public List<ApartmentDoor> getApartmentDoors() {
            return apartmentDoors;
        }

        public void setApartmentDoors(List<ApartmentDoor> apartmentDoors) {
            this.apartmentDoors = apartmentDoors;
        }

        public File getRegistryFile() {
            return registryFile;
        }

        public void setRegistryFile(File registryFile) {
            this.registryFile = registryFile;
        }

        public FileConfiguration getRegistryFileConfiguration() {
            return registryFileConfiguration;
        }

        public void setRegistryFileConfiguration(FileConfiguration registryFileConfiguration) {
            this.registryFileConfiguration = registryFileConfiguration;
        }

        public void setUp() {
            try {
                if (!registryFile.exists()) {
                    boolean success = registryFile.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        public void saveRegistry() {
            try {
                registryFileConfiguration.save(registryFile);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        public void reloadRegistry() {
            registryFileConfiguration = YamlConfiguration.loadConfiguration(registryFile);
        }

        public void register(ApartmentDoor apartmentDoor) {
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().add(apartmentDoor);
        }

        public void unregister(ApartmentDoor apartmentDoor) {
            String key = apartmentDoor.getId().toString();
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().remove(apartmentDoor);
            LockPicks.getApartmentDoorsRegistry.getRegistryFileConfiguration().set(key, null);
            LockPicks.getApartmentDoorsRegistry.saveRegistry();
        }

        public void load() {
            registryFileConfiguration.getKeys(false).forEach(key -> {
                String id = key;

                /*
                *
                * As players that disconnect lose their apartments, this two thingies aren't needed,
                * yet are obtainable.
                *
                -> String owner = registryFileConfiguration.getString(key + ".owner");
                -> boolean rented = registryFileConfiguration.getBoolean(key + ".rented");
                *
                */

                double doorX = registryFileConfiguration.getDouble(key + ".door.location.x");
                double doorY = registryFileConfiguration.getDouble(key + ".door.location.y");
                double doorZ = registryFileConfiguration.getDouble(key + ".door.location.z");

                double signX = registryFileConfiguration.getDouble(key + ".sign.location.x");
                double signY = registryFileConfiguration.getDouble(key + ".sign.location.y");
                double signZ = registryFileConfiguration.getDouble(key + ".sign.location.z");

                double price = registryFileConfiguration.getDouble(key + ".price");

                // TODO: Add multiple worlds support
                Location doorLocation = new Location(Bukkit.getWorlds().get(0), doorX, doorY, doorZ);
                Location signLocation = new Location(Bukkit.getWorlds().get(0), signX, signY, signZ);

                Block door = Bukkit.getWorlds().get(0).getBlockAt(doorLocation);
                Block sign = Bukkit.getWorlds().get(0).getBlockAt(signLocation);

                ApartmentDoor apartmentDoor = new ApartmentDoor(UUID.fromString(id), door, sign, price);

                apartmentDoor.setOwner(null);
                apartmentDoor.setRented(false);

                // TODO: Update apartments sign's content upon reload.

                LockPicks.getApartmentDoorsRegistry.register(apartmentDoor);
                apartmentDoor.touchRegistry();
            });
        }

        public ApartmentDoor getApartmentBySignLocation(Location signLocationToCompare) {
            AtomicReference<ApartmentDoor> apartmentDoorMatch = new AtomicReference<>();
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().forEach(apartmentDoor -> {
                // Apparently Locations can't be ==? yet turning them into Strings seems to do the trick
                if (apartmentDoor.getSign().getLocation().toString().equals(signLocationToCompare.toString())) {
                    apartmentDoorMatch.set(apartmentDoor);
                }
            });
            return apartmentDoorMatch.get();
        }

        public boolean doorIsRegistered(Block door) {
            AtomicBoolean isRegistered = new AtomicBoolean(false);
            String inputDoor = door.toString();
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().forEach(apartmentDoor -> {
                String doorInstance = apartmentDoor.getDoor().toString();
                if (doorInstance.equals(inputDoor)) {
                    isRegistered.set(true);
                }
            });
            return isRegistered.get();
        }

        public List<ApartmentDoor> getPlayerApartments(Player owner) {
            List<ApartmentDoor> ownedApartments = new ArrayList<>();
            LockPicks.getApartmentDoorsRegistry.getApartmentDoors().forEach(apartmentDoor -> {
                if (apartmentDoor.getOwner().getName().equals(owner.getName())) {
                    ownedApartments.add(apartmentDoor);
                }
            });
            return ownedApartments;
        }
    }
}
