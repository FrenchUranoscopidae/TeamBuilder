package uranoscopidae.teambuilder.moves;

public abstract class LearningMode
{

    private final Type type;

    public LearningMode(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public enum Type
    {
        TM_HM,
        LEVELING,
        BREEDING,
        TUTORING,
        PRIOR_EVOLUTION;
    }
}
