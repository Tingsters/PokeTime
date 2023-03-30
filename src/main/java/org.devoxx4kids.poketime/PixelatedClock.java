package org.devoxx4kids.poketime;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;

public class PixelatedClock extends Label {
    private DateTimeFormatter clockFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    public BooleanProperty isNight = new SimpleBooleanProperty(false);
    public long multiplier = 1;
    TemporalAdjuster friday13Adjuster = temporal -> {
        if (temporal.get(ChronoField.DAY_OF_MONTH) > 13) temporal = temporal.plus(1, ChronoUnit.MONTHS);
        temporal = temporal.with(ChronoField.DAY_OF_MONTH, 13);
        while (temporal.get(ChronoField.DAY_OF_WEEK) != DayOfWeek.FRIDAY.getValue()) {
            temporal = temporal.plus(1, ChronoUnit.MONTHS);
        }
        return temporal;
    };
    private Clock f13 = Clock.fixed(LocalDateTime.now().with(friday13Adjuster).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private Clock friday13Clock = Clock.offset(Clock.systemDefaultZone(), java.time.Duration.between(LocalDateTime.now(), LocalDateTime.now().with(friday13Adjuster)));
    private MultiplierClock multiplierClock = new MultiplierClock(Clock.systemDefaultZone(), 1000);

    public Clock getClock() {
        return Clock.systemDefaultZone();
    }

    public PixelatedClock() {
        setFont(Main.pixelated);
        setLayoutX((double) Main.CELL_SIZE / 4);
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.millis(1), actionEvent -> {
            LocalDateTime date = LocalDateTime.now(getClock());
            setText(date.format(clockFormat));
            LocalTime time = date.toLocalTime();
//            if (!isNight.isBound()) {
//                isNight.setValue(time.isBefore(LocalTime.of(7, 0)) || time.isAfter(LocalTime.of(19, 0)));
//            }
        }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }
}
