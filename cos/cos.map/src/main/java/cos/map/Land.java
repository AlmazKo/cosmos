package cos.map;

import cos.json.JsObject;
import cos.json.Json;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Land {

    Lands load() throws IOException, URISyntaxException {

        var baseFile = Paths.get(Land.class.getResource("/base1.json").toURI());
        var mapFile = Paths.get(Land.class.getResource("/map.json").toURI());

        var base = (JsObject) Json.parseObject(Files.readString(baseFile));
        var map = Json.parseObject(Files.readString(mapFile));
        return MapParser.parse(map, base);
    }
}
