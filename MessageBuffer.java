/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicalclock;

/**
 *
 * @author Ian Gortan
 */
public class MessageBuffer {
    private Message messages[];
    final private int CAPACITY;
    private int front, back = 0;

    private int numItems = 0;

    public MessageBuffer (int size) {
        CAPACITY = size;
        messages = new Message [CAPACITY];
    }

    public synchronized Message get() {
        // Wait until message is available.
        while (numItems == 0) {
            try {
                System.out.println("Waiting for a message");
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        numItems--;
        // Notify producer that buffer is empty
        notifyAll();
        Message msg = messages [back];
        back = (back + 1) % CAPACITY ;
        return msg;

    }

    public synchronized void put(Message message) {
        // Wait until message has been retrieved.
        while (numItems == CAPACITY) {
            try {
                wait();
                System.out.println("Buffer is full. Waiting ....");
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        numItems++;
        // Store message.
        this.messages[front] = message;
        front = (front + 1 ) % CAPACITY ;
        // Notify consumer that message is available
        notifyAll();
    }

}
    

