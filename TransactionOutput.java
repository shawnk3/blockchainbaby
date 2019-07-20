import java.security.PublicKey;
public class TransactionOutput{

    public String id;
    public PublicKey recipient;//new owner of coins
    public float value;// value user owns
    public String parentTransactionId;//the id of the transaction this output was created 

    public TransactionOutput(PublicKey recipient,float value, String parentTransactionId){
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = StringUtil.applySha256(StringUtil.getStringfromKey(recipient)+Float.toString(value)+parentTransactionId);
    }
    
    public boolean isMine(PublicKey key){
        return (key==recipient);    }

}