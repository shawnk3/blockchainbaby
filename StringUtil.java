import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

 class StringUtil {

    public static byte[] applySignature(PrivateKey key, String input){
        Signature dsa;
        byte[] output = new byte[0];
        try{
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(key);
            byte[] Strbyte = input.getBytes();
            dsa.update(Strbyte);
            byte[] realSig = dsa.sign();
            output = realSig;
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
        return output;
    }
    public static boolean verifySig(PublicKey key, String transaction, byte[]signature){
        try{
            Signature ecdsaVerify = Signature.getInstance("ECDSA","BC");
            ecdsaVerify.initVerify(key);
            ecdsaVerify.update(transaction.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public static String getStringFromKey(Key key){
    return Base64.getEncoder().encodeToString(key.getEncoded());
    }
     public static String applySha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
    
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
    
            return hexString.toString();
        } catch(Exception ex){
           throw new RuntimeException(ex);
        }
    }

     public static String getMerkleRoot(ArrayList<Transaction> transactions) {
     	int count = transactions.size();
     	ArrayList<String> previousTreeLayer = new ArrayList<String>();
     	for(Transaction X: transactions) {
     		previousTreeLayer.add(X.getID());
     	}
     	ArrayList<String> TreeLayer = previousTreeLayer;
     	while(count>1) {
     		TreeLayer = new ArrayList<String>();
     		for(int i=1 ; i< previousTreeLayer.size();i++) {
     			TreeLayer.add(StringUtil.applySha256(previousTreeLayer.get(i-1)+ previousTreeLayer.get(i)));
     		}
     		count = TreeLayer.size();
     		previousTreeLayer= TreeLayer;
     	}
     	String merkleRoot = (TreeLayer.size() == 1)? TreeLayer.get(0): "";
     return merkleRoot;
     }
}
