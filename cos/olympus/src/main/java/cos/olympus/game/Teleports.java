package cos.olympus.game;

import cos.map.PortalSpot;

import java.util.Map;

public class Teleports {

    private final Map<String, Game> games;

    public Teleports(Map<String, Game> games) {
        this.games = games;
    }

    public void activate(int tick, Player avatar, PortalSpot portal) {
    //    new TeleportStrategy(tick, null, avatar, portal.map());
    }
}
