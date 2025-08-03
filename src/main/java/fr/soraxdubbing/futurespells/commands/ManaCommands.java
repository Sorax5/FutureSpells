package fr.soraxdubbing.futurespells.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.mana.ManaHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

@CommandAlias("chakra|cha|ck")
public class ManaCommands extends BaseCommand {

    @Dependency private ManaHandler manaHandler;
    @Dependency private Logger logger;

    @Subcommand("set")
    @Description("Set max mana to a player")
    @Syntax("<player> <amount>")
    @CommandPermission("futurespells.mana.set.max")
    public void set(CommandSender sender, OnlinePlayer target, int amount) {
        try {
            Player player = target.getPlayer();
            if(amount <= 0) {
                throw new InvalidCommandArgument("The amount of mana must be greater than 0.");
            }
            manaHandler.setMaxMana(player, amount);
            manaHandler.setMana(player, amount, ManaChangeReason.OTHER);

            sender.sendMessage("§aYou have set §e" + player.getName() + "§a's mana to §e" + amount);
            player.sendMessage("§aYour mana has been set to §e" + amount);

            manaHandler.updateManaRankIfNecessary(player);
        }
        catch(InvalidCommandArgument e) {
            throw e;
        }
        catch (Exception e) {
            logger.severe("An error occurred while setting mana: " + e.getMessage());
            throw new InvalidCommandArgument("An error occurred while setting mana. Please check the console for more details.");
        }
    }

    @Subcommand("add")
    @Description("Add max mana to a player")
    @Syntax("<player> <amount>")
    @CommandPermission("futurespells.mana.add")
    public void add(CommandSender sender, OnlinePlayer target, int amount) {
        try {
            Player player = target.getPlayer();
            if(amount <= 0) {
                throw new InvalidCommandArgument("The amount of mana must be greater than 0.");
            }

            int maxMana = manaHandler.getMaxMana(player);

            if (amount + maxMana <= 0) {
                throw new InvalidCommandArgument("You cannot add a negative amount of mana that results in zero or less max mana.");
            }

            manaHandler.setMaxMana(player, maxMana + amount);
            manaHandler.setMana(player, maxMana + amount, ManaChangeReason.OTHER);

            sender.sendMessage("§aYou have added §e" + amount + "§a mana to §e" + player.getName());
            player.sendMessage("§aYour mana has been increased by §e" + amount);

            manaHandler.updateManaRankIfNecessary(player);
        }
        catch(InvalidCommandArgument e) {
            throw e;
        }
        catch (Exception e) {
            logger.severe("An error occurred while adding mana: " + e.getMessage());
            throw new InvalidCommandArgument("An error occurred while adding mana. Please check the console for more details.");
        }
    }

    @Subcommand("remove")
    @Description("Remove max mana from a player")
    @Syntax("<player> <amount>")
    @CommandPermission("futurespells.mana.remove")
    public void remove(CommandSender sender, OnlinePlayer target, int amount) {
        try {
            Player player = target.getPlayer();
            if(amount <= 0) {
                throw new InvalidCommandArgument("The amount of mana must be greater than 0.");
            }

            int maxMana = manaHandler.getMaxMana(player);

            if (maxMana - amount < 0) {
                throw new InvalidCommandArgument("You cannot remove a negative amount of mana that results in less than zero max mana.");
            }

            manaHandler.setMaxMana(player, maxMana - amount);
            manaHandler.setMana(player, maxMana - amount, ManaChangeReason.OTHER);

            sender.sendMessage("§aYou have removed §e" + amount + "§a mana from §e" + player.getName());
            player.sendMessage("§aYour mana has been decreased by §e" + amount);

            manaHandler.updateManaRankIfNecessary(player);
        }
        catch(InvalidCommandArgument e) {
            throw e;
        }
        catch (Exception e) {
            logger.severe("An error occurred while removing mana: " + e.getMessage());
            throw new InvalidCommandArgument("An error occurred while removing mana. Please check the console for more details.");
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
