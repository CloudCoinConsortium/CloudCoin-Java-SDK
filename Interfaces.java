package CloudCoinJavaSDK;


import java.util.concurrent.CompletableFuture;

interface ICloudBankAccessable
{
    /*BankKeys LoadKeysFromFile(string filepath);
    CloudBankUtils CloudBankUtils { get; }*/
}

interface ICloudBankUtils
{
    /*int onesInBank;
    int fivesInBank;
    int twentyFivesInBank;
    int hundredsInBank;
    int twohundredfiftiesInBank;*/
    CompletableFuture showCoins();
    void loadStackFromFile(String filepath);
    void saveStackToFile(String filepath);
    String getStackName();
    CompletableFuture sendStackToCloudBank();
    CompletableFuture getStackFromCloudBank(int amountToWithdraw);
    CompletableFuture getReceipt();
    CompletableFuture getReceiptFromCloudBank();
    CompletableFuture transferCloudCoins(String toPublicKey, int coinsToSend);
}

interface IKeys
{
    /*String publicKey = null;
    String privateKey = null;
    String email = null;*/
}

interface IBankResponse
{
    /*String bank_server = null;
    String time = null;*/
}
