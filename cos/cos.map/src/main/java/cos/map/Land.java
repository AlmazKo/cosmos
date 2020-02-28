package cos.map;

import cos.json.JsObject;
import cos.json.Json;
import cos.map.parser.MapParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Land {

    public static Lands load() throws IOException {

        Path baseFile;
        Path mapFile;
        try {
            baseFile = Paths.get(Land.class.getResource("/base1.json").toURI());
            mapFile = Paths.get(Land.class.getResource("/map.json").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        var base = (JsObject) Json.parseObject(Files.readString(baseFile));
        var map = Json.parseObject(Files.readString(mapFile));
        return MapParser.parse(map, base);
    }
}
