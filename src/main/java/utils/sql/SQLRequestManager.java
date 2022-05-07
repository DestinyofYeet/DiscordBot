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

    private final LinkedBlockingQueue<Request> queue;

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

    private synchronized void addRequest(Request request){
        if (request.getOverwriteRequest() != null){
            this.queue.add(request);
        }

        this.queue.add(request);
    }

    public void queue(Request request){
        if (!running) return;

        if (!pool.isConnected()){
            logger.error("Not connected to database! Not queuing request!");
            return;
        }

        switch (request.getType()){
            case EXECUTE -> {
                addRequest(request);
            }

            case RESULT -> {
                synchronized (request.getLock()){
                    addRequest(request);

                    try {
                        request.getLock().wait();
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

            Request request = this.queue.poll();

            synchronized (request.getLock()){
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
                    statement = connection.prepareStatement(request.getSql());

                    for (int i = 1; i <= request.getData().size(); i++) {
                        statement.setString(i, request.getData().get(i - 1));
                    }

                    if (request.getType() == RequestType.EXECUTE)
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

                        request.setResult(result);
                    }

                    connection.close();

                } catch (SQLException e) {
                    logger.error("Failed to create statement! SQL: " + request.getSql() + " | Data: " + request.getData().toString());
                    e.printStackTrace();

                } finally {
                    request.getLock().notify();
                }
            }


        }

        logger.info("Stopped!");
    }
}
