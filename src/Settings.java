import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import romanow.snn_simulator.fft.FFT;

/**
 * Класс настроек
 */
public class Settings
{
    private double FirstFreq = 0.4;               // Нижняя граница частоты при поиске максимусов
    private double LastFreq = 30;                 // Верхняя граница частоты при поиске максимусов
    private int  nTrendPoints = 50;               // Точек при сглаживании тренда =0 - отключено
    private int  p_BlockSize = 1;                 // Количество блоков по 1024 отсчета
    private int  p_OverProc = 50;                 // Процент перекрытия окна
    private int  kSmooth = 30;                    // Циклов сглаживания
    private int  winFun = FFT.WinModeRectangle;   // Вид функции окна
    private int  measureDuration = 10;            // Время снятия вибрации в сек (1...300)
    private String measureGroup = "СМ-300";       // Подпись группы
    private String measureTitle = "Опора 125";    // Подпись опоры
    private int measureCounter = 1;               // Счетчик измерения

    private Stage settingsWindow;


    public Settings()
    {

    }

    /**
     * Создание окна настроек
     */
    public void CreateWindow()
    {
        settingsWindow = new Stage();
        Group root = new Group();
        Scene scene = new Scene(root, Main.WIDTH/2, Main.HEIGHT/2);
        settingsWindow.setTitle("Настройки");
        settingsWindow.setScene(scene);
        settingsWindow.show();
    }
}
