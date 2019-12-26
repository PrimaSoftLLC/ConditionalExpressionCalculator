package by.aurorasoft.expressioncalculator.model;

public class RawTokenImpl implements RawToken {

    private final Long id;
    private final String name;

    public RawTokenImpl(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
