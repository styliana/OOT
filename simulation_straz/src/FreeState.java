// Stan: Wolny [cite: 63]
public class FreeState implements ICarState {
    @Override
    public void handleTick(FireTruck truck) {
        // Czeka na zgłoszenie. Nic nie robi w pętli czasu.
    }
    @Override
    public String getStatusName() { return "WOLNY"; }
}