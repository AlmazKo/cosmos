package cos.map;

import java.util.ArrayList;

public record Lands(
        int width,
        int height,
        int offsetX,
        int offsetY,
        short[] basis,
        short[] objects,
        Tile[] tiles,
        ArrayList<RespawnSpot> respawns,
        ArrayList<PortalSpot> portals
) {

}
