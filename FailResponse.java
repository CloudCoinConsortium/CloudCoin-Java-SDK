package CloudCoinJavaSDK;

import com.google.gson.annotations.SerializedName;

public class FailResponse implements IBankResponse {


    // Fields

    @SerializedName("bank_server")
    public String bank_server;

    @SerializedName("time")
    public String time;

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
