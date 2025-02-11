package com;

import com.endpoints.JettyServer;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Application {
    private JettyServer httpServer;

    private static final String API_KEY = "7b175ee259644546b22793c7175e815a";
    //private static final String PRIVATE_KEY = "0xb27a127aa3ed489dc63a66672f4bbd0e69596a6f731cb21313297c8a26016c01";


    private static final String RPC_URL = "https://holesky.infura.io/v3/" + API_KEY; // Use your RPC provider
    private static final String FACTORY_CONTRACT_ADDRESS = "0x64cc6d99fEDa46eab97BB1D9FaBE635641ba0760";
    private static final String TEACHER_ADDRESS = "0x721b8eD3fa9dB2304aDc90732c8B4b50b6C8F894";

    private static final byte[] DEFAULT_ADMIN_ROLE = decodeHexString("0000000000000000000000000000000000000000000000000000000000000000");

    private static final byte[] CONFIRMER_ROLE = decodeHexString("2882d409365a50c684fae709d3db405f6fd025cd4d815228377ccc14efd8b57c");

    private static final byte[] STUDENT_ROLE = decodeHexString("36a5c4aaacb6b388bbd448bf11096b7dafc5652bcc9046084fd0e95b1fb0b2cc");


    public Application() {
        httpServer = new JettyServer();
    }

    public void start() throws Exception {
        httpServer.start();
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }
/*
    private static Role decodeRole(String roleName) {
        return switch (roleName) {
            case "0x0000000000000000000000000000000000000000000000000000000000000000"
                -> Role.DEFAULT_ADMIN_ROLE;
            case "0x2882d409365a50c684fae709d3db405f6fd025cd4d815228377ccc14efd8b57c"
                    -> Role.CONFIRMER_ROLE;
            case "0x36a5c4aaacb6b388bbd448bf11096b7dafc5652bcc9046084fd0e95b1fb0b2cc\n"
                    -> Role.STUDENT_ROLE;
            default -> Role.STUDENT_ROLE;
        };
    }
*/

    public static void getCoursesOfTeacher(Web3j web3, String teacherAddress) throws IOException {
        Function function = new Function(
                "getCoursesOfTeacher",
                Arrays.asList(new Address(teacherAddress)),  // Input args
                Arrays.asList(new org.web3j.abi.TypeReference<DynamicArray<Address>>() {
                })
        );

        String encodedFunction = FunctionEncoder.encode(function);
        Transaction tx = Transaction.createEthCallTransaction(null, FACTORY_CONTRACT_ADDRESS, encodedFunction);
        EthCall response = web3.ethCall(tx, DefaultBlockParameterName.LATEST).send();

        // Decode the response
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        List<Address> courses = ((DynamicArray<Address>) decoded.get(0)).getValue();

        // Print courses
        System.out.println("Courses of teacher " + teacherAddress + ":");
        for (Address course : courses) {
            System.out.println("- " + course.toString());
        }
    }

    public static void getCourseByIndex(Web3j web3, String teacherAddress, int index) throws IOException {
        Function function = new Function(
                "getCourseByIndex",
                Arrays.asList(new Address(teacherAddress), new Uint256(index)), // Input args
                Arrays.asList(new org.web3j.abi.TypeReference<Address>() {
                }) // Return type: address
        );

        String encodedFunction = FunctionEncoder.encode(function);
        Transaction tx = Transaction.createEthCallTransaction(null, FACTORY_CONTRACT_ADDRESS, encodedFunction);
        EthCall response = web3.ethCall(tx, DefaultBlockParameterName.LATEST).send();

        // Decode the response
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        Address course = (Address) decoded.get(0);

        // Print course
        System.out.println("Course at index " + index + " for teacher " + teacherAddress + ": " + course.toString());
    }

    public static boolean hasRole(Web3j web3, String contractAddress, byte[] role, String account) throws IOException {
        Function function = new Function(
                "hasRole",
                Arrays.asList(new Bytes32(role), new Address(account)),  // Input args
                Arrays.asList(new org.web3j.abi.TypeReference<Bool>() {
                })
        );

        String encodedFunction = FunctionEncoder.encode(function);
        Transaction tx = Transaction.createEthCallTransaction(null, contractAddress, encodedFunction);
        EthCall response = web3.ethCall(tx, DefaultBlockParameterName.LATEST).send();

        // Decode the response
        List<Type> decoded = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());
        Bool hasReceivedRole = (Bool) decoded.get(0);

        return hasReceivedRole.getValue();
    }


    public static void main(String[] args) {

        //Web3j web3 = Web3j.build(new HttpService(RPC_URL));

        try {
            Application application = new Application();
            application.start();

            // Call getCoursesOfTeacher
            //getCoursesOfTeacher(web3, TEACHER_ADDRESS);

            // Call getCourseByIndex with index 0
            //getCourseByIndex(web3, TEACHER_ADDRESS, 0);

        /*
            //SHOULD RETURN HAS CONFIRMER AND DEFAULT ADMIN BOTH
            String testContractAddress = "0xe9cBCE353Fd50e9a6bf1fF864F55489efeDE86d7";
            String testAccountAddress = "0x721b8eD3fa9dB2304aDc90732c8B4b50b6C8F894";
            System.out.println(
                    hasRole(web3, testContractAddress, DEFAULT_ADMIN_ROLE, testAccountAddress)
                            ? "Has DEFAULT_ADMIN_ROLE"
                            : "Doesn't have DEFAULT_ADMIN_ROLE");
            System.out.println(
                    hasRole(web3, testContractAddress, CONFIRMER_ROLE, testAccountAddress)
                            ? "Has CONFIRMER_ROLE"
                            : "Doesn't have CONFIRMER_ROLE");
            System.out.println(
                    hasRole(web3, testContractAddress, STUDENT_ROLE, testAccountAddress)
                            ? "Has STUDENT_ROLE"
                            : "Doesn't have STUDENT_ROLE");
        */} catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
