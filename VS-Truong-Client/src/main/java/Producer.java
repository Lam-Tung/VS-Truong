public class Producer extends Client {
    public Producer(int id, String name, int tPort, int uPort, String hostname) {
        super(id, name, ClientType.PRODUCER, tPort, uPort, hostname);
    }
}
