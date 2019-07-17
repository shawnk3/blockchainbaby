
public class TransactionInput {

    public String TransactionOutputID;//TransactionOutput -> transactionId
    public TransactionOutput UTXO;//unspent transaction TransactionOutput

    public TransactionOutput(String TransactionOutputID){
        this.TransactionOutputID = TransactionOutputID;
    }
}