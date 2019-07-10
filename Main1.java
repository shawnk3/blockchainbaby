public class Main1 {

  public static void main(String[] args) {
    Blockchain blockchain = new Blockchain(4);
    blockchain.addBlock(blockchain.newBlock("AAAA"));
    blockchain.addBlock(blockchain.newBlock("BBBB"));
    blockchain.addBlock(blockchain.newBlock("CCCC"));

    System.out.println("Blockchain valid ? " + blockchain.isBlockChainValid() + "\n");
    System.out.println(blockchain);
  }

}
