
package mp3player;
import java.awt.Color;
import mp3player.Login;
import java.awt.GraphicsConfiguration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.*;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import data.ConnectData;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 *
 * @author ali
 */
public class playmp3 extends javax.swing.JFrame {
    Playlist pl = new Playlist();
    ArrayList updateList = new ArrayList();
    javazoom.jl.player.Player player;
    File simpan;
    playmp3() {
        initComponents();
       this.setIconImage(new ImageIcon(getClass().getResource("music-icon.png")).getImage());  
        playlistevent();
       
//jLabel15 and jLabel16
    }
     
void updateList() {
        updateList = pl.getListSong();
        DefaultListModel model =  new DefaultListModel();
        for (int i = 0; i < updateList.size(); i++) {
            int j = i + 1;
            model.add(i, j + " | " + ((File) updateList.get(i)).getName());
        }
        jPlaylist.setModel(model);

    }


//panel kontrol

void add() { //Thêm thông báo trùng lặp
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

    int result = fileChooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();

        if (pl.ls.contains(selectedFile)) {
            int option = JOptionPane.showConfirmDialog(this, "Bài hát đã tồn tại. Bạn có muốn thay thế nó không?",
                    "Tệp đã tồn tại", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {

                int index = pl.ls.indexOf(selectedFile);
                pl.ls.set(index, selectedFile);
            }
        } else {
            pl.ls.add(selectedFile);
            updateList();
        }
    }
}


void remove(){
    try{
        int akandihapus = jPlaylist.getLeadSelectionIndex();
        pl.ls.remove(akandihapus);
        updateList();
    }catch(Exception e){
    }
}

void up(){
    try{
        int s1 = jPlaylist.getLeadSelectionIndex();
        simpan = (File) pl.ls.get(s1);
        pl.ls.remove(s1);
        pl.ls.add(s1 - 1, simpan );
        updateList();
        jPlaylist.setSelectedIndex(s1-1);
    }catch(Exception e){
    }
}

void down(){
    try{
        int s1 = jPlaylist.getLeadSelectionIndex();
        simpan = (File) pl.ls.get(s1);
        pl.ls.remove(s1);
        pl.ls.add(s1 + 1, simpan );
        updateList();
        jPlaylist.setSelectedIndex(s1+1);
    }catch(Exception e){
    }
}

void open(){
    pl.openPls(this);
    updateList();
}

void save(){
    pl.saveAsPlaylist(this);
    updateList();
}

File play1;
static int a = 0;

void putar(){
    if(a==0){
        try{
            int p1 = jPlaylist.getSelectedIndex();
            play1 = (File) this.updateList.get(p1);
            FileInputStream fis = new FileInputStream(play1);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new javazoom.jl.player.Player(bis);
            a =1;
        }catch(Exception e){
            System.out.println("Problem playing file");
            System.out.println(e);
        }
        
        new Thread(){
            @Override
            public void run(){
                try{
                    player.play();
                
            }catch (Exception e){
            }
        }
    }.start();
    }else{
        player.close();
        
        a=0;
        putar();
    }
}


File sa;
void next(){
    if(a==0){
        try{
            int s1 = jPlaylist.getSelectedIndex() +1;
            sa = (File) this.pl.ls.get(s1);
            FileInputStream fis = new FileInputStream(sa);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new javazoom.jl.player.Player(bis);
            a =1;
            jPlaylist.setSelectedIndex(s1);
        }catch(Exception e){
            System.out.println("Problem playing file");
            System.out.println(e);
        }
        
        new Thread(){
            @Override
            public void run(){
                try{
                    player.play();
                
            }catch (Exception e){
            }
        }
    }.start();
    }else{
        player.close();
        a=0;
        next();
    }

}

void previous(){
    if(a==0){
        try{
            int s1 = jPlaylist.getSelectedIndex() -1;
            sa = (File) this.pl.ls.get(s1);
            FileInputStream fis = new FileInputStream(sa);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new javazoom.jl.player.Player(bis);
            a =1;
            jPlaylist.setSelectedIndex(s1);
        }catch(Exception e){
            System.out.println("Problem playing file");
            System.out.println(e);
        }
        
        new Thread(){
            @Override
            public void run(){
                try{
                    player.play();
                
            }catch (Exception e){
            }
        }
    }.start();
        
    }else{
        player.close();
        a=0;
        previous();
    }
}
void playlistevent() {//MouseEvent cho Playlist
    jPlaylist.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                int index = jPlaylist.getSelectedIndex();
                if (index >= 0) {
                    putar();
                }
            }
        }
    });

    jPlaylist.addMouseMotionListener(new MouseMotionAdapter() {
        @Override
        public void mouseMoved(MouseEvent e) {
            int index = jPlaylist.locationToIndex(e.getPoint());
            jPlaylist.clearSelection(); 

            if (index >= 0) {
                jPlaylist.setSelectedIndex(index);
            }
        }

        public void mouseExited(MouseEvent e) {
            jPlaylist.clearSelection(); 
        }
    });
    
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        InforPage = new javax.swing.JPanel();
        CloseInfor = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        HMenu = new javax.swing.JPanel();
        CloseMenu = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        logoutForm = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPlaylist = new javax.swing.JList<>();
        jButton3 = new javax.swing.JButton();
        ply = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        OpenInfor = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        OpenMenu = new javax.swing.JLabel();

        jButton2.setText("jButton2");

        jButton6.setText("jButton6");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Mp3 Player");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        InforPage.setBackground(new java.awt.Color(204, 255, 204));
        InforPage.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        CloseInfor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CloseInforMouseClicked(evt);
            }
        });
        InforPage.add(CloseInfor, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setText("Thông tin");
        InforPage.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 120, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Người dùng");
        InforPage.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("SongSync");
        InforPage.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, 130, -1));

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setText("Mô tả: Tận hưởng âm nhạc của riêng bạn");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, 240, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel11.setText("Giới thiệu:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 110, -1));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel12.setText("cập nhật");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, 110, -1));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel13.setText("Mật khẩu:");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 110, -1));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel14.setText("Tên tài khoản: ");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 110, -1));

        jTextField1.setText("cập nhật");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel2.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 20, 140, -1));

        jTextField2.setText("cập nhật");
        jPanel2.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 140, 20));

        InforPage.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 310, 130));
        searchfield = new PlaceholderTextField("Search");//PlaceHolder
        searchfield.getDocument().addDocumentListener(new DocumentListener() {//Tính năng tìm Kiếm
            @Override
            public void insertUpdate(DocumentEvent e) {
            	updatePlaylist();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	updatePlaylist();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {        
            }
        });
        
        jPanel3.setBackground(new java.awt.Color(204, 204, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel8.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel8.setText("Ngày ra đời: 4/1/2024");
        jPanel3.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 240, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel9.setText("Mô tả: Tận hưởng âm hưởng của riêng bạn");
        jPanel3.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 300, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel10.setText("Phiên bản: 1.0");
        jPanel3.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 240, -1));

        InforPage.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 310, 120));

        getContentPane().add(InforPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 0, 460));

        HMenu.setBackground(new java.awt.Color(204, 255, 255));
        HMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        CloseMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/cross.png"))); // NOI18N
        CloseMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CloseMenuMouseClicked(evt);
            }
        });
        HMenu.add(CloseMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 0, -1, -1));

        jLabel1.setText("Logo");
        HMenu.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 100, 40));

        logoutForm.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        logoutForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/log-out.png"))); // NOI18N
        logoutForm.setText("Log out");
        logoutForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutFormMouseClicked(evt);
            }
        });
        HMenu.add(logoutForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 480, 90, -1));
        HMenu.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 140, 50));

        getContentPane().add(HMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 530));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_add.png"))); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        getContentPane().add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 370, 80, -1));

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_remove.png"))); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        getContentPane().add(btnRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 80, -1));

        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_up.png"))); // NOI18N
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });
        getContentPane().add(btnUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 370, 70, -1));

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_down.png"))); // NOI18N
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });
        getContentPane().add(btnDown, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 370, 70, -1));

        jScrollPane1.setViewportView(jPlaylist);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 90, 900, 270));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/fast-backward.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 480, 60, -1));

        ply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/play-button.png"))); // NOI18N
        ply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plyActionPerformed(evt);
            }
        });
        getContentPane().add(ply, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 480, 70, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/fast-forward-button.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 480, 50, -1));

        stop.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pause-button.png"))); // NOI18N
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        getContentPane().add(stop, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 480, -1, -1));

        OpenInfor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/information.png"))); // NOI18N
        OpenInfor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OpenInforMouseClicked(evt);
            }
        });
        getContentPane().add(OpenInfor, new org.netbeans.lib.awtextra.AbsoluteConstraints(1020, 0, 30, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Danh sách bài hát");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 170, 40));

        OpenMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/menu.png"))); // NOI18N
        OpenMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OpenMenuMouseClicked(evt);
            }
        });
        getContentPane().add(OpenMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 30));
        getContentPane().add(searchfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 30, 540, 40));

        setSize(new java.awt.Dimension(1075, 563));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void updatePlaylist() {//Tính năng tìm kiếm
        String searchTerm = searchfield.getText().toLowerCase(); 
        DefaultListModel model = new DefaultListModel();

        for (int i = 0; i < updateList.size(); i++) {
            File file = (File) updateList.get(i);
            String itemName = file.getName();

            if (itemName.toLowerCase().contains(searchTerm)) {
                model.addElement((i + 1) + " | " + itemName);
            }
        }

        jPlaylist.setModel(model);
    }


    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
         add();
    }//GEN-LAST:event_btnAddActionPerformed

    private void plyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plyActionPerformed
putar();   
    
ply.setBackground(Color.GREEN);
stop.setBackground(Color.white);// TODO add your handling code here:
    }//GEN-LAST:event_plyActionPerformed
    
    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
      remove();  // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveActionPerformed
   
    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
    up();    // TODO add your handling code here:
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
down();        // TODO add your handling code here:
    }//GEN-LAST:event_btnDownActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
previous();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
next();       // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
player.close();    
stop.setBackground(Color.red);
ply.setBackground(Color.white);
// TODO add your handling code here:
    }//GEN-LAST:event_stopActionPerformed

    private void CloseMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseMenuMouseClicked
CloseMenu();        // TODO add your handling code here:
    }//GEN-LAST:event_CloseMenuMouseClicked

    private void OpenMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_OpenMenuMouseClicked
OpenMenu();        // TODO add your handling code here:
    }//GEN-LAST:event_OpenMenuMouseClicked

    private void CloseInforMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CloseInforMouseClicked
CloseInfor();        // TODO add your handling code here:
    }//GEN-LAST:event_CloseInforMouseClicked

    private void OpenInforMouseClicked(java.awt.event.MouseEvent evt) {
    	 
	//GEN-FIRST:event_OpenInforMouseClicked
OpenInfor();      
// TODO add your handling code here:
    }//GEN-LAST:event_OpenInforMouseClicked

    private void logoutFormMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logoutFormMouseClicked
        // TODO add your handling code here:
        Login login = new Login(); 
        login.setVisible(true);
        this.hide();
    }//GEN-LAST:event_logoutFormMouseClicked

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
     
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new playmp3().setVisible(true);
            }
        });    
    }

   private int WidthMenu =  160; 
    private int HeightMenu =  560;
    
    private int WidthInfor = 350; 
    private int HeightInfor = 460;
    //Mở Infor: 
    private void OpenInfor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i= 300; i <= WidthInfor; i++) {
                    InforPage.setSize(i, HeightInfor);
                }
            }
            
        }).start();
    }
     private void CloseInfor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i= WidthInfor; i >= 0; i -- ) {
                    InforPage.setSize(i, HeightInfor);
                }
            }
            
        }).start();
    }
    
    // Mở menu
    private void OpenMenu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i= 0; i <= WidthMenu; i++) {
                    HMenu.setSize(i, HeightMenu);
                }
            }    
        }).start();
    };
     private void CloseMenu() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i= WidthMenu; i > 0; i -- ) {
                    HMenu.setSize(i, HeightMenu);
                }
            }
            
        }).start();
    } 
     public playmp3(String tenTaiKhoan, String matKhau) {
         initComponents(); // Gọi constructor mặc định
         jTextField1.setText(tenTaiKhoan);
         jTextField2.setText(matKhau);
     }
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CloseInfor;
    private javax.swing.JLabel CloseMenu;
    private javax.swing.JPanel HMenu;
    private javax.swing.JPanel InforPage;
    private javax.swing.JLabel OpenInfor;
    private javax.swing.JLabel OpenMenu;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUp;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JList<String> jPlaylist;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel logoutForm;
    private javax.swing.JButton ply;
    private javax.swing.JTextField searchfield;
    private javax.swing.JButton stop;
    // End of variables declaration//GEN-END:variables
}
//Class PlaceHolder
class PlaceholderTextField extends JTextField implements FocusListener, CaretListener {
    private String placeholder;
    private boolean isPlaceholderVisible = true;
    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        addFocusListener(this);
        addCaretListener(this);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isPlaceholderVisible && (getText().isEmpty() || hasFocus())) {
            Font sansSerifFont = new Font(Font.SANS_SERIF, Font.ITALIC, getHeight() / 2);
            g.setFont(sansSerifFont);
            g.setColor(Color.GRAY);
            int textWidth = g.getFontMetrics().stringWidth(placeholder);
            int xShift = getWidth() / 30;
            int x = xShift;
            int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
            g.drawString(placeholder, x, y);
        }
    }
    @Override
    public void focusGained(FocusEvent e) {
        isPlaceholderVisible = false;
        repaint();
    }
    @Override
    public void focusLost(FocusEvent e) {
        if (getText().isEmpty()) {
            isPlaceholderVisible = true;
            repaint();
        }
    }
    @Override
    public void caretUpdate(CaretEvent e) {
        if (getText().isEmpty()) {
            isPlaceholderVisible = true;
        } else {
            isPlaceholderVisible = false;
        }
        repaint();
    }
}

//tình trạng : không dùng
class CustomDefaultTableModel extends DefaultTableModel {
    public CustomDefaultTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }
    @Override
    public int getRowCount() {
        int rowCount = super.getRowCount();
        for (int i = 0; i < super.getRowCount(); i++) {
            if (getValueAt(i, 0) == null) {
                rowCount--;
            }
        }
        return rowCount;
    }
}


