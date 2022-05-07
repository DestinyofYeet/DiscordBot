package utils.sql;

import utils.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class SQLRequestManager {
    private SQLConnectionPool pool;

    private boolean running;

    private Thread thread;

    private final LinkedBlockingQueue<SQLRequest> queue;

    private final Logger logger;

    public SQLRequestManager(SQLConnectionPool pool){
        this.pool = pool;

        this.running = true;

        this.queue = new LinkedBlockingQueue<>();
        this.logger = new Logger("SQLRequestManager");

        this.thread = null;
    }

    public void start(){
        logger.info("Starting...");
        thread = new Thread(this::run);
        thread.start();
    }

    private synchronized void addRequest(SQLRequest SQLRequest){
        if (SQLRequest.getOverwriteRequest() != null){
            this.queue.add(SQLRequest);
        }

        this.queue.add(SQLRequest);
    }

    public void queue(SQLRequest SQLRequest){
        if (!running) return;

        if (!pool.isConnected()){
            logger.error("Not connected to database! Not queuing request!");
            return;
        }

        switch (SQLRequest.getType()){
            case EXECUTE -> {
                addRequest(SQLRequest);
            }

            case RESULT -> {
                synchronized (SQLRequest.getLock()){
                    addRequest(SQLRequest);

                    try {
                        SQLRequest.getLock().wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


            default -> {
                throw new IllegalArgumentException("Invalid request type!");
            }
        }
    }

    public void stop(){
        logger.info("Stopping...");
        this.running = false;
    }

    public boolean isConnected(){
        return pool.isConnected();
    }


    private void run(){
        logger.info("Started!");

        while (true) {
            if (!running) {
                if (!this.queue.isEmpty()) {
                    logger.info("Not stopping because there are still " + queue.size() + " requests in the queue!");
                } else break;
            }

            if (this.queue.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            SQLRequest SQLRequest = this.queue.poll();

            synchronized (SQLRequest.getLock()){
                Connection connection = pool.getConnection();

                if (connection == null) {
                    logger.error("Connection is null!");
                    stop();
                    continue;
                }

                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error("Could not set auto commit to true");
                }

                PreparedStatement statement;

                ResultSet rs = null;

                Map<String, String> result;

                try {
                    statement = connection.prepareStatement(SQLRequest.getSql());

                    for (int i = 1; i <= SQLRequest.getData().size(); i++) {
                        statement.setString(i, SQLRequest.getData().get(i - 1));
                    }

                    if (SQLRequest.getType() == RequestType.EXECUTE)
                        statement.execute();

                    else{
                        rs = statement.executeQuery();

                        result = new HashMap<>();

                        while (rs.next()){
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++){
                                String columnName = rs.getMetaData().getColumnName(i);
                                String value = rs.getString(i);
                                result.put(columnName, value);
                            }
                        }

                        rs.close();

                        SQLRequest.setResult(result);
                    }

                    connection.close();

                } catch (SQLException e) {
                    logger.error("Failed to create statement! SQL: " + SQLRequest.getSql() + " | Data: " + SQLRequest.getData().toString());
                    e.printStackTrace();

                } finally {
                    SQLRequest.getLock().notify();
                }
            }


        }

        logger.info("Stopped!");
    }
}
