import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.*;

public class Main {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private volatile boolean isShutdown = false;

    // Job interface
    interface Job {
        void execute();
    }

    // "Hello World" Job
    static class HelloWorldJob implements Job {
        private final String jobName;

        public HelloWorldJob(String jobName) {
            this.jobName = jobName;
        }

        @Override
        public void execute() {
            System.out.println("[" + jobName + "] Hello World - " + LocalDateTime.now());
        }
    }

    // Schedule interface
    interface Schedule {
        long getDelayInSeconds();
        long getIntervalInSeconds();
    }

    // Hourly Schedule
    static class HourlySchedule implements Schedule {
        private final int minuteOfHour;

        public HourlySchedule(int minuteOfHour) {
            if (minuteOfHour < 0 || minuteOfHour > 59)
                throw new IllegalArgumentException("Invalid minute");
            this.minuteOfHour = minuteOfHour;
        }

        @Override
        public long getDelayInSeconds() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextRun = now.withMinute(minuteOfHour).withSecond(0).withNano(0);
            if (!nextRun.isAfter(now)) {
                nextRun = nextRun.plusHours(1);
            }
            return ChronoUnit.SECONDS.between(now, nextRun);
        }

        @Override
        public long getIntervalInSeconds() {
            return 3600;
        }
    }

    // Daily Schedule
    static class DailySchedule implements Schedule {
        private final int hour;
        private final int minute;

        public DailySchedule(int hour, int minute) {
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
                throw new IllegalArgumentException("Invalid time");
            this.hour = hour;
            this.minute = minute;
        }

        @Override
        public long getDelayInSeconds() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            if (!nextRun.isAfter(now)) {
                nextRun = nextRun.plusDays(1);
            }
            return ChronoUnit.SECONDS.between(now, nextRun);
        }

        @Override
        public long getIntervalInSeconds() {
            return 24 * 3600;
        }
    }

    // Weekly Schedule
    static class WeeklySchedule implements Schedule {
        private final DayOfWeek dayOfWeek;
        private final int hour;
        private final int minute;

        public WeeklySchedule(DayOfWeek dayOfWeek, int hour, int minute) {
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
                throw new IllegalArgumentException("Invalid time");
            this.dayOfWeek = dayOfWeek;
            this.hour = hour;
            this.minute = minute;
        }

        @Override
        public long getDelayInSeconds() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextRun = now.with(TemporalAdjusters.nextOrSame(dayOfWeek))
                    .withHour(hour).withMinute(minute).withSecond(0).withNano(0);
            if (!nextRun.isAfter(now)) {
                nextRun = nextRun.plusWeeks(1);
            }
            return ChronoUnit.SECONDS.between(now, nextRun);
        }

        @Override
        public long getIntervalInSeconds() {
            return 7 * 24 * 3600;
        }
    }

    // Method to schedule jobs
    public void scheduleJob(Job job, Schedule schedule) {
        Runnable taskWrapper = new Runnable() {
            @Override
            public void run() {
                if (isShutdown) return;
                try {
                    job.execute();
                } catch (Exception e) {
                    System.err.println("Error executing job: " + e.getMessage());
                } finally {
                    if (!isShutdown) {
                        scheduler.schedule(this, schedule.getIntervalInSeconds(), TimeUnit.SECONDS);
                    }
                }
            }
        };

        long initialDelay = schedule.getDelayInSeconds();
        scheduler.schedule(taskWrapper, initialDelay, TimeUnit.SECONDS);
    }

    public void shutdown() {
        isShutdown = true;
        scheduler.shutdown();
        System.out.println("Scheduler shut down.");
    }

    // Main method (entry point)
    public static void main(String[] args) throws InterruptedException {
        Main schedulerInstance = new Main();

        // Schedule hourly at minute 15
        schedulerInstance.scheduleJob(new HelloWorldJob("Hourly@15"), new HourlySchedule(15));

        // Schedule daily at 14:30
        schedulerInstance.scheduleJob(new HelloWorldJob("Daily@14:30"), new DailySchedule(14, 30));

        // Schedule weekly on Sunday at 10:00
        schedulerInstance.scheduleJob(new HelloWorldJob("Weekly@Sun10"), new WeeklySchedule(DayOfWeek.SUNDAY, 10, 0));

        // Keep app running for 10 minutes (adjust as needed)
        Thread.sleep(1000 * 60 * 10);

        schedulerInstance.shutdown();
    }
}
