package it.unipi.gamerlist.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private static final Logger logger = Logger.getLogger("MyLog");

    public static void initLog() throws IOException {
        FileHandler fh;

        try {
            fh = new FileHandler("./src/main/resources/log/databases-info.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.info("Init Log");
        } catch (SecurityException | IOException e) {
            logger.severe("Log creation error");
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}