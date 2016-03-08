package uranoscopidae.teambuilder.moves;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LevelingLearning extends LearningMode
{
    private int level;

    protected LevelingLearning()
    {
        super(Type.LEVELING);
    }

    public LevelingLearning(int level)
    {
        this();
        this.level = level;
    }

    public int getLevel()
    {
        return level;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        out.writeInt(level);
    }

    @Override
    public void read(DataInputStream in) throws IOException
    {
        level = in.readInt();
    }

    @Override
    public String toString()
    {
        return "Leveling (level: "+level+")";
    }
}
