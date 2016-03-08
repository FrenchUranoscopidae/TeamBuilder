package uranoscopidae.teambuilder.moves;

import uranoscopidae.teambuilder.items.Item;

public class TMLearning extends LearningMode
{
    private final Item from;

    public TMLearning(Item from)
    {
        super(Type.TM_HM);
        this.from = from;
    }

    public Item getFrom()
    {
        return from;
    }
}
