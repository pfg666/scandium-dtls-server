package org.eclipse.californium.scandium.examples;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.function.Supplier;

/**
 * We use this class to avoid having to restart the vm (which is can be a slow process). 
 */
public class ThreadStarter {
	
	private Supplier<Thread> supplier;
	private DatagramSocket socket;

	public ThreadStarter(Supplier<Thread> supplier, String ipPort) throws SocketException {
		String[] addr = ipPort.split("\\:");
		InetSocketAddress address = new InetSocketAddress(addr[0], Integer.valueOf(addr[1]));
		this.supplier = supplier;
		socket = new DatagramSocket(address);
		socket.setSoTimeout(60000);
	}
	
	public void run() {
		byte[] buf = new byte[100000];
		Thread thread = null;
		while (true) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				if (thread != null) {
					thread.interrupt();
				}
				thread = supplier.get();
				thread.start();
			} catch (IOException e) {
				if (thread != null) {
					thread.interrupt();
				}
				System.out.println("Exiting");
				return;
			}
		}
	}
}