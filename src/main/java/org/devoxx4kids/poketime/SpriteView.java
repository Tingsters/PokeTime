package org.devoxx4kids.poketime;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;

import javafx.application.Platform;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import javafx.beans.value.ChangeListener;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;

import javafx.scene.Group;
import javafx.scene.Node;

import javafx.scene.control.Label;

import javafx.scene.effect.ColorAdjust;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import javafx.util.Duration;
import org.devoxx4kids.poketime.Main.Direction;
import org.devoxx4kids.poketime.Main.Location;


public class SpriteView extends StackPane {

    protected final ImageView imageView;
    private Color color;
    EventHandler<ActionEvent> arrivalHandler;
    double colorOffset;
    private int spritesX;

    private SpriteView following;
    IntegerProperty number = new SimpleIntegerProperty();

    ObjectProperty<Direction> direction = new SimpleObjectProperty<>();
    ObjectProperty<Location> location = new SimpleObjectProperty<>();
    IntegerProperty frame = new SimpleIntegerProperty(1);
    int spriteWidth;
    int spriteHeight;
    Timeline walking;
    SpriteView follower;
    double speed;

    public SpriteView(Image spriteSheet, SpriteView following) {

        this(spriteSheet,
            following.getLocation()
                .offset(-following.getDirection().getXOffset(), -following.getDirection().getYOffset()));
        number.set(following.number.get() + 1);
        this.following = following;
        setDirection(following.getDirection());
        following.follower = this;
        setMouseTransparent(true);
    }


    public SpriteView(Image spriteSheet, Location loc) {

        this(spriteSheet, loc, 3, 4, 1);
    }


    public SpriteView(Image spriteSheet, Location loc, int spritesX, int spritesY, double speed) {

        this.spritesX = spritesX;
        this.speed = speed;
        imageView = new ImageView(spriteSheet);
        this.location.set(loc);
        Main.sprites.add(this);
        setTranslateX(loc.getX() * Main.CELL_SIZE);
        setTranslateY(loc.getY() * Main.CELL_SIZE);

        ChangeListener<Object> updateImage = (ov, o, o2) ->
                imageView.setViewport(new Rectangle2D(frame.get() * spriteWidth,
                        direction.get().getOffset() * spriteHeight, spriteWidth, spriteHeight));
        direction.addListener(updateImage);
        frame.addListener(updateImage);
        spriteWidth = (int) (spriteSheet.getWidth() / spritesX);
        spriteHeight = (int) (spriteSheet.getHeight() / spritesY);
        direction.set(Direction.RIGHT);
        getChildren().add(imageView);
        setPrefSize(Main.CELL_SIZE, Main.CELL_SIZE);
        StackPane.setAlignment(imageView, Pos.BOTTOM_CENTER);
    }

    public void setDirection(Direction direction) {

        this.direction.setValue(direction);
    }


    protected boolean inBounds(Direction direction) {

        Location loc = location.getValue().offset(direction.getXOffset(), direction.getYOffset());

        return (loc.cell_x >= 0) && (loc.cell_x < Main.HORIZONTAL_CELLS) && (loc.cell_y >= 0)
                && (loc.cell_y < Main.VERTICAL_CELLS);
    }


    public int getNumber() {

        return number.get();
    }


    public SpriteView getFollowing() {

        return following;
    }


    static Image loadImage(String url) {

        return loadImage(url, 3, 4);
    }


    static Image loadImage(String url, int spritesX, int spritesY) {

        return new Image(SpriteView.class.getResource(url).toString(), Main.SPRITE_SIZE * spritesX * Main.SCALE,
                Main.SPRITE_SIZE * spritesY * Main.SCALE, true, false);
    }


    public void startAnimation() {

        long mult = Main.pixelatedClock.multiplier;
        Timeline timeline = new Timeline(Animation.INDEFINITE,
                new KeyFrame(Duration.seconds(.25 / speed * mult), new KeyValue(frame, 0)),
                new KeyFrame(Duration.seconds(.5 / speed * mult), new KeyValue(frame, 1)),
                new KeyFrame(Duration.seconds(.75 / speed * mult), new KeyValue(frame, 2)),
                new KeyFrame(Duration.seconds(1L / speed * mult), new KeyValue(frame, spritesX == 3 ? 1 : 3)));
        timeline.onFinishedProperty().setValue(e -> timeline.play());
        timeline.play();
    }


    public void moveTo(Location loc) {

        long mult = Main.pixelatedClock.multiplier;

        if (loc.cell_x < 0)
            loc.cell_x = Main.HORIZONTAL_CELLS - 1;

        if (loc.cell_x >= Main.HORIZONTAL_CELLS)
            loc.cell_x = 0;

        if (loc.cell_y < 0)
            loc.cell_y = Main.VERTICAL_CELLS - 1;

        if (loc.cell_y >= Main.VERTICAL_CELLS) {
            loc.cell_y = 0;
        }

        direction.setValue(location.getValue().directionTo(loc));
        location.setValue(loc);
        walking = new Timeline(Animation.INDEFINITE,
                new KeyFrame(Duration.seconds(1.0 / speed * mult),
                        new KeyValue(translateXProperty(), loc.getX() * Main.CELL_SIZE)),
                new KeyFrame(Duration.seconds(1.0 / speed * mult),
                        new KeyValue(translateYProperty(), loc.getY() * Main.CELL_SIZE)),
                new KeyFrame(Duration.seconds(.25 / speed * mult), new KeyValue(frame, 0)),
                new KeyFrame(Duration.seconds(.5 / speed * mult), new KeyValue(frame, 1)),
                new KeyFrame(Duration.seconds(.75 / speed * mult), new KeyValue(frame, 2)),
                new KeyFrame(Duration.seconds(1L / speed * mult), new KeyValue(frame, spritesX == 3 ? 1 : 3)));
        walking.setOnFinished(e -> {
            if (arrivalHandler != null) {
                arrivalHandler.handle(e);
            }
        });
        Platform.runLater(walking::play);
    }


    public void move(Direction direction) {

        if (walking != null && walking.getStatus().equals(Animation.Status.RUNNING))
            return;

        moveTo(location.getValue().offset(direction.getXOffset(), direction.getYOffset()));
    }


    public Location getLocation() {

        return location.get();
    }


    public Direction getDirection() {

        return direction.get();
    }


    public void setColor(Color color) {

        this.color = color;

        if (color == null) {
            imageView.setEffect(null);
        } else {
            imageView.setEffect(new ColorAdjust(color.getHue() / 180 - colorOffset, 0.3, 0, 0));
        }
    }


    public Color getColor() {

        return color;
    }

    // Images by Stevie [aka Laufeyon]: http://laufeyon.deviantart.com/art/UTAU-RPG-VX-Sprite-Sheet-1-323819435
    // Zombified by Cassandra Chin
    public static class Rattfratz extends Pokemon {

        public Rattfratz(Location loc) {

            super("rattfratz", loc, 3);
            avoid = Main.pokeTrainer;
        }
    }

    public static class Griffel extends Pokemon {

        public Griffel(Location loc) {

            super("griffel", loc, 2);
            avoid = Main.pokeTrainer;
            sleepy();
        }
    }

    public static class Bidiza extends Pokemon {

        public Bidiza(Location loc) {

            super("bidiza", loc, .5);
            avoid = Main.pokeTrainer;
            sleepy();
        }
    }

    static class Krebscorps extends Pokemon {

        public Krebscorps(Location loc) {

            super("krebscorps", loc, 1.2);
            avoid = Main.pokeTrainer;
        }
    }

    public static class Larvitar extends Pokemon {

        public Larvitar(Location loc) {

            super("larvitar", loc, 1.2);
            avoid = Main.pokeTrainer;
            sleepy();
        }
    }

    public static class Mampfaxo extends Pokemon {

        public Mampfaxo(Location loc) {

            super("mampfaxo", loc, .5);
            sleepy();
        }
    }

    static class Pokemon extends RandomWalker {

        private String name;
        private final Image front;
        private final Image back;

        private Pokemon(String name, Location loc, double speed) {

            super(loadImage("/images/" + name + ".png", 4, 4), loc, 4, 4, speed);
            front = new Image(getClass().getResourceAsStream("/images/" + name + "-front.png"));
            back = new Image(getClass().getResourceAsStream("/images/" + name + "-back.png"));
            this.name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            arrivalHandler =
                    e -> {
                        if (Main.pokeTrainer.location.get().equals(location.get())) {
                            Main.battle(this);
                        }
                    };
        }

        public String getName() {

            return name;
        }


        public Image getFront() {

            return front;
        }


        public Image getBack() {

            return back;
        }
    }

    public static class PokeTrainer extends Shepherd {

        static final Image ANN = loadImage("/images/black-girl.png");

        public PokeTrainer(Location loc) {

            super(ANN, loc);
            arrivalHandler =
                    e -> {
                        for (SpriteView s : Main.sprites) {
                            if (s instanceof Pokemon) {
                                Pokemon p = (Pokemon) s;

                                if (s.location.get().equals(location.get())) {
                                    Main.battle(p);
                                }
                            }
                        }
                    };
        }

        public void die() {

            Main.gameover = true;

            RotateTransition rotate = new RotateTransition(Duration.seconds(3), PokeTrainer.this);
            rotate.byAngleProperty().set(1080);
            rotate.setOnFinished(actionEvent -> Main.root.getChildren().remove(PokeTrainer.this));
            rotate.play();
            Main.sprites.remove(this);
            Main.root.getChildren().add(new Rectangle(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, Color.color(0, 0, 0, .4)));

            Label label = new Label("GAME OVER");
            label.setTextFill(Color.WHITESMOKE);
            label.setAlignment(Pos.BASELINE_CENTER);
            label.setFont(Main.pixelated);
            label.setPrefHeight(Main.BOARD_HEIGHT);
            label.setPrefWidth(Main.BOARD_WIDTH);
            Main.root.getChildren().add(label);
        }


        public void win() {

            Main.root.getChildren().add(new Rectangle(Main.BOARD_WIDTH, Main.BOARD_HEIGHT, Color.color(0, 0, 0, .4)));

            Label label = new Label("DU HAST GEWONNEN!!!");
            label.setTextFill(Color.LIGHTGREEN);
            label.setAlignment(Pos.BASELINE_CENTER);
            label.setFont(Main.pixelated);
            label.setPrefHeight(Main.BOARD_HEIGHT);
            label.setPrefWidth(Main.BOARD_WIDTH);
            Main.root.getChildren().add(label);
        }
    }

    public static class RandomWalker extends SpriteView {

        protected Timeline walk;
        protected boolean idle = false;
        protected Location target;
        protected SpriteView avoid;

        public RandomWalker(Image spriteSheet, Location loc) {

            this(spriteSheet, loc, 3, 4, 1);
        }


        public RandomWalker(Image spriteSheet, Location loc, int spritesX, int spritesY, double speed) {

            super(spriteSheet, loc, spritesX, spritesY, speed);

            long mult = Main.pixelatedClock.multiplier;
            walk = new Timeline(new KeyFrame(Duration.seconds(.2 * mult),
                    actionEvent -> {
                        if (idle)
                            return;

                        if (target != null) {
                            move(getLocation().directionTo(target));
                        } else if (avoid != null && (getLocation().distance(avoid.location.get()) < 2)) {
                            move(getLocation().directionFrom(avoid.location.get()));
                        } else {
                            Direction random = Direction.random();

                            if (inBounds(random)) {
                                move(random);
                            }
                        }
                    }));
            walk.setCycleCount(Timeline.INDEFINITE);
            walk.play();
            Main.earthquake.addListener((observable, oldValue, earthquake) -> {
                if (earthquake) {
                    stop();
                } else {
                    play();
                }
            });
        }

        public void sleepy() {

            Main.nacht.addListener((observable, oldValue, nacht) -> {
                if (nacht) {
                    stop();
                    setEffect(new ColorAdjust(0, 0, -.5, 0));
                } else {
                    play();
                    setEffect(null);
                }
            });
        }


        public void stop() {

            walk.stop();
        }


        public void play() {

            walk.play();
        }
    }

    public static class Shepherd extends RandomWalker {

        private ObservableList<SpriteView> animals;

        public Shepherd(Image spriteSheet, Location loc) {

            this(spriteSheet, loc, 3, 4);
        }


        public Shepherd(Image spriteSheet, Location loc, int spritesX, int spritesY) {

            super(spriteSheet, loc, spritesX, spritesY, 1);
            animals = FXCollections.observableArrayList();
            animals.addListener((ListChangeListener) c -> {
                ObservableList<Node> children = ((Group) getParent()).getChildren();

                while (c.next()) {
                    if (c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
                        children.removeAll(c.getRemoved());
                        children.addAll(c.getAddedSubList());

                        SpriteView prev = this;
                        int number = 0;

                        for (SpriteView a : animals) {
                            a.following = prev;
                            a.number.set(++number);
                            prev.follower = a;
                            prev = a;
                        }
                    }
                }
            });
        }

        public ObservableList<SpriteView> getAnimals() {

            return animals;
        }


        @Override
        public void move(Direction direction) {

            if (walking != null && walking.getStatus().equals(Animation.Status.RUNNING))
                return;

            if (!inBounds(direction))
                return;

            Location myOldLoc = location.get();
            moveTo(location.getValue().offset(direction.getXOffset(), direction.getYOffset()));
            animals.stream()
                    .reduce(myOldLoc,
                            (loc, sprt) -> {
                                Location oldLoc = sprt.location.get();
                                sprt.moveTo(loc);

                                return oldLoc;
                            },
                            (loc1, loc2) -> loc1);
        }
    }

    public static class Ghoul extends SpriteView {

        static final Image GHOUL = loadImage("/images/ghoul.png");

        public Ghoul(SpriteView following) {

            super(GHOUL, following);
        }
    }
}
