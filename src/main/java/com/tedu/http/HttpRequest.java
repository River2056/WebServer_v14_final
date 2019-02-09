package com.tedu.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tedu.core.HttpContext;
import com.tedu.core.ServerContext;

/**
 * 該類的每一個實例用於表示客戶端發送過來的一次HTTP請求內容
 * 這裡包含: 請求行信息, 消息頭, 消息正文
 * @author User
 *
 */
public class HttpRequest {
	/**
	 * 請求行中的內容
	 */
	// 請求的方法
	private String method;
	// 請求的資源路徑
	private String uri;
	// 請求使用的HTTP協議版本
	private String protocol;
	
	// 請求信息
	private String requestLine;
	// 請求所附帶的所有參數
	private Map<String, String> params = new HashMap<String, String>(); 
	
	/**
	 * 消息頭
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * 構造方法, 
	 * 通過給定的輸入流讀取客戶端發送過來的HTTP請求內容
	 * @param in
	 */
	public HttpRequest(InputStream in) {
		/**
		 * 解析分為三步
		 * 1: 解析請求行
		 * 2: 解析消息頭
		 * 3: 解析消息正文
		 */
		parseRequestLine(in);   
		parseHeaders(in);
		parseContent(in);
	}
	
	/**
	 * 解析消息正文
	 * @param in
	 */
	private void parseContent(InputStream in) {
		// 獲取消息頭中的Content-Type
		String contentType = this.headers.get("Content-Type");
		if (contentType != null && "application/x-www-form-urlencoded".equals(contentType)) {
			System.out.println("<parseform>解析表單數據...</parseform>");
			int contentLength = Integer.parseInt(this.headers.get("Content-Length")); // 獲取字節長度

			byte[] buf = new byte[contentLength];
			try {
				in.read(buf); // 讀取數組裡所有內如, 剛好是個String
				System.out.println("---用戶傳過來的註冊訊息: ---");
				String userdata = new String(buf); // username=TungChinChen&password=river2056&nickname=chenchen
				System.out.println("---未經編碼的新用戶註冊訊息: ---" + userdata);
				userdata = URLDecoder.decode(userdata, ServerContext.URIEncoding); // 如有中文先經過編碼還原
				System.out.println("---重新編碼的新用戶註冊訊息: " + userdata + "---");
				parseParams(userdata);
				
				System.out.println("解析消息正文完畢!");
				System.out.println("---Lambda expression println params key-value pairs---");
				this.params.forEach((k, v) -> System.out.println(k + ":" + v));
				System.out.println("---Lambda expression println finished---");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * 解析消息頭
	 * @param in
	 */
	private void parseHeaders(InputStream in) {
		/**
		 * 由於之前的方法已經從流中將請求行內容
		 * 讀取了, 所以從這個流中繼續讀取的就應當是
		 * 消息頭內容了.
		 * 
		 * 讀取若干行內容(CRLF結尾算一行)
		 * 每一行按照":"拆分成兩部分, 第一部分應當是消息頭的名子, 
		 * 而第二部分為對應的值將名子作為key, 值作為value存入到header
		 * 這個Map中保存.
		 */
		String test = readLine(in);
		System.out.println("test line: " + test);
		while(true) {
			String line = readLine(in);
			System.out.println("header line: " + line);
			// 單獨讀取到了CRLF
			if("".equals(line)) {
				break;
			}
			// 找到每條頭信息中":"的位置
			int index = line.indexOf(":");
			String name = line.substring(0, index).trim();
			String value = line.substring(index + 1).trim();
			this.headers.put(name, value);
			
		}
		System.out.println("消息頭解析完畢");
		System.out.println("---Lambda expression start--- ");
		this.headers.forEach((k, v) -> System.out.println(k + ":" + v)); // 輸出每個頭
		System.out.println("---Lambda expression end---");
		Set<Entry<String, String>> headerSet = this.headers.entrySet();
		System.out.println("---Enhanced for each loop println---");
		for(Entry<String, String> header : headerSet) {
			String line = header.getKey() + ":" + header.getValue();
			System.out.println("headers map: " + line);
		}
		System.out.println("---Enhanced for each loop end---");
		
	}
	
	/**
	 * 解析請求行
	 * @param in
	 */
	private void parseRequestLine(InputStream in) {
		/**
		 * 解析請求行大致步驟:
		 * 1: 通過輸入流讀取一行字符串以CRLF結尾
		 * 		CR: 迴車, ASC編碼對應為13
		 * 		LF: 換行, ASC編碼對應為10
		 * 		CRLF : 13 10  c1 = 13  c2 = 10
		 * 2: 將讀取的請求行內容按照空格拆分
		 * 3: 將拆分出來的三個部分分別對應的設置到屬性method, uri, protocol上
		 */
		try {
			
			String line = readLine(in);
			System.out.println("browser requestline: " + line);
			
			String[] requestData = line.split("\\s");
//			for(String str : requestData) {
//				System.out.println("str: " + str);
//			}
			// [0] = method, [1] = uri, [2] = protocol
			this.method = requestData[0];
			this.uri = requestData[1];
			parseUri();
			this.protocol = requestData[2];
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析URI
	 * URI可能會包含客戶端傳過來的數據
	 * 所以要對它進行解析
	 * 例如:
	 * uri:/myweb/reg?user=fancq&pwd=123456
	 * 或
	 * uri:/myweb/reg.html
	 * 對於第一種情況, 需要將?左邊的內容賦值給requestLine這個屬性, 
	 * 而右面的內容則每個參數都存入params這個map中
	 * 而第二種情況由於沒有參數, 那麼直接將uri賦值給requestLine即可.
	 */
	private void parseUri() {
		String line = getUri();
		System.out.println("line of uri: " + line);
		int mark = line.indexOf("?");
		System.out.println("mark: " + mark);
		if (mark > 0) {
			requestLine = line.substring(0, mark);
			String message = line.substring(mark + 1);
			System.out.println("message: " + message);
			parseParams(message);

		} else {
			requestLine = line;
			System.out.println("requestLine: " + requestLine);
		}
		System.out.println("解析uri完畢!");
	}

	/**
	 * 解析瀏覽器發送過來的參數
	 * @param line
	 */
	private void parseParams(String line) {
		String[] data = line.split("&"); // [username=TungChinChen] [password=river2056] [nickname=chenchen]
		for(String uline : data) {
			String[] udata = uline.split("="); // [username] [TungChinChen]
			String key = udata[0];
			String value = udata[1];
			if(value.length() >= 0) {
				this.params.put(key, value);
			} else if(value.length() <= 0) {
				this.params.put(key, "");
			}
		}
	}
	
	private String readLine(InputStream in) {
		StringBuilder builder = new StringBuilder();
		try {
			int d = -1;
			char c1 = 0, c2 = 0;
			while((d = in.read()) != -1) {
				c2 = (char)d;
				if(c1 == HttpContext.CR && c2 == HttpContext.LF) {
					break;
				}
				builder.append(c2);
				c1 = c2;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString().trim();
	}
	
	/**
	 * 根據給定的參數名獲取參數的值
	 * @param name
	 * @return
	 */
	public String getParameter(String name) {
		return params.get(name);
	}
	
	public String getRequestLine() {
		return requestLine;
	}

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public String getProtocol() {
		return protocol;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	
	
}
