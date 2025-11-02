# Bluestaq Online Assessment: Elevator System

## Summary
This project models a self-sustaining elevator system with three key stages: requests, assignment, and transit. Development was iterative. I started with a simple single-carriage MVP, then expanded to a centralized dispatcher controlling multiple elevators, and finally added virtual guests and carrying-capacity logic for realistic testing.

Read the project outline PDF for more information on specifics. Flowcharts included!

Key ideas:
- Simple sweep strategy (one direction, then reverse) for elevator operation
- Greedy dispatcher strategy for multicarriage assignment
- Dispatcher request queue to account for carrying capacity and overflows
- Two input modes: manual prompts and auto-generated drop-off and pick-up requests

---

## Features
- **MVP: Single Elevator**: Elevator handles pickup/dropoff requests, sweeps floors, clears requests, then waits for more input.
- **Multicarriage system**: Centralized dispatcher assigns requests to multiple elevators that operate in tandem.
- **Carrying Capacity**: Carriages only hold up to certain amount of guests before dumping excess requests. Dispatcher queue tracks unassigned requests and pairs them up with available carriages.
- **Virtual Simulation**: Program simulates virtual guests by randomly generating drop-off and pick-up requests across different floors, with randomized passenger counts. This lets us stress-test the system and confirm reliable operation.

---

## Architecture
### **Dispatcher**
- Collects pickup/dropoff requests and stores in queue.
- Assigns requests to elevators using a greedy strategy: choose the closest elevator that’s moving toward the floor that the fewest outstanding requests.
- Receives returned requests from elevators when they can’t accept them immediately.

### **Elevator**
- Tracks current floor, direction, onboard guest count, capacity, and assigned stops.
- `update()` advances elevator by one action: move, pick up, drop off, update capacity, and report steady-state.
- If full, elevator dumps incomplete requests back to Dispatcher.

### **Simulation**
- Represents an automated guest request: origin floor, destination floor, and number of guests.
- Used to stress-test capacity and scheduling.
