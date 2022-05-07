package utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import utils.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLConnectionPool {

    private final int connections;
    private final String server, port, database, username, password;

    private boolean connected;

    private HikariConfig config;
    private HikariDataSource source;

    private Logger logger;


    public SQLConnectionPool(int connections, String server, String port, String database, String username, String password){
        this.connections = connections;
        this.server = server;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        this.connected = false;

        logger = new Logger("SQL Connection Pool");
        connect();
    }

    private void connect(){
        config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + server + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("maximumPoolSize", connections);

        try {
            source = new HikariDataSource(config);
            logger.info("Successfully connected to database!");
            this.connected = true;
        } catch (HikariPool.PoolInitializationException e){
            e.printStackTrace();
            logger.error("Failed to connect to database!");
            this.connected = false;
        }
    }

    public Connection getConnection(){
        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
