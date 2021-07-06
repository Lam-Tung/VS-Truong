import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ExternalClientThriftImpl implements ExternalClientThriftService.Iface{
    private HQ hq;
    private final int DATA_SETS = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClientThriftImpl.class);
    public ExternalClientThriftImpl(HQ hq) {
        this.hq = hq;
    }

    @Override
    public String getStatus() throws TException {
        return hq.status;
    }

    @Override
    public String getHistory(int index) throws TException {
        LOGGER.info("History request received...");
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


        return result.toString();
    }
}
