package CloudCoinJavaSDK;

import com.google.gson.annotations.SerializedName;

public class BankKeys implements IKeys {


    // Fields

    @SerializedName("publickey")
    public String publickey;

    @SerializedName("privatekey")
    public String privatekey;

    @SerializedName("email")
    public String email;


    // Constructors

    public BankKeys() {

    }

    public BankKeys(String publickey, String privatekey, String email) {
        this.publickey = publickey;
        this.privatekey = privatekey;
        this.email = email;
    }
}
