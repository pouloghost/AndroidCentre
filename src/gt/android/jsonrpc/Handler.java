package gt.android.jsonrpc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.server.Dispatcher;

public class Handler implements Runnable {
	private Socket socket;
	private InputStreamReader isr = null;
	private OutputStreamWriter osw = null;
	private String SERVER = "Android";
	private Dispatcher dispatcher = null;

	public Handler(Socket client, Dispatcher dispatcher) {
		socket = client;
		this.dispatcher = dispatcher;
		try {
			isr = new InputStreamReader(socket.getInputStream());
			osw = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if (isr != null && osw != null) {
			String body;
			try {
				body = readJson();
				System.out.println(body);
				JSONRPC2Request request = JSONRPC2Request.parse(body);
				JSONRPC2Response response = dispatcher.process(request, null);
				String res = response.toJSONString();
				System.out.println(res);
				sendResponse(res);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONRPC2ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					isr.close();
					osw.close();
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private String readline(InputStreamReader isr) throws IOException {
		char c = 0;
		StringBuilder sb = new StringBuilder();
		boolean eol = false;
		int len = 0;
		while (true) {
			c = (char) isr.read();
			++len;
			if (c == '\n') {
				if (1 == len) {// \n left from last line \r\n
					continue;
				} else {
					eol = true;
				}
			}
			if (c == '\r') {
				eol = true;
			}
			if (c == (char) -1) {// no more
				--len;
				eol = true;
			}
			if (eol) {// end
				break;
			}
			sb.append(c);
		}
		if (1 == len && -1 == c) {
			return null;
		}
		return sb.toString();
	}

	private String readJson() throws NumberFormatException, IOException {
		String line = null;
		int len = 0;
		String body;

		while ((line = readline(isr)) != null) {
			if (line.length() == 0) {
				break;
			}
			if (line.startsWith("Content-Length")) {
				len = Integer.parseInt(line.split(":")[1].trim());
			}
		}
		char[] cbuf = new char[len + 1];
		isr.read(cbuf, 0, len + 1);
		body = String.copyValueOf(cbuf);
		return body;
	}

	private void applyHeader(int len) throws IOException {
		osw.append("HTTP/1.1 200 OK\r\n");
		osw.append("Content-Length: " + len + "\r\n");
		osw.append("Server: " + SERVER + "\r\n");
		osw.append("Date: " + new Date().toString() + "\r\n");
		osw.append("Content-Type: application/json-rpc\r\n");
		osw.append("\r\n");
	}

	private void sendResponse(String res) throws IOException {
		int resLen = res.getBytes().length;
		applyHeader(resLen);
		osw.append(res + "\r\n");
		osw.flush();
	}

}
