public class Consumer extends Client {
    public Consumer(int id, String name, int tPort, int uPort, String hostname) {
        super(id, name, ClientType.CONSUMER, tPort, uPort, hostname);
    }
}
