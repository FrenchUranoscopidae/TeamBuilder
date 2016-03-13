package uranoscopidae.teambuilder.pkmn.items;

public enum Pokeballs
{
    POKE_BALL("Poké Ball"),
    GREAT_BALL("Great Ball"),
    ULTRA_BALL("Ultra Ball"),
    MASTER_BALL("Master Ball"),
    SAFARI_BALL("Safari Ball"),
    NET_BALL("Net Ball"),
    DIVE_BALL("Dive Ball"),
    NEST_BALL("Nest Ball"),
    REPEAT_BALL("Repeat Ball"),
    TIMER_BALL("Timer Ball"),
    LUXURY_BALL("Luxury Ball"),
    PREMIER_BALL("Premier Ball"),
    DUSK_BALL("Dusk Ball"),
    HEAL_BALL("Heal Ball"),
    QUICK_BALL("Quick Ball"),
    CHERISH_BALL("Cherish Ball"),

    LURE_BALL("Lure Ball"),
    SPORT_BALL("Sport Ball"),
    MOON_BALL("Moon Ball"),
    FRIEND_BALL("Friend Ball"),
    LOVE_BALL("Love Ball"),
    LEVEL_BALL("Level Ball"),
    HEAVY_BALL("Heavy Ball"),
    PARK_BALL("Park Ball"),
    FAST_BALL("Fast Ball"),
    DREAM_BALL("Dream Ball")
    ;

    private final String itemName;

    Pokeballs(String itemName)
    {

        this.itemName = itemName;
    }

    public String getItemName()
    {
        return itemName;
    }
}
