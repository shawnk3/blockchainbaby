import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
    
public class Wallet{

    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap <String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();//only UTXOs

    public Wallet(){
        generateKeyPair();
    }

    public void generateKeyPair(){
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
            //initializes key and generates pair
            keyGen.initialize(ecSpec,random);
            KeyPair keyPair = keyGen.generateKeyPair();
            //set public and private key
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public float getBalance(){
        float total = 0;
        for(Map.Entry<String,TransactionOutput> item : this.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            if(UTXO.isMine(publicKey)){UTXOs.put(UTXO.id,UTXO); total+= UTXO.value;}
         
        }
    return total;
    }
    public Transaction sendFunds(PublicKey _recipient, float value){
        if(getBalance() < value){
            System.out.println("#Not Enough Funds to send transaction. Transaction discarded.");
            return null;
        }

        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

        float total = 0;
        for(Map.Entry<String,TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if(total > value) break;
        }
        Transaction newTransaction = new Transaction(_recipient,value,inputs,publicKey);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput X: inputs){
                UTXOs.remove(X.TransactionOutputId);
        }
        return newTransaction;
    }



}


