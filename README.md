# FutureSpells

FutureSpells est un plugin Minecraft qui ajoute un système de mana persistant et des sorts personnalisés à votre serveur.

## Commandes

### Mana
Alias : `/chakra`, `/cha`, `/ck`

- `/chakra set <joueur> <montant>`
  - Définit la quantité maximale de mana d'un joueur et la remplit.
  - Permission : `futurespells.mana.set.max`

- `/chakra add <joueur> <montant>`
  - Ajoute de la mana maximale à un joueur et la remplit.
  - Permission : `futurespells.mana.add`

- `/chakra remove <joueur> <montant>`
  - Retire de la mana maximale à un joueur et ajuste la mana actuelle.
  - Permission : `futurespells.mana.remove`

- `/chakra help`
  - Affiche l'aide des commandes mana.
  - Permission : aucune (accessible à tous)

### Spells
Alias : `/spells`, `/spell`, `/sp`

- `/spells forcecast <joueur> <sort> [arguments...]`
  - Force un joueur à lancer un sort avec des arguments optionnels.
  - Permission : `futurespells.forcecast`

- `/spells help`
  - Affiche l'aide des commandes de sorts.
  - Permission : aucune (accessible à tous)

## Permissions

- `futurespells.mana.set.max` : Permet de définir la mana maximale d'un joueur.
- `futurespells.mana.add` : Permet d'ajouter de la mana maximale à un joueur.
- `futurespells.mana.remove` : Permet de retirer de la mana maximale à un joueur.
- `futurespells.forcecast` : Permet de forcer un joueur à lancer un sort.

## Dépendances
- [MagicSpells](https://dev.bukkit.org/projects/magicspells)

## Support
Pour toute question ou suggestion, contactez le développeur ou ouvrez une issue sur le dépôt GitHub.

