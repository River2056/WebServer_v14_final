package com.tedu.servlets;

import java.io.File;

import com.tedu.core.HttpContext;
import com.tedu.http.HttpRequest;
import com.tedu.http.HttpResponse;

/**
 * �Ҧ�Servlet���W��. �w�qServlet�������ƪ��\��
 * @author User
 *
 */
public abstract class HttpServlet {
	public abstract void service(HttpRequest request, HttpResponse response);
	
	protected void forward(String path, HttpRequest request, HttpResponse response) {
		try {
			File file = new File("webapps" + path);
			/**
			 * �T���Ȥ��
			 */
			// �]�m�T���Y�H��
			// �����󪺫��
			String name = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			// �ھګ���������Content-Type����
			String contentType = HttpContext.getContentTypeByMime(name);
			// �]�m�T���YContent-Type, Content-Length
			response.setContentLength((int) file.length());
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
