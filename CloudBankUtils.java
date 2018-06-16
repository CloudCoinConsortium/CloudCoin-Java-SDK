package CloudCoinJavaSDK;

import jdk.incubator.http.HttpClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudBankUtils {


    // Fields

    private BankKeys keys;
    private String rawStackForDeposit;
    private String rawStackFromWithdrawal;
    private String rawReceipt;
    private HttpClient cli;
    private String receiptNumber;
    private int totalCoinsWithdrawn;
    private int onesInBank;
    private int fivesInBank;
    private int twentyFivesInBank;
    private int hundredsInBank;
    private int twohundredfiftiesInBank;


    // Constructor

    public CloudBankUtils(BankKeys startKeys)
    {
        keys = startKeys;
        cli = HttpClient.newHttpClient();
    }


    // Methods

    ///<summary>Sets rawStackForDeposit to a CloudCoin stack read from a file</summary>
    ///<param name="filepath">The full filepath and filename of the CloudCoin stack that is being loaded</param> 
    public void loadStackFromFile(String filepath) throws IOException
    {
        rawStackForDeposit = new String(Files.readAllBytes(Paths.get(filepath)));
    }

    ///<summary>Calculate a CloudCoin note's denomination using it serial number(sn)</summary>
    private int getDenomination(int sn)
    {
        int nom = 0;
        if ((sn < 1))
        {
            nom = 0;
        }
        else if ((sn < 2097153))
        {
            nom = 1;
        }
        else if ((sn < 4194305))
        {
            nom = 5;
        }
        else if ((sn < 6291457))
        {
            nom = 25;
        }
        else if ((sn < 14680065))
        {
            nom = 100;
        }
        else if ((sn < 16777217))
        {
            nom = 250;
        }
        else
        {
            nom = '0';
        }

        return nom;
    }//end get denomination

    ///<summary>Writes a CloudCoin stack file for the CloudCoin retrieved the last call of either getStackFromCloudBank or getReceiptFromCloudBank</summary>
    ///<param name="path">The full file path where the new file will be written</param> 
    public void saveStackToFile(String path) throws IOException {
        Files.write(Paths.get(path + getStackName()), rawStackFromWithdrawal.getBytes());
        //WriteFile(path + stackName, rawStackFromWithdrawal);
    }

    ///<summary>Generates a filename for the CloudCoin stack file to be written by saveStackToFile</summary>
    public String getStackName()
    {
        if (receiptNumber == null)
        {
            Date date = new Date();
            String tag = "Withdrawal" + new SimpleDateFormat("MMddyyyyhhmmsSS").format(date);
            return totalCoinsWithdrawn + ".CloudCoin." + tag + ".stack";
        }
        return totalCoinsWithdrawn + ".CloudCoin." + receiptNumber + ".stack";
    }


    // Getters and Setters

    public int getOnesInBank()
    {
        return onesInBank;
    }
    public int getFivesInBank()
    {
        return fivesInBank;
    }
    public int getTwentyFivesInBank()
    {
        return twentyFivesInBank;
    }
    public int getHundredsInBank()
    {
        return hundredsInBank;
    }
    public int getTwoHundredFiftiesInBank()
    {
        return twohundredfiftiesInBank;
    }
}
