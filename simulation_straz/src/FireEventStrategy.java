// Strategia dla PZ (Pożar)
public class FireEventStrategy implements IEventStrategy {
    @Override
    public int getRequiredCars() {
        return 3; // [cite: 61]
    }
}