public class FireTruck {
    private int id;
    private String unitName;        // Nazwa jednostki macierzystej
    private ICarState state;
    private EventType currentEvent; // Typ zdarzenia
    private Location targetLocation; // Cel podróży (baza lub zdarzenie)

    public FireTruck(int id, String unitName) {
        this.id = id;
        this.unitName = unitName;
        this.state = new FreeState();
    }

    public void setState(ICarState state) {
        this.state = state;
    }

    public ICarState getState() {
        return state;
    }

    public void tick() {
        state.handleTick(this);
    }

    public int getId() { return id; }

    // --- Nowe metody ---

    public String getUnitName() { return unitName; }

    public void assignEvent(EventType type) {
        this.currentEvent = type;
    }

    public EventType getCurrentEvent() { return currentEvent; }

    public void setTargetLocation(Location loc) {
        this.targetLocation = loc;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }
}