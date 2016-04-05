package uranoscopidae.teambuilder.utils.mediawiki;

import java.util.List;

public class WikiTemplate extends WikiSourceElement
{

    private WikiSourceElement[] elements;
    private String name;
    private int elemOffset;

    public WikiTemplate()
    {
        super("{{$UNNAMED$}}");
        elements = new WikiSourceElement[0];
    }

    public WikiTemplate(String source)
    {
        super(source);
    }

    @Override
    public void setRaw(String source)
    {
        super.setRaw(source);
        elemOffset = 0;
        validateSource(source);
        extract(source);
    }

    private void extract(String source)
    {
        String contents = source.substring(2, source.lastIndexOf("}}"));
        List<String> data = properSplit(contents, '|');
        name = data.get(0);
        elements = new WikiSourceElement[data.size()-1];
        for (int i = 1; i < data.size(); i++)
        {
            String elem = data.get(i);
            elements[i-1] = new WikiSourceElement(elem);
        }
    }

    private void validateSource(String source)
    {
        if(!(source.startsWith("{{")))
        {
            throw new IllegalArgumentException("Malformed template: "+source);
        }
        if(source.lastIndexOf("}}") < source.lastIndexOf("{{"))
        {
            throw new IllegalArgumentException("Malformed template: "+source);
        }
    }

    public String getName()
    {
        return name;
    }

    public WikiSourceElement getElement(int index)
    {
        return elements[index+elemOffset];
    }

    public void setElementOffset(int elemOffset)
    {
        this.elemOffset = elemOffset;
    }

    public int getElementCount()
    {
        return elements.length;
    }
}
