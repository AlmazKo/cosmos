package cos.olympus.game;

import cos.logging.Logger;

public abstract class AbstractSpellStrategy implements SpellStrategy {
    protected final static Logger logger      = new Logger(FireballSpellStrategy.class);
    protected static       int    DAMAGES_IDS = 0;
    protected static       int    SPELL_IDS   = 0;

    protected boolean finished;

    @Override public boolean isFinish() {
        return finished;
    }
}
