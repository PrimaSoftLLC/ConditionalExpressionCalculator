package by.aurorasoft.expressioncalculator;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.EType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExpressionUtils {

    /**
     * Iterate by {@param list} if string starts with {@link SupportedOperations#START_SENSOR_ID_TOKEN}
     * then get token number.
     *
     * @return Set of token numbers
     */
    public static Set<Long> getTokenIds(Iterable<EToken> tokens) {
        Set<Long> r = new HashSet<>();
        if (tokens == null) {
            return r;
        }
        for (EToken t : tokens) {
            if (t.is(EType.SENSOR_ID)) {
                r.add(t.getSensorId());
            }
        }
        return r;
    }

    public static List<String> getAllParamNamesFromExpression(String expression) {
        List<String> result = new ArrayList<>();
        int start = 0;
        int finish;
        while (start != -1) {
            start = expression.indexOf(SupportedOperations.MESSAGE_PARAMETER_TOKEN, start);
            if (start > 0) {
                finish = expression.indexOf('}', start);
                start++;
                result.add(expression.substring(start, finish));
            }
        }
        return result;
    }

//    /**
//     * Handle {@param list} Firstly convert {@param s} to Deque<String> and then get token by {@link #getTokenIds(Iterable)}
//     *
//     * @return Set of token numbers
//     */
//    public static Set<Long> getTokenIds(String s) {
//        return s != null ? getTokenIds(ReversePolishNotation.create(s)) : new HashSet<>();
//    }
//
//    /**
//     * convert {@param raw} to string token Arrays;
//     */
//    public static String[] toArray(String raw) {
//        String[] arr = new String[raw.length()];
//        int i = 0;
//        ExpressionIterator iterator = new ExpressionIterator(raw);
//        while(iterator.hasNext()){
//            String s  = iterator.nextString();
//            arr[i++] = s;
//        }
//        return Arrays.copyOfRange(arr, 0, i);
//    }
}
