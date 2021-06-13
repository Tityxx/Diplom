import javafx.stage.FileChooser;
import javafx.stage.Stage;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.fft.FFTAudioTextFile;
import romanow.snn_simulator.fft.FFTParams;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileController
{
    private String dir = "C:/";

    private Stage stage;
    private Logs logs;
    private Settings settings;

    private File currFile;
    private boolean isConvertToWave = false;

    public FileController(Stage _stage, Logs _logs, Settings _settings, boolean _isConvertToWave)
    {
        Path currentRelativePath = Paths.get("");
        dir = currentRelativePath.toAbsolutePath().toString();

        stage = _stage;
        logs = _logs;
        settings = _settings;
        isConvertToWave = _isConvertToWave;
    }

    /**
     * Открытие файла
     * @throws FileNotFoundException
     */
    public void OpenFile() throws Throwable
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        currFile = fileChooser.showOpenDialog(stage);
        Pair<InputStream, FileDescription> res = openSelected(currFile);
        InputStream is = res.o1;
        if (is==null)
        {
            return;
        }
        processInputStream(is,res.o2.toString());
    }

    /**
     * Открытие выбранного файла
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public Pair<InputStream, FileDescription> openSelected(File file) throws FileNotFoundException
    {
        FileDescription description = new FileDescription(file.getName());
        String out = description.parseFromName();
        if (out!=null){
            logs.Print("Имя файла: "+out);
            return new Pair(null,null);
        }
        logs.Print(description.toString());
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        return new Pair(is,description);
    }

    /**
     * Парсинг файла, удаление тренда и вывод информации
     * @param is
     * @param title
     * @throws Throwable
     */
    public void processInputStream(InputStream is, String title) throws Throwable
    {
        FFTAudioTextFile xx = new FFTAudioTextFile();
        xx.setnPoints(settings.NTrendPoints);
        xx.readData(new BufferedReader(new InputStreamReader(is, "Windows-1251")));
        xx.removeTrend(settings.NTrendPoints);
        long lnt = xx.getFrameLength();
        FFTParams params = new FFTParams().W(settings.P_BlockSize * FFT.Size0).procOver(settings.P_OverProc).
                FFTWindowReduce(false).p_Cohleogram(false).p_GPU(false).compressMode(false).
                winMode(settings.WinFun);
        FFT fft = new FFT();
        fft.setFFTParams(params);
        fft.calcFFTParams();
        settings.FreqStep = fft.getStepHZLinear()/settings.KF100;
        if (!isConvertToWave) logs.Print("Отсчетов "+xx.getFrameLength());
        if (!isConvertToWave) logs.Print("Кадр: "+ settings.P_BlockSize*FFT.Size0);
        if (!isConvertToWave) logs.Print("Перекрытие: "+ settings.P_BlockSize);
        if (!isConvertToWave) logs.Print("Дискретность: "+String.format("%5.4f", settings.FreqStep)+" гц");
        fft.fftDirect(xx,new FFTAdapter(logs, settings,title, false));
    }

    public void CreateWave() throws Throwable
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        currFile = fileChooser.showOpenDialog(stage);
        Pair<InputStream, FileDescription> res = openSelected(currFile);
        InputStream is = res.o1;
        if (is==null)
        {
            return;
        }
        ProcessWaveInputStream(is,res.o2.toString());
    }

    public void ProcessWaveInputStream(InputStream is, String title) throws Throwable
    {
        logs.Print(title);
        FFTAudioTextFile xx = new FFTAudioTextFile();
        xx.readData(new BufferedReader(new InputStreamReader(is, "Windows-1251")));
        procWaveForm(xx);
    }

    private void procWaveForm(FFTAudioTextFile currentWave)
    {
        int firstPoint = (int)(0*100);
        int size = currentWave.getData().length;
        int count = size/1;
        int lastPoint = size - firstPoint - count;
        if (lastPoint<0) lastPoint=0;
        Statistic statistic = new Statistic(settings, logs, false);
        statistic.paintOne(currentWave.getData(),firstPoint,lastPoint,false);
        logs.Print();
    }

    public void ConvertToWaveFile()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        currFile = fileChooser.showOpenDialog(stage);
        FileDescription fd = new FileDescription(currFile.getName());
        FFTAudioTextFile xx = new FFTAudioTextFile();
        xx.setnPoints(settings.NTrendPoints);
        xx.convertToWave(currFile.getPath(), new FFTAdapter(logs, settings, fd.toString(), false));
    }
}
