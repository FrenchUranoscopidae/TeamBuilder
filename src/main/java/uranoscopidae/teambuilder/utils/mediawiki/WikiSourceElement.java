package uranoscopidae.teambuilder.utils.mediawiki;

import java.util.LinkedList;
import java.util.List;

public class WikiSourceElement
{

    private String raw;

    public WikiSourceElement(String raw)
    {
        setRaw(raw);
    }

    public String getRaw()
    {
        return raw;
    }

    public void setRaw(String raw)
    {
        this.raw = raw;
    }

    public int indexOf(String s)
    {
        return indexOf(s, 0);
    }

    public int indexOf(String s, int start)
    {
        return raw.indexOf(s, start);
    }

   /* public WikiTable getTable(int startIndex)
    {
        if(indexOf("{|", startIndex) != 0)
        {
            throw new IllegalArgumentException("Invalid index");
        }

        return null;
    }*/

    public WikiTemplate asTemplate()
    {
        return new WikiTemplate(raw);
    }

    protected List<String> properSplit(String content, char splitter)
    {
        List<String> list = new LinkedList<>();
        char[] chars = content.toCharArray();
        int unclosed = 0;
        StringBuilder builder = new StringBuilder();
        for (char c : chars)
        {
            if (c == splitter && unclosed == 0)
            {
                list.add(builder.toString());
                builder.delete(0, builder.length());
                continue;
            }
            else if (c == '{')
            {
                unclosed++;
            }
            else if (c == '}')
            {
                unclosed--;
            }
            builder.append(c);
        }
        if(builder.length() > 0)
            list.add(builder.toString());
        return list;
    }
}
