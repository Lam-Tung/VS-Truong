import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class HqThriftServiceImpl implements HqThriftService.Iface{
    private HQ hq;
    private final int DATA_SETS = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(HqThriftServiceImpl.class);

    public HqThriftServiceImpl(HQ hq) {
        this.hq = hq;
    }

    @Override
    public String getOtherHqStatus(int index) throws TException {

        // history
        StringBuilder result = new StringBuilder();
        Map<Integer, List<Integer>> history = hq.getHistory();

        for (Map.Entry<Integer, List<Integer>> entry : history.entrySet()) {
            result.append("Client ").append(entry.getKey()).append(":\n");
            List<Integer> powerHistory = entry.getValue();
            int startIndex = index * DATA_SETS;

            if (startIndex > powerHistory.size()) {
                startIndex--;
            }

            for (int i = startIndex; i < powerHistory.size(); i++) {
                result.append(powerHistory.get(i)).append(", ");
            }

            result.append("\n");
        }

        result.append("Client status: \n");

        // client status
        Map<Integer, Boolean> statusClients = hq.getStatusClients();
        for (Map.Entry<Integer, Boolean> entry : statusClients.entrySet()) {
            result.append("Client ").append(entry.getKey()).append(":\n");
            boolean value = entry.getValue();
            if (value) {
                result.append("down").append(", ");
            } else {
                result.append("up").append(", ");
            }
            result.append("\n");
        }

        return result.toString();
    }
}
