package CloudCoinJavaSDK;

import com.google.gson.Gson;
import org.asynchttpclient.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class CloudBankUtils {


    // Fields

    private BankKeys keys;
    private AsyncHttpClient client;
    private Gson gson;

    private String rawStackForDeposit;
    private String rawStackFromWithdrawal;
    private String rawReceipt;
    private String receiptNumber;
    private int totalCoinsWithdrawn;
    private int onesInBank;
    private int fivesInBank;
    private int twentyFivesInBank;
    private int hundredsInBank;
    private int twohundredfiftiesInBank;


    // Constructor

    public CloudBankUtils(BankKeys startKeys) {
        keys = startKeys;
        client = asyncHttpClient();
        gson = new Gson();
    }


    // Methods

    /**
     * Calls the CloudService's show_coins service for the server that this object holds the keys for.
     * The results are saved in this class's public properties if successful.
     * @return CompletableFuture
    */
    public CompletableFuture<Object> showCoins() {
        //the private key is sent as form url encoded content
        CompletableFuture<Object> clientCall = client.preparePost("https://" + keys.publickey + "/show_coins.aspx")
                .addFormParam("pk", keys.privatekey)
                .execute(new AsyncHandler<>() {
                    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                    private Integer status;

                    @Override
                    public State onStatusReceived(HttpResponseStatus responseStatus) {
                        builder.accumulate(responseStatus);
                        status = responseStatus.getStatusCode();
                        return State.CONTINUE;
                    }

                    @Override
                    public State onHeadersReceived(HttpResponseHeaders headers) {
                        builder.accumulate(headers);
                        return State.CONTINUE;
                    }

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                        builder.accumulate(bodyPart);
                        return State.CONTINUE;
                    }

                    @Override
                    public Integer onCompleted() {
                        String response = builder.build().toString();
                        BankTotal bankTotals = gson.fromJson(response, BankTotal.class);
                        if ("coins_shown".equals(bankTotals.status)) {
                            onesInBank = bankTotals.ones;
                            fivesInBank = bankTotals.fives;
                            twentyFivesInBank = bankTotals.twentyfives;
                            hundredsInBank = bankTotals.hundreds;
                            twohundredfiftiesInBank = bankTotals.twohundredfifties;
                        } else {
                            System.out.println(bankTotals.status);
                            FailResponse failResponse = gson.fromJson(response, FailResponse.class);
                            System.out.println(failResponse.message);
                        }
                        return status;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("Exception: " + t.getMessage());
                        System.out.println("Check your connection, or your public key");
                    }
                })
                .toCompletableFuture();

        return clientCall;
    }

    /**
     * Sets rawStackForDeposit to a CloudCoin stack read from a file.
     * @param filepath The full filepath and filename of the CloudCoin stack that is being loaded
     * @throws IOException if the CloudCoin stack file is not found
    */
    public void loadStackFromFile(String filepath) throws IOException {
        rawStackForDeposit = new String(Files.readAllBytes(Paths.get(filepath)));
    }

    /**
     * Sends the CloudCoin in rawStackForDeposit to the CloudService server that this object holds the keys for.
     * LoadStackFromFile needs to be called first
     * @return CompletableFuture
    */
    public CompletableFuture<Object> sendStackToCloudBank() {
        CompletableFuture<Object> clientCall = client.preparePost("https://" + keys.publickey + "/deposit_one_stack.aspx")
                .addFormParam("stack", rawStackForDeposit)
                .execute(new AsyncHandler<>() {
                    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                    private Integer status;

                    @Override
                    public State onStatusReceived(HttpResponseStatus responseStatus) {
                        builder.accumulate(responseStatus);
                        status = responseStatus.getStatusCode();
                        return State.CONTINUE;
                    }

                    @Override
                    public State onHeadersReceived(HttpResponseHeaders headers) {
                        builder.accumulate(headers);
                        return State.CONTINUE;
                    }

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                        builder.accumulate(bodyPart);
                        return State.CONTINUE;
                    }

                    @Override
                    public Integer onCompleted() {
                        String response = builder.build().toString();
                        DepositResponse cbf = gson.fromJson(response, DepositResponse.class);
                        System.out.println(cbf.message);
                        receiptNumber = cbf.receipt;
                        return status;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("Exception: " + t.getMessage());
                        System.out.println("Check your connection, or your public key");
                    }
                })
                .toCompletableFuture();

        return clientCall;
    }

    /**
     * Sends the CloudCoin in rawStackForDeposit to a CloudService server specified by parameter toPublicURL
     * loadStackFromFile needs to be called first.
     * @param toPublicURL url of the CloudService server the CloudCoins are being sent to. Do not include "https://"
     * @return CompletableFuture
    */
    public CompletableFuture<Object> sendStackToCloudBank(String toPublicURL) {
        CompletableFuture<Object> clientCall = client.preparePost("https://" + toPublicURL + "/deposit_one_stack.aspx")
                .addFormParam("stack", rawStackForDeposit)
                .execute(new AsyncHandler<>() {
                    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                    private Integer status;

                    @Override
                    public State onStatusReceived(HttpResponseStatus responseStatus) {
                        builder.accumulate(responseStatus);
                        status = responseStatus.getStatusCode();
                        return State.CONTINUE;
                    }

                    @Override
                    public State onHeadersReceived(HttpResponseHeaders headers) {
                        builder.accumulate(headers);
                        return State.CONTINUE;
                    }

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                        builder.accumulate(bodyPart);
                        return State.CONTINUE;
                    }

                    @Override
                    public Integer onCompleted() {
                        String response = builder.build().toString();
                        DepositResponse cbf = gson.fromJson(response, DepositResponse.class);
                        System.out.println(cbf.message);
                        receiptNumber = cbf.receipt;
                        return status;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("Exception: " + t.getMessage());
                        System.out.println("Check your connection, or your public key");
                    }
                })
                .toCompletableFuture();

        return clientCall;
    }

    /**
     * Retrieve the receipt generated by the CloudService for the last sendStackToCloudBank call.
     * Requires sendStackToCloudBank to have been previously called.
     * The retrieved receipt will be saved in rawReceipt.
     * @return CompletableFuture
    */
    public CompletableFuture<Object> getReceipt() {
        CompletableFuture<Object> clientCall = client.prepareGet("https://" + keys.publickey + "/" + keys.privatekey + "/Receipts/" + receiptNumber + ".json")
                .execute(new AsyncHandler<>() {
                    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                    private Integer status;

                    @Override
                    public State onStatusReceived(HttpResponseStatus responseStatus) {
                        builder.accumulate(responseStatus);
                        status = responseStatus.getStatusCode();
                        return State.CONTINUE;
                    }

                    @Override
                    public State onHeadersReceived(HttpResponseHeaders headers) {
                        builder.accumulate(headers);
                        return State.CONTINUE;
                    }

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                        builder.accumulate(bodyPart);
                        return State.CONTINUE;
                    }

                    @Override
                    public Integer onCompleted() {
                        rawReceipt = builder.build().toString();
                        return status;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("Exception: " + t.getMessage());
                        System.out.println("Check your connection, or your public key, or you may not have made a Deposit yet.");
                    }
                })
                .toCompletableFuture();

        return clientCall;
    }


    /**
     * Retrieves CloudCoins from CloudService server that this object holds the keys for.
     * The resulting stack that is retrieved is saved in rawStackFromWithdrawal.
     * @param amountToWithdraw The amount of CloudCoins to withdraw
    */
    public CompletableFuture<Object> getStackFromCloudBank(int amountToWithdraw) {
        totalCoinsWithdrawn = amountToWithdraw;
        CompletableFuture<Object> clientCall = client.preparePost("https://" + keys.publickey + "/withdraw_account.aspx")
                .addFormParam("amount", Integer.toString(amountToWithdraw))
                .addFormParam("pk", keys.privatekey)
                .execute(new AsyncHandler<>() {
                    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                    private Integer status;

                    @Override
                    public State onStatusReceived(HttpResponseStatus responseStatus) {
                        builder.accumulate(responseStatus);
                        status = responseStatus.getStatusCode();
                        return State.CONTINUE;
                    }

                    @Override
                    public State onHeadersReceived(HttpResponseHeaders headers) {
                        builder.accumulate(headers);
                        return State.CONTINUE;
                    }

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                        builder.accumulate(bodyPart);
                        return State.CONTINUE;
                    }

                    @Override
                    public Integer onCompleted() {
                        rawStackFromWithdrawal = builder.build().toString();
                        FailResponse failResponse = gson.fromJson(rawStackFromWithdrawal, FailResponse.class);
                        System.out.println(failResponse.status);
                        System.out.println(failResponse.message);
                        return status;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("Exception: " + t.getMessage());
                        System.out.println("Check your connection, or your public key");
                    }
                })
                .toCompletableFuture();

        return clientCall;
    }

    /**
     * Calculate a CloudCoin note's denomination using it serial number(sn).
    */
    private int getDenomination(int sn) {
        int nom;
        if ((sn < 1)) {
            nom = 0;
        } else if ((sn < 2097153)) {
            nom = 1;
        } else if ((sn < 4194305)) {
            nom = 5;
        } else if ((sn < 6291457)) {
            nom = 25;
        } else if ((sn < 14680065)) {
            nom = 100;
        } else if ((sn < 16777217)) {
            nom = 250;
        } else {
            nom = '0';
        }

        return nom;
    }

    /**
     * Retrieves CloudCoins from CloudService server that this object holds the keys for.
     * The amount withdrawn is the same as the amount last deposited with sendStackToCloudBank.
     * The resulting stack that is retrieved is saved in rawStackFromWithdrawal.
    */
    public CompletableFuture<Object> getReceiptFromCloudBank() {
        // ThreadStopper is used to stop the thread if the call to /get_receipt fails, and prevents the call to /withdraw_account
        final AsyncThreadStopper threadStopper = new AsyncThreadStopper();

        CompletableFuture<Object> clientCall = client.preparePost("https://" + keys.publickey + "/get_receipt.aspx")
                .addFormParam("rn", receiptNumber)
                .addFormParam("pk", keys.privatekey)
                .execute(new AsyncHandler<>() {
                    private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                    private Integer status;

                    @Override
                    public State onStatusReceived(HttpResponseStatus responseStatus) {
                        builder.accumulate(responseStatus);
                        status = responseStatus.getStatusCode();
                        return State.CONTINUE;
                    }

                    @Override
                    public State onHeadersReceived(HttpResponseHeaders headers) {
                        builder.accumulate(headers);
                        return State.CONTINUE;
                    }

                    @Override
                    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                        builder.accumulate(bodyPart);
                        return State.CONTINUE;
                    }

                    @Override
                    public Integer onCompleted() {
                        String rawReceipt = builder.build().toString();
                        Receipt deserialReceipt = gson.fromJson(rawReceipt, Receipt.class);
                        for (int i = 0; i < deserialReceipt.rd.length; i++)
                            if ("authentic".equals(deserialReceipt.rd[i].status))
                                totalCoinsWithdrawn += getDenomination(deserialReceipt.rd[i].sn);
                        return status;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        System.out.println("Exception: " + t.getMessage());
                        System.out.println("Check your connection, or your public key");
                        threadStopper.stopThread = true;
                    }
                })
                .toCompletableFuture();

        clientCall.thenRun(() -> {
            if (threadStopper.stopThread)
                return;

            ListenableFuture<Integer> clientCall2 = client.preparePost("https://" + keys.publickey + "/withdraw_account.aspx")
                    .addFormParam("amount", Integer.toString(totalCoinsWithdrawn))
                    .addFormParam("pk", keys.privatekey)
                    .execute(new AsyncHandler<>() {
                        private final Response.ResponseBuilder builder = new Response.ResponseBuilder();
                        private Integer status;

                        @Override
                        public State onStatusReceived(HttpResponseStatus responseStatus) {
                            builder.accumulate(responseStatus);
                            status = responseStatus.getStatusCode();
                            return State.CONTINUE;
                        }

                        @Override
                        public State onHeadersReceived(HttpResponseHeaders headers) {
                            builder.accumulate(headers);
                            return State.CONTINUE;
                        }

                        @Override
                        public State onBodyPartReceived(HttpResponseBodyPart bodyPart) {
                            builder.accumulate(bodyPart);
                            return State.CONTINUE;
                        }

                        @Override
                        public Integer onCompleted() {
                            rawStackFromWithdrawal = builder.build().toString();
                            FailResponse failResponse = gson.fromJson(rawStackFromWithdrawal, FailResponse.class);
                            System.out.println(failResponse.status);
                            System.out.println(failResponse.message);
                            return status;
                        }

                        @Override
                        public void onThrowable(Throwable t) {
                            System.out.println("Exception: " + t.getMessage());
                            System.out.println("Check your connection, or your public key");
                        }
                    });
        });

        return clientCall;
    }

    /**
     * Parses pertinent information from the receipt last gathered by getReceipt and returns it in the form of an Interpretation object.
    */
    public Interpretation interpretReceipt() {
        Interpretation inter = new Interpretation();
        String interpretation;

        // Tell the client how many coins were uploaded, how many counterfeit, etc.
        Receipt deserialReceipt = gson.fromJson(rawReceipt, Receipt.class);
        int totalNotes = deserialReceipt.total_authentic + deserialReceipt.total_fracked;
        int totalCoins = 0;
        for (int i = 0; i < deserialReceipt.rd.length; i++)
            if ("authentic".equals(deserialReceipt.rd[i].status))
                totalCoins += getDenomination(deserialReceipt.rd[i].sn);
        interpretation = "receipt number: " + deserialReceipt.receipt_id + " total authentic notes: " + totalNotes + " total authentic coins: " + totalCoins;
        inter.interpretation = interpretation;
        inter.receipt = deserialReceipt;
        inter.totalAuthenticCoins = totalCoins;
        inter.totalAuthenticNotes = totalNotes;

        return inter;
    }

    /**
     * Writes a CloudCoin stack file for the CloudCoin retrieved the last call of either getStackFromCloudBank or getReceiptFromCloudBank.
     * @param path The full file path where the new file will be written
     * @throws IOException if the CloudCoin stack file is not found
    */
    public void saveStackToFile(String path) throws IOException {
        Files.write(Paths.get(path + getStackName()), rawStackFromWithdrawal.getBytes());
        //WriteFile(path + stackName, rawStackFromWithdrawal);
    }

    /**
     * Generates a filename for the CloudCoin stack file to be written by saveStackToFile.
    */
    public String getStackName() {
        if (receiptNumber == null) {
            Date date = new Date();
            String tag = "Withdrawal" + new SimpleDateFormat("MMddyyyyhhmmsSS").format(date);
            return totalCoinsWithdrawn + ".CloudCoin." + tag + ".stack";
        }
        return totalCoinsWithdrawn + ".CloudCoin." + receiptNumber + ".stack";
    }

    /**
     * Calls getStackFromCloudBank and sendStackToCloudBank in order to transfer CloudCoins from one CloudService to another.
     * @param coinsToSend The amount of CloudCoins to be transferred
     * @param toPublicKey The public url of the CloudService that is receiving the CloudCoins
    */
    public void transferCloudCoins(final String toPublicKey, int coinsToSend) {
        //Download amount
        getStackFromCloudBank(coinsToSend).thenRun(() -> {
            rawStackForDeposit = rawStackFromWithdrawal; // Make it so it will send the stack it received
            sendStackToCloudBank(toPublicKey);
        });
        //Upload amount
    }

    private class AsyncThreadStopper {
        public volatile boolean stopThread;
    }


    // Getters and Setters

    public int getOnesInBank() {
        return onesInBank;
    }

    public int getFivesInBank() {
        return fivesInBank;
    }

    public int getTwentyFivesInBank() {
        return twentyFivesInBank;
    }

    public int getHundredsInBank() {
        return hundredsInBank;
    }

    public int getTwoHundredFiftiesInBank() {
        return twohundredfiftiesInBank;
    }
}
