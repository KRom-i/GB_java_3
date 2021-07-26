package Lesson_02.DemoUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WatchServiceDir implements Runnable {

    private Path path;

    public WatchServiceDir (String path) {
        this.path = Paths.get(path);
    }

    @Override
    public void run () {

        try {

            java.nio.file.WatchService service = FileSystems.getDefault().newWatchService();
            path.register(service,
                          StandardWatchEventKinds.ENTRY_CREATE,
                          StandardWatchEventKinds.ENTRY_DELETE,
                          StandardWatchEventKinds.ENTRY_MODIFY);

            String notification = "Event type: %s. File: %s\n";

            while (true) {

                try {
                    WatchKey key = service.take();
                    if(key.isValid()) {
                        List<WatchEvent<?>> listKeys = key.pollEvents();
                        for(WatchEvent<?> event : listKeys) {

                            System.out.printf(notification, event.kind(), event.context());
                        }
                    }
                    key.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void main (String[] args) throws InterruptedException {

        ExecutorService watchDirectory = Executors.newFixedThreadPool(1);

        System.out.println("NEW WATCH");
        WatchServiceDir watchServiceDir = new WatchServiceDir("SERVER");
        watchDirectory.execute(watchServiceDir);

        System.out.println("THREAD SLEEP");
        Thread.sleep(20_000);

        System.out.println("EXECUTE SHOWDOWN");

        System.out.println("THREAD SLEEP");
        Thread.sleep(20_000);

        System.out.println("NEW WATCH");
        watchDirectory.execute(new WatchServiceDir("SERVER"));

        System.out.println("THREAD SLEEP");
        Thread.sleep(20_000);



        watchDirectory.shutdown();
        System.out.println("EXECUTE SHOWDOWN");


    }



}
