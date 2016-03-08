package uranoscopidae.teambuilder.moves;

public class LevelingLearning extends LearningMode
{
    private final int level;

    public LevelingLearning(int level)
    {
        super(Type.LEVELING);
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }
}
