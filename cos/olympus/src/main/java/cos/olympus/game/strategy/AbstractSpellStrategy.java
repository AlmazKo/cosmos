package cos.olympus.game.strategy;

import cos.logging.Logger;

public abstract class AbstractSpellStrategy implements SpellStrategy {
    protected final static Logger logger      = Logger.get(FireballSpellStrategy.class);
    protected static       int    DAMAGES_IDS = 0;
//    protected static       int    SPELL_IDS   = 0;

    protected boolean finished;

    @Override public boolean isFinished() {
        return finished;
    }
}
