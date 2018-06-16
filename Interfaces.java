package CloudCoinJavaSDK;


import javafx.concurrent.Task;

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
    Task showCoins();
    void loadStackFromFile(String filepath);
    void saveStackToFile(String filepath);
    String getStackName();
    Task sendStackToCloudBank();
    Task getStackFromCloudBank(int amountToWithdraw);
    Task getReceipt();
    Task getReceiptFromCloudBank();
    Task transferCloudCoins(String toPublicKey, int coinsToSend);
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
