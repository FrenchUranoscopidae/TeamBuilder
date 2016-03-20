package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.AbilityExtractor;
import uranoscopidae.teambuilder.pkmn.Ability;

import java.io.IOException;
import java.util.List;

public class AbilitiesRefresher extends Refresher<Ability>
{

    private final AbilityExtractor extractor;
    private final TeamBuilderApp app;

    public AbilitiesRefresher(Settings settings, TeamBuilderApp app)
    {
        super("Abilities", settings);
        this.app = app;
        extractor = new AbilityExtractor();
    }

    @Override
    public List<Ability> init() throws IOException
    {
        return extractor.findAllAbilities();
    }

    @Override
    public void handle(Ability part) throws IOException
    {
        app.registerAbility(part);
    }

    @Override
    public String getText(Ability part) throws IOException
    {
        return "Retrieved "+part.getEnglishName();
    }
}
