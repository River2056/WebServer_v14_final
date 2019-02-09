package com.tedu.core;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;
import com.tedu.servlets.HttpServlet;
import com.tedu.servlets.LoginServlet;
import com.tedu.servlets.RegServlet;

/**
 * Web服務端主類
 * @author User
 *
 */
public class WebServer {

	private ServerSocket server;
	
	private ExecutorService threadPool;
	
	public WebServer() {
		try {
			System.out.println("正在初始化服務端...");
			server = new ServerSocket(ServerContext.port);
			threadPool = Executors.newFixedThreadPool(ServerContext.threadPool);
			System.out.println("服務端初始化完畢!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			while(true) {
				System.out.println("等待客戶端連接...");
				Socket socket = server.accept();
				System.out.println("一個客戶端連接了!");
				
				ClientHandler handler = new ClientHandler(socket);
//				Thread t = new Thread(handler);
//				t.start();
				threadPool.execute(handler); // 將任務交給線程池處理
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
	/**
	 * 處理客戶端請求應完成響應
	 * @author User
	 *
	 */
	private class ClientHandler implements Runnable {
		private Socket socket;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			System.out.println("處理客戶端請求!");
			HttpRequest request = null;
			HttpResponse response = null;

			try {
				// 根據輸入流解析請求
				request = new HttpRequest(socket.getInputStream());
				// 根據輸出流創建響應對象
				response = new HttpResponse(socket.getOutputStream());
				// 打樁
				System.out.println("requestLine: " + request.getRequestLine());
				
				/**
				 * public class ServerContext public static Map<String, String> servletMapping; 
				 * servletMapping.put("/myweb/reg", "com.tedu.servlets.RegServlet");
				 * servletMapping.put("/myweb/login", "com.tedu.servlets.LoginServlet"); 
				 * 
				 * 路徑: request.getRequestLine() // e.g. /myweb/reg 
				 * 
				 * // 先檢查Map裡是否有對應的值
				 * ServerContext.servletMapping.containsKey(request.getRequestLine())
				 * 
				 * // if true ==> 
				 * // 動態加載類 
				 * Class cls = Class.forName(ServerContext.servletMapping.get(request.getRequestLine())) // 根據名字找到對應value // "com.tedu.servlets.RegServlet" 
				 * // 創建實例 
				 * HttpServlet servlet = (HttpServlet)cls.newInstance(); 
				 * // 調用方法
				 * servlet.service(request, response);
				 */
				// 是否請求業務功能
				if(ServerContext.servletMapping.containsKey(request.getRequestLine())) {
					// 通過請求路徑找到對應的Servlet名字
					String className = ServerContext.servletMapping.get(request.getRequestLine());
					System.out.println("請求的Servlet名字:" + className);
					// 通過反射機制加載這個類
					Class cls = Class.forName(className);
					// 實例化這個Servlet
					System.out.println("實例化" + className + " Servlet");
					HttpServlet servlet = (HttpServlet)cls.newInstance();
					servlet.service(request, response);
					System.out.println("業務處理完畢!");
				} else {
					/**
					 * 查看請求的該頁面是否存在
					 */
					File file = new File("webapps" + request.getRequestLine());
					if(file.exists()) {
						System.out.println("該文件存在!");
						/**
						 *  響應客戶端
						 */
						forward(request.getRequestLine(), request, response);
						
					} else {
						// 設置狀態代碼
						response.setStatusCode(HttpContext.STATUS_CODE_NOT_FOUND);
						forward(File.separator + "global" + File.separator + "404.html",
								request, response);
					}
					
				}
				
			} catch (Exception e) {
				try {
					response = new HttpResponse(socket.getOutputStream());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				response.setStatusCode(HttpContext.STATUS_CODE_ERROR);
				forward(File.separator + "global" + File.separator + "500.html",
						request, response);
//				e.printStackTrace();
			} finally {
				// 處理客戶端段開連接後的操作
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 跳轉頁面
		 * @param filePath
		 * @param response
		 */
		private void forward(String path, HttpRequest request, HttpResponse response) {
			try {
				File file = new File("webapps" + path);
				/**
				 *  響應客戶端
				 */
				// 設置響應頭信息
				// 獲取文件的後綴
				String name = file.getName().substring(file.getName().lastIndexOf(".") + 1);
				// 根據後綴找到對應的Content-Type的值
				String contentType = HttpContext.getContentTypeByMime(name);
				// 設置響應頭Content-Type, Content-Length
				response.setContentLength((int)file.length());
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
}
