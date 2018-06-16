package CloudCoinJavaSDK;

import com.google.gson.annotations.SerializedName;

public class ReceiptDetail {


    //Fields

    @SerializedName("nn")
    public int nn;

    @SerializedName("sn")
    public int sn;

    @SerializedName("status")
    public String status;

    @SerializedName("pown")
    public String pown;

    @SerializedName("note")
    public String note;


    //Constructors

    public ReceiptDetail() {

    }

    public ReceiptDetail(int nn, int sn, String status, String pown, String note) {
        this.nn = nn;
        this.sn = sn;
        this.status = status;
        this.pown = pown;
        this.note = note;
    }
}
