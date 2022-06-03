package uw.edu.css553.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(name = "Statistics", category = "Core", elementType = "appender", printObject = true)
public class StatisticsAppender extends AbstractAppender {
    private String url;
    private String driver;
    private String username;
    private String password;

    private static final String sql = "insert into log_records(level, message, timestamp, username) values(?,?,?,?)";
    private Connection conn = null;
    private PreparedStatement pst = null;
    private Statement st = null;
    private static final String regexPattern = "\\[([^\\]]*)\\]";

    /**
     * Initialize needed parameters
     * @param name Appender's name
     * @param filter filter
     * @param layout layout
     * @param url URL of database
     * @param driver driver
     * @param username username for database
     * @param password password for database
     */
    protected StatisticsAppender(String name,
                                 Filter filter,
                                 Layout<? extends Serializable> layout,
                                 String url,
                                 String driver,
                                 String username,
                                 String password) {
        super(name, filter, layout);
        this.url = url;
        this.driver = driver;
        this.username = username;
        this.password = password;
        try {
            Class.forName(this.driver);
        } catch (ClassNotFoundException e) {
            LOGGER.error("fail to load driver");
        }
    }

    /**
     * Before append message to the database, make sure there's a table connected with it
     */
    @Override
    public void start() {
        createNewTableForLogs();
        super.start();
    }

    /**
     *  Create new table for store log messages if not exists.
     */
    public void createNewTableForLogs() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            st = conn.createStatement();
            String tableSql = "CREATE TABLE IF NOT EXISTS `log_records`  (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `message` varchar(255) DEFAULT NULL,\n" +
                    "  `username` varchar(255) DEFAULT NULL,\n" +
                    "  `timestamp` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,\n" +
                    "  `level` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;";
//            st.executeUpdate("DROP TABLE IF EXISTS `log_records`;");
            st.executeUpdate(tableSql);
        } catch (SQLException e) {
            LOGGER.error("Something went wrong when creating new table");
        } finally {
            closeConnection();
        }
    }

    /**
     * Append Message to MySQL Database in a pre-defined form
     * @param logEvent
     */
    @Override
    public void append(LogEvent logEvent) {
        try {
            conn = this.getConnection();
            pst = conn.prepareStatement(sql);

            String level = logEvent.getLevel().toString();
            String msg = logEvent.getMessage().getFormattedMessage();
            Timestamp t = new Timestamp(logEvent.getTimeMillis());
            List<String> infos = getAllRequiredInfo(msg);
            String username = infos.size() == 0 ? "" : infos.get(0);

            pst.setString(1, level);
            pst.setString(2, msg);
            pst.setTimestamp(3, t);
            pst.setString(4, username);

            pst.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Something went wrong when insert data into the database!");
        } finally {
            closeConnection();
        }
    }

    /**
     * Extract All needed info from LogMessage
     * @param msg LogMessage
     * @return All required info corresponding to table fields
     */
    private List<String> getAllRequiredInfo(String msg) {
        Pattern r = Pattern.compile(regexPattern);
        Matcher m = r.matcher(msg);
        List<String> infos = new ArrayList<>();
        while (m.find()) {
            infos.add(m.group(1));
        }
        return infos;
    }

    /**
     * Open connection to Database and return client
     * @return Connection
     * @throws SQLException fail to get Connection
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Close connection to the Database
     */
    private void closeConnection() {
        try {
            if (pst != null) {
                pst.close();
            }
            if (st != null) {
                st.close();
            }
            if (conn != null) {
                conn.close();
            }
            pst = null;
            st = null;
            conn = null;
        } catch (SQLException e) {
            LOGGER.error("Fail to close connection!");
        }
    }

    /**
     *
     * @param name
     * @param filter
     * @param layout
     * @param url
     * @param driver
     * @param username
     * @param password
     * @return
     */
    @PluginFactory
    public static StatisticsAppender createAppender(@PluginAttribute("name") String name,
                                                    @PluginElement("Filter") final Filter filter,
                                                    @PluginElement("Layout") Layout<? extends Serializable> layout,
                                                    @PluginAttribute("url") String url,
                                                    @PluginAttribute("driver") String driver,
                                                    @PluginAttribute("username") String username,
                                                    @PluginAttribute("password") String password) {
        if (name == null) {
            LOGGER.error("No name provided for MyCustomAppenderImpl");
            return null;
        }
        if (driver == null) {
            LOGGER.error("No driver was found");
            return null;
        }
        if (url == null) {
            LOGGER.error("Please specify database URL!");
            return null;
        }
        if (username == null) {
            LOGGER.error("No username was found when connecting to database");
            return null;
        }
        if (password == null) {
            LOGGER.error("No password was found when connecting to database");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new StatisticsAppender(name, filter, layout, url, driver, username, password);
    }
}