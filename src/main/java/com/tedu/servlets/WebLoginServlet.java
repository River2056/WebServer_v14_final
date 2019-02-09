package com.tedu.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

public class WebLoginServlet extends HttpServlet {

	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("-----�i�J�n�J�B�z�~��...-----");
		
		String name = request.getParameter("lname");
		String pwd = request.getParameter("lwd");
		
		BufferedReader br = null;
		String userinfo = null;
		boolean login = false;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream("userinfo.txt"), "UTF-8"));
			while((userinfo = br.readLine()) != null) {
				String[] data = userinfo.split(", ");
				String username = data[0];
				String password = data[1];
				if(name.equals(username) && pwd.equals(password)) { // ��令�\
					System.out.println("-----�Τ�n�J��T���T-----");
					login = true;
					break;
				}
			}
			
			if(login) {
				// �n�J���\
				forward(File.separator + "web" + File.separator + "web" + File.separator + "login_success.html", request, response);
			} else{
				// �n�J����
				forward(File.separator + "web" + File.separator + "web" + File.separator + "login_fail.html", request, response);
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
