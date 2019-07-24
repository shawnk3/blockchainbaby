import java.security.Security;
import java.util.Base64;  
import java.util.*;
import com.google.gson.GsonBuilder;
import org.bouncycastle.*;

public class PerniChain{

     public static int difficulty = 3;
     public static Blockchain chain = new Blockchain(difficulty);
     public static Wallet walletA;
     public static Wallet walletB;
     public static float minimumTransaction = 0.1f;
     public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
     public static Transaction genesisT;
     
     public static void main(String []args){

            Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider());

            walletA = new Wallet();
            walletB = new Wallet();
            Wallet coinBase = new Wallet();
            
            //genesis transaction
            genesisT = new Transaction(walletA.publicKey,100f,null, coinBase.publicKey);
            
            genesisT.generateSig(coinBase.privateKey);// manual sign genesis transaction
            genesisT.setID("0");
            genesisT.outputs.add(new TransactionOutput(genesisT.receiver,genesisT.value,genesisT.getID()));	
            UTXOs.put(genesisT.outputs.get(0).getID(),genesisT.outputs.get(0));
            
            System.out.println("Creating and Mining Genesis Block..");
            Block genesis = new Block(0,0,"0","");
            boolean genesisTAdd = genesis.addTransaction(genesisT);
            chain.addBlock(genesis);
            System.out.println("Block added?:" + genesisTAdd);
            
            //block 1 test
            
            Block block1 = chain.newBlock("Block 1");
            System.out.println("\nWallet A's balance is:" + walletA.getBalance());
            System.out.println("\nTransferring money from Wallet A to B");
            block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
            System.out.println("\nWallet A balance:" + walletA.getBalance());
            System.out.println("\nWallet B balance:" + walletB.getBalance());
            
            //block 2 test
            
            
           System.out.println( "Is chain valid:" + chain.isBlockChainValid());
            
            
            System.out.println("-----------------------------------------");
            System.out.println("Private and Public keys:");
            System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
            System.out.println("\n" + StringUtil.getStringFromKey(walletA.publicKey));
          
            
            Transaction test= new Transaction(walletB.publicKey, 5, null, walletA.publicKey);
            test.generateSig(walletA.privateKey);

            System.out.println("Is signature verified");
            System.out.println(test.verifySignature());
     	 //wallet testing
            
            
     }
     


}