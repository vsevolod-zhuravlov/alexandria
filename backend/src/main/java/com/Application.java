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

    public Application() {
        httpServer = new JettyServer();
    }

    public void start() throws Exception {
        httpServer.start();
    }

    public static void main(String[] args) {


        try {
            Application application = new Application();
            application.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
