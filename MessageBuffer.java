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

    public synchronized Message get(int myPid) {
        // Wait until message is available.
        while (numItems == 0) {
            try {
                System.out.println(myPid + " is waiting for a message");
                wait();
            } catch (InterruptedException e) {}
        }
        
        //check the available message isn't one I sent ..
        if ( messages[back].getPid() == myPid) {
            // I don't want this message. Notify others so someone else can grab it
            System.out.println(myPid + ":Same pid as message in buffer");
            notifyAll();
            return null;
        } else {
            // Toggle status.
            numItems--;
            // Notify producer that buffer is empty
            Message msg = messages [back];
            back = (back + 1) % CAPACITY ;
            notifyAll();
            return msg;
        }
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
    

