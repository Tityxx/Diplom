import javafx.stage.FileChooser;
import javafx.stage.Stage;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.fft.FFTAudioTextFile;
import romanow.snn_simulator.fft.FFTParams;

import java.io.*;

public class FileController
{
    private Stage stage;
    private Logs logs;
    private Settings settings;

    private File currFile;

    public FileController(Stage _stage, Logs _logs, Settings _settings)
    {
        stage = _stage;
        logs = _logs;
        settings = _settings;
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
        String ss = file.getName();

        FileDescription description = new FileDescription(ss);
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
     * Парсинг файла и вывод информации
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
        //for(p_BlockSize=1;p_BlockSize*FFT.Size0<=lnt;p_BlockSize*=2);
        //if (p_BlockSize!=1) p_BlockSize/=2;
        FFTParams params = new FFTParams().W(settings.P_BlockSize * FFT.Size0).procOver(settings.P_OverProc).
                FFTWindowReduce(false).p_Cohleogram(false).p_GPU(false).compressMode(false).
                winMode(settings.WinFun);
        FFT fft = new FFT();
        fft.setFFTParams(params);
        fft.calcFFTParams();
        settings.FreqStep = fft.getStepHZLinear()/settings.KF100;
        logs.Print("Отсчетов "+xx.getFrameLength());
        logs.Print("Кадр: "+ settings.P_BlockSize*FFT.Size0);
        logs.Print("Перекрытие: "+ settings.P_BlockSize);
        logs.Print("Дискретность: "+String.format("%5.4f", settings.FreqStep)+" гц");
        //inputStat.reset();
        //fft.fftDirect(xx,back);
        fft.fftDirect(xx,new FFTAdapter(logs, settings,title));
    }
}
