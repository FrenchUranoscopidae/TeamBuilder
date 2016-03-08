package uranoscopidae.teambuilder.moves;

import uranoscopidae.teambuilder.Type;

public class MoveDefinition
{
    private final Type type;
    private final MoveCategory category;
    private final String englishName;

    private final int power;
    private final int accuracy;
    private final int powerPoints;

    public MoveDefinition(Type type, MoveCategory category, String englishName, int power, int accuracy, int powerPoints)
    {
        this.type = type;
        this.category = category;
        this.englishName = englishName;
        this.power = power;
        this.accuracy = accuracy;
        this.powerPoints = powerPoints;
    }

    public int getAccuracy()
    {
        return accuracy;
    }

    public MoveCategory getCategory()
    {
        return category;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public int getPower()
    {
        return power;
    }

    public int getPowerPoints()
    {
        return powerPoints;
    }

    public Type getType()
    {
        return type;
    }
}
