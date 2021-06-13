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

    public FileController(Stage _stage, Logs _logs, Settings _settings)
    {
        Path currentRelativePath = Paths.get("");
        dir = currentRelativePath.toAbsolutePath().toString();

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
        Statistic statistic = new Statistic(settings, logs, "Волна");
        statistic.paintOne(currentWave.getData(),firstPoint,lastPoint,false);
        //statistic.pack();
        //statistic.setVisible(true);
        logs.Print();
    }

    private  I_ArchiveSelector convertSelector = new I_ArchiveSelector()
    {
        @Override
        public void onSelect(FileDescription fd, boolean longClick) {
            String pathName = dir + "/" + fd.originalFileName;
            FFTAudioTextFile xx = new FFTAudioTextFile();
            xx.setnPoints(settings.NTrendPoints);
            //hideFFTOutput=false;
            xx.convertToWave(pathName, new FFTAdapter(logs, settings, fd.toString()));
        }
    };

    public void selectFromArchive(String title, final I_ArchiveSelector selector)
    {
        final ArrayList<FileDescription> ss = createArchive();
        ArrayList<String> out = new ArrayList<>();
        for(FileDescription ff : ss)
        {
            out.add(ff.toString());
        }
        /*new ListBoxDialog(this, out, title, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                selector.onSelect(ss.get(index),false);
            }
            @Override
            public void onLongSelect(int index) {
                selector.onSelect(ss.get(index),true);
            }
        }).create();*/
    }

    public ArrayList<FileDescription> createArchive()
    {
        return createArchive(null);
    }
    public ArrayList<FileDescription> createArchive(String subdir)
    {
        File ff = new File(dir + (subdir!=null ? "/"+subdir : ""));
        if (!ff.exists())
        {
            ff.mkdir();
        }
        FileDescriptionList out = new FileDescriptionList();
        for(String ss : ff.list())
        {
            File file = new File(dir + "/" + ss);
            if (file.isDirectory())
            {
                continue;
            }
            FileDescription dd = new FileDescription(ss);
            if (!dd.originalFileName.toUpperCase().endsWith(".TXT"))
            {
                continue;
            }
            String zz = dd.parseFromName();
            if (zz!=null)
            {
                logs.Print("Файл: " + ss + " " + zz);
            }
            else
            {
                out.add(dd);
            }
        }
        out.sort((I_FDComparator) (o2, o1) -> (int)(o2.createDate.getMillis() - o1.createDate.getMillis()));
        return out;
    }
}
