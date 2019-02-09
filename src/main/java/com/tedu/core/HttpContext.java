package com.tedu.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Http��ĳ�������e�w�q
 * @author User
 *
 */
public class HttpContext {
	public static final int CR = 13;
	public static final int LF = 10;
	
	/**
	 * �T�����A�N�X�w�q
	 */
	/**
	 * �N�X: ���`
	 */
	public static final int STATUS_CODE_OK = 200;
	/**
	 * �N�X: �����
	 */
	public static final int STATUS_CODE_NOT_FOUND = 404;
	/**
	 * �N�X: ���~
	 */
	public static final int STATUS_CODE_ERROR = 500;
	
	/**
	 * ���A�N�X�P�y�z���M�g
	 */
	private static Map<Integer, String> code_reason_mapping;
	
	
	/**
	 * ���������M�g
	 * key: ��������
	 * value: Content-Type��������������
	 */
	private static Map<String, String> mimeTypeMapping;
	
	static {
		// HttpContext�[�����ɭԶ}�l��l��
		System.out.println("�R�A���ժ�l�Ƥ�k...");
		init();
		System.out.println("��l�Ƨ���!");
	}
	
	/**
	 * ��l��HttpContext�������e
	 */
	public static void init() {
		// 1 ��l�Ƥ��������M�g
//		System.out.println("�եΪ�l��HashMap...");
		initMimeTypeMapping();
//		System.out.println("Map��l�Ƨ���!");
		// 2 ��l�ƪ��A�N�X�P�y�z�M�g
		initCodeReasonMapping();
	}
	
	/**
	 * ��l�ƪ��A�N�X�P�y�z�M�g
	 */
	private static void initCodeReasonMapping() {
		code_reason_mapping = new HashMap<Integer, String>();
		code_reason_mapping.put(STATUS_CODE_OK, "OK");
		code_reason_mapping.put(STATUS_CODE_NOT_FOUND, "NOT FOUND");
		code_reason_mapping.put(STATUS_CODE_ERROR, "ERROR");
		
	}
	
	/**
	 * ��l�Ƥ��������M�g
	 */
	private static void initMimeTypeMapping() {
		System.out.println("�եΤ���������l�Ƥ�k......");
		mimeTypeMapping = new HashMap<String, String>();
		/**
		 * �ѪRconf/web.xml���� 
		 * �N�Ҧ���<mime-mapping>����Ū���X�� 
		 * �N�䤤���l����<extension>�������e�@��key
		 * �N�䤤���l����<mime-type>�������e�@��value 
		 * �s�J��mimeTypeMapping���Y�i
		 */
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream("conf" + File.separator + "web.xml"));
			// done reading the whole file
			
			Element root = doc.getRootElement();
			System.out.println(root.getName()); // <web-app>
			
			List<Element> eMap = root.elements("mime-mapping"); // get<mime-mapping>
			
			for(Element elem : eMap) { // <entension>, <mime-type>
				String key = elem.elementText("extension"); // get text between <extension> tabs
				String value = elem.elementText("mime-type"); // get text between <mime-type> tabs
				
				mimeTypeMapping.put(key, value); // put in mimeTypeMapping HashMap
			}
			System.out.println("Map size: " + mimeTypeMapping.size());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
//		mimeTypeMapping.put("html", "text/html");
//		mimeTypeMapping.put("jpg", "image/jpg");
//		mimeTypeMapping.put("gif", "image/gif");
//		mimeTypeMapping.put("png", "image/png");
//		mimeTypeMapping.put("css", "text/css");
		System.out.println("Done!");
		System.out.println("mimeTypeMapping: " + mimeTypeMapping);
		
	}
	
	/**
	 * ������w�����A�N�X�ҹ��������A�y�z
	 * @param code
	 * @return
	 */
	public static String getReasonByCode(int code) {
		return code_reason_mapping.get(code);
	}
	
	/**
	 * �ھڵ��w�������������������Content-Type����
	 * @param mime
	 * @return
	 */
	public static String getContentTypeByMime(String mime) {
		return mimeTypeMapping.get(mime);
	}
	
	public static void main(String[] args) {
		System.out.println("1");
		String contentType = HttpContext.getContentTypeByMime("iso");
		System.out.println("2");
		System.out.println("contentType: " + contentType);
		System.out.println("3");
	}
	
}
