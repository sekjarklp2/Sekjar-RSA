/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javacertificate;

/**
 *
 * @author prameswari
 */

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import algoritmarsa.RSA;

public class clientServer2 {
    
    // deklarasi socket
    private static Socket socket;

    /**
     * @return the ea
     */
    public static BigInteger getEa() {
        return ea;
    }

    /**
     * @param aEa the ea to set
     */
    public static void setEa(BigInteger aEa) {
        ea = aEa;
    }

    /**
     * @return the Na
     */
    public static BigInteger getNa() {
        return Na;
    }

    /**
     * @param aNa the Na to set
     */
    public static void setNa(BigInteger aNa) {
        Na = aNa;
    }
    private String sertifikat;
    private static String pesan;
    // deklarasi I/O stream
    private static BufferedReader in;
    private static PrintWriter out;
    private static clientServer2 cl= new clientServer2();
    private  static  BigInteger ea;
    private static BigInteger Na;
    // deklarasi boolean untuk status, diletakkan dalam scope private static agar dapat diakses keseluruhan class
    private static boolean active;
    
    public static void main(String[] args) throws IOException{
        
        // inisialisasi socket dan I/O
        socket = null;
        out = null;
        in = null;
            BigInteger p = RSA.getRandomPrime(512);
            BigInteger q = RSA.getRandomPrime(512);
            BigInteger N = RSA.getModulus(p, q);
            BigInteger phi = RSA.getPhi(p, q);
            BigInteger e = RSA.getPublicKey(phi);
            BigInteger d = RSA.getPrivateKey(e, phi);
           
        try{
            // mengkoneksikan client dengan socket yang sudah ada di port 4444 untuk host: localhost
            socket = new Socket("localhost",4441);

            // menghubungkan I/O stream dengan socket
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
           
            
           
          
            
        } catch (UnknownHostException er) {
            System.err.println("Don't know about host: localhost.");
            System.exit(1);
        } catch (IOException er) {
            System.err.println("Couldn't get I/O for " + "the connection to: localhost.");
            System.exit(1);
        }
        
        // deklarasi input stream untuk membaca input dari console
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String fromUser;
        
         // inisialisasi awal adalah true dimana setiap client terhubung berarti client aktif
        active = true;
        // class threads diinstansiasi kemudian dijalankan agar setiap thread yg terhubung dpt merima pesan dari server
        new clientServer2.threads().start();
        
        //generate key untuk user
           //fungsi generate
        //simpen public key+ private key si user
       
        
        // selama thread masih aktif akan meminta input dari console
        while(active){
            fromUser = stdIn.readLine();
            if (fromUser != null) {
                if(fromUser.startsWith("certificate"))
                {
                    fromUser= fromUser+" "+e+" "+N;
                }
                 if(fromUser.startsWith("@"))
                {
                     
                    fromUser= fromUser+" "+cl.getSertifikat();
                }
                    // inputan dari console nantinya akan ditampilkan dari PrintWriter stream pada thread
                    out.println(fromUser);
            }            
        }
 
        // socket dan I/O stream ditutup
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }

    /**
     * @return the sertifikat
     */
    public String getSertifikat() {
        return sertifikat;
    }

    /**
     * @param sertifikat the sertifikat to set
     */
    public void setSertifikat(String sertifikat) {
        this.sertifikat = sertifikat;
    }
    
    private static class threads extends Thread {
        
        // tidak membutuhkan constructor, hanya berjalan menjalan fungsi run setiap diinstansiasi di fungsi utama
        public void run(){
            try{
                String fromServer;
                // membaca input dari server
                while ((fromServer = in.readLine()) != null) {
                    // cek apabila input dari server adalah "<Server> Exit." maka berhenti membaca dan keluar
                    if (fromServer.equals("Server : you quit")){
                        break;
                    }
                    // pesan dari server akan ditampilkan pada console
                    if(fromServer.startsWith("certificate"))
                    {
                          String[] words = fromServer.split("\\s", 2);
                                        if (words.length > 1 && words[1] != null) {
                                        words[1] = words[1].trim();
                                         if (!words[1].isEmpty()) {
                        cl.setSertifikat(words[1]);
                        System.out.println(words[1]);
                                         }}
                                        
                    }
                    System.out.println(fromServer); 

                }
                // ketika sudah berhenti membaca maka status thread diubah jadi false sehingga tidak dapat lagi membaca input dari user pada console
                active = false;
            } catch(IOException e){
                
            } 
            
        }
    }
}