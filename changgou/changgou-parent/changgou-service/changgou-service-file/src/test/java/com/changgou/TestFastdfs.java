package com.changgou;

import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou *
 * @since 1.0
 */

public class TestFastdfs {

    //图片的上传 *****

    @Test
    public void uploadPic() throws Exception {
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer
        StorageServer storageServer = null;

        //6.创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //7.使用storageClient  上传图片 /下载

        //参数1 :表示要上传的图片的路径
        //参数2:表示图片的扩展名 不要带点
        //参数3:表示图片的元数据 比如图片的像素 ,图片的拍摄作者,文件名,文件大小
        String[] jpgs = storageClient.upload_file("C:\\Users\\Administrator\\Pictures\\5b13cd6cN8e12d4aa.jpg", "jpg", null);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }

    }

    //图片的下载
    @Test
    public void downloadPic() throws Exception {
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer
        StorageServer storageServer = null;

        //6.创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);


        //7.下载图片
        //参数1 表示组名
        //参数2 表示远程文件的路径(虚拟磁盘路径+ 数据两级目录+...)
        byte[] bytes = storageClient.download_file("group1", "M00/00/00/wKjThF19_iSAJ3U7AANdC6JX9KA588.jpg");

        //8.流写入磁盘
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\1234.jpg"));

        fileOutputStream.write(bytes);

        fileOutputStream.close();//finally

    }


    //删除图片
    @Test
    public void delete() throws Exception {
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer
        StorageServer storageServer = null;

        //6.创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //7.删除文件
        int group1 = storageClient.delete_file("group1", "M00/00/00/wKjThF19_iSAJ3U7AANdC6JX9KA588.jpg");
        if (group1 == 0) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }

    //根据组名和文件名获取文件对象信息(获取里面的数据:文件的大小,文件名,服务器的ip....)
    @Test
    public void getFileInfo() throws  Exception{
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer
        StorageServer storageServer = null;

        //6.创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //7.获取文件的信息
        FileInfo fileInfo = storageClient.get_file_info("group1", "M00/00/00/wKjThF1-ASaAGXNyAANdC6JX9KA351.jpg");
        System.out.println("文件大小:"+fileInfo.getFileSize());
        String sourceIpAddr = fileInfo.getSourceIpAddr();
        System.out.println("源路径的IP地址:"+sourceIpAddr);
    }

    //获取组信息

    @Test
    public void getStroageServerInfo() throws Exception{
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.通过tracker获取组信息
        StorageServer group1 = trackerClient.getStoreStorage(trackerServer, "group1");
        System.out.println(group1.getInetSocketAddress().getHostString()+":"+group1.getInetSocketAddress().getPort());


    }
    //获取组相关的数组信息
    @Test
    public void getStroageInfoArray() throws Exception{
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.通过tracker获取组信息
        ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, "group1", "M00/00/00/wKjThF1-ASaAGXNyAANdC6JX9KA351.jpg");

        for (ServerInfo group1 : group1s) {
            System.out.println(group1.getIpAddr()+":"+group1.getPort());
        }

    }

    //获取Tracker的URL路径
    @Test
    public void getTrackerUrl() throws Exception{
        //1.创建配置文件 指定服务器的ip和端口以及其他的信息

        //2.加载配置文件
        ClientGlobal.init("C:\\Users\\Administrator\\IdeaProjects\\changgou70\\changgou-parent\\changgou-service\\changgou-service-file\\src\\main\\resources\\fdfs_client.conf");
        //3.创建一个trackerClient对象
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        InetSocketAddress inetSocketAddress = trackerServer.getInetSocketAddress();
        String hostString = inetSocketAddress.getHostString();//ip

        //http://192.168.211.132:8080/group1/M00/00/00/wKjThF1-ASaAGXNyAANdC6JX9KA351.jpg
        int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();//8080

        System.out.println("url:"+hostString+":"+g_tracker_http_port);


    }



}
