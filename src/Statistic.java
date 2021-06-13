import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import romanow.snn_simulator.layer.Extreme;
import romanow.snn_simulator.layer.LayerStatistic;

import java.util.ArrayList;

public class Statistic
{
    private Settings settings;
    private Logs logs;
    private boolean isConvertToWave = false;

    public Statistic(Settings _settings, Logs _logs, boolean _isConvertToWave)
    {
        settings = _settings;
        logs = _logs;
        isConvertToWave = _isConvertToWave;
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
            if (!isConvertToWave) logs.PrintError("Экстремумов не найдено");
            return;
        }
        if (!isConvertToWave) logs.Print(String.format("Основная частота=%6.4f гц", list.get(0).idx * settings.FreqStep));
        if (!isConvertToWave) logs.Print();
    }

    private synchronized void showStatistic(LayerStatistic inputStat)
    {
        showExtrems(inputStat, true);
        showExtrems(inputStat, false);
    }

    private void showExtrems(LayerStatistic inputStat, boolean mode)
    {
        int sz = inputStat.getMids().length;

        if (!isConvertToWave) logs.Print(String.format("Диапазон экстремумов: %6.4f-%6.4f", 50. / sz * settings.NoFirstPoints,
                50. / sz * (sz-settings.NoLastPoints)));
        ArrayList<Extreme> list = inputStat.createExtrems(mode, settings.NoFirstPoints, settings.NoLastPoints,true);
        if (list.size()==0){
            logs.PrintError("Экстремумов не найдено");
            return;
        }
        if (mode)
        {
            if (!isConvertToWave) logs.Print(String.format("Основная частота=%6.4f гц", list.get(0).idx * settings.FreqStep));
        }
        int count = settings.NFirstMax < list.size() ? settings.NFirstMax : list.size();
        Extreme extreme = list.get(0);
        double val0 = mode ? extreme.value : extreme.diff;
        if (!isConvertToWave) logs.Print(mode ? "По амплитуде" : "По спаду");
        if (!isConvertToWave) logs.Print(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц",
                extreme.value, extreme.diff, extreme.idx * settings.FreqStep));
        double sum = 0;
        for(int i = 1; i < count; i++)
        {
            extreme = list.get(i);
            double proc = (mode ? extreme.value : extreme.diff) * 100 / val0;
            sum += proc;
            if (!isConvertToWave) logs.Print(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц %d%%", extreme.value, extreme.diff,
                    extreme.idx * settings.FreqStep, (int)proc));
        }
        if (!isConvertToWave) logs.Print(String.format("Средний - %d%% к первому", (int)(sum / (count - 1))));
        if (!isConvertToWave) logs.Print();
    }

    public synchronized void addGraphView(LayerStatistic inputStat)
    {
        paintOne(inputStat.getMids(),0,0,true);
    }

    public void paintOne(float data[], int noFirst, int noLast, boolean freqMode)
    {
        XYChart.Series<Double, Float> series = new XYChart.Series<>();

        for(int j=noFirst;j<data.length-noLast;j++)                 // Подпись значений факторов j-ой ячейки
        {
            double freq = freqMode ? (j*50./data.length) : (j/100.);
            series.getData().add(new XYChart.Data<>(freq, data[j]));
        }
        LineChart lineChart = new LineChart(new NumberAxis(), new NumberAxis());
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.getData().add(series);
        logs.Print(lineChart);
    }
}
