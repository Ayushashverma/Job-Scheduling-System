# ⏰ Java Job Scheduler

A simple Java-based job scheduling system that allows users to schedule recurring tasks such as printing "Hello World" to the console at hourly, daily, or weekly intervals.

## 🚀 Features

- ✅ Schedule jobs to run:
  - Hourly (at a specific minute)
  - Daily (at a specific time)
  - Weekly (on a specific day and time)
- ✅ Prints "Hello World" with timestamp
- ✅ Multi-threaded using `ScheduledExecutorService`
- ✅ Clean, extensible design using `Job` and `Schedule` interfaces

## 📦 Technologies Used

- Java 8+
- ScheduledExecutorService (Java Concurrency API)
- java.time API (`LocalDateTime`, `DayOfWeek`, etc.)

## 🧠 Scheduling Logic

- **Hourly**: Runs at a specific minute every hour (e.g., 15th minute of every hour)
- **Daily**: Runs at a specific time daily (e.g., 14:30)
- **Weekly**: Runs on a specific day and time (e.g., Sunday at 10:00 AM)

## 🔧 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/job-scheduler.git
   cd job-scheduler
2. Compile:

bash
Copy
Edit
javac Main.java
3.Run:

3.Run 
bash
java Main
