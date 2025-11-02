package MultipleCarriages;

import java.util.*;
import java.math.*;

public class Dispatcher {
    private Elevator[] elevators;
    public Dispatcher() {
        elevators = new Elevator[1 + (int) (Math.log(Setting.NUM_FLOORS / 5) / Math.log(2))];
        for (int i=0;i<elevators.length;i++) {
            elevators[i] = new Elevator(i);
        }
    }

    /**
     * Prompts users for floors to drop-off to, called from within specific elevator.
     * Adds requests to the elevator's request list.
     * @param e the elevator that performs the drop-off
     */
    public void promptDropoff(Elevator e) {
        //Prompt user for drop-off floors 
        String dropoffPrompt = "What floor(s) would you like to call drop off for elevator " + e.getId();
        Vector<Integer> dropoffFloors = TextPrompt.promptSequence(dropoffPrompt, 1, Setting.NUM_FLOORS, " ");
        
        //Add requests to that specific elevator
        for (int f: dropoffFloors) e.addDropoff(f);
    }

    /**
     * Prompts users floors to pick-up from. Because there is no specified elevator, the dispatcher
     * calculates which elevator to assign to based on transit direction and proximity.
     * Essentially, we find the closest elevator traveling toward the target floor. If there's a
     * tie, then we pick the elevator with the lesser requests.
     */
    public void promptPickup() {
        //Prompt user for pick-up floors
        String pickupPrompt = "What floor(s) would you like to call pick up?";
        Vector<Integer> pickupFloors = TextPrompt.promptSequence(pickupPrompt, 1, Setting.NUM_FLOORS, " ");
        
        for (int f: pickupFloors) {
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
            best.addPickup(f);
        }
    }

    /**
     * Steps through next frame in the elevator simulation, calling update on all elevators
     * in sequence and prompting user if necessary
     */
    public void step() {
        for (Elevator e: elevators) {
            boolean madeUpdate = e.update();
            if (!madeUpdate) {
                promptDropoff(e);
                promptPickup();
            }
        }
    }
    
}
