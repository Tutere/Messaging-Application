package Security;




import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


/**
 * DES is a class used for DES styled encryption and decryption of messages. 
 * The DES class saves and loads a key to for all encryption.
 *
 * @author tuteredurie
 * @version 1.0
 * @since 1.0
 */
public class DES {
    
    private SecretKey secretkey; 
    
    
    public DES() throws NoSuchAlgorithmException 
    {
        generateKey();
    }
    
    
    /**
	* Generate a DES key using KeyGenerator
    */ 
    public void generateKey() throws NoSuchAlgorithmException 
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        this.setSecretkey(keyGen.generateKey());        
    }
    
    /**
     * saves the secret key for the class.
     * 
     * @param secretKeyFileName represents the name of the secret key being used
     */
    public void saveKey(String secretKeyFileName) throws FileNotFoundException, IOException
    {       
        ObjectOutputStream oout = new ObjectOutputStream(new FileOutputStream (secretKeyFileName));
        try 
        {
          oout.writeObject(this.secretkey);
        } 
        finally 
        {
          oout.close();
        }
    }
    /**
     * loads the key for the class
     * 
     * @param secretKeyFileName is the filename of the key to be used for encryption/decryption
     */
    public void loadKey(String secretKeyFileName) throws FileNotFoundException, IOException, ClassNotFoundException
    {       
        ObjectInputStream in = new ObjectInputStream(new FileInputStream (secretKeyFileName));
        try 
        {
          this.secretkey = (SecretKey)in.readObject();
        } 
        finally   
        {
          in.close();
        }
    }
    
    /**
     * encrypts a string and returns a byte
     * 
     * @param strDataToEncrypt represents the string to be encrypted.
     * @return returns a byte representing the encrypted message.
     */
    public byte[] encrypt (String strDataToEncrypt) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!
        desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);       
        return byteCipherText;
    }
    /**
     * decrypts a byte back into a string formatted message
     * 
     * @param strCipherText represents the byte to be decrypted.
     * @return returns a string formatted message.
  
     */
    public String decrypt (byte[] strCipherText) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {        
        Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding"); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
        desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());        
        byte[] byteDecryptedText = desCipher.doFinal(strCipherText);        
        return new String(byteDecryptedText);
    }   

    /**
     * @return the secretkey
     */
    public SecretKey getSecretkey() {
        return secretkey;
    }

    /**
     * @param secretkey the secretkey to set
     */
    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }
}
