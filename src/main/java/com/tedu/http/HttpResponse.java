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
 * 表示一個Http響應
 * @author User
 *
 */
public class HttpResponse {
	/**
	 * 用於向客戶端坐響應的輸出流.
	 * 它應該是ClientHandler通過Socket
	 * 獲取的輸出流
	 */
	private OutputStream out;
	/**
	 * 一個要響應給客戶端的文件資源
	 * 這個文件的數據最終會在響應正文中
	 * 以字節的形式發送給客戶端
	 */
	private File entity;
	/**
	 * 當前響應中包含的所有響應頭的信息
	 * key: 響應頭的名子
	 * value: 響應頭對應的值
	 * 例如:
	 * key:Content-Type
	 * value:text/html
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * 當前響應的狀態代碼
	 */
	private int statusCode = HttpContext.STATUS_CODE_OK;
	
	public HttpResponse(OutputStream out) {
		this.out = out;
	}
	
	/**
	 * 將該響應對象中的內容響應給客戶端
	 */
	public void flush() {
		/**
		 * 發送給客戶端的HTTP響應分為三步:
		 * 1: 發送狀態信息
		 * 2: 發送響應頭信息
		 * 3: 發送響應正文信息
		 */
		System.out.println("調了flush方法, 將響應三部分送出!");
		sendStatusLine();
		sendHeaders();
		sendContent();
		System.out.println("完成調動三個方法!");
		
	}
	
	/**
	 * 發送狀態行信息
	 */
	private void sendStatusLine() {
		String line = ServerContext.protocol + " " + statusCode + " " + HttpContext.getReasonByCode(statusCode);
		println(line);
		System.out.println("發送完狀態行: " + line);
	}
	
	/**
	 * 發送響應頭信息
	 */
	private void sendHeaders() {
		/**
		 * save everything in the headers HashMap, any headers needed to be sent in the future,
		 * just concat every key-value pairs in the HashMap
		 */
		System.out.println("發送響應頭...");
		Set<Entry<String, String>> headerSet = headers.entrySet();
		for(Entry<String, String> header : headerSet) {
			String line = header.getKey() + ":" + header.getValue();
			System.out.println("line: " + line);
			println(line); // 發送每一個頭訊息
		}
		println(""); // 單獨發送CRLF表示頭發送完畢
		System.out.println("發送完所有響應頭");
	}
	
	/**
	 * 發送響應正文信息
	 */
	private void sendContent() {
		FileInputStream fis = null;
		try {
			// 讀字節(你的html文件)
			fis = new FileInputStream(entity);
			int len = -1; // 文件末尾
			byte[] data = new byte[1024 * 10]; // 一次讀10KB
			while((len = fis.read(data)) != -1) { // 把讀到的資料數組寫出
				out.write(data, 0, len);
			}
			System.out.println("發送完所有響應正文");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null) { // 因為讀到末尾值為-1
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 向客戶端發送一行字符串
	 * 該字符串會通過ISO8859-1轉換為一組
	 * 字節並寫出. 寫出後會自動連續寫出CRLF
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
	 * 根據實體文件的名子獲取對應的介質類型
	 * Contene-Type使用的值
	 * 
	 * 臨時將該方法寫在response中. 實際上Content-Type的值應當是設置response內容
	 * 的時候一同設置的.
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
	 * 設置頭信息Content-Type對應的值
	 * @param contentType
	 */
	public void setContentType(String contentType) {
		this.headers.put("Content-Type", contentType);
	}
	/**
	 * 設置頭信息Content-Length
	 * @param length
	 */
	public void setContentLength(int length) {
		this.headers.put("Content-Length", length + "");
	}
	
	/**
	 * 設置狀態代碼
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
