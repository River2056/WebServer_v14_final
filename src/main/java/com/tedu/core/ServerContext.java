package com.tedu.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
/**
 * 服務器配置相關信息
 * @author User
 *
 */
public class ServerContext {
	public static String URIEncoding;
	public static int port;
	public static String protocol;
	public static int threadPool;
	
	public static Map<String, String> servletMapping;

	static {
		init();
	}

	private static void init() {
		initServletMapping();
		initServerInfo();
	}
	
	/**
	 * 加載conf/server.xml文件, 初始化相關信息
	 */
	private static void initServerInfo() {
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new InputStreamReader(new FileInputStream("conf/server.xml"), "UTF-8"));
			Element root = doc.getRootElement(); // get <server>
			Element connector = root.element("Connector"); // get <connector>
			URIEncoding = connector.attributeValue("URIEncoding");
			port = Integer.parseInt(connector.attributeValue("port"));
			protocol = connector.attributeValue("protocol");
			threadPool = Integer.parseInt(connector.attributeValue("threadPool"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化Servlet映射
	 */
	private static void initServletMapping() {
		servletMapping = new HashMap<String, String>();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(new InputStreamReader(new FileInputStream("conf/servletMapping.xml"), "UTF-8"));
			Element root = doc.getRootElement(); // get<mappings>
			List<Element> mapElem = root.elements();
			for(Element servMap : mapElem) {
				String key = servMap.attributeValue("uri");
				String value = servMap.attributeValue("classname");
				servletMapping.put(key, value);
			}
			System.out.println("ServerContext.servletMapping.size() = " + servletMapping.size());
			System.out.println("ServerContext servletMapping: " + servletMapping);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
