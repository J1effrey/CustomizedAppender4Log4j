package uw.edu.css553.appender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestLog4j2 {
    public static void main(String[] args)  {
        Logger logger = LogManager.getLogger(TestLog4j2.class);
//        MyLoggerCustom logger = MyLoggerCustom.create(TestLog4j.class);
        logger.info("TEST");
        logger.error("ERROR");
        logger.debug("debug");
        logger.warn("warn");
        logger.fatal("fatal");
        logger.trace("trace");
    }
}
