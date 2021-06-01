import romanow.snn_simulator.layer.Extreme;
import romanow.snn_simulator.layer.LayerStatistic;

import java.util.ArrayList;

public class Statistic
{
    private Settings settings;
    private Logs logs;

    public Statistic(Settings _settings, Logs _logs)
    {
        settings = _settings;
        logs = _logs;
    }

    public void showStatisticFull(LayerStatistic inputStat)
    {
        if (settings.FullInfo)
        {
            //showStatistic(inputStat);
        }
        else
        {
            showShort(inputStat);
        }
    }

    private synchronized void showShort(LayerStatistic inputStat)
    {
        ArrayList<Extreme> list = inputStat.createExtrems(
                true, settings.NoFirstPoints, settings.NoLastPoints,true);
        if (list.size() == 0)
        {
            logs.PrintError("Экстремумов не найдено");
            return;
        }
        logs.Print(String.format("Основная частота=%6.4f гц", list.get(0).idx * settings.FreqStep));
    }
}
