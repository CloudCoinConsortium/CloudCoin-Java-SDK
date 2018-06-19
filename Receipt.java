package CloudCoinJavaSDK;

import com.google.gson.annotations.SerializedName;

public class Receipt extends BaseBankResponse {


    // Fields

    @SerializedName("receipt_id")
    public String receipt_id;

    @SerializedName("timezone")
    public String timezone;

    @SerializedName("total_authentic")
    public int total_authentic;

    @SerializedName("total_fracked")
    public int total_fracked;

    @SerializedName("total_counterfeit")
    public int total_counterfeit;

    @SerializedName("total_lost")
    public int total_lost;

    @SerializedName(value = "receipt_detail", alternate = "receipt")
    public ReceiptDetail[] rd;
}
