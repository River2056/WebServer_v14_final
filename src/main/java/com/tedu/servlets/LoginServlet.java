package com.tedu.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * 處理登錄業務
 * 
 * @author User
 *
 */
public class LoginServlet extends HttpServlet {
	public void service(HttpRequest request, HttpResponse response) {
		// 用戶回傳requestLine長這樣:
		// http:\\localhost:8080\myweb\login?username=River2056&password=kevin2056
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("LoginServlet: user input: " + username + ", " + password);

		// 判斷用戶輸入是否和現有用戶重複, 1: 讀取資料庫
		BufferedReader br = null;
		boolean login = false;
		try {

			br = new BufferedReader(new InputStreamReader(new FileInputStream("userdata.txt"), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] data = line.split(", ");
				String user = data[0];
				String pwd = data[1];
				System.out.println("LoginServlet: checking pair: " + user + ", " + pwd);
				if (username.equals(user) && password.equals(pwd)) {
					System.out.println("登入成功");
					login = true;
					break;
				}
			}

			// return login success page if login == true, else return login
			// fail page
			if (login) {
				// login_success
				forward(File.separator + "myweb" + File.separator + "login_success.html", request, response);
			} else {
				// login fail
				forward(File.separator + "myweb" + File.separator + "login_fail.html", request, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
