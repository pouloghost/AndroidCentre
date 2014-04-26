package gt.android.jsonrpc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.thetransactioncompany.jsonrpc2.server.Dispatcher;
import com.thetransactioncompany.jsonrpc2.server.NotificationHandler;
import com.thetransactioncompany.jsonrpc2.server.RequestHandler;

public class Server {
	private Dispatcher dispatcher = null;
	private ExecutorService pool = null;
	private ServerSocket socket = null;
	private ServiceThread service = null;

	public Server(int port) throws IOException {
		dispatcher = new Dispatcher();
		socket = new ServerSocket(port);
		pool = Executors.newCachedThreadPool();
	}

	public void startup() {
		service = new ServiceThread();
		new Thread(service).start();
	}

	public void shutdown() throws IOException {
		service.stop();
		socket.close();
	}

	public void register(RequestHandler handler) {
		dispatcher.register(handler);
	}

	public void register(NotificationHandler handler) {
		dispatcher.register(handler);
	}

	class ServiceThread implements Runnable {
		private boolean finish = false;

		public void stop() {
			finish = true;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!finish) {
				Socket client = null;
				try {
					client = socket.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (client != null) {
					System.out.println("connected to "
							+ client.getLocalAddress().toString());
					pool.execute(new Handler(client, dispatcher));
				}
			}
		}

	}
}
