import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        // 创建服务器UI
        JFrame frame = new JFrame("服务器");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // 创建文本区域显示日志
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        try {
            // 创建ServerSocket监听端口25565
            ServerSocket serverSocket = new ServerSocket(25565);
            logArea.append("服务器已启动，监听端口: 25565\n");

            // 持续监听客户端连接
            while (true) {
                // 接受客户端连接
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                logArea.append("客户端已连接!\n");

                // 循环处理客户端的请求
                while (true) {
                    String clientMessage = dis.readUTF();

                    // 判断是否为文件传输请求
                    if (clientMessage.equalsIgnoreCase("sendfile")) {
                        // 接收文件
                        String fileName = dis.readUTF();

                        // 检测文件名是否包含空格并替换
                        if (fileName.contains(" ")) {
                            fileName = fileName.replace(" ", "_");
                            logArea.append("文件名包含空格，已重命名为: " + fileName + "\n");
                        }

                        File downloadDir = new File("C:\\SocketDownloads");
                        if (!downloadDir.exists()) {
                            downloadDir.mkdirs();
                        }

                        File outputFile = new File(downloadDir, fileName);
                        FileOutputStream fos = new FileOutputStream(outputFile);
                        byte[] buffer = new byte[4096];
                        int bytesRead;

                        logArea.append("正在接收文件: " + fileName + "\n");

                        // 读取文件内容并保存
                        while ((bytesRead = dis.read(buffer)) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            if (bytesRead < buffer.length) break;
                        }

                        fos.close();
                        String downloadLink = "\"file:///" + outputFile.getAbsolutePath().replace("\\", "/") + "\"";
                        logArea.append("文件接收完成，并保存至: " + outputFile.getAbsolutePath() + "\n");
                        logArea.append("下载链接: " + downloadLink + "\n");
                    } else {
                        // 显示客户端消息
                        logArea.append("客户端消息: " + clientMessage + "\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
