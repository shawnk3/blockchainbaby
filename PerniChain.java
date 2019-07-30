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
            
            Block genesis = chain.get(0);
            //genesis transaction
            genesisT = new Transaction(walletA.publicKey,100f,null, coinBase.publicKey);
            
            genesisT.generateSig(coinBase.privateKey);// manual sign genesis transaction
            genesisT.setID("0");
            genesisT.outputs.add(new TransactionOutput(genesisT.receiver,genesisT.value,genesisT.getID()));	
            UTXOs.put(genesisT.outputs.get(0).getID(),genesisT.outputs.get(0));
            
            System.out.println("Creating and Mining Genesis Block..");
            
          
          
            //block 1 test
            System.out.println(chain.get(0).getData());
            
            
            chain.addBlock(chain.newBlock("Second Block"));
            Block block1 = chain.get(1);
            
            System.out.println(chain.get(0).getHash());
            System.out.println(chain.get(1).getPreviousHash());
            System.out.println(); 
            
            
            System.out.println("\nWallet A's balance is:" + walletA.getBalance());
            System.out.println("\nTransferring money from Wallet A to B");
            block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
            System.out.println("\nWallet A balance:" + walletA.getBalance());
            System.out.println("\nWallet B balance:" + walletB.getBalance());
            
            
            chain.addBlock(chain.newBlock("Third Block"));
            Block block2 = chain.get(2);
    		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
    		
    		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
    		block2.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
    		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
    		System.out.println("WalletB's balance is: " + walletB.getBalance());
    		 System.out.println("-----------------------------------------");
    		System.out.println(chain.toString());
    		 System.out.println("-----------------------------------------");
           System.out.println( "Is block  valid:" + chain.isValidNewBlock(block1, genesis));
            System.out.println("is blockchain valid:"+ isChainValid());
          // System.out.println(chain.isFirstBlockValid());
            //isValidNewBlock keeps printing false for blocks 1 and 2
           //isBlockChain	
         
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
     public static Boolean isChainValid() {
 		Block currentBlock; 
 		Block previousBlock;
 		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
 		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
 		tempUTXOs.put(genesisT.outputs.get(0).id, genesisT.outputs.get(0));
 		
 		//loop through blockchain to check hashes:
 		for(int i=1; i < chain.size(); i++) {
 			
 			currentBlock = chain.get(i);
 			previousBlock = chain.get(i-1);
 			//compare registered hash and calculated hash:
 			if(!currentBlock.getHash().equals(currentBlock.calculateHash(currentBlock)) ){
 				System.out.println("#Current Hashes not equal");
 				return false;
 			}
 			//compare previous hash and registered previous hash
 			if(!(previousBlock.getHash().equals(currentBlock.getPreviousHash())) ) {
 				System.out.println("#Previous Hashes not equal");
 				return false;
 			}
 			//check if hash is solved
 			if(!(currentBlock.getHash().substring( 0, difficulty).equals(hashTarget))) {
 				System.out.println("#This block hasn't been mined");
 				return false;
 			}
 			
 			//loop thru blockchains transactions:
 			TransactionOutput tempOutput;
 			for(int t=0; t <currentBlock.getTransactions().size(); t++) {
 				Transaction currentTransaction = currentBlock.getTransactions().get(t);
 				
 				if(!currentTransaction.verifySignature()) {
 					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
 					return false; 
 				}
 				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
 					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
 					return false; 
 				}
 				
 				for(TransactionInput input: currentTransaction.inputs) {	
 					tempOutput = tempUTXOs.get(input.getID());
 					
 					if(tempOutput == null) {
 						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
 						return false;
 					}
 					
 					if(input.UTXO.value != tempOutput.value) {
 						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
 						return false;
 					}
 					
 					tempUTXOs.remove(input.getID());
 				}
 				
 				for(TransactionOutput output: currentTransaction.outputs) {
 					tempUTXOs.put(output.id, output);
 				}
 				
 				if( currentTransaction.outputs.get(0).recipient != currentTransaction.receiver) {
 					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
 					return false;
 				}
 				if( currentTransaction.outputs.get(1).recipient != currentTransaction.sender) {
 					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
 					return false;
 				}
 				
 			}
 			
 		}
 		System.out.println("Blockchain is valid");
 		return true;
 	}
     


}