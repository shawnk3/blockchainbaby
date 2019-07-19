import java.security.Security;
import java.util.Base64;  
//import com.google.gson.GsonBuilder;


public class PerniChain{

     public static int difficulty = 5;
     public static Blockchain chain = new Blockchain(difficulty);
     public static Wallet walletA;
     public static Wallet walletB;
     public static float minimumTransaction = 0.1f;
     public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
     
     public static void main(String []args){

           // Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider());

            walletA = new Wallet();
            walletB = new Wallet();

            System.out.println("Private and Public keys:");
            System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
            System.out.println(StringUtil.getStringFromKey(walletB.privateKey));
            
            Transaction test= new Transaction(walletA.publicKey, 5, null, walletB.publicKey);
            test.generateSig(walletA.privateKey);

            System.out.println("Is signature verified");
            System.out.println(test.verifySignature());
     }
     


}