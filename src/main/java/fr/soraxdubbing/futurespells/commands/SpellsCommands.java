package fr.soraxdubbing.futurespells.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.nisovin.magicspells.Spell;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

@CommandAlias("spells|spell|sp")
public class SpellsCommands extends BaseCommand {

    @Dependency private Logger logger;

    @Subcommand("forcecast|fc")
    @Description("Force a player to cast a spell")
    @Syntax("<player> <spell> [args...]")
    @CommandCompletion("@players @spells")
    @CommandPermission("futurespells.forcecast")
    public void forceCast(CommandSender sender, OnlinePlayer target, Spell spell, @Optional String args) {
        try {
            Player player = target.getPlayer();

            if (args == null) {
                args = "";
            }

            String[] argsArray = args.split(" ");

            spell.cast(player, argsArray);
            sender.sendMessage("§bPlayer " + player.getName() + " forced to cast " + spell.getName());
        }
        catch (InvalidCommandArgument e) {
            throw e;
        }
        catch (Exception e) {
            logger.log(Level.WARNING, "SpellsCommands failed to force cast " + spell.getName(), e);
            throw new IllegalArgumentException("An error occurred while executing the command. Please check the console for more details.");
        }
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
