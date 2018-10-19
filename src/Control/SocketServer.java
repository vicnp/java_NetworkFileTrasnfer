/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Victor
 */
public class SocketServer extends Thread {
	
	private ServerSocket ss;
	
	public SocketServer(int port) {
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (true) {
			try {
				Socket clientSock = ss.accept();
                                if(clientSock.isConnected()){
                                    Control.MainFrame.status.setText("Conectado:"+clientSock.getInetAddress());
                                }else{
                                     Control.MainFrame.status.setText("Desconectado.");
                                }
                                
				saveFile(clientSock);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveFile(Socket clientSock) throws IOException {
		DataInputStream dis = new DataInputStream(clientSock.getInputStream());
                String fileName = dis.readUTF();
                System.err.println(fileName);
		FileOutputStream fos = new FileOutputStream("C:\\Downloads\\"+fileName);
                Control.MainFrame.FileNameReceived.setText(fileName);
		byte[] buffer = new byte[4096]; 
		
		int read = 0;
		double totalRead = 0;
		while((read = dis.read(buffer)) > 0) {
			totalRead += read;
			System.out.println("read " + totalRead + " bytes.");
                        Control.MainFrame.sizeReceived.setText(totalRead/1024/1024+"");
			fos.write(buffer, 0, read);
		}
		
		fos.close();
		dis.close();
	}

}