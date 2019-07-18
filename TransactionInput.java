
public class TransactionInput {

    public String TransactionOutputID;//TransactionOutput -> transactionId
    public TransactionOutput UTXO;//unspent transaction TransactionOutput

    public TransactionInput(String TransactionOutputID){
        this.TransactionOutputID = TransactionOutputID;
    }
    public String getID(){
        return this.TransactionOutputID;
    }
}