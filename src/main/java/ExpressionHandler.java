import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class ExpressionHandler {

    private final ReversePolishNotationStack reversePolishNotationStack = new ReversePolishNotationStack();

    /**
     * Iterate by {@param list} if string starts with {@link SupportedOperations#START_SENSOR_ID_TOKEN}
     * then get token number.
     * @return Set of token numbers
     */
    public Set<Long> getTokenIds(Iterable<String> list) {
        Set<Long> result = new HashSet<>();
        for (String s : list) {
            if(s.charAt(0) == SupportedOperations.START_SENSOR_ID_TOKEN){
                result.add(Long.valueOf(s.substring(1, s.length() - 1)));
            }
        }
        return result;
    }

    /**
     * Handle {@param list} Firstly convert {@param s} to Deque<String> and then get token by {@link #getTokenIds(Iterable)}
     * @return Set of token numbers
     */
    public Set<Long> getTokenIds(String s) {
        Deque<String> list = reversePolishNotationStack.create(s);
        return getTokenIds(list);
    }
}
