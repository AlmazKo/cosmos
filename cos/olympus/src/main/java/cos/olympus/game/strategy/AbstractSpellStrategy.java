package cos.olympus.game.strategy;

import cos.logging.Logger;

public abstract class AbstractSpellStrategy implements SpellStrategy {
    protected final static Logger logger = Logger.get(FireballSpellStrategy.class);

    protected boolean finished;

    @Override
    public boolean isFinished() {
        return finished;
    }
}
