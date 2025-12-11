// Strategia dla MZ (Miejscowe Zagrożenie)
public class LocalThreatEventStrategy implements IEventStrategy {
    @Override
    public int getRequiredCars() {
        return 2; // [cite: 61]
    }
}