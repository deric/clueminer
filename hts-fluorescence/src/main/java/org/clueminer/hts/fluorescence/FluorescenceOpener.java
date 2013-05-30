package org.clueminer.hts.fluorescence;

import eu.medsea.mimeutil.MimeUtil2;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.clueminer.dataset.api.ContinuousInstance;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.api.Timeseries;
import org.clueminer.dendrogram.DendrogramTopComponent;
import org.clueminer.openfile.OpenFileImpl;
import org.clueminer.project.ProjectControllerImpl;
import org.clueminer.project.ProjectImpl;
import org.clueminer.project.ProjectInformationImpl;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.Workspace;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.Task;
import org.openide.util.TaskListener;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 60)
public class FluorescenceOpener implements OpenFileImpl, TaskListener {

    private MimeUtil2 mimeUtil = new MimeUtil2();
    private static final RequestProcessor RP = new RequestProcessor("non-interruptible tasks", 1, false);
    private static Project project;
    private FluorescenceImporter importer;
    private static final Logger logger = Logger.getLogger(FluorescenceOpener.class.getName());

    public FluorescenceOpener() {
        //MIME type detection
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector");
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.OpendesktopMimeDetector");
    }

    protected Collection detectMIME(File file) {
        Collection mimeTypes = null;
        try {
            byte[] data;
            InputStream in = new FileInputStream(file);
            data = new byte[1024];
            in.read(data, 0, 1024);
            in.close();
            mimeTypes = mimeUtil.getMimeTypes(data);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return mimeTypes;
    }

    @Override
    public boolean open(FileObject fileObject) {
        File f = FileUtil.toFile(fileObject);
        return openFile(f);
    }

    protected boolean openFile(File f) {
        Collection mimeTypes = detectMIME(f);
        if (mimeTypes.contains("text/x-tex")) {
            String line;
            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                line = br.readLine();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                return false;
            }

            if ("% Fluorescence version 1.0".equals(line)) {
                importer = new FluorescenceImporter(f);
                openFluorescenceFile(importer);
                return true;
            }
        }

        return false;
    }

    protected void openFluorescenceFile(FluorescenceImporter importer) {
        ProgressHandle ph = ProgressHandleFactory.createHandle("Opening file " + importer.getFile().getName());
        importer.setProgressHandle(ph);
        //Project instance
        project = new ProjectImpl();
        project.getLookup().lookup(ProjectInformationImpl.class).setFile(importer.getFile());
        final RequestProcessor.Task task = RP.create(importer);
        task.addTaskListener(this);
        task.schedule(0);
    }

    @Override
    public void taskFinished(Task task) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                System.out.println("opening task finished");
                ProjectControllerImpl pc = Lookup.getDefault().lookup(ProjectControllerImpl.class);
                project.add(importer.getDataset());
                DendrogramTopComponent tc = new DendrogramTopComponent();
                FluorescenceDataset plate = importer.getDataset();

                FluorescenceDataset normalized = (FluorescenceDataset) normalize(plate);

                saveDataset(plate, "import", false);
                saveDataset(normalized, "norm", true);

                tc.setDataset(normalized);
                //tc.setDataset(plate);
                tc.setProject(project);
                tc.setDisplayName(plate.getName());
                tc.open();
                tc.requestActive();

                pc.openProject(project);
                Workspace workspace = pc.getCurrentWorkspace();
                if (workspace != null) {
                    System.out.println("workspace: " + workspace.toString());
                    System.out.println("adding plate to lookup");
                    workspace.add(importer.getDataset());  //add plate to project's lookup
                } else {
                    System.out.println("workspace is null!!!!");
                }

                //     DataPreprocessing preprocess = new DataPreprocessing(plate, tc);
                //     preprocess.start();
            }
        });
    }

    public void saveDataset(FluorescenceDataset plate, String ident, boolean normalized) {
        String filename = System.getProperty("user.home") /*
                 * FileUtils.LocalFolder()
                 */ + "/" + "david-" + plate.getName() + "-" + ident + ".csv";


        String separator = ",";
        String eol = "\n";
        double value;

        try {
            System.out.println("writing to " + filename);
            FileWriter writer = new FileWriter(filename);
            //header
                /*    writer.append(separator);
             for (String s : paramNames) {
             writer.append(s).append(separator);
             }
             writer.append(eol);*/
            //content
            FluorescenceInstance current;
            String sampleName;
            logger.log(Level.INFO, "export size {0}", plate.size());

            if (normalized) {
                for (int i = 0; i < plate.size(); i++) {
                    current = plate.instance(i);
                    if (current.getColumn() < 46) {
                        sampleName = current.getName();
                        writer.append(sampleName).append(separator);

                        for (int j = 0; j < plate.attributeCount(); j++) {
                            value = plate.getAttributeValue(j, i);
                            writer.append(String.valueOf(value)).append(separator);
                        }
                        writer.append(eol);
                    }
                }
            } else {
                for (int i = 0; i < plate.size(); i++) {
                    current = plate.instance(i);
                    sampleName = current.getName();
                    writer.append(sampleName).append(separator);
                    for (int j = 0; j < plate.attributeCount(); j++) {
                        value = plate.getAttributeValue(j, i);
                        writer.append(String.valueOf(value)).append(separator);
                    }
                    writer.append(eol);
                }

            }


            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Dataset<FluorescenceInstance> normalize(FluorescenceDataset plate) {
        //Dataset normalized = plate.duplicate();
        //columns are numbered from 0
        int colCnt = plate.getColumnsCount();
        System.out.println("normalizing dataset....");
        System.out.println("col cnt: " + colCnt);
        System.out.println("attr : " + plate.attributeCount());
        double avg;
        double sum, posCtrl;
        Instance control1, control2;
        FluorescenceDataset normalize = (FluorescenceDataset) plate.duplicate();
        // time point for positive control
        int positiveTimepoint = 9;
        for (int i = 0; i < plate.getRowsCount() - 3; i += 4) {
            try {

                int pos;
                Instance negativeControl = new FluorescenceInstance(plate, plate.attributeCount());
                // Instance negativeControl = new FluorescenceInstance(plate, colCnt);
                //System.out.println("row: " + i);
                //compute average instance
                posCtrl = 0.0;
                for (int j = 0; j < 4; j++) {
                    pos = i + j;

                    control2 = plate.instance(translatePosition(pos, 46, colCnt));
                    posCtrl += control2.value(positiveTimepoint);

                }
                posCtrl /= 4.0;

                for (int k = 0; k < plate.attributeCount(); k++) {
                    sum = 0.0;

                    for (int j = 0; j < 4; j++) {
                        pos = i + j;
                        control1 = plate.instance(translatePosition(pos, 45, colCnt));
                        //  System.out.println("control: " + control1.getFullName() + " - " + control1.toString());
                        sum += control1.value(k);
                        System.out.println("well " + control1.getName() + " = " + control1.value(k));
                    }
                    avg = sum / 4.0;
                    negativeControl.put(k, avg);
                }

                logger.log(Level.INFO, "negative= {0}", negativeControl.toString());
                logger.log(Level.INFO, "positive= {0}", posCtrl);
                //normalize quadruplicate
                double value, divisor;

                for (int j = 0; j < 4; j++) {
                    for (int m = 0; m < plate.getColumnsCount(); m++) {
                        pos = (i + j) * colCnt + m;

                        FluorescenceInstance inst = plate.instance(pos);
                        //  System.out.println("well = "+inst.getName());
                        FluorescenceInstance out = new FluorescenceInstance((Timeseries<? extends ContinuousInstance>) normalize, plate.attributeCount());
                        for (int k = 0; k < plate.attributeCount(); k++) {
                            //substract background
                            divisor = posCtrl - negativeControl.value(k);

                            if (divisor == 0.0) {
                                value = 0.0;
                            } else {
                                value = ((inst.value(k) - negativeControl.value(k)) / divisor) * 100;
                            }

                            out.put(k, value);
                            out.setName(inst.getName());
                        }
                        normalize.add(out);
                    }
                }


            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return normalize;
    }

    /**
     *
     * @param ord
     * @param col starts from 1, not zero!
     * @param colCnt
     * @return
     * @throws IOException
     */
    public int translatePosition(int ord, int col, int colCnt) throws IOException {
        int res = ord * colCnt + col - 1;
        return res;
    }
}
