package uranoscopidae.teambuilder.init;

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
}
