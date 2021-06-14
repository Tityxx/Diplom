import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import romanow.snn_simulator.layer.Extreme;
import romanow.snn_simulator.layer.LayerStatistic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Statistic
{
    private Settings settings;
    private Logs logs;
    private Main.FuncType type;

    public Statistic(Settings _settings, Logs _logs, Main.FuncType _type)
    {
        settings = _settings;
        logs = _logs;
        type = _type;
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
            if (type == Main.FuncType.ShowInfo) logs.PrintError("Экстремумов не найдено");
            return;
        }
        if (type == Main.FuncType.ShowInfo) logs.Print(String.format("Основная частота=%6.4f гц", list.get(0).idx * settings.FreqStep));
        if (type == Main.FuncType.ShowInfo) logs.Print();
    }

    private synchronized void showStatistic(LayerStatistic inputStat)
    {
        showExtrems(inputStat, true);
        showExtrems(inputStat, false);
    }

    private void showExtrems(LayerStatistic inputStat, boolean mode)
    {
        int sz = inputStat.getMids().length;

        if (type == Main.FuncType.ShowInfo) logs.Print(String.format("Диапазон экстремумов: %6.4f-%6.4f", 50. / sz * settings.NoFirstPoints,
                50. / sz * (sz-settings.NoLastPoints)));
        ArrayList<Extreme> list = inputStat.createExtrems(mode, settings.NoFirstPoints, settings.NoLastPoints,true);
        if (list.size()==0){
            logs.PrintError("Экстремумов не найдено");
            return;
        }
        if (mode)
        {
            if (type == Main.FuncType.ShowInfo) logs.Print(String.format("Основная частота=%6.4f гц", list.get(0).idx * settings.FreqStep));
        }
        int count = settings.NFirstMax < list.size() ? settings.NFirstMax : list.size();
        Extreme extreme = list.get(0);
        double val0 = mode ? extreme.value : extreme.diff;
        if (type == Main.FuncType.ShowInfo) logs.Print(mode ? "По амплитуде" : "По спаду");
        if (type == Main.FuncType.ShowInfo) logs.Print(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц",
                extreme.value, extreme.diff, extreme.idx * settings.FreqStep));
        double sum = 0;
        for(int i = 1; i < count; i++)
        {
            extreme = list.get(i);
            double proc = (mode ? extreme.value : extreme.diff) * 100 / val0;
            sum += proc;
            if (type == Main.FuncType.ShowInfo) logs.Print(String.format("Ампл=%6.4f Пик=%6.4f f=%6.4f гц %d%%", extreme.value, extreme.diff,
                    extreme.idx * settings.FreqStep, (int)proc));
        }
        if (type == Main.FuncType.ShowInfo) logs.Print(String.format("Средний - %d%% к первому", (int)(sum / (count - 1))));
        if (type == Main.FuncType.ShowInfo) logs.Print();
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
        if (type == Main.FuncType.ShowInfo) logs.Print(lineChart);
    }

    public void SaveToJson(String path, float data[])
    {
        int k = path.lastIndexOf(".");
        String newPath = path.substring(0, k)+".json";

        Data json = new Data();

        List<Float> list = new ArrayList<>();

        for(float i : data)
        {
            list.add(i);
        }
        json.Data = list;
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            mapper.writeValue(new File(newPath), json );
        }
        catch (IOException e)
        {
            logs.PrintError(e.getMessage());
            return;
        }
        logs.Print("Сохранено в: " + newPath);
    }
}
