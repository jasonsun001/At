package sample;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;


public class Main extends Application {

    Downloader downloader;
    private String fileToUploadPath;
    private String fileToDownloadPath;
    String file_Name;
    private File file_To_Upload;
    long file_length;
    ProgressIndicator progressIndicator;
    Text downloadReminder;
    Text sendReminder;
    TextField filePathTextField;
    //Text left_up_label;
    TextField targetIPTextField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Jason Transferation");
        primaryStage.getIcons().add(new Image("T.png"));

        Button browseButton = (Button) root.lookup("#browseButton");
        Button BrowseDownloadButton = (Button) root.lookup("#BrowseDownloadButton");
        Button SetGateButton = (Button) root.lookup("#SetGateButton");
        Button ConfirmButton = (Button) root.lookup("#ConfirmButton");
        filePathTextField = (TextField) root.lookup("#filePathTextField");
        TextField fileToSavePathField = (TextField) root.lookup("#fileToSavePathField");
        targetIPTextField = (TextField) root.lookup("#targetIPTextField");
        downloadReminder = (Text) root.lookup("#downloadReminder");
        sendReminder = (Text) root.lookup("#sendReminder");
        //ProgressBarUpload = (ProgressBar) root.lookup("#ProgressBarUpload");
        progressIndicator = (ProgressIndicator) root.lookup("#progressIndicator");
        //left_up_label = (Text) root.lookup("#left_up_label");



        javafx.scene.image.Image power = new Image("power84.png",85,75,true,true);
        SetGateButton.setGraphic(new ImageView(power));
        SetGateButton.setStyle("-fx-background-color:gray");     //初始化Gate按钮颜色
        //targetIPTextField.setText("192.168.");

        //left_up_label.setText("Now you are using\n  AuraTransfer\nPowered by Jason Tech.");

        fileToSavePathField.setText("D:/");
        targetIPTextField.setText("localhost");
        filePathTextField.setText("D:\\音频\\Surface\\Introducing_Surface_Go.mp4");


        /**
         *浏览要上传的文件 按钮监听器
         */
        browseButton.setOnAction(

                (final ActionEvent e) -> {
                    final FileChooser fileChooser = new FileChooser();
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        //openFile(file);
                        filePathTextField.setText(file.getAbsolutePath());
                        fileToUploadPath = filePathTextField.getText();
                        file_Name = filePathTextField.getText().substring(fileToUploadPath.lastIndexOf(File.separator) + 1);
                        System.out.println(file_Name);
                        file_To_Upload = new File(fileToUploadPath);
                        file_length = file_To_Upload.length();
                        System.out.println(file_length);

                    }
                }
        );


        /**
         * 浏览要下载的文件的保存路径 按钮监听器
         */
        BrowseDownloadButton.setOnAction(

                (final ActionEvent e) -> {
                    downloadReminder.setText("");
                    final DirectoryChooser directoryChooser = new DirectoryChooser();
                    final File selectedDirectory = directoryChooser.showDialog(primaryStage);
                    if (selectedDirectory != null) {
                        fileToSavePathField.setText(selectedDirectory.getAbsolutePath());
                        fileToDownloadPath = fileToSavePathField.getText();
                    }

                });

        /**
         * Gate按钮监听器
         */
        SetGateButton.setOnAction(
                (final ActionEvent e) -> {
                    if(fileToSavePathField.getText().equals("")){
                        downloadReminder.setFill(Color.FIREBRICK);
                        downloadReminder.setText("           你还没有在下面的文本框中指定文件将要被保存的位置.");
                    }
                    else if (!new File(fileToSavePathField.getText()).exists()) {
                        downloadReminder.setFill(Color.FIREBRICK);
                        downloadReminder.setText("          你所输入的路径不存在，请检查路径是否正确,并作出必要的修改.");
                    }

                    else{

                        if(SetGateButton.getStyle().equals("-fx-background-color:gray") && new File(fileToSavePathField.getText()).exists()) {
                            SetGateButton.setStyle("-fx-background-color:green");
                            downloader = new Downloader(fileToSavePathField.getText());
                            downloader.StartDownloadClient();
                        }
                        else if(SetGateButton.getStyle().equals("-fx-background-color:green")) {
                            SetGateButton.setStyle("-fx-background-color:gray");
                            downloadReminder.setText("");
                            downloader.downloadShutdown();
                        }

                    }
                }
        );

        /**
         * 确认发送文件 按钮监听器
         */
        ConfirmButton.setOnAction(

                (final ActionEvent e) -> {


                    if(targetIPTextField.getText().equals("")){
                        sendReminder.setFill(Color.FIREBRICK);
                        sendReminder.setText("请填写文件接收方的IP地址.");
                    }
                    else if (filePathTextField.getText().equals("")){
                        sendReminder.setFill(Color.FIREBRICK);
                        sendReminder.setText("请填写文件路径.");
                    }

                    else{
                        sendReminder.setText("");
                        sendReminder.setText("程序正在初始化...");
                        Uploader uploader = new Uploader(targetIPTextField.getText(),fileToUploadPath);
                        uploader.StartUploaderClient();
                    }

                }
        );

        targetIPTextField.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mouseEvent){
                sendReminder.setText("");
            }
        });
        fileToSavePathField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                downloadReminder.setText("");
            }
        });
        filePathTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sendReminder.setText("");
            }
        });


        primaryStage.setTitle("AuraTransfer");
        primaryStage.setScene(new Scene(root, 550, 350));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /*
    private void openFile(File file) {
        EventQueue.invokeLater(() -> {
            try {
                desktop.open(file);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    */

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("AuraTransfer");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }


    /**
     * ***********************************************Uploader**********************************************
     */

    public class Uploader {

        private String KEY_SENT;
        private int port1=55255;
        private int port2=55256;
        private int port3=55257;
        private String target_IP;
        String file_Path;


        public Uploader(String target_IP,String file_Path){
            this.target_IP = target_IP;
            this.file_Path = file_Path;
        }



        private void Upload_fileName_Task() {

            try {
                //left_up_label.setText("目标IP："+targetIPTextField.getText()+";文件路径："+filePathTextField.getText()+";正在初始化...文件即将传输...");
                file_Path = filePathTextField.getText();
                String file_Name = file_Path.substring(file_Path.lastIndexOf(File.separator) + 1);
                Socket socket = new Socket(target_IP, port1);
                OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream());
                outWriter.write(file_Name);
                outWriter.flush();
                outWriter.close();
                socket.close();
                System.out.println("^^ ! ^^");
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                //left_up_label.setText("目标IP："+targetIPTextField.getText()+";文件路径："+filePathTextField.getText());
                sendReminder.setFill(Color.FIREBRICK);
                sendReminder.setText("请检查文件接收方是否已正确打开接收许可，或者你输入的目标主机的IP地址，和所要上传的文件路径是否正确！");
            } catch (ConnectException e1) {
                e1.printStackTrace();
                //left_up_label.setText("目标IP："+targetIPTextField.getText()+";文件路径："+filePathTextField.getText());
                sendReminder.setFill(Color.FIREBRICK);
                sendReminder.setText("请检查文件接收方是否已正确打开接收许可，或者你输入的目标主机的IP地址，和所要上传的文件路径是否正确！");
            } catch (IOException e) {
                e.printStackTrace();
                //left_up_label.setText("目标IP："+targetIPTextField.getText()+";文件路径："+filePathTextField.getText());
                sendReminder.setText("出现IO异常");
                System.out.println("出现IO异常");
            }

        }

        private void Upload_file_Task() {

            if (Recieve_CS_Task()) {
                try {
                    progressIndicator.setProgress(0);
                    File file = new File(file_Path);
                    System.out.println("开始传输文件...");
                    //left_up_label.setText("目标IP："+targetIPTextField.getText()+";文件路径："+filePathTextField.getText());
                    sendReminder.setFill(Color.FORESTGREEN);
                    sendReminder.setText("正在传输文件...");
                    Socket socket = new Socket(target_IP, port3);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    int len;
                    int count = 0;
                    float progress;
                    Number fileSum = file.length();
                    float fileSum_FloatValue = fileSum.floatValue();
                    //int fileSum_intValue = fileSum.intValue();
                    //long fileSum_longValue = file.length();

                    byte[] bytes = new byte[2048];
                    while ((len = bufferedInputStream.read(bytes)) != -1) {

                        bufferedOutputStream.write(bytes, 0, len);
                        bufferedOutputStream.flush();

                        // updateProgress(len,fileSum_intValue);
                        count = count + len;
                        progress = count/fileSum_FloatValue;

                        progressIndicator.setProgress(progress);
                        //  System.out.println(progress);

                    }
                    System.out.println("文件传输完成! ^^");
                    System.out.println("文件下载完成! ^^\n你可以选择进行第二次文件传输或者退出.\n\n");
                    sendReminder.setFill(Color.FORESTGREEN);
                    sendReminder.setText("传输完成!");
                    socket.close();
                    bufferedInputStream.close();
                    bufferedOutputStream.close();
                }catch (UnknownHostException e1) {
                    e1.printStackTrace();
                    sendReminder.setFill(Color.FIREBRICK);
                    sendReminder.setText("请检查文件接收方是否已正确打开接收许可，或者你输入的目标主机的IP地址，和所要上传的文件路径是否正确！");
                } catch (ConnectException e1) {
                    e1.printStackTrace();
                    sendReminder.setFill(Color.FIREBRICK);
                    sendReminder.setText("请检查文件接收方是否已正确打开接收许可，或者你输入的目标主机的IP地址，和所要上传的文件路径是否正确！");
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("出现IO异常");
                    sendReminder.setFill(Color.FIREBRICK);
                    sendReminder.setText("出现IO异常");
                }
            }

        }

        private boolean Recieve_CS_Task() {
            try {
                ServerSocket serverSocket = new ServerSocket(port2);
                System.out.println("uploader正在等待对方(receiver)的密钥验证...  ");
                //sendReminder.setFill(Color.FORESTGREEN);
                //sendReminder.setText("正在等待最终许可协议密钥验证...");
                Socket connection = serverSocket.accept();
                System.out.println("sender已连接到uploader(本机)的55256端口...");

                InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
                char[] chars = new char[1024];
                int len = inReader.read(chars);
                System.out.println("已读取到receiver的许可协议...");
                KEY_SENT = new String(chars, 0, len);
                System.out.println("读取到的结果为： "  + " ~ " + KEY_SENT.equals("Y")+"通过验证...");
                sendReminder.setFill(Color.FORESTGREEN);
                sendReminder.setText("通过验证...");
                serverSocket.close();
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return (KEY_SENT.equals("Y"));
        }



        private class StartUploaderThread implements Runnable{
            @Override
            public void run(){
                synchronized (this) {
                    Upload_fileName_Task();
                    Upload_file_Task();
                }
            }

        }


        /**
         * 上传确认按钮监听器调用的方法 ************************************************************
         */
        public void StartUploaderClient(){
            StartUploaderThread startUploaderThread = new StartUploaderThread();
            new Thread(startUploaderThread).start();

            //progressIndicator.progressProperty().bind(StartUploaderClientTask.progressProperty());
            //ProgressBarUpload.progressProperty().bind(StartUploaderClientTask.progressProperty());
            // new Thread(StartUploaderClientTask).start();

        }


        /*
        Task StartUploaderClientTask = new Task() {
            @Override
            protected Object call() throws Exception {
                synchronized (this) {
                    Upload_fileName_Task();
                    Upload_file_Task();
                }
                return null;
            }
        };
        */


    }


    /**
     *  ***************************************************Downloader**************************************
     */
    public class Downloader {

        private Socket connection;
        private ServerSocket serverSocket1;
        private ServerSocket serverSocket3;
        private int port1=55255;
        private int port2=55256;
        private int port3=55257;
        private File file_to_save;
        private String file_parent;


        public Downloader(String file_parent){
            this.file_parent = file_parent;
        }



        private void Download_fileName_Task() {
            try {
                serverSocket1 = new ServerSocket(port1);
                System.out.println("已进入监听模式，正在等待对方连接...");
                downloadReminder.setFill(Color.FORESTGREEN);
                downloadReminder.setText("正在等待对方连接...");
                connection = serverSocket1.accept();
                System.out.println("对方已连接...");
                downloadReminder.setFill(Color.FORESTGREEN);
                downloadReminder.setText("对方已连接，正在进行验证，即将开始下载文件...");
                InputStreamReader inReader = new InputStreamReader(connection.getInputStream());
                char[] chars = new char[1024];                                            //读取客户端发来的文件名
                int len = inReader.read(chars);
                System.out.println("文件名读取完成...");
                String file_name_local = new String(chars, 0, len);
                System.out.println("读取到的文件名是： " + file_name_local + "\t文件名长度为" + file_name_local.length()+" ...");
                System.out.println("file_parent: " + file_parent + "\tfile_name_local: " + file_name_local+" ...");
                String file_to_save_path = file_parent + file_name_local;
                file_to_save = new File(file_to_save_path);
                file_to_save.createNewFile();  //本地先创建一个空文件，文件名为file_name_local
                System.out.println("已在本地的" + file_parent + " 的目录下创建了一个名为" + file_name_local + "空文件...");
                serverSocket1.close();
                connection.close();
                inReader.close();
                Thread.sleep(5000);
                send_CS_Task();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



        private void Download_file_Task(){
            try {
                System.out.println("正在下载文件中... ...");
                downloadReminder.setFill(Color.FORESTGREEN);
                downloadReminder.setText("文件正在下载中...");
                serverSocket3 = new ServerSocket(port3);
                connection = serverSocket3.accept();
                BufferedInputStream bufin = new BufferedInputStream(connection.getInputStream());
                BufferedOutputStream bufout = new BufferedOutputStream(new FileOutputStream(file_to_save));            //下载（客户端发来的）文件
                int len;
                byte[] bytes = new byte[1024];
                while ((len = bufin.read(bytes)) != -1) {
                    bufout.write(bytes, 0, len);
                    bufout.flush();
                }
                System.out.println("文件下载完成! ^^\n你可以选择进行第二次文件传输或者退出\n\n.");
                downloadReminder.setFill(Color.FORESTGREEN);
                downloadReminder.setText("下载完成!");
                if(serverSocket3!=null){serverSocket3.close();}
                if (connection!=null){connection.close();}
                if (bufin!=null){bufin.close();}
                if (bufout!=null){bufout.close();}
            }catch (IOException e){
                e.printStackTrace();
            }

        }



        private void send_CS_Task() {
            //  for(int i=0;i<=2;i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
                System.out.println("开始传输CS...");
                Socket socket = new Socket(connection.getInetAddress(), port2);
                System.out.println("receiver执行send_CS_Task()方法时，实例的socket中传入的client_IP是: " + connection.getInetAddress());
                OutputStreamWriter outWriter = new OutputStreamWriter(socket.getOutputStream());
                String CONFIRM_SIGNAL = "Y";
                outWriter.write(CONFIRM_SIGNAL);
                outWriter.flush();
                System.out.println("CS传输完成...");
                if (socket != null) {
                    socket.close();
                }
                if (outWriter != null) {
                    outWriter.close();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // }
        }

        private class StartDownloaderThread extends Thread{
            @Override
            public void run(){
                Download_fileName_Task();
                Download_file_Task();
            }
        }


        public void StartDownloadClient(){
            StartDownloaderThread startDownloaderThread = new StartDownloaderThread();
            startDownloaderThread.start();
        }

        public void downloadShutdown(){
            try {

                if(serverSocket1 != null) {
                    serverSocket1.close();
                    System.out.println("关机触发已起作用.. 文件名传输端口的ServerSocket已关闭...");
                }
                else{
                    System.out.println("关机触发时..ServerSocket(55255)的值： null");
                }
                if(serverSocket3 != null) {
                    serverSocket3.close();
                    System.out.println("关机触发已起作用.. 55257端口的ServerSocket已关闭...");
                }
                else
                    System.out.println("关机触发时..ServerSocket(55257)的值： null");

            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }
}
