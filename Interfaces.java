package CloudCoinJavaSDK;


import java.util.concurrent.CompletableFuture;


interface ICloudBankAccessable  {
    BankKeys LoadKeysFromFile(String filepath);
    CloudBankUtils getCloudBankUtils();
}

interface ICloudBankUtils  {
    CompletableFuture showCoins();
    void loadStackFromFile(String filepath) throws IOException;
    void saveStackToFile(String filepath) throws IOException;
    String getStackName();
    CompletableFuture sendStackToCloudBank();
    CompletableFuture getStackFromCloudBank(int amountToWithdraw);
    CompletableFuture getReceipt();
    CompletableFuture getReceiptFromCloudBank();
    void transferCloudCoins(String toPublicKey, int coinsToSend);
}

class BaseKeys {
    @SerializedName("publickey")
    public String publickey;

    @SerializedName("privatekey")
    public String privatekey;

    @SerializedName("email")
    public String email;
}

class BaseBankResponse {
    @SerializedName("bank_server")
    public String bank_server;

    @SerializedName("time")
    public String time;
}
