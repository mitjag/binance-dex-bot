package si.babypanda.binance.dex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.binance.dex.api.client.BinanceDexApiClientFactory;
import com.binance.dex.api.client.BinanceDexApiException;
import com.binance.dex.api.client.BinanceDexApiRestClient;
import com.binance.dex.api.client.BinanceDexEnvironment;
import com.binance.dex.api.client.Wallet;
import com.binance.dex.api.client.domain.Account;

public class WalletManager {
    
    private static final Logger log = LoggerFactory.getLogger(WalletManager.class);
    
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";
        
    private static final List<String> pandaKey1 = Arrays.asList("hello", "sail", "foam", "session", "office", "isolate", "moment", "hollow",
    "prison", "very", "lumber", "flavor", "orbit", "update", "wrap", "beyond", "idle", "cute", "hundred", "program", "prefer", "foam", "ensure", "afraid");
    
    private static final List<String> pandaKey2 = Arrays.asList("afraid", "hello", "sail", "foam", "session", "office", "isolate", "moment", "hollow",
    "prison", "very", "lumber", "flavor", "orbit", "update", "wrap", "beyond", "idle", "cute", "hundred", "program", "prefer", "foam", "ensure");
    
    // ...

    private Random random;
    private Long seed = 1L;
    
    public Wallet create(int id) {
        random = new Random(seed + id);
        // List<String> mnemonicCodeWords = Crypto.generateMnemonicCode();
        List<String> mnemonicCodeWords = list(24);
        return create(id, mnemonicCodeWords);
    }
    
    public Wallet init(int id) {
        if (id == 1) {
            return create(id, pandaKey1);
        } else if (id == 2) {
            return create(id, pandaKey2);
        } else {
            return null;
        }
    }
    
    private Wallet create(int id, List<String> mnemonicCodeWords) {
        try {
            Wallet wallet = Wallet.createWalletFromMnemonicCode(mnemonicCodeWords, BinanceDexEnvironment.TEST_NET);
            log.info("mnemonic id: {} address: {} private key: {} code: {}", id, wallet.getAddress(), wallet.getPrivateKey(),
                    String.join(" ", mnemonicCodeWords));
            return wallet;
        } catch (IOException ex) {
            log.error("IOException ex: {}", ex.getMessage(), ex);
            return null;
        }
    }
    
    private List<String> list(int length) {
        ArrayList<String> array = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            array.add(code(6));
        }
        return array;
    }
    
    private String code(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
    
    public Account account(Wallet wallet) {
        BinanceDexApiRestClient client = BinanceDexApiClientFactory.newInstance().newRestClient(BinanceDexEnvironment.TEST_NET.getBaseUrl());
        //Time time = client.getTime();
        //Infos nodeInfo = client.getNodeInfo();
        //System.out.println(nodeInfo);
        try {
            return client.getAccount(wallet.getAddress());
        } catch (BinanceDexApiException ex) {
            log.error("account BinanceDexApiException ex: {}", ex.getMessage(), ex);
            return null;
        }
    }
}
