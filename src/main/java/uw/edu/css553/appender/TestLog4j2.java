package uw.edu.css553.appender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TestLog4j2 {
    public static void main(String[] args)  {
        Logger logger = LogManager.getLogger(TestLog4j2.class);
        // normal messages
        logger.info("This is a info message!!!");
        logger.error("This is a error message!!!");
        logger.debug("This is a debug message!!!");
        logger.warn("This is a warn message!!!");
        logger.fatal("This is a fatal message!!!");
        logger.trace("This is a trace message!!!");
        // with username
        logger.info("[Jack] login to the system!!!");
        logger.info("[Bob] require home pages!!!");
    }
}
