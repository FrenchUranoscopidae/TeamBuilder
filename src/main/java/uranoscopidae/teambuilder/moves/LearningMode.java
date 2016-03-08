package uranoscopidae.teambuilder.moves;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

    public abstract void write(DataOutputStream out) throws IOException;

    public abstract void read(DataInputStream in) throws IOException;

    public abstract String toString();

    public enum Type
    {
        TM_HM(TMLearning.class),
        LEVELING(LevelingLearning.class),
        BREEDING,
        TUTORING,
        PRIOR_EVOLUTION;

        private final Class<? extends LearningMode> correspondingClass;

        Type()
        {
            this(null);
        }

        Type(Class<? extends LearningMode> mode)
        {
            this.correspondingClass = mode;
        }

        public Class<? extends LearningMode> getCorrespondingClass()
        {
            return correspondingClass;
        }
    }
}
