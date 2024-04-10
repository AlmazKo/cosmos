package cos;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class Properties {
    public final static Path resourcesDir = Paths.get("", Env.get("CosResourcesDir")).toAbsolutePath();
}
