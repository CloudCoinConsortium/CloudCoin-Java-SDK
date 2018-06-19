package CloudCoinJavaSDK;


import com.google.gson.annotations.SerializedName;

public class FailResponse extends BaseBankResponse {


    // Fields

    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("receipt")
    public String receipt;


    // Constructor

    public FailResponse() {

    }
}
