package com.zhiwei.credit.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import com.zhiwei.core.util.AppUtil;
public class FtpUtils {
	 FtpClient ftpClient; 
	
	 public FtpUtils(){ 
	  ftpClient=FtpClient.create(); 
	  ftpClient.setConnectTimeout(6000000);
	 } 
	 
	 //建立链接
	 public void connect(String host,int port,String user,String password)throws IOException, FtpProtocolException{ 
		  SocketAddress addr = new InetSocketAddress(host,port);
		  ftpClient.connect(addr); 
		  ftpClient.login(user,password.toCharArray()); 
		  ftpClient.setBinaryType(); 
		  System.out.println("ftp服务器已连接----------------------");
	 } 
	 
	 
	 public void cd(String relativePath)throws IOException, FtpProtocolException{ 
		 ftpClient.changeDirectory(relativePath);
	 } 
	 
	 public void uploadDirectory(String directory,boolean bl,String nameFtp,String n,String serverIp,String username,String password)throws IOException, FtpProtocolException{ 
		 File file=new File(directory); 
		 String name=null;//待上传文件名 
		 if(file.isDirectory()){//如果为目录，则按目录传 
			 File[] files=file.listFiles();   
			 //在ftp服务器上创建对应目录 
			 ftpClient.setAsciiType();
			 String dir = file.getName();
			 if(bl){
				 dir = nameFtp;
				 bl = false;
			 }
			 ftpClient.makeDirectory(dir + "\r\n");
//			 ftpClient.sendServer("XMKD " + dir + "\r\n"); 
//			 ftpClient.readServerResponse(); 
			 ftpClient.changeDirectory(dir); 
			 ftpClient.setBinaryType(); 
	   //循环传递目录下的所有文件与目录 
			 int i=0; 
			 for(i=0;i<files.length;i++){
				 File tmpFile=files[i]; 
	   
				 if(tmpFile.isDirectory()){
					 ftpClient.changeDirectory("..");
					 ftpClient.changeDirectory(dir); 
					 uploadDirectory(tmpFile.getAbsolutePath(),bl,nameFtp, n, serverIp, username, password);
				 }else{ 
					 ftpClient.changeDirectory("..");
					 ftpClient.changeDirectory(dir); 
					 name=tmpFile.getName(); 
					 upload(directory+"/"+name, n, serverIp, username, password); 
				 } 
			 } 
	       ftpClient.changeDirectory("..");
	   } 
	     else //如果为文件，则按文件上传 
//	    	 upload(directory,fileName, n, serverIp, username, password);
	      upload(directory,file.getName(), n, serverIp, username, password);
	 } 
	 
	
	 public void upload(String srcFile,String n,String serverIp,String username,String password)throws IOException, FtpProtocolException{
		
	  File file=new File(srcFile); 
	  FileInputStream fin=new FileInputStream(srcFile);
	  OutputStream ots = ftpClient.putFileStream(file.getName(), true);
//	  TelnetOutputStream tos = ftpClient.put(file.getName());
	  if(file.getName().equals("spring.xml")){
	   InputStreamReader ie=new InputStreamReader(fin, "utf-8");
	   BufferedReader reader = new BufferedReader(ie);
	   String line = null;
	   while ((line = reader.readLine()) != null) {
	    if(line.indexOf("serverIp")!=-1){
	     line=line.replaceAll("serverIp", serverIp);
	     line=line.replaceAll("dataName", n);
	    }
	    if(line.indexOf("datausername")!=-1){
	     line=line.replaceAll("datausername", username);
	    }
	    if(line.indexOf("datapassword")!=-1){
	     line=line.replaceAll("datapassword", password);
	    }
	    ots.write(line.getBytes("utf-8"));
	   }
	   reader.close();
	   ie.close();
	  }else{
	   int readLength = 0;
	   byte[] buf = new byte[1024];
	   while ( (readLength = fin.read(buf)) != -1) {
	       ots.write(buf, 0, readLength);
	   }
	  }
	  fin.close();
	  ots.close();
	 } 
	 
	          
	 public void upload(String srcFile,String destFile,String n,String serverIp,String username,String password)throws IOException, FtpProtocolException{ 
	  upload(srcFile, n, serverIp, username, password); 
	  File file=new File(srcFile); 
	  //文件重命名 打开 上传 zip文件会报错 先关闭
	 // ftpClient.rename(file.getName(), destFile); 
	 } 
	 public void close(){ 
	  try { 
	   ftpClient.close(); 
	  } catch (IOException e) { 
	   e.printStackTrace(); 
	  } 
	 } 
	
	     /**
	      * @param directory  上传文件在服务器上的存放路径
	      * @param srcFilePath  要上传文件的存放路径
		 * svn：songwj 
		 * 创建文件夹、调用上传方法进行上传
	     * @throws FtpProtocolException 
		 */
		 public void swjUploadDirectory(String directory,String srcFilePath,boolean bl,String nameFtp,String n,String serverIp,String username,String password,String dirStr)throws IOException, FtpProtocolException{ 
		
			 System.out.println("服务器上要创建文件夹路径-------------"+dirStr);
			 //1 创建各个节点的文件夹
			  if(!"".equals(dirStr)){
				  String dir =dirStr;
				  ftpClient.makeDirectory(dir + "\r\n");
//				  ftpClient.sendServer("XMKD " + dir + "\r\n"); 
//				  ftpClient.readServerResponse(); 
				  ftpClient.changeDirectory(dir); 
				  ftpClient.setBinaryType();
			  }
			  //2 调用上传方法上传文件
			  swjUpload(srcFilePath,directory, n, serverIp, username, password);
		 }
	 
		 /**
		  * 文件上传代码
		  * @param srcFile  要上传文件的路径
		  * @param fileToServerPath  上传文件存放在服务器上的位置
		  * @param n
		  * @param serverIp
		  * @param username
		  * @param password
		  * @throws IOException
		 * @throws FtpProtocolException 
		  */
		 public void swjUpload(String srcFile,String fileToServerPath,String n,String serverIp,String username,String password)throws IOException, FtpProtocolException{
			
		  //创建一个文件（服务器上对应的文件）
	      File serverFile = new File(fileToServerPath);
	      
	      //创建选择要上传的文件对象
		  File srFile=new File(srcFile); 
		  
		  //把上传的文件对象放入文件流中
		  FileInputStream fin=new FileInputStream(srcFile);
		  
		  System.out.println("fileToServerPath==="+fileToServerPath);
		  //打开ftp上的文件 准备接收
		  OutputStream ots = ftpClient.putFileStream(serverFile.getName(), true);
//		  TelnetOutputStream tos = ftpClient.put(serverFile.getName());
		  
		  if(srFile.getName().equals("spring.xml")){
		     InputStreamReader ie=new InputStreamReader(fin, "utf-8");
		     BufferedReader reader = new BufferedReader(ie);//读取要上传的 文件
		     String line = null;
		     while ((line = reader.readLine()) != null) {
		    	 if(line.indexOf("serverIp")!=-1){
		    		 line=line.replaceAll("serverIp", serverIp);
		    		 line=line.replaceAll("dataName", n);
		    	 }
		    	 if(line.indexOf("datausername")!=-1){
		    		 line=line.replaceAll("datausername", username);
		    	 }
		    	 if(line.indexOf("datapassword")!=-1){
		    		 line=line.replaceAll("datapassword", password);
		    	 }
		    	 ots.write(line.getBytes("utf-8"));
		   }
		   reader.close();
		   ie.close();
		  }else{
		   int readLength = 0;
		   byte[] buf = new byte[1024];
		   while ( (readLength = fin.read(buf)) != -1) {
			   ots.write(buf, 0, readLength);//把要上传文件流写入服务器的文件中
		   }
		  }
		  fin.close();
		  ots.close();
		 } 
		 

		 /**
		  * Ftp 服务器文件下载单个文件
		  * svn:songwj
		  * @param srcStr  要下载文件的路径
		  * @param disStr  下载文件存放的路径
		 * @throws FtpProtocolException 
		  */
		 public  void  ftpDownFile(String srcStr,String disStr) throws FtpProtocolException{
			 
			// 读取配置文件读取ftp ip地址 端口号  账户  密码
				String ip = AppUtil.getFtpIp();
				String us = AppUtil.getFtpUsName();//帐号
				String ps = AppUtil.getFtpPss();//密码
				int port = Integer.valueOf(AppUtil.getFtpPort());//端口 默认为 21
				
				try {
					connect(ip, port, us, ps);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			InputStream is = null;
	        FileOutputStream os = null;
	        
	        System.out.println("srcStr--------------------"+srcStr);
	        System.out.println("disStr--------------------"+disStr);
	        
	        
	        try {
	            //获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。
	           
	            ftpClient.changeDirectory(srcStr);
	            is = ftpClient.getFileStream(srcStr);
//	            is = ftpClient.get(srcStr);
	          
	            System.out.println("os--------------------"+os.toString());
	            
	            
	            byte[] bytes = new byte[2048];
	            int c;
	            while ((c = is.read(bytes)) != -1) {
	                os.write(bytes, 0, c);
	            }
	            System.out.println("download success");
	        } catch (IOException ex) {
	            System.out.println("not download");
	            ex.printStackTrace();
	            throw new RuntimeException(ex);
	        } finally{
	            try {
	                if(is != null){
	                    is.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    if(os != null){
	                        os.close();
	                    }
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		 }
		 
		 /**
		  * 删除ftp服务器上指定的文件
		  * @param fileName
		 * @throws IOException 
		 * @throws FtpProtocolException 
		  */
		 public String  removeFile(String deleteFilePath) throws FtpProtocolException, IOException{
			 
			 System.out.println("---------进入到删除方法中");
//			 D:\新建文件夹\tomcat633\tomcat6\webapps\hurong_p2p_v3.5.1\attachFiles\webfile\pl_Web_Show_Materials\sl_smallloan_project\39683772240
			 String resultStr = "";
			 
			 if(ftpClient!= null){ 
				  ftpClient.deleteFile("D:/sendMessagePicture.png" + "\r\n");
				  ftpClient.deleteFile("D:\\sendMessagePicture.png" + "\r\n");
//				  ftpClient.sendServer("DELE " + "D:/sendMessagePicture.png" + "\r\n"); 
//				  ftpClient.sendServer("DELE " + "D:\\sendMessagePicture.png" + "\r\n"); 
				  
				try {
					String status = ftpClient.getLastResponseString();
//					int status = ftpClient.readServerResponse();
					System.out.println("status----------------"+status);
//					if(status==250){
//						resultStr= "SUCCESS";
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }else{
				 resultStr = "FAIL";
			 }
			return resultStr;
		 }
		 
		 
		 
	}