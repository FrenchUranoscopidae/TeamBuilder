package uranoscopidae.teambuilder.utils.mediawiki;

import java.util.List;

public class WikiTable extends WikiSourceElement
{
    public WikiTable(String raw)
    {
        super(raw);
    }

    @Override
    public void setRaw(String raw)
    {
        super.setRaw(raw);
        extract(raw);
    }

    private void extract(String source)
    {
        // TODO
    }
}
