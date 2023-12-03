package fr.soraxdubbing.futurespells.command.providers;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.bukkit.parametric.provider.BukkitProvider;
import app.ashcon.intake.parametric.ProvisionException;
import com.nisovin.magicspells.MagicSpells;
import com.nisovin.magicspells.Spell;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Provider for spells (all)
 */
public class SpellsProvider implements BukkitProvider<Spell> {
    @Override
    public Spell get(CommandSender commandSender, CommandArgs commandArgs, List<? extends Annotation> list) throws ArgumentException, ProvisionException {
        if(!commandArgs.hasNext()){
            throw new ArgumentException("Spell name is required");
        }

        String spellName = commandArgs.next();

        Spell spell = MagicSpells.getSpellByInGameName(spellName);

        if(spell == null){
            spell = MagicSpells.getSpellByInternalName(spellName);
        }

        return spell;
    }

    @Override
    public List<String> getSuggestions(String prefix, CommandSender sender, Namespace namespace, List<? extends Annotation> mods) {
        List<String> suggestions = new ArrayList<>();

        for (Spell spell : MagicSpells.spells()) {
            if (spell.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                suggestions.add(spell.getName());
            }
        }

        return suggestions;
    }

    @Override
    public String getName() {
        return "spell";
    }
}
