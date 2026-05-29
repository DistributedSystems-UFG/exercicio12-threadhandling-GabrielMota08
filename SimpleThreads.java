public class SimpleThreads {

    // Display a message, preceded by the name of the current thread
    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    private static class MessageLoop
        implements Runnable {
        public void run() {
            String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
            };
            try {
                for (int i = 0; i < importantInfo.length; i++) {
                    // Pause for 4 seconds
                    Thread.sleep(4000);
                    // Print a message
                    threadMessage(importantInfo[i]);
                }
            } catch (InterruptedException e) {
                threadMessage("I wasn't done!");
            }
        }
    }

    private static class CpuIntensiveLoop implements Runnable {
        public void run() {
            long count = 0;
            double dummyResult = 0;
            
            while (!Thread.currentThread().isInterrupted()) {
                dummyResult += Math.sqrt(count);
                count++;
                
                if (count % 500_000_000L == 0) {
                    threadMessage("Processando... (Iteração: " + count + ")");
                }
            }
            
            threadMessage("Thread interrompida " + count);
        }
    }

    public static void main(String args[])
        throws InterruptedException {

        // Delay, in milliseconds before we interrupt MessageLoop thread (default one hour)
        long patience = 1000 * 60 * 60;

        // If command line argument present, gives patience in seconds
        if (args.length > 0) {
            try {
                patience = Long.parseLong(args[0]) * 1000;
            } catch (NumberFormatException e) {
                System.err.println("Argument must be an integer.");
                System.exit(1);
            }
        }

        threadMessage("Starting MessageLoop thread");
        long startTime = System.currentTimeMillis();
        Thread t = new Thread(new MessageLoop(), "Thread-Mensagens");
        Thread t2 = new Thread(new CpuIntensiveLoop(), "Thread-CPU");

        t.start();
        t2.start();
	// Put the MessageLoop thread to run
        while (t.isAlive() || t2.isAlive()) {
            threadMessage("Still waiting...");
            Thread.sleep(1000); 
            
            long tempoDecorrido = System.currentTimeMillis() - startTime;
            
            if (tempoDecorrido > patience) {
                threadMessage("Tired of waiting!");
                
                if (t.isAlive()) t.interrupt();
                if (t2.isAlive()) t2.interrupt();
                
                t.join();
                t2.join();
            }
        }
        threadMessage("Finally!");
    }
}
