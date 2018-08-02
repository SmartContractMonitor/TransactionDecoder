package decoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import org.web3j.crypto.Hash;

public class Decoder {

    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static String getInputData(String transaction) {
        JSONObject obj = new JSONObject(transaction);
        return obj
                .getJSONObject("result")
                .getString("input");
    }

    public static String getFunctionName(String abi, String transaction) {
        Map<String, String> functionNamesByHash = getContractFunctionNamesByHash(abi);

        String inputData = getInputData(transaction);
        System.out.println(inputData.substring(2, 10));
        return functionNamesByHash.get(inputData.substring(2, 10));
    }

    public static Map<String, String> getContractFunctionNamesByHash(String abi) {
        Map<String, String> result = new HashMap<>();
        JSONArray obj = new JSONArray(abi);

        for (int i = 0; i < obj.length(); i++) {
            JSONObject item = obj.getJSONObject(i);
            if (item.getString("type").equals("function")) { //or there's no type key??
                String hash = getFunctionHash(item);
                System.out.println(hash + " : " + item.getString("name"));
                result.put(hash, item.getString("name"));
            }
        }
        return result;
    }

    private static String getFunctionHash(JSONObject item) {
        String name = item.getString("name");
        StringJoiner joiner = new StringJoiner(",");

        JSONArray inputs = item.getJSONArray("inputs");
        for (int i = 0; i < inputs.length(); i++) {
            JSONObject input = inputs.getJSONObject(i);
            joiner.add(input.getString("type"));
        }

        return signatureHash(name + "(" + joiner.toString() + ")");
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String signatureHash(String s) {
        String hash = bytesToHex(Hash.sha3(s.getBytes(StandardCharsets.UTF_8)));
        return hash.substring(0, 8);
    }

    /*public static void main(String[] args) throws IOException {
        String abi = Files.lines(Paths.get("abi.json")).findFirst().orElse("");
        String transaction = Files.lines(Paths.get("transaction.json")).findFirst().orElse("");
        System.out.println(getFunctionName(abi, transaction));
    }
    */
}
