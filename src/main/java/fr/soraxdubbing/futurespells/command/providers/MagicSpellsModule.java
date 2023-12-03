package fr.soraxdubbing.futurespells.command.providers;

import app.ashcon.intake.parametric.AbstractModule;
import com.nisovin.magicspells.Spell;

/**
 * Module for MagicSpells providers
 */
public class MagicSpellsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Spell.class).toProvider(new SpellsProvider());
    }
}
