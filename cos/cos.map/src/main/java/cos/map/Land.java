package cos.map;

import almazko.microjson.JsObject;
import almazko.microjson.Json;
import cos.map.parser.MapParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Land {

    public static Lands load(Path dir) throws IOException {
        var baseFile = dir.resolve("base1.json");
        var mapFile =  dir.resolve("untitled.json");
        var base = (JsObject) Json.parseObject(Files.readString(baseFile));
        var map = Json.parseObject(Files.readString(mapFile));
        return MapParser.parse(map, base);
    }
}
