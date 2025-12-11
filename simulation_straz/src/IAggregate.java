public interface IAggregate<T> {
    IIterator<T> iterator();
}