package cos.map;

import cos.json.JsObject;
import cos.json.Json;
import cos.map.parser.MapParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Land {

    public static Lands load(String dir) throws IOException {
        Path baseFile = Paths.get(dir, "/base1.json");
        Path mapFile = Paths.get(dir, "/map.json");
        var base = (JsObject) Json.parseObject(Files.readString(baseFile));
        var map = Json.parseObject(Files.readString(mapFile));
        return MapParser.parse(map, base);
    }
}
