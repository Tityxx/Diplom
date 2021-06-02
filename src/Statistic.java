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
            showStatistic(inputStat);
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
        logs.Print();
    }

    public synchronized void showStatistic(LayerStatistic inputStat)
    {
        showExtrems(inputStat, true);
        showExtrems(inputStat, false);
    }

    private void showExtrems(LayerStatistic inputStat, boolean mode)
    {
        int sz = inputStat.getMids().length;
        logs.Print(String.format("Диапазон экстремумов: %6.4f-%6.4f", 50. / sz * settings.NoFirstPoints,
                50. / sz * (sz-settings.NoLastPoints)));
        ArrayList<Extreme> list = inputStat.createExtrems(mode, settings.NoFirstPoints, settings.NoLastPoints,true);
        if (list.size()==0){
            logs.PrintError("Экстремумов не найдено");
            return;
        }
        if (mode)
        {
            logs.Print(String.format("Основная частота=%6.4f гц", list.get(0).idx * settings.FreqStep));
        }
        int count = settings.NFirstMax < list.size() ? settings.NFirstMax : list.size();
        Extreme extreme = list.get(0);
        double val0 = mode ? extreme.value : extreme.diff;
        logs.Print(mode ? "По амплитуде" : "По спаду");
        logs.Print(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц",
                extreme.value, extreme.diff, extreme.idx * settings.FreqStep));
        double sum = 0;
        for(int i = 1; i < count; i++)
        {
            extreme = list.get(i);
            double proc = (mode ? extreme.value : extreme.diff) * 100 / val0;
            sum += proc;
            logs.Print(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц %d%%", extreme.value, extreme.diff,
                    extreme.idx * settings.FreqStep, (int)proc));
        }
        logs.Print(String.format("Средний - %d%% к первому", (int)(sum / (count - 1))));
        logs.Print();
    }
}
