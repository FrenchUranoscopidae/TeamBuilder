package uranoscopidae.teambuilder.init;

import uranoscopidae.teambuilder.TypeList;
import uranoscopidae.teambuilder.moves.MoveCategory;
import uranoscopidae.teambuilder.moves.Move;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class MoveExtractor
{

    private final BulbapediaExtractor extractor;

    public MoveExtractor()
    {
        extractor = new BulbapediaExtractor();
    }

    public BulbapediaExtractor getExtractor()
    {
        return extractor;
    }

    public List<Move> findAllMoves() throws IOException
    {
        List<Move> moves = new LinkedList<>();
        String source = extractor.getPageSourceCode("List_of_moves");
        String start = "|-";
        String end = "|}\n" +
                "|}";
        String list = source.substring(source.indexOf(start)+start.length(), source.indexOf(end, source.indexOf(start)));
        String[] movesList = list.split(Pattern.quote("|-"));

        // skip first 2 because starts with table definition
        for(int i = 2;i<movesList.length;i++)
        {
            String m = movesList[i];
            String[] parts = m.split(Pattern.quote("\n|"));
            String infos = parts[2];

            int extractStart = infos.indexOf("{{m|");
            String name = infos.substring(extractStart+"{{m|".length(), infos.indexOf("}}", extractStart));

            extractStart = infos.indexOf("{{typetable|");
            String type = infos.substring(extractStart+"{{typetable|".length(), infos.indexOf("}}", extractStart));

            extractStart = infos.indexOf("{{statustable|");
            String category = infos.substring(extractStart+"{{statustable|".length(), infos.indexOf("}}", extractStart));

            int pp = readInt(parts[3]);
            int power = readInt(parts[4]);

            String acc = parts[5].replace(" ", "");
            int accuracy = 100;
            if(!acc.contains("&mdash;"))
            {
                accuracy = Integer.parseInt(acc.substring(0, acc.indexOf("%")));
            }

            moves.add(new Move(TypeList.getFromID(type), MoveCategory.valueOf(category.toUpperCase()), name, power, accuracy, pp));
        }
        return moves;
    }

    public static int readInt(String part)
    {
        part = part.replace(" ", "");

        int value = 0;
        if(!part.contains("&mdash;"))
        {
            int end = part.indexOf("{{");
            if(end < 0)
                end = part.length();
            part = part.substring(0, end);
            if(part.isEmpty())
            {
                return 0;
            }
            value = Integer.parseInt(part);
        }
        return value;
    }
}
