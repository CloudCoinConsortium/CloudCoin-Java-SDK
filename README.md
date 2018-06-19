# CloudCoin-Java-SDK
A Java Framework for connecting an application to a CloudCoinConsortium CloudService.

Usage
Create a BankKeys object using keys from a CloudService server, then use it to create a CloudBankUtils instance. Call CloudBankUtils' methods to communicate with the CloudService.

Dependencies
The Java SDK uses Gson for converting server calls to Java objects, and AsyncHttpClient for asynchronous HTTP requests.
