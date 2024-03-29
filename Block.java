import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.ArrayList;
import java.util.*;
public class Block {

  private int index;
  private long timestamp;
  private String hash;
  private String previousHash;
  private String data; //list of 10 transactions
  private int nonce;
  private ArrayList<Transaction>transactions = new ArrayList<Transaction>();
  private String merkleroot;

  public Block(int index, long timestamp, String previousHash, String data) {
    this.index = index;
    this.timestamp = timestamp;
    this.previousHash = previousHash;
    this.data = data;
    nonce = 0;
    hash = Block.calculateHash(this);
  }	
  public ArrayList<Transaction> getTransactions(){ return this.transactions;}
  public Transaction GenesisT() { return this.transactions.get(0);}
  public int getIndex() {
    return index;
  }
  
  public long getTimestamp() {
    return timestamp;
  }

  public String getHash() {
    return hash;
  }

  public String getPreviousHash() {
    return previousHash;
  }

  public String getData() {
    return data;
  }

  public String str() {
    return index + timestamp + previousHash + data + nonce + merkleroot;
  }

  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Block #").append(index).append(" [previousHash: ").append(previousHash).append("]\n").
    append("timestamp: ").append(new Date(timestamp)).append("\n").append("data: ").append(data).append("\n").
    append("hash: ").append(hash).append("]");
    return builder.toString();
  }
  //merkle root needs to be added
  public static String calculateHash(Block block) {
    if (block != null) {
      MessageDigest digest = null;

      try {
        digest = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException e) {
        return null;
      }

      String txt = block.str();
      final byte bytes[] = digest.digest(txt.getBytes());
      final StringBuilder builder = new StringBuilder();

      for (final byte b : bytes) {
        String hex = Integer.toHexString(0xff & b);

        if (hex.length() == 1) {
          builder.append('0');
        }

        builder.append(hex);
      }

      return builder.toString();
    }

    return null;
  }

   public void mineBlock(int difficulty) {
     nonce = 0;
     merkleroot = StringUtil.getMerkleRoot(transactions);
     while (!getHash().substring(0,  difficulty).equals(Utils.zeros(difficulty))) {
       nonce++;
       hash = Block.calculateHash(this);
     }
  }
   public boolean addTransaction(Transaction transaction) {
	   //if genesis block it ignores, else processes transaction
	   if(transaction == null)return false;
	   if(previousHash!="0") {
		   if(transaction.processTransaction() != true) {
			   System.out.println("Transaction failed to process. Discarded.");
			   return false;
		   }
	   }
	   this.transactions.add(transaction);
	   System.out.println("Transaction successfully added to Block");
	   return true;
   }
   
}
