package uranoscopidae.teambuilder.moves;

public class Move
{
    private final MoveDefinition definition;
    private final LearningMode mode;

    public Move(MoveDefinition definition, LearningMode mode)
    {
        this.definition = definition;
        this.mode = mode;
    }

    public LearningMode getLearningMode()
    {
        return mode;
    }

    public MoveDefinition getDefinition()
    {
        return definition;
    }
}
