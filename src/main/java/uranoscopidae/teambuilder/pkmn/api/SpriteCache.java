package uranoscopidae.teambuilder.pkmn.api;

import me.sargunvohra.lib.pokekotlin.model.Pokemon;
import me.sargunvohra.lib.pokekotlin.model.PokemonSprites;
import uranoscopidae.teambuilder.app.team.PokemonGender;
import uranoscopidae.teambuilder.pkmn.PokeApiInterface;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SpriteCache {
    private final Map<Integer, Map<PokemonGender, BufferedImage[]>> cache;

    public SpriteCache(PokeApiInterface apiInterface) {
        cache = new HashMap<>();
    }

    public BufferedImage get(int apiID, PokemonGender gender, boolean isShiny) {
        Map<PokemonGender, BufferedImage[]> sprites = loadSprites(apiID);
        return sprites.get(gender)[isShiny ? 1 : 0];
    }

    private Map<PokemonGender, BufferedImage[]> loadSprites(int apiID) {
        if(cache.containsKey(apiID))
            return cache.get(apiID);
        Map<PokemonGender, BufferedImage[]> map = new HashMap<>();
        String fallback = apiID+".png";
        BufferedImage[] defaultSprites = new BufferedImage[]{loadImage(fallback, fallback), loadImage("shiny/"+fallback, fallback)};
        map.put(PokemonGender.ASEXUAL, defaultSprites);
        map.put(PokemonGender.MALE, defaultSprites);
        map.put(PokemonGender.FEMALE, new BufferedImage[] {loadImage("female/"+fallback, fallback), loadImage("shiny/female/"+fallback, fallback)});
        cache.put(apiID, map);
        return map;
    }

    private BufferedImage loadImage(String url, String fallback) {
        final String root = "/building/pokeapi/data/v2/sprites/pokemon/";
        try {
            return ImageIO.read(getClass().getResourceAsStream(root+url));
        } catch (Exception e) {
            try {
                return ImageIO.read(getClass().getResourceAsStream(root+fallback));
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }
}
