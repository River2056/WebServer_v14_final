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
 * Web�A�ȺݥD��
 * @author User
 *
 */
public class WebServer {

	private ServerSocket server;
	
	private ExecutorService threadPool;
	
	public WebServer() {
		try {
			System.out.println("���b��l�ƪA�Ⱥ�...");
			server = new ServerSocket(ServerContext.port);
			threadPool = Executors.newFixedThreadPool(ServerContext.threadPool);
			System.out.println("�A�Ⱥݪ�l�Ƨ���!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		try {
			while(true) {
				System.out.println("���ݫȤ�ݳs��...");
				Socket socket = server.accept();
				System.out.println("�@�ӫȤ�ݳs���F!");
				
				ClientHandler handler = new ClientHandler(socket);
//				Thread t = new Thread(handler);
//				t.start();
				threadPool.execute(handler); // �N���ȥ浹�u�{���B�z
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
	 * �B�z�Ȥ�ݽШD�������T��
	 * @author User
	 *
	 */
	private class ClientHandler implements Runnable {
		private Socket socket;
		
		public ClientHandler(Socket socket) {
			this.socket = socket;
		}
		
		public void run() {
			System.out.println("�B�z�Ȥ�ݽШD!");
			HttpRequest request = null;
			HttpResponse response = null;

			try {
				// �ھڿ�J�y�ѪR�ШD
				request = new HttpRequest(socket.getInputStream());
				// �ھڿ�X�y�Ы��T����H
				response = new HttpResponse(socket.getOutputStream());
				// ����
				System.out.println("requestLine: " + request.getRequestLine());
				
				/**
				 * public class ServerContext public static Map<String, String> servletMapping; 
				 * servletMapping.put("/myweb/reg", "com.tedu.servlets.RegServlet");
				 * servletMapping.put("/myweb/login", "com.tedu.servlets.LoginServlet"); 
				 * 
				 * ���|: request.getRequestLine() // e.g. /myweb/reg 
				 * 
				 * // ���ˬdMap�̬O�_����������
				 * ServerContext.servletMapping.containsKey(request.getRequestLine())
				 * 
				 * // if true ==> 
				 * // �ʺA�[���� 
				 * Class cls = Class.forName(ServerContext.servletMapping.get(request.getRequestLine())) // �ھڦW�r������value // "com.tedu.servlets.RegServlet" 
				 * // �Ыع�� 
				 * HttpServlet servlet = (HttpServlet)cls.newInstance(); 
				 * // �եΤ�k
				 * servlet.service(request, response);
				 */
				// �O�_�ШD�~�ȥ\��
				if(ServerContext.servletMapping.containsKey(request.getRequestLine())) {
					// �q�L�ШD���|��������Servlet�W�r
					String className = ServerContext.servletMapping.get(request.getRequestLine());
					System.out.println("�ШD��Servlet�W�r:" + className);
					// �q�L�Ϯg����[���o����
					Class cls = Class.forName(className);
					// ��ҤƳo��Servlet
					System.out.println("��Ҥ�" + className + " Servlet");
					HttpServlet servlet = (HttpServlet)cls.newInstance();
					servlet.service(request, response);
					System.out.println("�~�ȳB�z����!");
				} else {
					/**
					 * �d�ݽШD���ӭ����O�_�s�b
					 */
					File file = new File("webapps" + request.getRequestLine());
					if(file.exists()) {
						System.out.println("�Ӥ��s�b!");
						/**
						 *  �T���Ȥ��
						 */
						forward(request.getRequestLine(), request, response);
						
					} else {
						// �]�m���A�N�X
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
				// �B�z�Ȥ�ݬq�}�s���᪺�ާ@
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * ���୶��
		 * @param filePath
		 * @param response
		 */
		private void forward(String path, HttpRequest request, HttpResponse response) {
			try {
				File file = new File("webapps" + path);
				/**
				 *  �T���Ȥ��
				 */
				// �]�m�T���Y�H��
				// �����󪺫��
				String name = file.getName().substring(file.getName().lastIndexOf(".") + 1);
				// �ھګ���������Content-Type����
				String contentType = HttpContext.getContentTypeByMime(name);
				// �]�m�T���YContent-Type, Content-Length
				response.setContentLength((int)file.length());
				response.setContentType(contentType);
				// �]�m�T������
				response.setEntity(file); // �N���n���Ƕi�h�@�~
				// �T���Ȥ��
				response.flush(); // �ھڥ��n���դ�k�����T��
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
