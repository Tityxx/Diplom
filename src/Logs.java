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
    }
}
