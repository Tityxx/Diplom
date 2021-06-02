import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import romanow.snn_simulator.fft.FFT;

/**
 * Класс настроек
 */
public class Settings
{
    public double FirstFreq = 0.4;               // Нижняя граница частоты при поиске максимусов
    public double LastFreq = 30;                 // Верхняя граница частоты при поиске максимусов
    public int  P_BlockSize = 1;                 // Количество блоков по 1024 отсчета
    public int  P_OverProc = 50;                 // Процент перекрытия окна
    public int  Smooth = 30;                     // Циклов сглаживания
    public int  MeasureDuration = 10;            // Время снятия вибрации в сек (1...300)
    public int  NTrendPoints = 50;               // Точек при сглаживании тренда =0 - отключено
    public int  WinFun = FFT.WinModeRectangle;   // Вид функции окна
    public String MeasureGroup = "СМ-300";       // Подпись группы
    public String MeasureTitle = "Опора 125";    // Подпись опоры
    public int MeasureCounter = 1;               // Счетчик измерения
    public boolean FullInfo=false;               // Данные отладки

    public double FreqStep = 0;
    public final int KF100 = FFT.sizeHZ / 100;

    public int NFirstMax=10;                    // Количество максимумов в статистике (вывод)
    public int NoFirstPoints = 20;              // Отрезать точек справа и слева
    public int NoLastPoints = 20;

    private int WIDTH;
    private int HEIGHT;

    private Stage settingsWindow;
    private VBox root;

    private Button minFreqBtn = new Button("Частота мин.");
    private Button maxFreqBtn = new Button("Частота макс.");
    private Button blocksCountBtn = new Button("Блоков * 1024");
    private Button percentBtn = new Button("% перекрытия");
    private Button smoothingBtn = new Button("Сглаживание");
    private Button measurementBtn = new Button("Измерение (сек)");
    private Button nTrendPointsBtn = new Button("ФВЧ (точек)");
    private Button windowFFTBtn = new Button("Окно БПФ");
    private Button groupBtn = new Button("Группа");
    private Button propBtn = new Button("Опора");
    private Button measureCounterBtn = new Button("№ замера");
    private Button fullInfoBtn = new Button("Данные отладки");

    private TextField minFreqTF = new TextField();
    private TextField maxFreqTF = new TextField();
    private TextField blocksCountTF = new TextField();
    private TextField percentTF = new TextField();
    private TextField smoothingTF = new TextField();
    private TextField measurementTF = new TextField();
    private TextField nTrendPointsTF = new TextField();
    private TextField windowFFTTF = new TextField();
    private TextField groupTF = new TextField();
    private TextField propTF = new TextField();
    private TextField measureCounterTF = new TextField();
    private TextField fullInfoTF = new TextField();

    private Stage fftWindow;
    private VBox fftRoot;

    private Button rectangleBtn = new Button("Прямоугольник");
    private Button triangleBtn = new Button("Треугольник");
    private Button sineBtn = new Button("Синус");
    private Button welchBtn = new Button("Парабола");

    Logs logs;

    public Settings(Logs _logs)
    {
        WIDTH = Main.WIDTH / 4;
        HEIGHT = Main.HEIGHT / 2;

        logs = _logs;

        InitTextFields();
    }

    /**
     * Создание окна настроек
     */
    public void CreateWindow()
    {
        settingsWindow = new Stage();
        settingsWindow.setResizable(false);
        root = new VBox(
                new HBox(minFreqBtn, minFreqTF),
                new HBox(maxFreqBtn, maxFreqTF),
                new HBox(blocksCountBtn, blocksCountTF),
                new HBox(percentBtn, percentTF),
                new HBox(smoothingBtn, smoothingTF),
                new HBox(measurementBtn, measurementTF),
                new HBox(nTrendPointsBtn, nTrendPointsTF),
                new HBox(windowFFTBtn, windowFFTTF),
                new HBox(groupBtn, groupTF),
                new HBox(propBtn, propTF),
                new HBox(measureCounterBtn, measureCounterTF),
                new HBox(fullInfoBtn, fullInfoTF)
        );
        InitSize(root);
        InitControls();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        settingsWindow.setTitle("Настройки");
        settingsWindow.setScene(scene);
        settingsWindow.show();
    }

    private void CreateFFTWindow()
    {
        fftWindow = new Stage();
        fftWindow.setResizable(false);
        fftRoot = new VBox(rectangleBtn, triangleBtn, sineBtn, welchBtn);
        InitSizeFFTBtns(fftRoot);
        Scene scene = new Scene(fftRoot, WIDTH, HEIGHT / 3);
        fftWindow.setTitle("Функ. окна");
        fftWindow.setScene(scene);
        fftWindow.show();
    }

    private void InitTextFields()
    {
        windowFFTTF.setEditable(false);
        fullInfoTF.setEditable(false);
        SetText(String.valueOf(FirstFreq), minFreqTF);
        SetText(String.valueOf(LastFreq), maxFreqTF);
        SetText(String.valueOf(P_BlockSize), blocksCountTF);
        SetText(String.valueOf(P_OverProc), percentTF);
        SetText(String.valueOf(Smooth), smoothingTF);
        SetText(String.valueOf(MeasureDuration), measurementTF);
        SetText(String.valueOf(NTrendPoints), nTrendPointsTF);
        SetTextWithWinFFT(WinFun, windowFFTTF);
        SetText(String.valueOf(MeasureGroup), groupTF);
        SetText(String.valueOf(MeasureTitle), propTF);
        SetText(String.valueOf(MeasureCounter), measureCounterTF);
        SetText(String.valueOf(FullInfo), fullInfoTF);
    }

    private void SetText(String str, TextField tf)
    {
        tf.setText(str);
    }

    private void SetTextWithWinFFT(int winType, TextField tf)
    {
        switch (winType)
        {
            case 1:
                SetText("Треугольник", tf);
                break;
            case 2:
                SetText("Синус", tf);
                break;
            case 3:
                SetText("Парабола", tf);
                break;
            default:
                SetText("Прямоугольник", tf);
                break;
        }
    }


    private void SetSize(Button btn, int size)
    {
        btn.setPrefWidth(size);
    }

    private void SetSize(TextField tf, int size)
    {
        tf.setPrefWidth(size);
    }

    private void InitSize(Pane root)
    {
        ObservableList<Node> children = root.getChildren();

        for (int i = 0; i < children.size(); i++)
        {
            ObservableList<Node> child = ((HBox)children.get(i)).getChildren();
            SetSize((Button) child.get(0), WIDTH / 2);
            SetSize((TextField) child.get(1), WIDTH / 2);
        }
    }

    private void InitSizeFFTBtns(Pane root)
    {
        ObservableList<Node> children = root.getChildren();
        for (int i = 0; i < children.size(); i++)
        {
            SetSize((Button) children.get(i), WIDTH);
        }
    }

    private int GetInt(TextField tf)
    {
        System.out.println(Integer.valueOf(tf.getText()));
        return Integer.valueOf(tf.getText());
    }

    private int GetIntWithWinFFT(TextField tf)
    {
        switch (tf.getText())
        {
            case "Треугольник":
                return 1;
            case "Синус":
                return 2;
            case "Парабола":
                return 3;
            default:
                return 0;
        }
    }

    private double GetDouble(TextField tf)
    {
        System.out.println(Double.valueOf(tf.getText()));
        return Double.valueOf(tf.getText());
    }

    private String GetString(TextField tf)
    {
        System.out.println(tf.getText());
        return tf.getText();
    }

    private void InitControls()
    {
        minFreqBtn.setOnAction(e ->
        {
            try
            {
                FirstFreq = GetDouble(minFreqTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        maxFreqBtn.setOnAction(e ->
        {
            try
            {
                LastFreq = GetDouble(maxFreqTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        blocksCountBtn.setOnAction(e ->
        {
            try
            {
                P_BlockSize = GetInt(blocksCountTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        percentBtn.setOnAction(e ->
        {
            try
            {
                P_OverProc = GetInt(percentTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        smoothingBtn.setOnAction(e ->
        {
            try
            {
                Smooth = GetInt(smoothingTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        measurementBtn.setOnAction(e ->
        {
            try
            {
                MeasureDuration = GetInt(measurementTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        nTrendPointsBtn.setOnAction(e ->
        {
            try
            {
                NTrendPoints = GetInt(nTrendPointsTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        groupBtn.setOnAction(e ->
        {
            try
            {
                MeasureGroup = GetString(groupTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        propBtn.setOnAction(e ->
        {
            try
            {
                MeasureTitle = GetString(propTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        measureCounterBtn.setOnAction(e ->
        {
            try
            {
                MeasureCounter = GetInt(measureCounterTF);
            }
            catch (Exception ex)
            {
                logs.PrintError("Ошибка при парсинге значений настроек.\n" + ex.getLocalizedMessage());
            }
        });
        fullInfoBtn.setOnAction(e ->
        {
            FullInfo = !FullInfo;
            SetText(String.valueOf(FullInfo), fullInfoTF);
        });
        windowFFTBtn.setOnAction(e -> CreateFFTWindow());
        rectangleBtn.setOnAction(e -> ChooseFFT(0));
        triangleBtn.setOnAction(e -> ChooseFFT(1));
        sineBtn.setOnAction(e -> ChooseFFT(2));
        welchBtn.setOnAction(e -> ChooseFFT(3));
    }

    private void ChooseFFT(int id)
    {
        WinFun = id;
        SetTextWithWinFFT(WinFun, windowFFTTF);
        fftWindow.close();
    }
}
