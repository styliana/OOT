import java.util.Random;

// Stan: Powrót [cite: 68]
public class ReturningState implements ICarState {
    private int timeRemaining;

    public ReturningState() {
        // Czas powrotu 0-3s [cite: 66]
        this.timeRemaining = new Random().nextInt(4);
    }

    @Override
    public void handleTick(FireTruck truck) {
        if (timeRemaining > 0) {
            timeRemaining--;
        } else {
            System.out.println("Samochód " + truck.getId() + ": Wrócił do bazy.");
            truck.setState(new FreeState()); // Ustawienie na wolny [cite: 68]
        }
    }
    @Override
    public String getStatusName() { return "POWRÓT (" + timeRemaining + "s)"; }
}