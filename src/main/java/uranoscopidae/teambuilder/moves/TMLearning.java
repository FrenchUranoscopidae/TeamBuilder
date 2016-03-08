package uranoscopidae.teambuilder.moves;

import uranoscopidae.teambuilder.items.Item;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TMLearning extends LearningMode
{
    private Item from;

    protected TMLearning()
    {
        super(Type.TM_HM);
    }

    public TMLearning(Item from)
    {
        this();
        this.from = from;
    }

    public Item getFrom()
    {
        return from;
    }

    @Override
    public void write(DataOutputStream out) throws IOException
    {
        // TODO
    }

    @Override
    public void read(DataInputStream in) throws IOException
    {
        // TODO
    }

    @Override
    public String toString()
    {
        return "TM/HM ("+"TODO"+")";
    }
}
