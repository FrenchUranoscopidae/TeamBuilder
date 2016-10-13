package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.MoveExtractor;
import uranoscopidae.teambuilder.pkmn.moves.MoveInfos;

import java.io.IOException;
import java.util.List;

public class MovesRefresher extends Refresher<MoveInfos>
{

    private final MoveExtractor extractor;
    private final TeamBuilderApp app;

    public MovesRefresher(Settings settings, TeamBuilderApp app)
    {
        super("Moves", settings);
        this.app = app;
        extractor = new MoveExtractor();
    }

    @Override
    public List<MoveInfos> init() throws IOException
    {
        return extractor.findAllMoves();
    }

    @Override
    public void handle(MoveInfos part) throws IOException
    {
        app.registerMove(part);
    }

    @Override
    public String getText(MoveInfos part) throws IOException
    {
        return "Retrieved "+part.getEnglishName();
    }
}
