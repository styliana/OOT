import java.util.ArrayList;
import java.util.List;

public class UnitsCollection implements IAggregate<FireUnit> {
    private List<FireUnit> units = new ArrayList<>();

    public void addUnit(FireUnit unit) {
        units.add(unit);
    }

    public List<FireUnit> getList() { return units; } // Pomocnicze dla sortowania

    @Override
    public IIterator<FireUnit> iterator() {
        return new UnitIterator(units);
    }

    // Klasa wewnętrzna iteratora
    private class UnitIterator implements IIterator<FireUnit> {
        private List<FireUnit> list;
        private int index = 0;

        public UnitIterator(List<FireUnit> list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return index < list.size();
        }

        @Override
        public FireUnit next() {
            return list.get(index++);
        }
    }
}