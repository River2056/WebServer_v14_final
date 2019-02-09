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
 * Http協議相關內容定義
 * @author User
 *
 */
public class HttpContext {
	public static final int CR = 13;
	public static final int LF = 10;
	
	/**
	 * 響應狀態代碼定義
	 */
	/**
	 * 代碼: 正常
	 */
	public static final int STATUS_CODE_OK = 200;
	/**
	 * 代碼: 未找到
	 */
	public static final int STATUS_CODE_NOT_FOUND = 404;
	/**
	 * 代碼: 錯誤
	 */
	public static final int STATUS_CODE_ERROR = 500;
	
	/**
	 * 狀態代碼與描述的映射
	 */
	private static Map<Integer, String> code_reason_mapping;
	
	
	/**
	 * 介質類型映射
	 * key: 介質類型
	 * value: Content-Type對應該類型的值
	 */
	private static Map<String, String> mimeTypeMapping;
	
	static {
		// HttpContext加載的時候開始初始化
		System.out.println("靜態塊調初始化方法...");
		init();
		System.out.println("初始化完成!");
	}
	
	/**
	 * 初始化HttpContext相關內容
	 */
	public static void init() {
		// 1 初始化介質類型映射
//		System.out.println("調用初始化HashMap...");
		initMimeTypeMapping();
//		System.out.println("Map初始化完成!");
		// 2 初始化狀態代碼與描述映射
		initCodeReasonMapping();
	}
	
	/**
	 * 初始化狀態代碼與描述映射
	 */
	private static void initCodeReasonMapping() {
		code_reason_mapping = new HashMap<Integer, String>();
		code_reason_mapping.put(STATUS_CODE_OK, "OK");
		code_reason_mapping.put(STATUS_CODE_NOT_FOUND, "NOT FOUND");
		code_reason_mapping.put(STATUS_CODE_ERROR, "ERROR");
		
	}
	
	/**
	 * 初始化介質類型映射
	 */
	private static void initMimeTypeMapping() {
		System.out.println("調用介質類型初始化方法......");
		mimeTypeMapping = new HashMap<String, String>();
		/**
		 * 解析conf/web.xml文檔 
		 * 將所有的<mime-mapping>標籤讀取出來 
		 * 將其中的子標籤<extension>中的內容作為key
		 * 將其中的子標籤<mime-type>中的內容作為value 
		 * 存入到mimeTypeMapping中即可
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
	 * 獲取給定的狀態代碼所對應的狀態描述
	 * @param code
	 * @return
	 */
	public static String getReasonByCode(int code) {
		return code_reason_mapping.get(code);
	}
	
	/**
	 * 根據給定的介質類型獲取對應的Content-Type的值
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
