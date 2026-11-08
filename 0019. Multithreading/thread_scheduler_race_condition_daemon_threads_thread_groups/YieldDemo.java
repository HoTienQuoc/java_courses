package thread_scheduler_race_condition_daemon_threads_thread_groups;

public class YieldDemo {

    public static void main(String[] args) {
        var t0 = new Thread(() -> {
            Thread.yield();
            System.out.println(Thread.currentThread().getName());
        });

        var t1 = new Thread(() -> System.out.println(Thread.currentThread().getName()));

        t0.start();
        t1.start();
    }

}
