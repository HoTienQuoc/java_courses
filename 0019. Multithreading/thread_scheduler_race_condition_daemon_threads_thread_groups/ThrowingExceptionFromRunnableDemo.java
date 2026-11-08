package thread_scheduler_race_condition_daemon_threads_thread_groups;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ThrowingExceptionFromRunnableDemo {

    public static void main(String[] args) {
        var t1 = new Thread(() -> {
            try {
                Files.readString(Paths.get("non-existing-path"));
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        });

        t1.start();

    }

}
