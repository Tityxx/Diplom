import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class Main extends Application
{
    public static int HEIGHT;
    public static int WIDTH;

    private final float WINDOW_COEFF = 1.2f;

    private Stage mainWindow;
    private Scene scene;
    private Pane root = new Pane();

    private ScrollPane leftScrollPane = new ScrollPane();
    private VBox leftVbox = new VBox();

    private ScrollPane rightScrollPane = new ScrollPane();
    private VBox rightVbox = new VBox();

    private MenuBar menuBar = new MenuBar();

    private Menu menuMenu = new Menu("Меню");

    private MenuItem openArchiveMI = new MenuItem("Архив");
    private MenuItem clearLogsMI = new MenuItem("Очистить ленту логов");
    private MenuItem clearGraphsMI = new MenuItem("Очистить ленту графиков");
    private MenuItem settingsMI = new MenuItem("Настройки");
    private MenuItem createWaveImageMI = new MenuItem("Просмотр волны");
    private MenuItem convertToWaveMI = new MenuItem("Конвентировать в wave");
    private MenuItem shortFileMI = new MenuItem("Файл кратко");
    private MenuItem longFileMI = new MenuItem("Файл подробно");
    private MenuItem loadFileMI = new MenuItem("Загрузить файл");

    private Settings settings;
    private Logs logs = new Logs(leftVbox, rightVbox);

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        mainWindow = primaryStage;
        Rectangle2D rec = Screen.getPrimary().getBounds();
        HEIGHT = (int)(rec.getHeight() / WINDOW_COEFF);
        WIDTH = (int)(rec.getWidth() / WINDOW_COEFF);
        settings = new Settings(logs);

        InitPanels();
        InitView();
        InitControls();
    }

    private void InitPanels()
    {
        mainWindow.setTitle("Диплом");
        mainWindow.setResizable(false);

        root.getChildren().addAll(leftScrollPane, rightScrollPane, menuBar);
        leftScrollPane.setContent(leftVbox);

        rightScrollPane.setContent(rightVbox);

        menuBar.getMenus().addAll(menuMenu);
        menuMenu.getItems().addAll(openArchiveMI, clearLogsMI, clearGraphsMI, createWaveImageMI,
                convertToWaveMI, shortFileMI, longFileMI, settingsMI);

        scene = new Scene(root, WIDTH, HEIGHT);
        mainWindow.setScene(scene);
        mainWindow.show();
    }

    private void InitView()
    {
        menuBar.setPrefWidth(WIDTH);

        int offsetY = (int)menuBar.getHeight();

        leftScrollPane.setPrefWidth(WIDTH / 6);
        leftScrollPane.setPrefHeight(HEIGHT - offsetY);
        leftScrollPane.setPadding(new Insets(0, 0, 0, 10));
        leftScrollPane.setTranslateY(offsetY);

        rightScrollPane.setPrefWidth(WIDTH - (WIDTH / 6));
        rightScrollPane.setPrefHeight(HEIGHT - offsetY);
        rightScrollPane.setPadding(new Insets(0, 10, 0, 20));
        rightScrollPane.setTranslateX(WIDTH / 6);
        rightScrollPane.setTranslateY(offsetY);
    }

    private void InitControls()
    {
        settingsMI.setOnAction(e -> settings.CreateWindow());
        loadFileMI.setOnAction(e -> OnClickLoadFile());
        clearLogsMI.setOnAction(e -> OnClickClearLogs());
        clearGraphsMI.setOnAction(e -> OnClickClearGraphs());
        shortFileMI.setOnAction(e ->
        {
            try
            {
                OnClickFileShort();
            }
            catch (Throwable fileNotFoundException)
            {
                fileNotFoundException.printStackTrace();
            }
        });
    }

    private void OnClickLoadFile()
    {
        System.out.println("Work");
    }

    private void OnClickClearLogs()
    {
        logs.ClearLogs();
    }

    private void OnClickClearGraphs()
    {
        logs.ClearGraphs();
    }

    private void OnClickFileShort() throws Throwable
    {
        settings.FullInfo = false;
        (new FileController(mainWindow, logs, settings)).OpenFile();
    }
}
