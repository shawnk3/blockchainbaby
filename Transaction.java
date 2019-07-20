import java.util.ArrayList;
import java.security.*;

public class Transaction{
    public PublicKey sender;
    public PublicKey receiver;
    public static int counter =0; // value of transactions;
    public float value;
    public String transactionId;
    public StringUtil tool = new StringUtil();
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
            return tool.applySha256(tool.getStringfromKey(sender)
            + tool.getStringfromKey(receiver)
        + Float.toString(value) + counter);
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

    public void generateSig(PrivateKey key){
        String data = tool.getStringfromKey(sender) + tool.getStringfromKey(receiver) + Float.toString(value);
        signature = tool.applySignature(key,data);
    }
    public boolean verifySignature(){
        String data= tool.getStringfromKey(sender) + tool.getStringfromKey(receiver)+ Float.toString(value);
        return tool.verifySignature(sender,data,signature);   
    }
}



