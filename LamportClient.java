/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logicalclock;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Ian Gortan
 */
public class LamportClient implements Runnable {
    
    private MessageBuffer buffer;
    final private int NUM_EVENTS = 10; 
    private int pid;
    

    public LamportClient(MessageBuffer buf, int pid) {
        this.buffer = buf;
        this.pid = pid;
    }

    public void run() {
        Random random = new Random();
        //start at -1 just so we can index in to events array easily
        int clock = -1;
        ArrayList<String> events  = new ArrayList<String> ();         
        
        // create a message object
        Message m = new Message ();
        m.setPid(pid);
        m.setClock(clock) ;
        
        // add some 'reandomness' to the order at startup
        if ( (pid%2) == 0 ) {
            clock++;
            m.setClock(clock);
            String event = Integer.toString(pid) + ":internal " + Integer.toString(clock); 
            events.add(event);
        }
        for (int i = 0; i < NUM_EVENTS ; i++ ) {
            // let's generate an event 
            clock++;
            m.setClock(clock);
            if ( ((i%2) == 0) ) {
                // internal event
                String event = Integer.toString(pid) + ":internal " + Integer.toString(clock); 
                events.add(event);
            } else {
                // external event
                String event = Integer.toString(pid) + ":send " + Integer.toString(clock); 
                events.add(event);
                m.setMessageID(event);
                buffer.put(m); // sleep for a random time to induce different orders
                try {
                      Thread.sleep(random.nextInt(2000));
                } catch (InterruptedException e) {}
            
                // get a message and set clock according to Lamport algorithm
                //System.out.println("Getting a message: " + Integer.toString(pid));
                Message recMsg = buffer.get(pid);
                 // chck it wasn't a message we sent (null returned) as we want to ignore those
                 
                if (recMsg != null) {
                    // add the event ...
                    if (recMsg.getClock() > clock) {
                        clock = recMsg.getClock() + 1; 
                    }
                    event = Integer.toString(pid) + ":receive from " + recMsg.getMessageID() +  ":new clock:" +Integer.toString(clock); 
                    events.add(event);
                }
                
                
            }
           
                
        }
        
        System.out.println("Process " + pid + " complete and ending");
        // your code
        for (String event: events) {
            System.out.println(event);
        }
            
          
     }
 
}
    

