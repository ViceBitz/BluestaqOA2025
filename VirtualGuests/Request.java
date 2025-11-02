package VirtualGuests;
/*
* Stores requests at floor and given time
*/
public class Request implements Comparable<Request>{
    int floor;
    int timestamp;
    int numGuests;
    public Request(int floor, int timestamp, int numGuests) {
        this.floor = floor;
        this.timestamp = timestamp;
        this.numGuests = numGuests;
    }
    public int compareTo(Request o) {
        return floor - o.floor;
    }
    public String toString() {
        return "Floor: " + floor + " | " + "Guests: " + numGuests;
    }
}