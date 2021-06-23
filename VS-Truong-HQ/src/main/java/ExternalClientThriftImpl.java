import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ExternalClientThriftImpl implements ExternalClientThriftService.Iface{
    private HQ hq;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalClientThriftImpl.class);
    public ExternalClientThriftImpl(HQ hq) {
        this.hq = hq;
    }

    @Override
    public String getStatus() throws TException {
        return hq.status;
    }

    @Override
    public String getHistory() throws TException {
        LOGGER.info("History request received...");
        StringBuilder result = new StringBuilder();
        Map<Integer, List<Integer>> history = hq.getHistory();

        for (Map.Entry<Integer, List<Integer>> entry : history.entrySet()) {
            result.append("Client ").append(entry.getKey()).append(":\n");
            List<Integer> powerHistory = entry.getValue();
            for (int i = 0; i < powerHistory.size(); i++) {
                if (i == (powerHistory.size() - 1)) {
                    result.append(powerHistory.get(i)).append("\n");
                } else {
                    result.append(powerHistory.get(i)).append(", ");
                }
            }
        }

        return result.toString();
    }
}
