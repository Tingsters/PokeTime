package sample;

import java.time.*;

public class MultiplierClock extends Clock {
    private final Instant creationTime;
    private Clock base;
    private final long multiplier;
    public MultiplierClock(Clock base, long multiplier) {
        this.base = base;
        this.multiplier = multiplier;
        creationTime = base.instant();
    }
    @Override
    public ZoneId getZone() {
        return base.getZone();
    }
    @Override
    public Clock withZone(ZoneId zone) {
        return base.withZone(zone);
    }
    @Override
    public Instant instant() {
        Instant now = base.instant();
        return creationTime.plus(Duration.between(creationTime, now).multipliedBy(multiplier));
    }
}
