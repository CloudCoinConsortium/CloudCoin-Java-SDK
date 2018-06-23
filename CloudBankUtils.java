package CloudCoinJavaSDK;

/*
  Copyright (c) 2018 Ben Ward, 15/06/18

  This work is licensed under the terms of the MIT license.
  For a copy, see <https://opensource.org/licenses/MIT>.
 */

import com.google.gson.Gson;
import org.asynchttpclient.*;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * CloudBankUtils handles all connectivity with a CloudServer, and initiates all CloudCoin exchanges. CloudBankUtils
 * allows you to exchange CloudCoins, view details for accounts or exchanges, load or save CloudCoins from files,
 * and write or cash CloudCoin checks.
 * <p>
 * <br>
 * CloudBankUtils contains methods that run server operations. These methods return a {@link CompletableFuture}
 * object that can be used to track server progress or chain methods together.
 * <br>
 * <h3>Usage</h3>
 * <p>
 * A CloudBankUtils instance requires a {@link BankKeys} object containing encryption keys for an account.
 * <p>
 * <br>
 * <code>BankKeys bankKeys = new BankKeys(publicKey, privateKey, email);
 * <br>
 * CloudBankUtils cloudBankUtils = new CloudBankUtils(bankKeys);</code>
 * <p>
 * <br>
 * To perform an action after a server call finishes, use the method {@link CompletableFuture#thenRun thenRun} on
 * its {@code CompletableFuture}.
 * <p>
 * <br>
 * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
 * <br>
 * <code>// This code runs after the server call is complete</code>
 * <br>
 * <code>}
 * <br>
 * CloudBankUtils cloudBankUtils = new CloudBankUtils(bankKeys);</code>
 * <p>
 * <br>
 */
public class CloudBankUtils implements ICloudBankUtils {


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

    /**
     * Constructs a CloudBankUtils object with complete access to an account. The account keys from
     * {@link BankKeys} are used to connect to an account, and cannot be changed. To connect to another
     * account, create a new {@code CloudBankUtils} object.
     * <br>
     * CloudBankUtils handles all connectivity with a CloudServer, and initiates all CloudCoin exchanges. CloudBankUtils
     * allows you to exchange CloudCoins, view details for accounts or exchanges, load or save CloudCoins from files,
     * and write or cash CloudCoin checks.
     * <p>
     * <br>
     * CloudBankUtils contains methods that run server operations. These methods return a {@link CompletableFuture}
     * object that can be used to track server progress or chain methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * A CloudBankUtils instance requires a {@link BankKeys} object containing encryption keys for an account.
     * <p>
     * <br>
     * <code>BankKeys bankKeys = new BankKeys(publicKey, privateKey, email);
     * <br>
     * CloudBankUtils cloudBankUtils = new CloudBankUtils(bankKeys);</code>
     * <p>
     * <br>
     * To perform an action after a server call finishes, use the method {@link CompletableFuture#thenRun thenRun} on
     * its {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}
     * <br>
     * CloudBankUtils cloudBankUtils = new CloudBankUtils(bankKeys);</code>
     * <p>
     * <br>
     *
     * @param bankKeys the {@link BankKeys} object containing the encryption keys for the current account.
     */
    public CloudBankUtils(BankKeys bankKeys) {
        keys = bankKeys;
        client = asyncHttpClient();
        gson = new Gson();
    }


    // Methods

    /**
     * Calls the CloudService's show coins service for the server that this object holds the keys for.
     * The results are saved in this class's public properties if successful.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @returns {@link CompletableFuture}
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
                        String response = builder.build().getResponseBody();
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
     * Sets rawStackForDeposit to a CloudCoin stack read from a file. If successful, this stack can be deposited
     * to the account with {@link #sendStackToCloudBank}.
     *
     * @param filepath the full filepath and filename of the CloudCoin stack that is being loaded
     * @throws IOException if the CloudCoin stack file is not found
     */
    public void loadStackFromFile(String filepath) throws IOException {
        rawStackForDeposit = new String(Files.readAllBytes(Paths.get(filepath)));
    }

    /**
     * Sends CloudCoins cached from {@code loadStackFromFile} to the CloudService server that this object holds the
     * keys for. {@link #loadStackFromFile} must be called first to load CloudCoins into the cache.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @returns {@link CompletableFuture}
     */
    public CompletableFuture<Object> sendStackToCloudBank() {
        CompletableFuture<Object> clientCall = client.preparePost("https://" + keys.publickey + "/deposit_one_stack.aspx")
                .addFormParam("pk", keys.privatekey)
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
                        String response = builder.build().getResponseBody();
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
     * Sends CloudCoins cached from {@code loadStackFromFile} to a CloudService server. The server is specified by
     * the URL defined in {@code toPublicURL}. {@link #loadStackFromFile} must be called first to load CloudCoins
     * into the cache.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @param toPublicURL the url of the CloudService server the CloudCoins are being sent to. Do not include
     *                    "https://".
     * @returns {@link CompletableFuture}
     */
    public CompletableFuture<Object> sendStackToCloudBank(String toPublicURL) {
        CompletableFuture<Object> clientCall = client.preparePost("https://" + toPublicURL + "/deposit_one_stack.aspx")
                .addFormParam("pk", keys.privatekey)
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
                        String response = builder.build().getResponseBody();
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
     * The retrieved receipt will be cached, and can be viewed with {@link #interpretReceipt}.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @returns {@link CompletableFuture}
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
                        rawReceipt = builder.build().getResponseBody();
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
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @param amountToWithdraw the amount of CloudCoins to withdraw
     * @returns {@link CompletableFuture}
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
                        rawStackFromWithdrawal = builder.build().getResponseBody();
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
     * Calculates a CloudCoin's denomination using its serial number {@code sn}.
     *
     * @param sn the serial number of a CloudCoin note
     * @returns denomination amount of the CloudCoin
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
     * The resulting stack is then retrieved with the method {@link #getStackFromCloudBank}.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.showCoins().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @returns {@link CompletableFuture}
     */
    public CompletableFuture<Object> getReceiptFromCloudBank() {
        // ThreadStopper is used to stop the thread if the call to /get_receipt fails, and prevents the call to /withdraw_account
        final AsyncThreadStopper threadStopper = new AsyncThreadStopper();

        CompletableFuture<Object> clientCall = client.prepareGet("https://" + keys.publickey + "/get_receipt.aspx?rn="
                + receiptNumber)
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
                        String rawReceipt = builder.build().getResponseBody();
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

            getStackFromCloudBank(totalCoinsWithdrawn);
        });

        return clientCall;
    }

    /**
     * Generates an Interpretation object which contains a cached receipt, its total amount, and a short description
     * of the receipt. There must first be a receipt cached from {@link #getReceipt}.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.getReceipt().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @returns Interpretation or null if no cached receipt is found.
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
        interpretation = "receipt number: " + deserialReceipt.receipt_id + " total authentic notes: " + totalNotes
                + " total authentic coins: " + totalCoins;
        inter.interpretation = interpretation;
        inter.receipt = deserialReceipt;
        inter.totalAuthenticCoins = totalCoins;
        inter.totalAuthenticNotes = totalNotes;

        return inter;
    }

    /**
     * Saves CloudCoins from a cache to a CloudCoin file.
     * The CloudCoins must first be cached using {@link #getStackFromCloudBank} or {@link #getReceiptFromCloudBank()}.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * <code>cloudBankUtils.getStackFromCloudBank().thenRun() -> {</code>
     * <br>
     * <code>saveStackToFile(path)</code>
     * <br>
     * <code>}</code>
     * @param path the full file path where the new file will be written
     * @throws IOException if no cached CloudCoins are found
     */
    public void saveStackToFile(String path) throws IOException {
        Files.write(Paths.get(path + getStackName()), rawStackFromWithdrawal.getBytes());
        //WriteFile(path + stackName, rawStackFromWithdrawal);
    }

    /**
     * Generates a filename for the CloudCoin stack file to be written by saveStackToFile.
     *
     * @returns {@link String}
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
     * Transfers CloudCoins from this account to another account. This method first guarantees the local cache has
     * enough CloudCoins with {@link #getStackFromCloudBank}, then transfers the cached CloudCoins with
     * {@link #sendStackToCloudBank}. If the account does not have enough for the transaction, then neither the
     * withdrawal or transaction will occur.
     *
     * @param toPublicKey the public url of the CloudService that is receiving the CloudCoins
     * @param coinsToSend the amount of CloudCoins to be transferred
     */
    public void transferCloudCoins(final String toPublicKey, int coinsToSend) {
        //Download amount
        getStackFromCloudBank(coinsToSend).thenRun(() -> {
            rawStackForDeposit = rawStackFromWithdrawal; // Make it so it will send the stack it received
            CloudBankUtils.this.sendStackToCloudBank(toPublicKey);
        });
        //Upload amount
    }

    /**
     * Creates a digital check containing CloudCoins. This effectively allows CloudCoins to be exchanged like checks,
     * which can be created in advance and cashed in at a later date. The url of the check is saved in
     * {@link DepositResponse#message}. A check can be cashed in with {@link #cashCheck}.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To perform an action after this server call finishes, use the method {@link CompletableFuture#thenRun thenRun}
     * on its returned {@code CompletableFuture}.
     * <p>
     * <br>
     * <code>cloudBankUtils.writeCheck().thenRun() -> {</code>
     * <br>
     * <code>// This code runs after the server call is complete</code>
     * <br>
     * <code>}</code>
     *
     * @param amountToSend the amount of CloudCoins to be transferred
     * @param payTo        the name of the recipient
     * @param signedBy     the name of the sender
     * @param memo         a short note describing the payment
     * @returns {@link CompletableFuture}
     */
    public CompletableFuture<Object> writeCheck(int amountToSend, String payTo, String signedBy, String memo) {
        CompletableFuture<Object> clientCall = client.preparePost("https://" + keys.publickey + "/write_check.aspx")
                .addFormParam("pk", keys.privatekey)
                .addFormParam("amount", Integer.toString(amountToSend))
                .addFormParam("payto", payTo)
                .addFormParam("signby", signedBy)
                .addFormParam("Memo", memo)
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
                        String response = builder.build().getResponseBody();
                        DepositResponse cbf = gson.fromJson(response, DepositResponse.class);
                        System.out.println(cbf.message);
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
     * Cashes a CloudCoin check and saves the CloudCoin stack. A check can be generated with {@link #writeCheck}.
     * <p>
     * <br>
     * This method returns a {@link CompletableFuture} object that can be used to track server progress or chain
     * methods together.
     * <br>
     * <h3>Usage</h3>
     * <p>
     * To deposit the check, call {@link #sendStackToCloudBank} after receiving the check.
     * <p>
     * <br>
     * <code>cloudBankUtils.cashCheck(checkId).thenRun() -> {</code>
     * <br>
     * <code>cloudBankUtils.sendStackToCloudBank()</code>
     * <br>
     * <code>}</code>
     *
     * @param checkId the ID of the check
     * @returns {@link CompletableFuture}
     */
    public CompletableFuture<Object> cashCheck(String checkId) {
        CompletableFuture<Object> clientCall = client.preparePost(
                "https://" + keys.publickey + "/checks.aspx?id=" + checkId)
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
                        String response = builder.build().getResponseBody();
                        FailResponse failResponse = gson.fromJson(response, FailResponse.class);
                        if (null == failResponse.message) {
                            rawStackForDeposit = response;
                        } else {
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
     * AsyncThreadStopper is used to prevent chained server calls from running if a server call is not successful.
     */
    private class AsyncThreadStopper {
        public volatile boolean stopThread;
    }


    // Getters and Setters

    /**
     * Returns the number of CloudCoin one notes in the bank account.
     *
     * @returns int
     */
    public int getOnesInBank() {
        return onesInBank;
    }

    /**
     * Returns the number of CloudCoin five notes in the bank account.
     *
     * @returns int
     */
    public int getFivesInBank() {
        return fivesInBank;
    }

    /**
     * Returns the number of CloudCoin twenty-five notes in the bank account.
     *
     * @returns int
     */
    public int getTwentyFivesInBank() {
        return twentyFivesInBank;
    }

    /**
     * Returns the number of CloudCoin one-hundred notes in the bank account.
     *
     * @returns int
     */
    public int getHundredsInBank() {
        return hundredsInBank;
    }

    /**
     * Returns the number of CloudCoin two-hundred-and-fifty notes in the bank account.
     *
     * @returns int
     */
    public int getTwoHundredFiftiesInBank() {
        return twohundredfiftiesInBank;
    }
}
