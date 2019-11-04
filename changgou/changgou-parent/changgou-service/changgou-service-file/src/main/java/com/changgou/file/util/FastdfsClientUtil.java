package com.changgou.file.util;

import com.changgou.file.pojo.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.file.util *
 * @since 1.0
 */
public class FastdfsClientUtil {

    static {
        //类被加载 ,就初始化加载配置文件
        ClassPathResource classPathResource = new ClassPathResource("fdfs_client.conf");
        String path = classPathResource.getPath();
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //图片上传
    public static String[] upload(FastDFSFile file) throws Exception {
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
        NameValuePair[] nameValuePairs = new NameValuePair[]{
                new NameValuePair(file.getName()),
                new NameValuePair(file.getAuthor())};
        String[] jpgs = storageClient.upload_file(file.getContent(), file.getExt(), nameValuePairs);
        return jpgs;//  /group1    M00/00/00/wKjThF1-ASaAGXNyAANdC6JX9KA351.jpg
    }


    //图片下载

    public static InputStream downFile(String groupName, String remoteFileName) throws Exception {
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
        byte[] bytes = storageClient.download_file(groupName, remoteFileName);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        return byteArrayInputStream;

    }

    //图片删除
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
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
        int i = storageClient.delete_file(groupName, remoteFileName);
        if (i == 0) {
            System.out.println("删除成功");
        } else {
            System.out.println("删除失败");
        }
    }


    //获取文件信息

    public static FileInfo getFile(String groupName, String remoteFileName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.创建storageServer
        StorageServer storageServer = null;

        //6.创建storageClient
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        //7.获取文件的信息
        FileInfo fileInfo = storageClient.get_file_info(groupName, remoteFileName);

        return fileInfo;

    }

    //获取组信息

    public static StorageServer getStorages(String groupName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.通过tracker获取组信息
        StorageServer group1 = trackerClient.getStoreStorage(trackerServer, groupName);
        return group1;
    }

    //获取group的数组信息
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName) throws Exception {
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //5.通过tracker获取组信息
        ServerInfo[] group1s = trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);

        return group1s;
    }

    //获取trackerurl信息  http://192.168.211.132:8080/group1/M00/12321739217.jpg

    public static String getTrackerUrl() throws Exception {// http://192.168.211.132:8080
        TrackerClient trackerClient = new TrackerClient();

        //4.创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        String hostName = trackerServer.getInetSocketAddress().getHostName();//  192.168.211.132

        int g_tracker_http_port = ClientGlobal.getG_tracker_http_port();//配置文件中的8080   8080

        return "http://" + hostName + ":" + g_tracker_http_port;
    }
}
