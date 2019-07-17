import java.util.ArrayList;
import java.security.*;

public class Transaction{
    public PublicKey sender;
    public PublicKey receiver;
    public static int counter =0; // value of transactions;
    public float value;
    public String transactionId;
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
            return StringUtil.applySha256(StringUtil.getStringfromKey(sender)
            + StringUtil.getStringfromKey(receiver)
        + Float.toString(value) + counter);
    }

    public boolean processTransaction(){

        if(verifySignature() == false){
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }
        //gather transaction input(check if unspent)

        for(TransactionInput X:inputs){
                X.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
        }
        //check for valid transaction
        if(getInputsValue()<Blockchain.minimumTransaction) { //both methods nonexistant
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
            Blockchain.UXTOs.put(Y.id,Y);    
        }
        //remove transaction from UTXO list
        for(TransactionInput X: inputs){
            if(X.UTXO == null) continue;//skip it if not found
            
            Blockchain.UTXOs.remove(X.UTXO.id);
        }

        }

    //return sum of UTXO list inputs
    public float getInputsValue(){
        float total =0;
        for(TransactionInput X: inputs){
            if(i.UTXO == null) continue;

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
        String data = StringUtil.getStringfromKey(sender) + StringUtil.getStringfromKey(receiver) + Float.toString(value);
        signature = StringUtil.applySignature(key,data);
    }
    public boolean verifySignature(){
        String data= StringUtil.getStringfromKey(sender) + StringUtil.getStringfromKey(receiver)+ Float.toString(value);
            return StringUtil.verifySignature(sender,data,signature);   
    }
}



