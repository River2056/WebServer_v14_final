package com.tedu.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

public class WebRegServlet extends HttpServlet {

	public void service(HttpRequest request, HttpResponse response) {
		System.out.println("-----WebRegServlet: �}�l���U!-----");
		String uname = request.getParameter("uname");
		String upwd = request.getParameter("upwd");
		String upwdconfirm = request.getParameter("upwdconfirm");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		System.out.println(uname + ", " + upwd + ", " + upwdconfirm + ", " + email + ", " + phone);
		if(!upwd.equals(upwdconfirm)) {
			forward(File.separator + "web" + File.separator + "web" + File.separator + "reg_fail.html", request, response);
		} else {
			BufferedReader br = null;
			String line = null;
			boolean infoRepeat = false;
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream("userinfo.txt"), "UTF-8"));
				while((line = br.readLine()) != null) {
					String[] data = line.split(", ");
					String username = data[0]; // �ˬd�Τ�W
					String uemail = data[3]; // �ˬd�Τ�email
					if(uname.equals(username) || email.equals(uemail)) { // �ˬd�Τ�W��email�O�_����
						System.out.println("�Τ�W��email����!!!");
						infoRepeat = true;
					}
					
				}
				
				if(infoRepeat) {
					// �Τ�W��email����, ����ܵ��U���ѭ���
					forward(File.separator + "web" + File.separator + "web" + File.separator + "reg_fail.html", request, response);
				} else {
					// ���S����, ���U���\, �g�J��T, �D��ܵ��U���\����
					// �g�J��T  ---> PrintWriter �g�J���@�� xxx, xxx, xxx, xxx, xxx
					PrintWriter pw = null;
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream("userinfo.txt", true), "UTF-8"), true);
						Date regDate = cal.getTime();
						String time = sdf.format(regDate);
						String userinfo = uname + ", " + upwd + ", " + upwdconfirm + ", " + email + ", " + phone + ", " + time;
						pw.println(userinfo);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						if(pw != null) {
							pw.close();
						}
					}
					forward(File.separator + "web" + File.separator + "web" + File.separator + "reg_success.html", request, response);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
