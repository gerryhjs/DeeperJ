package graphViz;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static main_core.Core.*;

class  GraphViz{
    private String runPath;
    private String dotPath;
    private String runOrder= "";
    private String dotCodeFile="dotcode.txt";
    private String resultGif="dotGif";
    private StringBuilder graph = new StringBuilder();


    void setPicName(String name)
    {
        this.resultGif=name;
    }

    public void run() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddhhmmssSSS");
        dotCodeFile=sdf.format(new Date())+"dotcode.txt";
        File file=new File(runPath);
        printSys(runPath);
        final boolean result = file.mkdirs();
        if (!result) printLog("Auto mkdir:Dir existed.");
        writeGraphToFile(graph.toString(), runPath);
        creatOrder();
        try {
            if  (System.getProperty("os.name").toLowerCase().startsWith("win"))
            {
                Runtime.getRuntime().exec(runOrder); //windows
            }
            else {
                command(runOrder); //linux
            }
        } catch (Exception e) {
                printLog(e.toString());
        }
    }
    private void command(String cmd)
    {
        printLog(cmd);
        executeCommand(new String[]{"sh", "-c", cmd});
    }
    /**
     * 执行系统命令
     */
    private static void executeCommand(String[] command)
    {
        Runtime r = Runtime.getRuntime();
        Process p;
        List<String> list = new ArrayList<>();
        try {
            p = r.exec(command);
            BufferedReader br = new BufferedReader(new InputStreamReader(p
                    .getInputStream()));
            String inline;
            while ((inline = br.readLine()) != null) {
                list.add(inline);
            }
            printLog(Arrays.toString(list.toArray()));
            br.close();
        } catch (IOException e) {
            printWarn(e.toString());
            e.printStackTrace();
        }
    }
    private void creatOrder(){
        runOrder+=dotPath+" ";
        runOrder+=runPath;
        runOrder+=File.separator+dotCodeFile+" ";
        runOrder+="-Tpng ";
        runOrder+="-o ";
        runOrder+=runPath;
        runOrder+=File.separator+resultGif+".png";
        //printLog((runOrder);
    }

    private void writeGraphToFile(String dotcode, String filename) {
        try {
            File file = new File(filename+File.separator+dotCodeFile);
//            printLog((file.getAbsoluteFile());
            if(!file.exists()){
                try {
                    boolean result = file.createNewFile();
                    if (!result) printWarn("Fail createGraph");
                }catch (Exception e)
                {
                    printLog(e.toString());
                }
            }
            FileOutputStream fos = new FileOutputStream(file);
           // OutputStreamWriter op = new OutputStreamWriter(newFileOutputStream(file), "utf-8");
            fos.write(dotcode.getBytes());
            fos.close();
            try {
                Desktop.getDesktop().open(new File(runPath + File.separator + resultGif + ".png"));
            }catch (Exception ignored)
            {

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    GraphViz(String runPath, String dotPath) {
        this.runPath=runPath;
        this.dotPath=dotPath;
    }

    public void add(String line) {
        graph.append("\t").append(line);
    }

    void addln(String line) {
        graph.append("\t").append(line).append("\n");
    }

//    public void addln() {
//        graph.append('\n');
//    }

    void start_graph() {
        graph.append("digraph G {\n") ;
    }

    void end_graph() {
        graph.append("}") ;
        printLog(graph);
    }

    public void delete() {
        File file = new File(runPath+File.separator+dotCodeFile);
        boolean result = file.delete();//删除临时dot文件
        if (!result) printLog("Fail delete dot-file");
    }
}
