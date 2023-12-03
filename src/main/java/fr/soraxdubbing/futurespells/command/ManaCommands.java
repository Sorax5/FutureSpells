package fr.soraxdubbing.futurespells.command;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.mana.ManaChangeReason;
import com.nisovin.magicspells.mana.ManaHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ManaCommands {

    @Command(
            aliases = "set",
            desc = "Set max mana to a player",
            usage = "<player> <amount>",
            perms = "futurespells.mana.set.max"
    )
    public void set(@Sender CommandSender sender, Player target, int amount) {
        ManaHandler manaHandler = this.getManaHandler();
        if(amount < 0) {
            sender.sendMessage("§cYou can't set a negative amount of mana");
            return;
        }
        manaHandler.setMaxMana(target, amount);
        manaHandler.setMana(target, amount, ManaChangeReason.OTHER);
        sender.sendMessage("§aYou have added §e" + amount + "§a mana to §e" + target.getName());
        target.sendMessage("§aYour mana has been set to §e" + amount);

        manaHandler.updateManaRankIfNecessary(target);
    }

    @Command(
            aliases = "add",
            desc = "Add max mana to a player",
            usage = "<player> <amount>",
            perms = "futurespells.mana.add"
    )
    public void add(@Sender CommandSender sender, Player target, int amount) {
        ManaHandler manaHandler = this.getManaHandler();
        int maxMana = manaHandler.getMaxMana(target);
        if(amount + maxMana < 0) {
            sender.sendMessage("§cYou can't add a negative amount of mana");
            return;
        }
        manaHandler.setMaxMana(target, maxMana + amount);
        manaHandler.setMana(target, maxMana + amount, ManaChangeReason.OTHER);
        sender.sendMessage("§aYou have added §e" + amount + "§a mana to §e" + target.getName());
        target.sendMessage("§aYour mana has been added by §e" + amount);

        manaHandler.updateManaRankIfNecessary(target);
    }

    @Command(
            aliases = "remove",
            desc = "Remove max mana to a player",
            usage = "<player> <amount>",
            perms = "futurespells.mana.remove"
    )
    public void remove(@Sender CommandSender sender, Player target, int amount) {
        ManaHandler manaHandler = this.getManaHandler();
        int maxMana = manaHandler.getMaxMana(target);
        if(amount + maxMana < 0) {
            sender.sendMessage("§cYou can't remove a negative amount of mana");
            return;
        }
        manaHandler.setMaxMana(target, maxMana - amount);
        manaHandler.setMana(target, maxMana - amount, ManaChangeReason.OTHER);
        sender.sendMessage("§aYou have removed §e" + amount + "§a mana to §e" + target.getName());
        target.sendMessage("§aYour mana has been removed by §e" + amount);

        manaHandler.updateManaRankIfNecessary(target);
    }

    /*@Command(
            aliases = "reload",
            desc = "Reload mana of a player",
            usage = "<player>",
            perms = "futurespells.mana.reload"
    )
    public void reload(@Sender Player sender, Player target) {
        ManaHandler manaHandler = this.getManaHandler();
        manaHandler.setMana(target, manaHandler.getMaxMana(target), ManaChangeReason.OTHER);
        sender.sendMessage("§aYou have reloaded mana of §e" + target.getName());
        target.sendMessage("§aYour mana has been reloaded");
    }*/

    private ManaHandler getManaHandler() {
        return MagicSpells.getManaHandler();
    }
}
