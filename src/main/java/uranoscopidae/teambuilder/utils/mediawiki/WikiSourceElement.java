package uranoscopidae.teambuilder.utils.mediawiki;

import uranoscopidae.teambuilder.utils.IOHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiSourceElement
{

    private String raw;
    private int indentationLevel;

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

    public WikiTable getTable(int startIndex)
    {
        if(indexOf("{{", startIndex) != 0)
        {
            throw new IllegalArgumentException("Invalid index");
        }

        return new WikiTable(readProperly(startIndex, "}}"));
    }

    public WikiTemplate asTemplate()
    {
        return new WikiTemplate(raw);
    }

    public WikiTable asTable()
    {
        return getTable(0);
    }

    public WikiKeyValue asKeyValue()
    {
        return new WikiKeyValue(raw);
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

    protected String readProperly(int start, String end)
    {
        int unclosed = 0;
        for (int i = start; i < raw.length(); i++)
        {
            char c = raw.charAt(i);
            if (raw.indexOf(end, start) == i)
            {
                if(unclosed == 0)
                {
                    return raw.substring(start, i);
                }
            }
            else if (c == '{')
            {
                unclosed++;
            }
            else if (c == '}')
            {
                unclosed--;
            }
        }
        return null;
    }

    public WikiSourceElement getSection(String section)
    {
        StringBuilder builder = new StringBuilder();
        int count = getIndentationLevel()+2;
        for (int i = 0; i < count; i++)
        {
            builder.append('=');
        }
        String halfWrapper = builder.toString();
        builder.append(section).append(halfWrapper);
        String start = builder.toString();
        String content = extractSection(halfWrapper, start);
        if(content == null)
            return null;
        WikiSourceElement result = new WikiSourceElement(content);
        result.setIndentationLevel(getIndentationLevel()+1);
        return result;
    }

    private String extractSection(String halfWrapper, String start)
    {
        if(!raw.contains(start))
            return null;
        String potentialSection = raw.substring(raw.indexOf(start)+start.length()+1);
        String[] lines = potentialSection.split("\n");
        int index = 0;
        for (String l : lines)
        {
            if (l.length() > halfWrapper.length() * 2 + 1)
            {
                if (l.startsWith(halfWrapper) && l.charAt(halfWrapper.length()+1) != '=' && l.endsWith(halfWrapper))
                {
                    return potentialSection.substring(0, index);
                }
            }
            index += l.length() + 1;
        }
        return potentialSection;
    }

    public int getIndentationLevel()
    {
        return indentationLevel;
    }

    protected void setIndentationLevel(int indentationLevel)
    {
        this.indentationLevel = indentationLevel;
    }

    public boolean isNumber()
    {
        for (char c : raw.toCharArray())
        {
            if(!(Character.isDigit(c) || c == '.' || c == ' '))
            {
                return false;
            }
        }
        return true;
    }

    public double asNumber()
    {
        int i = 0;
        while(raw.length()-i > 0 && raw.charAt(i) == ' ')
        {
            i++;
        }
        return Double.parseDouble(raw.substring(i));
    }
}
