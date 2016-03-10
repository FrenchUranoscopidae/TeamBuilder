package uranoscopidae.teambuilder.app.refreshers;

import uranoscopidae.teambuilder.app.Settings;
import uranoscopidae.teambuilder.app.TeamBuilderApp;
import uranoscopidae.teambuilder.init.MoveExtractor;
import uranoscopidae.teambuilder.pkmn.moves.Move;

import java.io.IOException;
import java.util.List;

public class MovesRefresher extends Refresher<Move>
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
    public List<Move> init() throws IOException
    {
        return extractor.findAllMoves();
    }

    @Override
    public void handle(Move part) throws IOException
    {
        app.registerMove(part);
    }

    @Override
    public String getText(Move part) throws IOException
    {
        return "Retrieved "+part.getEnglishName();
    }
}
