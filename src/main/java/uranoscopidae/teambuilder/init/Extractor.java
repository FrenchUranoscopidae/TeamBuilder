package uranoscopidae.teambuilder.init;

import java.util.LinkedList;
import java.util.List;

public abstract class Extractor
{

    protected int findCorrespondingBrace(String source, int start)
    {
        int unclosed = 0;
        for (int i = start; i < source.length(); i++)
        {
            char c = source.charAt(i);
            switch (c)
            {
                case '{':
                    unclosed++;
                    break;

                case '}':
                    unclosed--;
                    if(unclosed == 0)
                        return i;
                    break;
            }
        }
        return -1;
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
