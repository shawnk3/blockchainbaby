import java.util.ArrayList;
import java.security.*;

public class Transaction{
    public PublicKey sender;
    public PublicKey receiver;
    public static int counter =0; // value of transactions;
    public float value;
    public String transactionId;
    public byte [] signature;
    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();


    public Transaction(PublicKey to, float value, ArrayList<TransactionInput> inputs,PublicKey from){
        this.sender = from;  
        this.receiver = to;
        this.value = value;
        this.inputs = inputs;
    }
    
    //creates hash used as TransactionId
    private String calculateHash(){counter++;
            return StringUtil.applySha256(StringUtil.getStringFromKey(sender)
            + StringUtil.getStringFromKey(receiver)
        + Float.toString(value) + counter);
    }   
    public String getID() {
    	return this.transactionId;
    }
    public void setID(String transactionId) {
    	String ID = this.transactionId;
    	this.transactionId = transactionId;
     }
    public boolean processTransaction(){

        if(verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        //gather transaction input(check if unspent)

        for(TransactionInput X:inputs){
                X.UTXO = PerniChain.UTXOs.get(X.getID());
        }
        //check for valid transaction
        if(getInputsValue()<PerniChain.minimumTransaction) { //both methods nonexistant
            System.out.println("#Transaction Inputs to small:" + getInputsValue()); 
            return false;
        }

        //generate transaction outputs
        float leftOver =getInputsValue() - value;   
        transactionId  = calculateHash();
        outputs.add(new TransactionOutput(this.receiver,value,transactionId)); //send value to receiver
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));  //send "change" back to sender
        //add outputs to Unspent list 
        for(TransactionOutput Y: outputs){
            PerniChain.UTXOs.put(Y.id,Y);    
        }
        //remove transaction from UTXO list
        for(TransactionInput X: inputs){
            if(X.UTXO == null) continue;//skip it if not found
            
            PerniChain.UTXOs.remove(X.UTXO.id);
        }
      return true;
        }

    //return sum of UTXO list inputs
    public float getInputsValue(){
        float total =0;
        for(TransactionInput X: inputs){
            if(X.UTXO == null) continue;

            total+= X.UTXO.value;
        }
        return total;
    }    

    //return sum of outputs

    public float getOutputsValue(){
        float total=0;
        for(TransactionOutput Y: outputs){
            total+=Y.value;
        }
        return total;
    }
public ArrayList<TransactionInput> getInputs(){return this.inputs;}
public ArrayList<TransactionOutput>getOutputs(){return this.outputs;}
    public void generateSig(PrivateKey key){
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Float.toString(value);
        signature = StringUtil.applySignature(key,data);
    }
    public boolean verifySignature(){
        String data= StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver)+ Float.toString(value);
        return StringUtil.verifySig(sender,data,signature);   
    }
    
    //Pins array of transactions and returns merkleroot = mother hash of all hashes of transactions
    
  
    
}



