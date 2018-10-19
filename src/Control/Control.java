package Control;

import View.MainFrame;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Victor
 */
public class Control  extends Thread{
    public static MainFrame MainFrame;

    public static void fileChooser() {
        Control.MainFrame.barra.setValue(0);
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(MainFrame);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            MainFrame.fileNameLabel.setText(chooser.getSelectedFile().getName());    
            MainFrame.arquivo.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    public static void getIpList() {
         try {
            String ip = Control.MainFrame.tabelaips.getValueAt(Control.MainFrame.tabelaips.getSelectedRow(),0).toString();
            MainFrame.ip.setText(ip);   
        } catch (Exception e) {
            
        }

    }

    public static void openReceivedFile() {
        try {
            Desktop.getDesktop().open(new File("C:\\Received"));
        } catch (IOException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Control() {
        SetLookAndFeel("Windows");
        Control.MainFrame = new MainFrame(this);
        Control.MainFrame.setVisible(true);
        Control.MainFrame.setResizable(false);
        SetPositionSize("Center", 780, 520);
        try {
            MainFrame.meuip.setText(InetAddress.getLocalHost().toString());
        } catch (UnknownHostException ex) {
            
        }
        tableUpdater();
        run();
    }
    
    public void run(){
	try {
                SocketServer fs = new SocketServer(Integer.parseInt(MainFrame.portareceive.getText()));
		fs.start();  
            } catch (Exception e) {
                e.printStackTrace();
            }  
    }

    private void SetLookAndFeel(String name) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (name.equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
    private void SetPositionSize(String position, int x, int y){
        if(MainFrame != null){  
            if(position.equalsIgnoreCase("Center")){
                this.MainFrame.setLocationRelativeTo(null);
            }else{
                this.MainFrame.setLocationRelativeTo(MainFrame);
            }
            
            if(x > 0 && y > 0){
               this.MainFrame.setSize(x, y);
            }else{
                this.MainFrame.setSize(200, 200);
                System.err.println("You shouldn't set a window size as 0.\n>> Setted to default (200x, 200y)"); 
            }
        }else{
            System.err.println("MainFrame is not initialized.");
        }
        
    }
    
    public static void SocketCreate(){
        try {
            if(validIP(MainFrame.ip.getText())){
                
                SocketCliente fc = new SocketCliente(MainFrame.ip.getText(),Integer.parseInt(MainFrame.porta.getText()), MainFrame.arquivo.getText());
                Thread th = new Thread(fc);
                th.start();
            }else{
                System.err.println("IP Inválido");
            }
        } catch (Exception e) {
            
        }
    }
    
    public static boolean validIP(String ip) {
    if (ip == null || ip.isEmpty()) return false;
    ip = ip.trim();
    if ((ip.length() < 6) & (ip.length() > 15)) return false;

    try {
        Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    } catch (PatternSyntaxException ex) {
        return false;
    }
}
    
    public static ArrayList<String>checkHosts(String subnet, int size) throws UnknownHostException, IOException{
    int timeout=10;
    ArrayList<String> listaIp = null;
    
    listaIp = new ArrayList<>();
    for (int i=1;i<size;i++){
       Control.MainFrame.ip_atual.setText(""+(i+1));
       String host=subnet + "." + i;
       if (InetAddress.getByName(host).isReachable(timeout)){
           System.out.println(host + " is reachable");
           listaIp.add(host+"");
       }
   }
        return listaIp;
}
    
    public static void tableUpdater(){
        DefaultTableModel dtmIPS = (DefaultTableModel) Control.MainFrame.tabelaips.getModel();
        Control.MainFrame.tabelaips.setRowSorter(new TableRowSorter(dtmIPS));
        
        if(dtmIPS.getRowCount() > 0){ //Limpa a tabela;
              dtmIPS.setRowCount(0); // Define a contagem de linhas da Table para ZERO, se houver elementos na tabela eles serão descartados.
        }
        try {
             ArrayList<String> lista = null;
             lista = checkHosts("192.168.2", 10);
            
            for (String listaItera : lista) { // Atualiza a tabela
                Object[] dados = {listaItera};
                dtmIPS.addRow(dados);
            }
        } catch (IOException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
    }
}
