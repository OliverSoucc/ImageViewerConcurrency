package dk.easv;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import java.util.concurrent.Callable;

public class CountPixels implements Callable<Integer> {
    private Image image;
    private String pixelColor;

    @Override
    public Integer call() throws Exception {
        int greenCount = 0;
        int blueCount = 0;
        int redCount = 0;
        int mixedColor = 0;

        PixelReader pixelReader = image.getPixelReader();
        Color color;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                color = pixelReader.getColor(x, y);

                double blue = color.getBlue();
                double red = color.getRed();
                double green = color.getGreen();


                if(red > blue && red > green){
                    redCount++;
                }
                else if (green > blue && green > red) {
                    greenCount++;
                }
                else if (blue > green && blue > red) {
                    blueCount++;
                }
                else {
                    mixedColor++;
                }


//                if (pixelColor.equals("red")) {
//                    this.updateMessage(String.valueOf(redCount));
////                    this.updateValue(redCount);
//                }
//                if (pixelColor.equals("blue")) {
//                    this.updateMessage(String.valueOf(blueCount));
////                    this.updateValue(blueCount);
//                }
//                if (pixelColor.equals("green")) {
//                    this.updateMessage(String.valueOf(greenCount));
////                    this.updateValue(greenCount);
//                }
            }
        }
        System.out.println( "from count pixels" + Thread.currentThread().getName());
        switch (pixelColor) {
            case "red" -> {
                return redCount;
            }
            case "blue" -> {
                return blueCount;
            }
            case "green" -> {
                return greenCount;
            }
            case "mixed" ->{
                return mixedColor;
            }
        }
        return 0;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setPixelColor(String pixelColor) {
        this.pixelColor = pixelColor;
    }
}

