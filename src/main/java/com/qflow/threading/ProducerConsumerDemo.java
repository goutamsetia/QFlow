
package com.qflow.threading;

public class ProducerConsumerDemo {

    // Shared buffer
    static class Buffer {

        private int token;
        private boolean available = false;

        // Producer method
        public synchronized void produce(int token)
                throws InterruptedException {

            while (available) {
                System.out.println("Buffer full! Producer waiting...");
                wait();
            }

            this.token = token;
            available = true;

            System.out.println("Produced Token: " + token);

            notify();
        }

        // Consumer method
        public synchronized int consume()
                throws InterruptedException {

            while (!available) {
                System.out.println("Buffer empty! Consumer waiting...");
                wait();
            }

            int consumedToken = token;
            available = false;

            System.out.println("Consumed Token: " + consumedToken);

            notifyAll();

            return consumedToken;
        }
    }

    public static void main(String[] args) {

        Buffer buffer = new Buffer();

        // Producer Thread
        Thread producer = new Thread(new Runnable() {

            public void run() {

                try {
                    for (int i = 101; i <= 105; i++) {

                        buffer.produce(i);

                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Consumer Thread
        Thread consumer = new Thread(new Runnable() {

            public void run() {

                try {
                    for (int i = 101; i <= 105; i++) {

                        buffer.consume();

                        Thread.sleep(1500);
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Start both threads
        producer.start();
        consumer.start();
    }
}
