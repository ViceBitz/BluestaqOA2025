package MultipleCarriages;

import java.util.*;
import java.math.*;

public class Dispatcher {
    private Elevator[] elevators;
    public Dispatcher() {
        elevators = new Elevator[(int) (Math.log(Setting.NUM_FLOORS / 10) / Math.log(2))];
        for (int i=0;i<elevators.length;i++) {
            elevators[i] = new Elevator(this);
            elevators[i].start();
        }
    }

    public void nextAction(Elevator e) {
        String pickupPrompt = "What floor(s) would you like to call pick up?";
        Vector<Integer> pickupFloors = TextPrompt.promptSequence(pickupPrompt, 1, Setting.NUM_FLOORS, " ");
        String dropoffPrompt = "What floor(s) would you like to call drop off?";
        Vector<Integer> dropoffFloors = TextPrompt.promptSequence(dropoffPrompt, 1, Setting.NUM_FLOORS, " ");
        
        for (int f: pickupFloors) e.addPickup(f);
        for (int f: dropoffFloors) e.addDropoff(f);

        System.out.println(this);
    }
}
