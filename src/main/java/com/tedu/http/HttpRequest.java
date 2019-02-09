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
 * �������C�@�ӹ�ҥΩ��ܫȤ�ݵo�e�L�Ӫ��@��HTTP�ШD���e
 * �o�̥]�t: �ШD��H��, �����Y, ��������
 * @author User
 *
 */
public class HttpRequest {
	/**
	 * �ШD�椤�����e
	 */
	// �ШD����k
	private String method;
	// �ШD���귽���|
	private String uri;
	// �ШD�ϥΪ�HTTP��ĳ����
	private String protocol;
	
	// �ШD�H��
	private String requestLine;
	// �ШD�Ҫ��a���Ҧ��Ѽ�
	private Map<String, String> params = new HashMap<String, String>(); 
	
	/**
	 * �����Y
	 */
	private Map<String, String> headers = new HashMap<String, String>();
	
	/**
	 * �c�y��k, 
	 * �q�L���w����J�yŪ���Ȥ�ݵo�e�L�Ӫ�HTTP�ШD���e
	 * @param in
	 */
	public HttpRequest(InputStream in) {
		/**
		 * �ѪR�����T�B
		 * 1: �ѪR�ШD��
		 * 2: �ѪR�����Y
		 * 3: �ѪR��������
		 */
		parseRequestLine(in);   
		parseHeaders(in);
		parseContent(in);
	}
	
	/**
	 * �ѪR��������
	 * @param in
	 */
	private void parseContent(InputStream in) {
		// ��������Y����Content-Type
		String contentType = this.headers.get("Content-Type");
		if (contentType != null && "application/x-www-form-urlencoded".equals(contentType)) {
			System.out.println("<parseform>�ѪR���ƾ�...</parseform>");
			int contentLength = Integer.parseInt(this.headers.get("Content-Length")); // ����r�`����

			byte[] buf = new byte[contentLength];
			try {
				in.read(buf); // Ū���Ʋո̩Ҧ����p, ��n�O��String
				System.out.println("---�Τ�ǹL�Ӫ����U�T��: ---");
				String userdata = new String(buf); // username=TungChinChen&password=river2056&nickname=chenchen
				System.out.println("---���g�s�X���s�Τ���U�T��: ---" + userdata);
				userdata = URLDecoder.decode(userdata, ServerContext.URIEncoding); // �p��������g�L�s�X�٭�
				System.out.println("---���s�s�X���s�Τ���U�T��: " + userdata + "---");
				parseParams(userdata);
				
				System.out.println("�ѪR�������姹��!");
				System.out.println("---Lambda expression println params key-value pairs---");
				this.params.forEach((k, v) -> System.out.println(k + ":" + v));
				System.out.println("---Lambda expression println finished---");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	/**
	 * �ѪR�����Y
	 * @param in
	 */
	private void parseHeaders(InputStream in) {
		/**
		 * �ѩ󤧫e����k�w�g�q�y���N�ШD�椺�e
		 * Ū���F, �ҥH�q�o�Ӭy���~��Ū�����N����O
		 * �����Y���e�F.
		 * 
		 * Ū���Y�z�椺�e(CRLF������@��)
		 * �C�@�����":"������ⳡ��, �Ĥ@��������O�����Y���W�l, 
		 * �ӲĤG�������������ȱN�W�l�@��key, �ȧ@��value�s�J��header
		 * �o��Map���O�s.
		 */
		String test = readLine(in);
		System.out.println("test line: " + test);
		while(true) {
			String line = readLine(in);
			System.out.println("header line: " + line);
			// ��WŪ����FCRLF
			if("".equals(line)) {
				break;
			}
			// ���C���Y�H����":"����m
			int index = line.indexOf(":");
			String name = line.substring(0, index).trim();
			String value = line.substring(index + 1).trim();
			this.headers.put(name, value);
			
		}
		System.out.println("�����Y�ѪR����");
		System.out.println("---Lambda expression start--- ");
		this.headers.forEach((k, v) -> System.out.println(k + ":" + v)); // ��X�C���Y
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
	 * �ѪR�ШD��
	 * @param in
	 */
	private void parseRequestLine(InputStream in) {
		/**
		 * �ѪR�ШD��j�P�B�J:
		 * 1: �q�L��J�yŪ���@��r�Ŧ�HCRLF����
		 * 		CR: �j��, ASC�s�X������13
		 * 		LF: ����, ASC�s�X������10
		 * 		CRLF : 13 10  c1 = 13  c2 = 10
		 * 2: �NŪ�����ШD�椺�e���ӪŮ���
		 * 3: �N����X�Ӫ��T�ӳ������O�������]�m���ݩ�method, uri, protocol�W
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
	 * �ѪRURI
	 * URI�i��|�]�t�Ȥ�ݶǹL�Ӫ��ƾ�
	 * �ҥH�n�復�i��ѪR
	 * �Ҧp:
	 * uri:/myweb/reg?user=fancq&pwd=123456
	 * ��
	 * uri:/myweb/reg.html
	 * ���Ĥ@�ر��p, �ݭn�N?���䪺���e��ȵ�requestLine�o���ݩ�, 
	 * �ӥk�������e�h�C�ӰѼƳ��s�Jparams�o��map��
	 * �ӲĤG�ر��p�ѩ�S���Ѽ�, ���򪽱��Nuri��ȵ�requestLine�Y�i.
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
		System.out.println("�ѪRuri����!");
	}

	/**
	 * �ѪR�s�����o�e�L�Ӫ��Ѽ�
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
	 * �ھڵ��w���ѼƦW����Ѽƪ���
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
