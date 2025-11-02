package VirtualGuests;

import java.util.*;
import java.math.*;

public class Dispatcher {
    private Elevator[] elevators;
    private Queue<Request> requestQueue;

    public Dispatcher() {
        elevators = new Elevator[1 + (int) (Math.log(Setting.NUM_FLOORS / 5) / Math.log(2))];
        for (int i=0;i<elevators.length;i++) {
            elevators[i] = new Elevator(this, i);
        }
        requestQueue = new LinkedList();
    }
    
    /**
     * Prompts users for floors to drop-off to, called from within specific elevator.
     * Adds requests to the elevator's request list.
     * @param e the elevator that performs the drop-off
     */
    public void promptDropoff(Elevator e) {
        Vector<Integer> dropoffFloors;
        Vector<Integer> guestCount;
        if (!Setting.AUTO_REQUESTS) {
            //Prompt user for drop-off floors 
            String dropoffPrompt = "What floor(s) would you like to call drop off for elevator " + e.getId();
            dropoffFloors = TextPrompt.promptSequence(dropoffPrompt, 1, Setting.NUM_FLOORS, " ");
            
            int guestSum = 0;
            do {
                String guestPrompt = "How many guests per drop-off request?";
                guestCount = TextPrompt.promptSequence(guestPrompt, 1, Integer.MAX_VALUE, " ");
                
                //Tally guest counts to check for invalid input
                guestSum = 0;
                for (int g: guestCount) guestSum += g;
                if (guestSum > e.getCapacity()) {
                    System.out.println("Invalid number of guests! Drop-off quantity exceeds actual capacity.");
                }
            }
            while (guestSum > e.getCapacity());

            //Check size of input sequences
            if (dropoffFloors.size() != guestCount.size()) {
                System.out.println("Input lengths don't match! Try again.");
                promptDropoff(e);
            }
            
        }
        else {
            //Automatically populate dropoff requests
            dropoffFloors = new Vector<>();
            guestCount = new Vector<>();
            for (Request r: Simulator.generateDropoffs(e)) {
                dropoffFloors.add(r.floor);
                guestCount.add(r.numGuests);
            }
        }
        
        //Directly assign request to that specific elevator
        for (int i=0;i<dropoffFloors.size();i++) {
            int f = dropoffFloors.get(i);
            int num = guestCount.get(i);
            e.addDropoff(f, num);
        } 
    }

    /**
     * Calculates which elevator to assign pick-up request to based on transit direction and proximity.
     * Essentially, we find the closest elevator traveling toward the target floor. If there's a
     * tie, then we pick the elevator with the lesser requests.
     */
    public Elevator assignPickup(Request r) {
        int f = r.floor;

        //Calculate which elevator to assign each floor to
        Elevator best = elevators[0];
        int closestDist = Integer.MAX_VALUE;
        boolean bestMatchesDir = false;
        int minRequests = Integer.MAX_VALUE;
        for (Elevator e: elevators) {
            int dist = f - e.getFloor();
            int unitVec = (dist == 0) ? 0 : dist / Math.abs(dist);
            boolean matchesDir = unitVec == e.getDirection();
            int numRequests = e.getNumRequests();
            //Order by direction
            if (matchesDir || !bestMatchesDir) {
                bestMatchesDir = matchesDir;
                //Order by distance
                if (dist < closestDist) {
                    best = e;
                    closestDist = dist;
                    minRequests = numRequests;
                }
                else if (dist == closestDist) {
                    //Order by number of requests
                    if (numRequests < minRequests) {
                        best = e;
                        minRequests = numRequests;
                    }
                }
            }
        }

        return best;
    }
    /**
     * Prompts users floors to pick-up from. Because there is no specified elevator, the dispatcher
     * employs a scheduling strategy to assign requests to elevators. Requests are first
     * stored within a queue and then paired one-by-one with elevators.
     */
    public void promptPickup() {
        Vector<Integer> pickupFloors;
        Vector<Integer> guestCount;

        if (!Setting.AUTO_REQUESTS) {
            //Prompt user for pick-up floors
            String pickupPrompt = "What floor(s) would you like to call pick up?";
            pickupFloors = TextPrompt.promptSequence(pickupPrompt, 1, Setting.NUM_FLOORS, " ");
            String guestPrompt = "How many guests per pick-up request?";
            guestCount = TextPrompt.promptSequence(guestPrompt, 1, Integer.MAX_VALUE, " ");
            
            //Check size of input sequences
            if (pickupFloors.size() != guestCount.size()) {
                System.out.println("Input lengths don't match! Try again.");
                promptPickup();
            }
        }
        else {
            //Automatically populate pick-up requests
            pickupFloors = new Vector<>();
            guestCount = new Vector<>();

            for (Request r: Simulator.generatePickups()) {
                pickupFloors.add(r.floor);
                guestCount.add(r.numGuests);
            }
        }

        //Push all incoming requests to dispatcher queue
        for (int i=0;i<pickupFloors.size();i++) {
            int f = pickupFloors.get(i);
            int num = guestCount.get(i);
            requestQueue.add(new Request(f, -1, num));
        }

        //Process requests in queue
        while (!requestQueue.isEmpty()) {
            Request cur = requestQueue.peek();
            Elevator best = assignPickup(cur);
            best.addPickup(cur.floor, cur.numGuests);
            requestQueue.poll();
        }
    }

    /**
     * Adds a pick-up request back into dispatcher queue, presumably dumped by
     * elevator after max capacity reached.
     * @param floor the floor for pick-up
     * @param numGuests the number of guests
     */
    public void addPickup(int floor, int numGuests) {
        requestQueue.add(new Request(floor, -1, numGuests));
    }

    /**
     * Steps through next frame in the elevator simulation, calling update on all elevators
     * in sequence and prompting user if necessary
     */
    public void step() {
        for (Elevator e: elevators) {
            boolean madeUpdate = e.update(); //Is elevator in steady state?
            if (!madeUpdate) {
                System.out.println(e);

                System.out.println("Dispatcher Request Queue:");
                for (Request r: requestQueue) {
                    System.out.println(r);
                }

                promptDropoff(e);
                promptPickup();
            }
        }
    }
    
}
