package dk.easv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImageViewerWindowController implements Initializable {
    private final List<Image> images = new CopyOnWriteArrayList<>();
    private FileChooser fileChooser;
    private List<File> files;
    private int currentImageIndex = 0;
    private static boolean exitThread = false;

    @FXML
    private Label redNumber, greenNumber, blueNumber, mixedNumber;

    @FXML
    private Button loadImageButton, previousButton, nextButton, startPresentationButton, stopPresentationButton;

    @FXML
    private Label pictureName;

    @FXML
    Parent root;

    @FXML
    private ImageView imageView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previousButton.setDisable(true);
        nextButton.setDisable(true);
        startPresentationButton.setDisable(true);
        stopPresentationButton.setDisable(true);
    }

    @FXML
    private void handleBtnLoadAction() throws ExecutionException, InterruptedException {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Select image files");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images",
                "*.png", "*.jpg", "*.gif", "*.tif", "*.bmp"));
        files = fileChooser.showOpenMultipleDialog(new Stage());

        if (!files.isEmpty()) {
            files.forEach((File f) ->
                    images.add(new Image(f.toURI().toString())));
            displayImage();
        }

        loadImageButton.setDisable(true);
        previousButton.setDisable(false);
        nextButton.setDisable(false);
        startPresentationButton.setDisable(false);
        stopPresentationButton.setDisable(false);
    }

    @FXML
    private void handleBtnPreviousAction() throws ExecutionException, InterruptedException {
        if (!images.isEmpty()) {
            currentImageIndex =
                    (currentImageIndex - 1 + images.size()) % images.size();
            displayImage();
        }
    }

    @FXML
    private void handleBtnNextAction()  {
        if (!images.isEmpty()) {
            currentImageIndex = (currentImageIndex + 1) % images.size();
            try {
                displayImage();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void displayImage() throws ExecutionException, InterruptedException {
        if (!images.isEmpty()) {
            imageView.setImage(images.get(currentImageIndex));
            pictureName.setText(files.get(currentImageIndex).getName());

            countBlue(images.get(currentImageIndex));
            countRed(images.get(currentImageIndex));
            countGreen(images.get(currentImageIndex));
            countMixed(images.get(currentImageIndex));
        }
    }

    @FXML
    public void handleBtnStartPresentation() {
        Integer[] array = {1000, 2000, 3000, 4000, 5000};
        exitThread = false;

        Runnable runnable = () -> {
            while (!exitThread) {
                int randomNumber = array[ThreadLocalRandom.current().nextInt(array.length)];
                Platform.runLater(this::handleBtnNextAction);
                System.out.println( "from start presentation" + Thread.currentThread().getName());
                try {
                    Thread.sleep(randomNumber);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread threadSlideShow = new Thread(runnable);
        threadSlideShow.start();
    }

    @FXML
    public void stopButtonOnAction() {
        exitThread = true;
    }

    public void countBlue(Image image) throws ExecutionException, InterruptedException {
        CountPixels countPixelsBlue = new CountPixels();

        countPixelsBlue.setPixelColor("blue");
        countPixelsBlue.setImage(image);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
//        countPixelsBlue.messageProperty().addListener((obs, o, n) -> blueNumber.setText(n));
//        countPixels.valueProperty().addListener((obs, o, n) -> blueNumber.setText(String.valueOf(n)));
//        executorService.submit(countPixelsBlue);
        Future<Integer> bluePixels = executorService.submit(countPixelsBlue);
        blueNumber.setText(String.valueOf(bluePixels.get()));
        executorService.shutdown();
    }

    public void countRed(Image image) throws ExecutionException, InterruptedException {
        CountPixels countPixelsRed = new CountPixels();

        countPixelsRed.setPixelColor("red");
        countPixelsRed.setImage(image);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

//        countPixelsRed.messageProperty().addListener((obs, o, n) -> redNumber.setText(n));
//        countPixels.valueProperty().addListener((obs, o, n) -> redNumber.setText(String.valueOf(n)));
//        executorService.submit(countPixelsRed);
        Future<Integer> redPixels = executorService.submit(countPixelsRed);
        redNumber.setText(String.valueOf(redPixels.get()));
        executorService.shutdown();
    }

    public void countGreen(Image image) throws ExecutionException, InterruptedException {
        CountPixels countPixelsGreen = new CountPixels();

        countPixelsGreen.setImage(image);
        countPixelsGreen.setPixelColor("green");

        ExecutorService executorService = Executors.newSingleThreadExecutor();

//        countPixelsGreen.messageProperty().addListener((obs, o, n) -> greenNumber.setText(n));
//        countPixels.valueProperty().addListener((obs, o, n) -> greenNumber.setText(String.valueOf(n)));
         Future<Integer> greenPixels = executorService.submit(countPixelsGreen);
        greenNumber.setText(String.valueOf(greenPixels.get()));
         executorService.shutdown();
    }

    public void countMixed(Image image) throws ExecutionException, InterruptedException {
        CountPixels countPixelsMixed = new CountPixels();

        countPixelsMixed.setImage(image);
        countPixelsMixed.setPixelColor("mixed");

        ExecutorService executorService = Executors.newSingleThreadExecutor();

//        countPixelsGreen.messageProperty().addListener((obs, o, n) -> greenNumber.setText(n));
//        countPixels.valueProperty().addListener((obs, o, n) -> greenNumber.setText(String.valueOf(n)));
        Future<Integer> mixedPixels = executorService.submit(countPixelsMixed);
        mixedNumber.setText(String.valueOf(mixedPixels.get()));
        executorService.shutdown();
    }
}
