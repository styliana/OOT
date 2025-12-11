import java.util.ArrayList;
import java.util.List;

public class FireUnit implements IObserver {
    private String name;
    private Location location;
    private List<FireTruck> trucks;

    public FireUnit(String name, double lat, double lon) {
        this.name = name;
        this.location = new Location(lat, lon);
        this.trucks = new ArrayList<>();
        // Inicjalizacja z przypisaniem nazwy i początkowej lokalizacji (bazy)
        for (int i = 0; i < 5; i++) {
            FireTruck truck = new FireTruck(i + 1, this.name);
            truck.setTargetLocation(this.location);
            trucks.add(truck);
        }
    }

    public Location getLocation() { return location; }
    public String getName() { return name; }

    // Getter potrzebny dla Mapy
    public List<FireTruck> getTrucks() { return trucks; }

    public int getAvailableTrucksCount() {
        int count = 0;
        for (FireTruck t : trucks) {
            if (t.getState() instanceof FreeState) count++;
        }
        return count;
    }

    // Zaktualizowana metoda dysponowania (typ + lokalizacja)
    public int dispatchTrucks(int amount, EventType type, Location eventLoc) {
        int dispatched = 0;
        for (FireTruck t : trucks) {
            if (dispatched >= amount) break;
            if (t.getState() instanceof FreeState) {
                t.assignEvent(type);
                t.setTargetLocation(eventLoc); // Jedź do zdarzenia
                t.setState(new TravelingState());
                dispatched++;
            }
        }
        return dispatched;
    }

    @Override
    public void update() {
        for (FireTruck truck : trucks) {
            truck.tick();

            // Jeśli samochód jest wolny lub wraca -> jego pozycja to baza
            if (truck.getState() instanceof FreeState || truck.getState() instanceof ReturningState) {
                truck.setTargetLocation(this.location);
            }
        }
    }

    @Override
    public String toString() {
        return name + " " + location;
    }
}