package fr.soraxdubbing.futurespells.command;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Maybe;
import app.ashcon.intake.parametric.annotation.Text;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Commands for spells
 */
public class SpellsCommands {
    @Command(
            aliases = {"forcecast", "fc"},
            desc = "Force a player to cast a spell",
            usage = "<player> <spell>",
            perms = {"futurespells.forcecast"}
    )
    public void forceCast(@Sender CommandSender sender, Player target, Spell spell, @Maybe @Text String args) {
        if (spell == null) {
            throw new IllegalArgumentException("Spell not found");
        }

        if (args == null) {
            args = "";
        }

        String[] argsArray = args.split(" ");

        spell.cast(target, argsArray);
        sender.sendMessage("§bPlayer " + target.getName() + " forced to cast " + spell.getName());
    }
}
