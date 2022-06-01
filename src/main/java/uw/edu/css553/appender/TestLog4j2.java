package uw.edu.css553.appender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestLog4j2 {
    public static void main(String[] args)  {
        Logger logger = LogManager.getLogger(TestLog4j2.class);
//        MyLoggerCustom logger = MyLoggerCustom.create(TestLog4j.class);
        logger.info("TEST2");
        logger.error("ERROR2");
        logger.debug("debug2");
        logger.warn("warn2");
        logger.fatal("fatal2");
        logger.trace("trace2");
    }
}
