/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Control;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;


/**
 *
 * @author Victor
 */
public class SocketCliente implements Runnable{
	DataOutputStream dos;
	private Socket s;
	
	public SocketCliente(String host, int port, String file) {
		try {
			s = new Socket(host, port);
			sendFile(file);
		} catch (IOException e) {
		}		
	}
	
	private void sendFile(String file) throws IOException {
		dos = new DataOutputStream(s.getOutputStream());
		FileInputStream fis = new FileInputStream(file);
                File userfile = new File(file);
                dos.writeUTF(userfile.getName());
                Control.MainFrame.barra.setMaximum(fis.available());
		byte[] buffer = new byte[4096];
		int i = 0;
		while (fis.read(buffer) > 0) {
			dos.write(buffer);
		}
		fis.close();
		dos.close();
                userfile = null;
	}

    @Override
    public void run() {
        Control.MainFrame.barra.setValue(dos.size()-1000);
    }
}
