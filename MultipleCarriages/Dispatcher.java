package MultipleCarriages;

import java.util.*;
import java.math.*;

public class Dispatcher {
    private Elevator[] elevators;
    public Dispatcher() {
        elevators = new Elevator[(int) (Math.log(Setting.NUM_FLOORS / 10) / Math.log(2))];
        for (int i=0;i<elevators.length;i++) {
            elevators[i] = new Elevator();
        }
    }

    /**
     * Prompts users for floors to drop-off to, called from within specific elevator.
     * Adds requests to the elevator's request list.
     * @param e the elevator that performs the drop-off
     */
    public void promptDropoff(Elevator e) {
        //Prompt user for drop-off floors 
        String dropoffPrompt = "What floor(s) would you like to call drop off?";
        Vector<Integer> dropoffFloors = TextPrompt.promptSequence(dropoffPrompt, 1, Setting.NUM_FLOORS, " ");
        
        //Add requests to that specific elevator
        for (int f: dropoffFloors) e.addDropoff(f);

        System.out.println(this);
    }

    /**
     * Prompts users floors to pick-up from. No specified elevator, so we
     * must calculate which elevator to assign to based on transit direction and proximity.
     * Essentially, we find the closest elevator to the target floor traveling toward target.
     */
    public void promptPickup() {
        //Prompt user for pick-up floors
        String pickupPrompt = "What floor(s) would you like to call pick up?";
        Vector<Integer> pickupFloors = TextPrompt.promptSequence(pickupPrompt, 1, Setting.NUM_FLOORS, " ");
        
        //Calculate which elevator to assign to
        Elevator best = elevators[0];

        for (int f: pickupFloors) best.addPickup(f);
    }
    
}
