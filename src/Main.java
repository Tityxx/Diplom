import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application
{
    public enum FuncType
    {
        ShowInfo,
        ConvertToJson
    }


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

    private MenuItem clearLogsMI = new MenuItem("Очистить ленту логов");
    private MenuItem clearGraphsMI = new MenuItem("Очистить ленту графиков");
    private MenuItem clearAllMI = new MenuItem("Очистить обе ленты");
    private MenuItem settingsMI = new MenuItem("Настройки");
    private MenuItem createWaveImageMI = new MenuItem("Просмотр волны");
    private MenuItem convertToWaveMI = new MenuItem("Конвентировать в wave");
    private MenuItem convertToJsonMI = new MenuItem("Конвентировать в json");
    private MenuItem shortFileMI = new MenuItem("Файл кратко");
    private MenuItem longFileMI = new MenuItem("Файл подробно");

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
        menuMenu.getItems().addAll(clearLogsMI, clearGraphsMI, clearAllMI,
                convertToJsonMI, convertToWaveMI, createWaveImageMI, shortFileMI, longFileMI, settingsMI);

        scene = new Scene(root, WIDTH, HEIGHT);
        mainWindow.setScene(scene);
        mainWindow.show();
    }

    private void InitView()
    {
        menuBar.setPrefWidth(WIDTH);

        int offsetY = (int)menuBar.getHeight();

        leftScrollPane.setPrefWidth(WIDTH / 4);
        leftScrollPane.setPrefHeight(HEIGHT - offsetY);
        leftScrollPane.setPadding(new Insets(0, 0, 0, 10));
        leftScrollPane.setTranslateY(offsetY);

        rightScrollPane.setPrefWidth(WIDTH - (WIDTH / 4));
        rightScrollPane.setPrefHeight(HEIGHT - offsetY);
        rightScrollPane.setPadding(new Insets(0, 10, 0, 20));
        rightScrollPane.setTranslateX(WIDTH / 4);
        rightScrollPane.setTranslateY(offsetY);
    }

    private void InitControls()
    {
        settingsMI.setOnAction(e -> settings.CreateWindow());
        clearLogsMI.setOnAction(e -> OnClickClearLogs());
        clearGraphsMI.setOnAction(e -> OnClickClearGraphs());
        clearAllMI.setOnAction(e ->
        {
            OnClickClearLogs();
            OnClickClearGraphs();
        });
        convertToWaveMI.setOnAction(e -> OnClickConvertToWaveFile());
        shortFileMI.setOnAction(e ->
        {
            try
            {
                OnClickFile(false, FuncType.ShowInfo);
            }
            catch (Throwable fileNotFoundException)
            {
                fileNotFoundException.printStackTrace();
            }
        });
        longFileMI.setOnAction(e ->
        {
            try
            {
                OnClickFile(true, FuncType.ShowInfo);
            }
            catch (Throwable fileNotFoundException)
            {
                fileNotFoundException.printStackTrace();
            }
        });
        createWaveImageMI.setOnAction(e -> {
            try
            {
                (new FileController(mainWindow, logs, settings, FuncType.ShowInfo)).CreateWave();
            } catch (Throwable fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        });
        convertToJsonMI.setOnAction(e ->
        {
            try
            {
                OnClickFile(false, FuncType.ConvertToJson);
            }
            catch (Throwable fileNotFoundException)
            {
                fileNotFoundException.printStackTrace();
            }
        });
    }

    private void OnClickClearLogs()
    {
        logs.ClearLogs();
    }

    private void OnClickClearGraphs()
    {
        logs.ClearGraphs();
    }

    private void OnClickFile(boolean fullInfo, FuncType type) throws Throwable
    {
        settings.FullInfo = fullInfo;
        (new FileController(mainWindow, logs, settings, type)).OpenFile();
    }

    private void OnClickConvertToWaveFile()
    {
        (new FileController(mainWindow, logs, settings, FuncType.ConvertToJson)).ConvertToWaveFile();
    }


}
