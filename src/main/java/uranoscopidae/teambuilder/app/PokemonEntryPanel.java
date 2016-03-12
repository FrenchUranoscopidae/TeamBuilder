package uranoscopidae.teambuilder.app;

import uranoscopidae.teambuilder.pkmn.Pokemon;
import uranoscopidae.teambuilder.utils.IOHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PokemonEntryPanel extends JPanel
{

    private final Font levelFont;
    private final Font nameFont;
    private BufferedImage pokeball;
    private Pokemon pokemon;

    public PokemonEntryPanel(TeamBuilderApp app)
    {
        nameFont = new Font(null, Font.PLAIN, 20);
        levelFont = new Font(null, Font.PLAIN, 16);
        try
        {
            pokemon = app.getPokemon("025Pikachu");
            setPreferredSize(new Dimension(300,64));
            pokeball = ImageIO.read(getClass().getResourceAsStream("/pokeball.png"));

        }
        catch (IOException | ReflectiveOperationException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void paintComponent(Graphics gr)
    {
        Graphics2D g = (Graphics2D)gr;
        g.setColor(Color.black);

        // Draw box
        float sizeFactor = (float)getHeight()/pokeball.getHeight();
        final int pokeballWidth = (int) (pokeball.getWidth()*sizeFactor);
        final int boxOffset = pokeballWidth/2;
        final int offset = 20;
        final int heightCompensation = 20;
        g.setStroke(new BasicStroke(4));
        g.drawLine(boxOffset+offset, heightCompensation/2, getWidth()-1, heightCompensation/2);
        g.drawLine(boxOffset, getHeight()-1-heightCompensation/2, getWidth()-offset-1, getHeight()-1-heightCompensation/2);

        g.drawLine(boxOffset, getHeight()-1-heightCompensation/2, offset+boxOffset, heightCompensation/2);
        g.drawLine(getWidth()-offset-1, getHeight()-1-heightCompensation/2, getWidth()-1, heightCompensation/2);

        g.setStroke(new BasicStroke(1));

        // draw background
        Shape s = new Polygon(new int[]{boxOffset+offset, getWidth()-1, getWidth()-offset-1, boxOffset},
                new int[]{heightCompensation/2,heightCompensation/2, getHeight()-heightCompensation/2-1, getHeight()-heightCompensation/2-1},
                4);
        GradientPaint gradient = new GradientPaint(0,heightCompensation/2,Color.cyan.darker(), 0, getHeight()-heightCompensation/2-1, new Color(80,80,255));
        g.setPaint(gradient);
        g.fill(s);

        g.setPaint(Color.white);

        // Draw pokeball
        g.drawImage(pokeball, 0, 0, pokeballWidth, (int) (pokeball.getHeight()*sizeFactor), null);

        // Draw pokemon infos
        BufferedImage icon = pokemon.getIcon();
        float pokemonSizeFactor = (getHeight()-heightCompensation) / (float)icon.getHeight();
        int iconWidth = (int) (pokemonSizeFactor*icon.getWidth());
        int iconHeight = (int) (pokemonSizeFactor*icon.getHeight());
        g.drawImage(icon, pokeballWidth, getHeight()/2-iconHeight/2, iconWidth, iconHeight, null);

        g.setFont(nameFont);
        FontMetrics metrics = g.getFontMetrics();
        int nameY = heightCompensation+metrics.getHeight()/2;
        g.setColor(Color.black);
        g.drawString(pokemon.getEnglishName(), pokeballWidth+iconWidth+1, nameY+1);

        g.setColor(Color.white);
        g.drawString(pokemon.getEnglishName(), pokeballWidth+iconWidth, nameY);


        g.setFont(levelFont);
        metrics = g.getFontMetrics();
        int level = 100; // TODO

        g.setColor(Color.black);
        g.drawString("Lv."+level, pokeballWidth+iconWidth+1, getHeight()-nameY+metrics.getHeight()-5+1);
        g.setColor(Color.white);
        g.drawString("Lv."+level, pokeballWidth+iconWidth, getHeight()-nameY+metrics.getHeight()-5);
    }

}
