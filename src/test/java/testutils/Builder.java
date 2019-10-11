package testutils;

import by.aurorasoft.expressioncalculator.model.EToken;
import by.aurorasoft.expressioncalculator.model.ETokens;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Builder {
    public static EToken build(String s){
        return ETokens.create(s);
    }

    public static List<EToken> build(Collection<String> c){
        return c.stream().map(ETokens::create).collect(Collectors.toList());
    }
}
