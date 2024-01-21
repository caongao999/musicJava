package mp3player;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class MP3Player {
    public static String filepath = "E:\\MusicDB\\Alone with myself _ lofi hip hop mix.mp3";
    private AdvancedPlayer player;
    private int duration;

    public static void main(String args[]) throws UnsupportedAudioFileException, IOException, CannotReadException, TagException, ReadOnlyFileException, InvalidAudioFrameException, JavaLayerException {
        MP3Player mp3Player = new MP3Player();
        mp3Player.initPlayer(filepath);
        mp3Player.play();

        JFrame frame = new JFrame("MP3 Player");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);

        JProgressBar seekbar = new JProgressBar(0, mp3Player.getDuration());
        JLabel currentTimeLabel = new JLabel("Current Time: 0:00");

        frame.setLayout(new BorderLayout());
        frame.add(seekbar, BorderLayout.CENTER);
        frame.add(currentTimeLabel, BorderLayout.SOUTH);

        seekbar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int currentTime = seekbar.getValue();
                long minutes = currentTime / 60;
                long seconds = (currentTime % 60);
                currentTimeLabel.setText("Current Time: " + minutes + ":" + String.format("%02d", seconds)+"/"+minutes+"/"+seconds);
            }
        });

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentTime = seekbar.getValue() + 1;
                if (currentTime <= mp3Player.getDuration()) {
                    seekbar.setValue(currentTime);
                    long minutes = currentTime / 60;
                    long seconds = (currentTime % 60);
                    currentTimeLabel.setText("Current Time: " + minutes + ":" + String.format("%02d", seconds));
                }
            }
        });
        timer.start();

        seekbar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int mouseX = evt.getX();
                int newTime = (int) ((double) mouseX / seekbar.getWidth() * seekbar.getMaximum());
                seekbar.setValue(newTime);
                try {
                    mp3Player.seek(newTime);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
                } catch (BitstreamException ex) {
                    Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
                } catch (JavaLayerException ex) {
                    Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        frame.setVisible(true);
    }

    public void initPlayer(String filePath) throws JavaLayerException, CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
        File file = new File(filePath);
        java.util.logging.Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);

        AudioFile audioFile = AudioFileIO.read(file);
        duration = audioFile.getAudioHeader().getTrackLength();

        FileInputStream fileInputStream = new FileInputStream(file);
        player = new AdvancedPlayer(fileInputStream);
    }

    public void play() {
        Thread playerThread = new Thread(() -> {
            try {
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        // Handle playback finished event if needed
                    }
                });
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });

        playerThread.start();
    }

    public void seek(int time) throws FileNotFoundException, BitstreamException, JavaLayerException {
    int frame = time * 1000; // Convert seconds to milliseconds
    Bitstream bitstream = new Bitstream(new FileInputStream(new File(filepath)));
    int totalFrames = (int) bitstream.readFrame().max_number_of_frames(bitstream.readFrame().max_number_of_frames(-1));
    double percentage = (double) time / duration;
    int framesToSkip = (int) (totalFrames * percentage);

    // Close the player and create a new one with skipped frames
    player.close();
    FileInputStream fileInputStream = new FileInputStream(new File(filepath));
    player = new AdvancedPlayer(fileInputStream);
    
    // Skip frames
    for (int i = 0; i < framesToSkip; i++) {
        if (!player.play(1)) {
            break; // Break if unable to play more frames
        }
    }
    
    // Start playing from the skipped position
    play();
}

    public int getDuration() {
        return duration;
    }
}