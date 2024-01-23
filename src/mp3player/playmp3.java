
package mp3player;
import java.awt.Color;
import java.awt.Dimension;

import mp3player.Login;

import java.awt.GraphicsConfiguration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.*;
import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import data.ConnectData;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.JavaSoundAudioDevice;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.mail.internet.*;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;
import javax.activation.DataHandler; 
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.activation.DataHandler; 
import javax.activation.DataSource;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalSliderUI;


/**
 *
 * @author ali
 */
public class playmp3 extends javax.swing.JFrame {
    //volume variables
    private static final int MAX_VOLUME = 150;
    private static final float BOOST_THRESHOLD = 100.0f;
    private int duration;
    //default
    Playlist pl = new Playlist();
    ArrayList updateList = new ArrayList();
    javazoom.jl.player.Player player;
    File simpan;
    private int currentTime;
    playmp3() {
        initComponents();
        this.setIconImage(new ImageIcon(getClass().getResource("music-icon.png")).getImage());  
        playlistevent();
        addSearchFieldListener();
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

//panel control

private File lastChosenDirectory; 
//thêm và kiểm tra - đơn 
void add() {
    JFileChooser fileChooser = new JFileChooser();
    if (lastChosenDirectory != null) {
        fileChooser.setCurrentDirectory(lastChosenDirectory);
    }
    FileNameExtensionFilter audioFilter = new FileNameExtensionFilter("Audio Files", "mp3", "wav", "ogg");
    fileChooser.setFileFilter(audioFilter);
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        if (isAudioFile(selectedFile)) {
            lastChosenDirectory = selectedFile.getParentFile();
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
        } else {
            JOptionPane.showMessageDialog(this, "Định dạng tệp không hợp lệ: " + selectedFile.getName(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
//thêm từ thư mục 
void addAllFilesInDirectory() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    FileNameExtensionFilter audioFilter = new FileNameExtensionFilter("Audio Files", "mp3", "wav", "ogg");
    fileChooser.setFileFilter(audioFilter);
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedDirectory = fileChooser.getSelectedFile();
        File[] files = selectedDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    if (isAudioFile(file)) {
                        pl.ls.add(file);
                    } else {
                        JOptionPane.showMessageDialog(this, "Định dạng tệp không hợp lệ: " + file.getName(),
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            updateList();
        }
    }
}
//kiểm tra định dạng
private boolean isAudioFile(File file) {
    String fileName = file.getName().toLowerCase();
    return fileName.endsWith(".mp3") || fileName.endsWith(".wav") || fileName.endsWith(".ogg");
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
private boolean isPlaying = false;

void putar() {
    if (!isPlaying) {
    	currentTime = 0;
        if (player != null) {
            player.close();
        }

        try {
            int p1;
            if (searchfield.getText().isEmpty()) {
                p1 = jPlaylist.getSelectedIndex();
            } else {
                int selectedIndex = jPlaylist.getSelectedIndex();
                DefaultListModel model = (DefaultListModel) jPlaylist.getModel();
                SearchResult selectedResult = (SearchResult) model.getElementAt(selectedIndex);
                p1 = selectedResult.getIndex();
            }

            play1 = (File) this.updateList.get(p1);
            initPlayer(play1);
            FileInputStream fis = new FileInputStream(play1);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new javazoom.jl.player.Player(bis);
            new Thread() {
                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();

            isPlaying = true;
        } catch (Exception e) {
            System.out.println("Problem playing file");
            System.out.println(e);
        }
    } else {
        player.close();
        isPlaying = false;
    }
}
File sa;
void next() {
    if (a == 0) {
        player.close();  // Close the current player
        a = 1;
    	currentTime = 0;
        try {
            int s1 = jPlaylist.getSelectedIndex() + 1;
            sa = (File) this.pl.ls.get(s1);
            FileInputStream fis = new FileInputStream(sa);
            BufferedInputStream bis = new BufferedInputStream(fis);
            initPlayer(sa);
            player = new javazoom.jl.player.Player(bis);

            jPlaylist.setSelectedIndex(s1);
        } catch (Exception e) {
            System.out.println("Problem playing file");
            System.out.println(e);
        }

        play();  // Start playing the next song
    } else {
        player.close();
        a = 0;
        next();
    }
}

void previous() {
    if (a == 0) {
        player.close();  // Close the current player
        a = 1;
    	currentTime = 0;
        try {
            int s1 = jPlaylist.getSelectedIndex() - 1;
            sa = (File) this.pl.ls.get(s1);
            FileInputStream fis = new FileInputStream(sa);
            BufferedInputStream bis = new BufferedInputStream(fis);
            initPlayer(sa);
            player = new javazoom.jl.player.Player(bis);

            jPlaylist.setSelectedIndex(s1);
        } catch (Exception e) {
            System.out.println("Problem playing file");
            System.out.println(e);
        }

        play();  // Start playing the previous song
    } else {
        player.close();
        a = 0;
        previous();
    }
}

void play() {
    new Thread(() -> {
        try {
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();

    isPlaying = true;
}
private int lastSelectedIndex = -1;
private boolean isMouseOverList = false;
void playlistevent() {
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
        @Override
        public void mouseEntered(MouseEvent e) {
            isMouseOverList = true;
        }
        @Override
        public void mouseExited(MouseEvent e) {
            isMouseOverList = false;
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            int index = jPlaylist.locationToIndex(e.getPoint());
            jPlaylist.clearSelection();
            if (index >= 0) {
                jPlaylist.setSelectedIndex(index);
            } else {
                jPlaylist.setSelectedIndex(lastSelectedIndex);
            }
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
        jLabel3 = new javax.swing.JLabel();
        HMenu = new javax.swing.JPanel();
        CloseMenu = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        logoutForm = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        OpenInfor = new javax.swing.JLabel();
        OpenMenu = new javax.swing.JLabel();
        screens = new javax.swing.JPanel();
        Home = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        progressBar = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        currentDur = new javax.swing.JLabel();
        totaldur = new javax.swing.JLabel();
        stop = new javax.swing.JButton();
        ply = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPlaylist = new javax.swing.JList<>();
        searchfield = new javax.swing.JTextField();
        Report = new javax.swing.JPanel();
        reportLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtPassword = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtToEmail = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtContents = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

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

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/cross.png"))); // NOI18N
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        InforPage.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, -1, -1));

        getContentPane().add(InforPage, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 75, 0, 460));

        HMenu.setBackground(new java.awt.Color(204, 255, 255));
        HMenu.setPreferredSize(new java.awt.Dimension(200, 680));
        HMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        CloseMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/cross.png"))); // NOI18N
        CloseMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CloseMenuMouseClicked(evt);
            }
        });
        HMenu.add(CloseMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        jLabel1.setText("Logo");
        HMenu.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 100, 40));

        logoutForm.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        logoutForm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/log-out.png"))); // NOI18N
        logoutForm.setText("Log out");
        logoutForm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logoutFormMouseClicked(evt);
            }
        });
        HMenu.add(logoutForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 620, 90, -1));
        HMenu.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, 140, 50));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/home.png"))); // NOI18N
        jLabel15.setText("  Home");
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });
        HMenu.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, 200, 70));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/bug.png"))); // NOI18N
        jLabel16.setText("   Báo cáo lỗi");
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });
        HMenu.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 200, 70));
        HMenu.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 150, 10));

        getContentPane().add(HMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 0, 609));

        OpenInfor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/information.png"))); // NOI18N
        OpenInfor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OpenInforMouseClicked(evt);
            }
        });
        getContentPane().add(OpenInfor, new org.netbeans.lib.awtextra.AbsoluteConstraints(1310, 10, 30, 40));

        OpenMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/menu.png"))); // NOI18N
        OpenMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OpenMenuMouseClicked(evt);
            }
        });
        getContentPane().add(OpenMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 40, 30));

        screens.setLayout(new java.awt.CardLayout());

        Home.setMinimumSize(new java.awt.Dimension(1290, 710));
        Home.setOpaque(false);
        Home.setPreferredSize(new java.awt.Dimension(1290, 710));
        Home.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_add.png"))); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        Home.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 490, 80, -1));

        btnRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_remove.png"))); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });
        Home.add(btnRemove, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 490, 80, -1));

        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_up.png"))); // NOI18N
        btnUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpActionPerformed(evt);
            }
        });
        Home.add(btnUp, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 490, 70, -1));

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/p_down.png"))); // NOI18N
        btnDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownActionPerformed(evt);
            }
        });
        Home.add(btnDown, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 490, 70, -1));
        Home.add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 530, 410, -1));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/fast-backward.png"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        Home.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 560, 120, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/fast-forward-button.png"))); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        Home.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 560, 130, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Danh sách bài hát");
        Home.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 170, 40));

        jRadioButton1.setText("Nhập từ thư mục");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });
        Home.add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 530, -1, -1));

        currentDur.setText("hf");
        Home.add(currentDur, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 520, -1, -1));

        totaldur.setText("fs");
        Home.add(totaldur, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 520, 10, -1));

        stop.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/pause-button.png"))); // NOI18N
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        Home.add(stop, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 560, 80, -1));

        ply.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/play-button.png"))); // NOI18N
        ply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plyActionPerformed(evt);
            }
        });
        Home.add(ply, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 560, 80, -1));

        jScrollPane1.setViewportView(jPlaylist);

        Home.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1210, 410));

        searchfield.setText("Tìm kiếm");
        searchfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchfieldActionPerformed(evt);
            }
        });
        Home.add(searchfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 700, 40));

        screens.add(Home, "card3");

        Report.setBackground(new java.awt.Color(204, 255, 51));
        Report.setLayout(null);

        reportLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/configuration.png"))); // NOI18N
        Report.add(reportLabel);
        reportLabel.setBounds(3, 0, 540, 610);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel18.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel18.setText("BÁO CÁO LỖI");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 240, 90));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel17.setText("Email:");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, 80, 50));

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });
        jPanel1.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 140, 240, 30));

        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        jPanel1.add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, 240, 30));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel19.setText("Password:");
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 190, 80, 50));

        txtToEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtToEmailActionPerformed(evt);
            }
        });
        jPanel1.add(txtToEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 250, 240, 30));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel20.setText("ToEmail:");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 240, 80, 50));

        txtTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTitleActionPerformed(evt);
            }
        });
        jPanel1.add(txtTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 310, 240, 30));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel21.setText("Tiêu đề lỗi:");
        jPanel1.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 300, 80, 50));

        txtContents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContentsActionPerformed(evt);
            }
        });
        jPanel1.add(txtContents, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 370, 430, 150));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel22.setText("Nội dung lỗi:");
        jPanel1.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 360, 90, 50));

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setText("Gửi");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 540, 250, 40));

        Report.add(jPanel1);
        jPanel1.setBounds(590, 0, 620, 610);

        screens.add(Report, "card2");

        getContentPane().add(screens, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 1210, 610));

        setSize(new java.awt.Dimension(1366, 717));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    
    private void addSearchFieldListener() {
        searchfield.getDocument().addDocumentListener(new DocumentListener() {
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
                // Plain text components do not fire these events
            }
        });
    }
    
    //chức năng tìm kiếm
    private void updatePlaylist() {
	    String originalSearchTerm = searchfield.getText().toLowerCase().trim();
	    DefaultListModel model = new DefaultListModel();
	    for (int i = 0; i < updateList.size(); i++) {
	        File file = (File) updateList.get(i);
	        String itemName = file.getName();
	        String searchTerm = originalSearchTerm.toLowerCase();
	        int priority = calculatePriority(itemName, searchTerm);
	        if (priority > 0) {
	            model.addElement(new SearchResult(i, itemName, priority));
	        }
	    }
	    model = sortModel(model);
	    jPlaylist.setModel(model);
	}
    //thiết đặt mức độ ưu tiên kết quả tìm kiếm
    private int calculatePriority(String itemName, String searchTerm) {
	    String[] searchTerms = searchTerm.split("\\s+");
	    int priority = 0;
	    int lastMatchIndex = -1;
	    for (String term : searchTerms) {
	        term = term.toLowerCase();
	        int index = itemName.toLowerCase().indexOf(term, lastMatchIndex + 1);
	        if (index > lastMatchIndex) {
	            priority += index - lastMatchIndex;
	            lastMatchIndex = index;
	        } else {
	            return 0;
	        }
	    }
	    return priority;
	}
    //xếp bảng các kết quả tìm kiếm
    private DefaultListModel sortModel(DefaultListModel model) {
    ArrayList<SearchResult> resultList = new ArrayList<>();
    for (int i = 0; i < model.getSize(); i++) {
        SearchResult result = (SearchResult) model.getElementAt(i);
        resultList.add(result);
    }
    resultList.sort(Comparator.comparingInt(SearchResult::getPriority));
    DefaultListModel sortedModel = new DefaultListModel();
    for (SearchResult result : resultList) {
        sortedModel.addElement(result);
    }
    return sortedModel;
}
       
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

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        // TODO add your handling code here:
        CloseInfor();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void searchfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchfieldActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnUpActionPerformed

    private void btnDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDownActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stopActionPerformed

    private void plyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_plyActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        // TODO add your handling code here:
        screens.removeAll();
       screens.add(Home);
       screens.repaint();
       screens.revalidate();
    }//GEN-LAST:event_jLabel15MouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        // TODO add your handling code here:
        screens.removeAll();
       screens.add(Report);
       screens.repaint();
       screens.revalidate();
    }//GEN-LAST:event_jLabel16MouseClicked

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void txtToEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtToEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtToEmailActionPerformed

    private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTitleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTitleActionPerformed

    private void txtContentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContentsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContentsActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        //code gửi mail"
        final String username = "mduy.test2@gmail.com";
        final String password = "rcgbbixsiexitbpp";

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(txtToEmail.getText()));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(txtToEmail.getText())); //những email nhận, cách nhau dấu phẩy
            message.setSubject(txtTitle.getText());
            message.setText(txtContents.getText());

            Transport.send(message);

            System.out.println("Done");
            JOptionPane.showConfirmDialog(rootPane, "Đã gửi mail");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1MouseClicked
    //kiểm tra âm lượng
    private static void showBoostWarningDialog(float volume, JSlider volumeSlider) {
        int choice = JOptionPane.showConfirmDialog(null,
                "Bạn đang sử dụng Volume Boost, điều này có thể gây hư hại cho tai của bạn và thiết bị nghe nhạc.\nBạn có chắc chắn muốn tiếp tục?",
                "Cảnh báo Volume Boost", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            setVolume(volume);
        } else {
            volumeSlider.setValue(100);
        }
    }
    //Volume control
    private static void setVolume(float volume) {
        // Implement your volume control logic here
        System.out.println("Volume set to: " + volume);
    }
    private void adjustVolume(int volume) {
        // Sử dụng giá trị âm lượng để điều chỉnh âm thanh
        float volumeLevel = (float) volume / 100.0f;
        // Áp dụng giá trị âm lượng vào phát nhạc bằng JLayer hoặc bất kỳ thư viện âm thanh nào khác bạn đang sử dụng
        //player.setVolume(volumeLevel);
    }
    
    
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

   private int WidthMenu =  200; 
    private int HeightMenu =  680;
    
    private int WidthInfor = 375; 
    private int HeightInfor = 495;
    //Mở Infor: 
    private void OpenInfor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i= 350; i <= WidthInfor; i++) {
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
     public void initPlayer(File sa) throws JavaLayerException, CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
         java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
         AudioFile audioFile = AudioFileIO.read(sa);
         duration = audioFile.getAudioHeader().getTrackLength();
         FileInputStream fileInputStream = new FileInputStream(sa);
         System.out.println("Duration set in initPlayer: " + duration);
         long minutess = duration/60;
         long secondss = duration%60;
         String formattedDuration = String.format("%02d:%02d", minutess, secondss);   
         totaldur.setText(formattedDuration);
     }
     public int getDuration() {
         return duration;
     }
     
     private void updateProgressBar() {
    	    if (isPlaying) {
    	        currentTime++;
    	        progressBar.setValue(currentTime);
    	        updateCurrentTimeLabel();
    	    }
    	}
     private void updateCurrentTimeLabel() {
    	    long minutes = currentTime / 60;
    	    long seconds = currentTime % 60;
    	    String formattedTime = String.format("%d:%02d", minutes, seconds);
    	    currentDur.setText(formattedTime);
    	}
     
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CloseInfor;
    private javax.swing.JLabel CloseMenu;
    private javax.swing.JPanel HMenu;
    private javax.swing.JPanel Home;
    private javax.swing.JPanel InforPage;
    private javax.swing.JLabel OpenInfor;
    private javax.swing.JLabel OpenMenu;
    javax.swing.JPanel Report;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnUp;
    private javax.swing.JLabel currentDur;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JList<String> jPlaylist;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel logoutForm;
    private javax.swing.JButton ply;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel reportLabel;
    private javax.swing.JPanel screens;
    private javax.swing.JTextField searchfield;
    private javax.swing.JButton stop;
    private javax.swing.JLabel totaldur;
    private javax.swing.JTextField txtContents;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JTextField txtToEmail;
    // End of variables declaration//GEN-END:variables

//Class PlaceHolder
class PlaceholderTextField extends JTextField implements FocusListener, CaretListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

class SearchResult {
    private final int index;
    private final String itemName;
    private final int priority;

    public SearchResult(int index, String itemName, int priority) {
        this.index = index;
        this.itemName = itemName;
        this.priority = priority;
    }
    
    public int getIndex() {
        return index;
    }
    
    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return index + " | " + itemName;
    }
}
}


