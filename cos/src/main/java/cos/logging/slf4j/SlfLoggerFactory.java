
package cos.logging.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SlfLoggerFactory implements ILoggerFactory {
    public Logger getLogger(String name) {
        return new SlfLogger(name);
    }
}