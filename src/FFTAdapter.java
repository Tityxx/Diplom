import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.fft.FFTCallBack;
import romanow.snn_simulator.layer.LayerStatistic;

import static romanow.snn_simulator.desktop.FFTView.createFatalMessage;

public class FFTAdapter implements FFTCallBack
{
    private LayerStatistic inputStat;
    private Logs logs;
    private Settings settings;

    public FFTAdapter(Logs _logs, Settings _settings, String title)
    {
        inputStat = new LayerStatistic(title);
        logs = _logs;
        settings = _settings;
    }

    @Override
    public void onStart(float msOnStep) {}

    @Override
    public void onFinish()
    {
        if (inputStat.getCount()==0)
        {
            logs.PrintError("Настройки: короткий период измерений/много блоков");
            return;
        }
        inputStat.smooth(settings.Smooth);
        Statistic statistic = new Statistic(settings, logs, "Статистка");
        statistic.showStatisticFull(inputStat);
        statistic.addGraphView(inputStat);
    }

    @Override
    public boolean onStep(int nBlock, int calcMS, float totalMS, FFT fft)
    {
        long tt = System.currentTimeMillis();
        float lineSpectrum[] = fft.getSpectrum();
        boolean xx;
        try
        {
            inputStat.addStatistic(lineSpectrum);
        } catch (Exception ex) {
            logs.PrintError(createFatalMessage(ex,10));
            return false;
        }
        return true;
    }
    @Override
    public void onError(Exception ee)
    {
        logs.PrintError(createFatalMessage(ee,10));
    }
    @Override
    public void onMessage(String mes)
    {
        logs.Print(mes);
    }

}
