package VirtualGuests;

import java.util.*;

/*
 * Simulates the arrival of virtual guests by making random number of requests at random
 * floors to stress-test the elevator system. Replaces user prompting as a means of input.
 */
public final class Simulator {
    private static Random random = new Random();

    /**
     * Generates a random number of drop-off requests across different floors
     * for a specific elevator. Dropped off guests will not exceed count.
     * 
     * @return the list of drop-off requests
     */
    public static Vector<Request> generateDropoffs(Elevator e) {
        Vector<Request> dropoffs = new Vector<>();
        boolean[] vis = new boolean[Setting.NUM_FLOORS+1];
        int guestSum = 0;
        for (int i=0;i<random.nextInt(Setting.NUM_FLOORS+1);i++) {
            if (guestSum >= e.getCapacity()) break; //Emptied carriage
            int floor = random.nextInt(Setting.NUM_FLOORS)+1;
            if (!vis[floor]) {
                vis[floor] = true;
                int numGuests = random.nextInt(e.getCapacity()-guestSum)+1;
                dropoffs.add(new Request(floor, -1, numGuests));
                guestSum += numGuests;
            }
        }
        return dropoffs;
    }

    /**
     * Generates a random number of pick-up requests across different floors
     * for a specific elevator. Capped at max guests per floor outlined in settings.
     * 
     * @return the list of pick-up requests
     */
    public static Vector<Request> generatePickups() {
        Vector<Request> pickups = new Vector<>();
        boolean[] vis = new boolean[Setting.NUM_FLOORS+1];
        for (int i=0;i<random.nextInt(Setting.NUM_FLOORS+1);i++) {
            int floor = random.nextInt(Setting.NUM_FLOORS)+1;
            if (!vis[floor]) {
                vis[floor] = true;
                int numGuests = random.nextInt(Setting.MAX_GENERATED_GUESTS)+1;
                pickups.add(new Request(floor, -1, numGuests));
            }
        }
        return pickups;
    }
}
