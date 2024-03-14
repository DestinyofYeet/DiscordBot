package utils.dd_download;

import utils.Logger;
import utils.uwuwhatsthis_api.requests.doubledouble.GetDownloadStatusRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MusicDownloadManager {
    private Thread thread;

    private boolean running;

    private final Object queueFlag;

    private ArrayList<MusicDownloadEntry> queue;

    private HashMap<String, LocalDateTime> idTimeoutMap;

    private final Logger logger = new Logger("MusicDownloadManager");
    public MusicDownloadManager(){
        queue = new ArrayList<>();
        queueFlag = new Object();
        idTimeoutMap = new HashMap<String, LocalDateTime>();
    }

    public void start(){
        logger.info("Starting thread!");
        this.running = true;
        this.thread = new Thread(this::run);
        this.thread.start();
    }

    public void stop(){
        logger.info("Stopping thread!");
        this.running = false;
    }

    public synchronized void queue(MusicDownloadEntry entry){
        if (!running) return;
        this.queue.add(entry);
        queueFlag.notifyAll();
    }

    private void run(){
        logger.info("Thread is running!");

        while (true){
            if (queue.isEmpty()){
                if (!running) break;

                synchronized (queueFlag){
                    try {
                        queueFlag.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (!running) break;
            }

            ArrayList<MusicDownloadEntry> copy = (ArrayList<MusicDownloadEntry>) this.queue.clone();

            for (MusicDownloadEntry entry: copy){
                // if (this.idTimeoutMap.get(entry.getId()))
                handleEntry(entry);
            }
        }

        logger.info("Thread is exiting!");
    }

    private void handleEntry(MusicDownloadEntry entry){
        GetDownloadStatusRequest request = new GetDownloadStatusRequest(entry.getId());

        try{
            request.doRequest();
        } catch (IOException e) {
            entry.getCallback().execute(entry, false, "Failed to get status (Network error): \n" + e.getMessage());
            this.queue.remove(entry);
            return;
        }

        if (!request.isSuccessful()){
            entry.getCallback().execute(entry, false, "Failed to get status: \n" + request.getError());
            this.queue.remove(entry);
        }

        if (Objects.equals(request.getStatus(), "Done!")) {
            this.queue.remove(entry);
        }

        this.idTimeoutMap.put(entry.getId(), LocalDateTime.now());
    }
}
