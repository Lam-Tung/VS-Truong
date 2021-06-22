public class Producer extends Client {
    public Producer(int id, String name, int tPort) {
        super(id, name, ClientType.PRODUCER, tPort);
    }
}
