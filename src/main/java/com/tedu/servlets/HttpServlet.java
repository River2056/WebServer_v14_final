package com.tedu.servlets;

import java.io.File;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * 所有Servlet的超類. 定義Servlet都應當具備的功能
 * @author User
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request, HttpResponse response);
	
	protected void forward(String path, HttpRequest request, HttpResponse response) {
		try {
			File file = new File("webapps" + path);
			/**
			 * 響應客戶端
			 */
			// 設置響應頭信息
			// 獲取文件的後綴
			String name = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			// 根據後綴找到對應的Content-Type的值
			String contentType = HttpContext.getContentTypeByMime(name);
			// 設置響應頭Content-Type, Content-Length
			response.setContentLength((int) file.length());
			response.setContentType(contentType);
			// 設置響應正文
			response.setEntity(file); // 將必要文件傳進去作業
			// 響應客戶端
			response.flush(); // 根據必要文件調方法完成響應
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
