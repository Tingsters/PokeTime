package sample;

import com.pi4j.component.gyroscope.analogdevices.ADXL345;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.beans.property.BooleanProperty;

import javafx.util.Duration;

import joachimeichborn.sensors.driver.Tsl2561;

import java.io.IOException;


/**
 * Created by Cassandra on 9/9/2016.
 */
public class SensorFactory {

    private static SensorFactory factory;
    private GpioController gpio;
    private I2CBus bus;
    private float lastGyroX;

    public SensorFactory() throws IOException, I2CFactory.UnsupportedBusNumberException {

        if (PiSystem.isPiUnix) {
            gpio = GpioFactory.getInstance();
            bus = I2CFactory.getInstance(I2CBus.BUS_1);
        }
    }

    public static SensorFactory create() throws IOException, I2CFactory.UnsupportedBusNumberException {

        if (factory == null) {
            factory = new SensorFactory();
        }

        return factory;
    }


    public void createButton(SpriteView.PokeTrainer pokeTrainer) {

        if (PiSystem.isPiUnix) {
            final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07,
                    PinPullResistance.PULL_UP);
            myButton.addListener(new GpioPinListenerDigital() {

                    @Override
                    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {

                        boolean buttonPressed = event.getState().isLow();

                        if (buttonPressed)
                            Main.display("Button pressed.");
                            // Attack Pokemon!
                    }
                });
        }
    }


    public void createLightSensor(BooleanProperty night) throws IOException {

        if (PiSystem.isPiUnix) {
            I2CDevice device = bus.getDevice(0x39);

            try {
                Tsl2561 lightSensor = new Tsl2561(device);
                Timeline lightTimeline = new Timeline(new KeyFrame(Duration.seconds(10),
                            actionEvent -> {
                                try {
                                    double lux = lightSensor.getLux();
                                    Main.display("lux = " + lux);
                                    // Make it night!
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }));
                lightTimeline.setCycleCount(Timeline.INDEFINITE);
                lightTimeline.play();
            } catch (IOException e) {
                System.out.println("The light sensor should be connected properly... " + e.getMessage());
            }
        }
    }


    public void createAccelerometer() {

        if (PiSystem.isPiUnix) {
            try {
                ADXL345 gyro = new ADXL345(bus);
                gyro.init(gyro.X, 4);
                lastGyroX = gyro.X.getRawValue();

                Timeline gyroscopeTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
                            actionEvent -> {
                                try {
                                    float x = gyro.X.getRawValue();

                                    if (!Main.earthquake.getValue()) {
                                        if (Math.abs(x - lastGyroX) > 2000) {
                                            Main.display("Earthquake!");
                                            // Make an earthquake!
                                        }
                                    }

                                    lastGyroX = x;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }));
                gyroscopeTimeline.setCycleCount(Timeline.INDEFINITE);
                gyroscopeTimeline.play();
            } catch (IOException e) {
                System.out.println("The gyroscope should be connected properly... " + e.getMessage());
            }
        }
    }
}
// Attack Pokemon!
//                Main.attack(3);

// Make it night!
//                    if (lux < 3) {
//                        night.setValue(true);
//                    } else {
//                        night.setValue(false);
//                    }

// Make an earthquake!
//                            Main.earthquake();
