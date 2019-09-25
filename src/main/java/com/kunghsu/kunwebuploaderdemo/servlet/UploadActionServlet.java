package com.kunghsu.kunwebuploaderdemo.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * 合并上传文件
 */
public class UploadActionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//文件后台统一存放位置
	private String serverPath = "D:\\home\\kunghsu\\upload";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("进入合并UploadActionServlet后台...");
		//根据前台的action参数决定要做的动作
		String action = request.getParameter("action");

		if ("mergeChunks".equals(action)) {
			// 获得需要合并的目录
			String fileMd5 = request.getParameter("fileMd5");
			String fileName = request.getParameter("fileName");//文件的原始文件名

			System.out.println("当前合并的目录fileMd5：" + fileMd5);
			// 读取目录所有文件
			File f = new File(serverPath + File.separator + fileMd5);
			File[] fileArray = f.listFiles(new FileFilter() {
				// 排除目录，只要文件
				@Override
				public boolean accept(File pathname) {
					if (pathname.isDirectory()) {
						return false;
					}
					return true;
				}
			});

			// 转成集合，便于排序
			List<File> fileList = new ArrayList<File>(Arrays.asList(fileArray));
			// 从小到大排序
			Collections.sort(fileList, new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {
					if (Integer.parseInt(o1.getName()) < Integer.parseInt(o2.getName())) {
						return -1;
					}
					return 1;
				}

			});

			// 新建保存文件(直接用生成zip文件这种方法会报错，因为不能直接这样生成zip文件，格式非法)
//			File outputFile = new File(serverPath + File.separator + UUID.randomUUID().toString() + File.separator + ".zip");

			//fileName：沿用原始的文件名，或者可以使用随机的字符串作为新文件名，但是要 保留原文件的后缀类型
			File outputFile = new File(serverPath + File.separator + UUID.randomUUID().toString() + File.separator + fileName);

			File parentFile = outputFile.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			// 创建文件
			outputFile.createNewFile();

			// 输出流
			FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
			FileChannel outChannel = fileOutputStream.getChannel();

			// 合并，核心就是FileChannel，将多个文件合并为一个文件
			FileChannel inChannel;
			for (File file : fileList) {
				inChannel = new FileInputStream(file).getChannel();
				inChannel.transferTo(0, inChannel.size(), outChannel);
				inChannel.close();

				// 删除分片
				file.delete();
			}

			// 关闭流
			fileOutputStream.close();
			outChannel.close();

			// 清除文件夹
			File tempFile = new File(serverPath + File.separator + fileMd5);
			if (tempFile.isDirectory() && tempFile.exists()) {
				tempFile.delete();
			}

			System.out.println("合并文件成功：" + outputFile.getAbsolutePath());

		} else if ("checkChunk".equals(action)) {
			// 校验文件是否已经上传并返回结果给前端，就一个作用：校验块是否存在，假如不存在，前端会再次用上传器传到后台

			// 文件唯一表示								
			String fileMd5 = request.getParameter("fileMd5");
			// 当前分块下标
			String chunk = request.getParameter("chunk");
			// 当前分块大小
			String chunkSize = request.getParameter("chunkSize");

			// 直接根据块的索引号找到分块文件
			File checkFile = new File(serverPath + File.separator + fileMd5 + File.separator + chunk);

			// 检查文件是否存在，且大小一致（必须满足这两个条件才认为块是已传成功）
			response.setContentType("text/html;charset=utf-8");
			if (checkFile.exists() && checkFile.length() == Integer.parseInt((chunkSize))) {
				response.getWriter().write("{\"ifExist\":1}");
			} else {
				//假如文件没存在，说明没有上传成功，返回0
				response.getWriter().write("{\"ifExist\":0}");
			}
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
