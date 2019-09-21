package me.bgmp.lockpicks.Commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import me.bgmp.lockpicks.LockPicks;
import me.bgmp.lockpicks.Utils.ChatConstant;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LockPickCommand {
    @Command(
            aliases = {"generate"},
            desc = "Gives the command sender a lockpick."
    )
    @CommandPermissions("lockpicks.generate")
    public static void generate(final CommandContext args, final CommandSender sender) throws CommandException {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack lockpick = LockPicks.getLockPickParams.buildItemStack();
            player.getInventory().addItem(lockpick);
            player.sendMessage(ChatConstant.GENERATED_LOCKPICK.formatAsSuccess());
        } else {
            sender.sendMessage(ChatConstant.NO_CONSOLE.formatAsException());
        }
    }

    public static class LockPickParentCommand {
        @Command(
                aliases = {"lockpick"},
                desc = "LockPick parent command.",
                min = 1
        )
        @CommandPermissions("lockpícks.parent")
        @NestedCommand({LockPickCommand.class})
        public static void lockpick(final CommandContext args, final CommandSender sender) throws CommandException {
        }
    }
}
