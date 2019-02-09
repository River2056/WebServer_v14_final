package com.tedu.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * 處理註冊業務
 * 
 * @author User
 *
 */
public class RegServlet extends HttpServlet {

	public void service(HttpRequest request, HttpResponse response) {

		System.out.println("開始註冊!");
		/**
		 * 首先獲取用戶輸入的註冊信息 將用戶註冊信息用下面的格式按行寫入到user.txt文件中保存 格式: username, password,
		 * nickname 例如: fancq, 123456, fanfan
		 */
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String nickname = request.getParameter("nickname");
		System.out.println(username + ", " + password + ", " + nickname);
		
		BufferedReader br = null;
		boolean nameRepeat = false; // 檢測用戶名是否重複
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("userdata.txt"), "UTF-8"));
			String line = null;
			while((line = br.readLine()) != null) {
				String[] data = line.split(", ");
				String user = data[0];
				if(username.equals(user)) { // 用戶名重複
					System.out.println("用戶名重複!!!");
					nameRepeat = true;
				}
			}
			
			if(nameRepeat) {
				System.out.println("請重新輸入用戶名!!");
				forward(File.separator + "myweb" + File.separator + "reg_fail.html" ,request, response);
			} else {
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("userdata.txt", true), "UTF-8"), true);

					pw.println(username + ", " + password + ", " + nickname);

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (pw != null) {
						pw.close();
					}
				}
				forward(File.separator + "myweb" + File.separator + "reg_success.html", request, response);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
