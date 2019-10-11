package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;

import java.util.HashSet;
import java.util.Set;

public class ExpressionHandler {

    private final ReversePolishNotationStack reversePolishNotationStack = new ReversePolishNotationStack();

    /**
     * Iterate by {@param list} if string starts with {@link SupportedOperations#START_SENSOR_ID_TOKEN}
     * then get token number.
     *
     * @return Set of token numbers
     */
    public Set<Long> getTokenIds(Iterable<EToken> tokens) {
        Set<Long> r = new HashSet<>();
        if (tokens == null) {
            return r;
        }
        for (EToken t : tokens) {
            if (t.is(EType.TOKEN)) {
                r.add(t.getTokenId());
            }
        }
        return r;
    }

    /**
     * Handle {@param list} Firstly convert {@param s} to Deque<String> and then get token by {@link #getTokenIds(Iterable)}
     *
     * @return Set of token numbers
     */
    public Set<Long> getTokenIds(String s) {
        return s != null ? getTokenIds(reversePolishNotationStack.create(s)) : new HashSet<>();
    }
}
