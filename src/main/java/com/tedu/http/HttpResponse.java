package com.tedu.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tedu.core.HttpContext;
import com.tedu.core.ServerContext;

/**
 * ��ܤ@��Http�T��
 * @author User
 *
 */
public class HttpResponse {
	/**
	 * �Ω�V�Ȥ�ݧ��T������X�y.
	 * �����ӬOClientHandler�q�LSocket
	 * �������X�y
	 */
	private OutputStream out;
	/**
	 * �@�ӭn�T�����Ȥ�ݪ����귽
	 * �o�Ӥ�󪺼ƾڳ̲׷|�b�T�����夤
	 * �H�r�`���Φ��o�e���Ȥ��
	 */
	private File entity;
	/**
	 * ��e�T�����]�t���Ҧ��T���Y���H��
	 * key: �T���Y���W�l
	 * value: �T���Y��������
	 * �Ҧp:
	 * key:Content-Type
	 * value:text/html
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * ��e�T�������A�N�X
	 */
	private int statusCode = HttpContext.STATUS_CODE_OK;
	
	public HttpResponse(OutputStream out) {
		this.out = out;
	}
	
	/**
	 * �N���T����H�������e�T�����Ȥ��
	 */
	public void flush() {
		/**
		 * �o�e���Ȥ�ݪ�HTTP�T�������T�B:
		 * 1: �o�e���A�H��
		 * 2: �o�e�T���Y�H��
		 * 3: �o�e�T������H��
		 */
		System.out.println("�դFflush��k, �N�T���T�����e�X!");
		sendStatusLine();
		sendHeaders();
		sendContent();
		System.out.println("�����հʤT�Ӥ�k!");
		
	}
	
	/**
	 * �o�e���A��H��
	 */
	private void sendStatusLine() {
		String line = ServerContext.protocol + " " + statusCode + " " + HttpContext.getReasonByCode(statusCode);
		println(line);
		System.out.println("�o�e�����A��: " + line);
	}
	
	/**
	 * �o�e�T���Y�H��
	 */
	private void sendHeaders() {
		/**
		 * save everything in the headers HashMap, any headers needed to be sent in the future,
		 * just concat every key-value pairs in the HashMap
		 */
		System.out.println("�o�e�T���Y...");
		Set<Entry<String, String>> headerSet = headers.entrySet();
		for(Entry<String, String> header : headerSet) {
			String line = header.getKey() + ":" + header.getValue();
			System.out.println("line: " + line);
			println(line); // �o�e�C�@���Y�T��
		}
		println(""); // ��W�o�eCRLF����Y�o�e����
		System.out.println("�o�e���Ҧ��T���Y");
	}
	
	/**
	 * �o�e�T������H��
	 */
	private void sendContent() {
		FileInputStream fis = null;
		try {
			// Ū�r�`(�A��html���)
			fis = new FileInputStream(entity);
			int len = -1; // ��󥽧�
			byte[] data = new byte[1024 * 10]; // �@��Ū10KB
			while((len = fis.read(data)) != -1) { // ��Ū�쪺��ƼƲռg�X
				out.write(data, 0, len);
			}
			System.out.println("�o�e���Ҧ��T������");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) { // �]��Ū�쥽���Ȭ�-1
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * �V�Ȥ�ݵo�e�@��r�Ŧ�
	 * �Ӧr�Ŧ�|�q�LISO8859-1�ഫ���@��
	 * �r�`�üg�X. �g�X��|�۰ʳs��g�XCRLF
	 * @param line
	 */
	private void println(String line) {
		try {
			out.write(line.getBytes("ISO8859-1"));
			out.write(HttpContext.CR);
			out.write(HttpContext.LF);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �ھڹ����󪺦W�l�����������������
	 * Contene-Type�ϥΪ���
	 * 
	 * �{�ɱN�Ӥ�k�g�bresponse��. ��ڤWContent-Type��������O�]�mresponse���e
	 * ���ɭԤ@�P�]�m��.
	 * @return
	 */
//	private String getMimeTypeByEntity() {
//		/**
//		 * html text/html
//		 * jpg image/jpg
//		 * png image/png
//		 * gif image/gif
//		 */
//		String fileType = entity.getName().substring(entity.getName().lastIndexOf(".") + 1);
//		if("html".equals(fileType)) {
//			return "text/" + fileType;
//		} else if("jpg".equals(fileType)) {
//			return "image/" + fileType;
//		} else if("png".equals(fileType)) {
//			return "image/" + fileType;
//		} else if("gif".equals(fileType)) {
//			return "image/" + fileType;
//		} else {
//			return "";
//		}
//	}
	
	/**
	 * �]�m�Y�H��Content-Type��������
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.headers.put("Content-Type", contentType);
	}
	/**
	 * �]�m�Y�H��Content-Length
	 * @param length
	 */
	public void setContentLength(int length) {
		this.headers.put("Content-Length", length + "");
	}
	
	/**
	 * �]�m���A�N�X
	 * @param code
	 */
	public void setStatusCode(int code) {
		this.statusCode = code;
	}
	
	public File getEntity() {
		return entity;
	}

	public void setEntity(File entity) {
		this.entity = entity;
	}
	
	
}
