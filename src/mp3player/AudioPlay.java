package mp3player;
import javax.media.*;
import java.net.*;
import java.io.*;
import java.util.*;
public class AudioPlay
{
 public static void main(String args[]) throws Exception
 {


 // Take the path of the audio file from command line
     String filePath = "E:\\MusicDB\\Exil - Hiboky.mp3";
 File f=new File(filePath);


 // Create a Player object that realizes the audio
 final Player p=Manager.createRealizedPlayer(f.toURI().toURL());


  // Start the music
  p.start();


  // Create a Scanner object for taking input from cmd
  Scanner s=new Scanner(System.in);


  // Read a line and store it in st
  String st=s.nextLine();


   // If user types 's', stop the audio
   if(st.equals("s"))
   {
   p.stop();
   }
 }
}