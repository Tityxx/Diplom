import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Вывод и очистка логов
 */
public class Logs extends Pane
{
    private Pane logsPanel;
    private Pane graphsPanel;

    public Logs(Pane _logsPanel, Pane _graphsPanel)
    {
        logsPanel = _logsPanel;
        graphsPanel = _graphsPanel;
        logsPanel.getChildren().addAll(new Label("Логи:"));
        graphsPanel.getChildren().addAll(new Label("Графики:"));
    }

    /**
     * Добавление нового графа
     * @param log
     */
    public void Print(LineChart log)
    {
        graphsPanel.getChildren().addAll(log);
    }

    /**
     * Добавление нового лога
     * @param log
     */
    public void Print(String log)
    {
        logsPanel.getChildren().addAll(new Label(log));
    }

    /**
     * Печать пустого лога
     */
    public void Print()
    {
        logsPanel.getChildren().addAll(new Label(""));
    }

    /**
     * Добавление нового лога ошибки
     * @param log
     */
    public void PrintError(String log)
    {
        Label label = new Label(log);
        label.setTextFill(Color.RED);
        logsPanel.getChildren().addAll(label);
    }

    /**
     * Очистка панели логов
     */
    public void ClearLogs()
    {
        logsPanel.getChildren().clear();
        logsPanel.getChildren().addAll(new Label("Логи:"));
    }

    /**
     * Очистка панели графиков
     */
    public void ClearGraphs()
    {
        graphsPanel.getChildren().clear();
        graphsPanel.getChildren().addAll(new Label("Графики:"));
    }
}
