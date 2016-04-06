package uranoscopidae.teambuilder.utils.mediawiki;

import java.util.List;

public class WikiKeyValue extends WikiSourceElement
{
    private WikiSourceElement key;
    private WikiSourceElement value;

    public WikiKeyValue(String raw)
    {
        super(raw);
    }

    @Override
    public void setRaw(String raw)
    {
        super.setRaw(raw);
        if(!raw.contains("="))
            throw new IllegalArgumentException("Malformed key/value-pair: "+raw);
        List<String> parts = properSplit(raw, '=');
        key = new WikiSourceElement(parts.get(0));
        value = new WikiSourceElement(parts.get(1));
    }

    public WikiSourceElement getKey()
    {
        return key;
    }

    public WikiSourceElement getValue()
    {
        return value;
    }
}
