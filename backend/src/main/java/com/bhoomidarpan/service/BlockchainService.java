package com.bhoomidarpan.service;

import com.bhoomidarpan.dto.BlockchainPropertyResponse;
import com.bhoomidarpan.entity.Property;
import com.bhoomidarpan.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.model.BhoomiDarpanRegistry;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.security.MessageDigest;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {

    private final Web3j web3j;

    @Value("${blockchain.private.key}")
    private String privateKey;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    /* ================= DEVELOPMENT OWNER ================= */

    private String getBackendWalletAddress() {
        return Credentials.create(privateKey).getAddress();
    }

    /* ================= CREDENTIALS ================= */

    private Credentials getCredentials() {
        return Credentials.create(privateKey);
    }

    private StaticGasProvider getGasProvider() {
        return new StaticGasProvider(
                BigInteger.valueOf(2_000_000_000L),
                BigInteger.valueOf(300000)
        );
    }

    public BlockchainPropertyResponse getPropertyFromBlockchain(String propertyCode) {

        try {

            Tuple4<String, String, BigInteger, BigInteger> data =
                    loadContract()
                            .getProperty(propertyCode)
                            .send();

            String owner = data.component1();
            String documentHash = data.component2();
            BigInteger registeredAt = data.component3();
            BigInteger status = data.component4();

            return new BlockchainPropertyResponse(
                    propertyCode,
                    owner,
                    documentHash,
                    registeredAt.longValue(),
                    status.intValue()
            );

        } catch (Exception e) {
            throw new RuntimeException("Blockchain fetch failed", e);
        }
    }


    private BhoomiDarpanRegistry loadContract() {
        return BhoomiDarpanRegistry.load(
                contractAddress,
                web3j,
                getCredentials(),
                getGasProvider()
        );
    }

    /* ================= HASH ================= */

    public String calculateDocumentHash(byte[] data) {

        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);

            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return "0x" + sb;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* ================= REGISTER PROPERTY ================= */

    public String registerPropertyOnBlockchain(Property property, User user)
            throws Exception {

        if (property.getDocumentHash() == null) {
            throw new IllegalStateException("Document hash missing");
        }

        log.info("Registering property {}", property.getPropertyCode());

        TransactionReceipt receipt = loadContract()
                .registerProperty(
                        property.getPropertyCode(),
                        getBackendWalletAddress(),
                        property.getDocumentHash()
                )
                .send();

        return receipt.getTransactionHash();
    }



    /* ================= TRANSFER OWNERSHIP ================= */

    public String transferOwnershipOnBlockchain(
            Property property,
            User seller,
            String newOwnerWalletAddress,
            User mediator
    ) {

        try {

            log.info("Transfer property {}", property.getPropertyCode());

            TransactionReceipt requestReceipt = loadContract()
                    .requestTransfer(
                            property.getPropertyCode(),
                            newOwnerWalletAddress,
                            property.getDocumentHash()
                    )
                    .send();

            log.info("Transfer requested: {}", requestReceipt.getTransactionHash());

            TransactionReceipt approveReceipt = loadContract()
                    .approveTransfer(property.getPropertyCode())
                    .send();

            log.info("Transfer approved: {}", approveReceipt.getTransactionHash());

            return approveReceipt.getTransactionHash();

        } catch (Exception e) {

            throw new RuntimeException("Blockchain transfer failed", e);
        }
    }


    public String reverseOwnershipTransfer(
            Property property,
            String sellerWalletAddress,
            User mediator
    ) {

        try {

            log.info("Reversing ownership for {}", property.getPropertyCode());

            TransactionReceipt requestReceipt = loadContract()
                    .requestTransfer(
                            property.getPropertyCode(),
                            sellerWalletAddress,
                            property.getDocumentHash()
                    )
                    .send();

            TransactionReceipt approveReceipt = loadContract()
                    .approveTransfer(property.getPropertyCode())
                    .send();

            log.info("Reverse transfer complete");

            return approveReceipt.getTransactionHash();

        } catch (Exception e) {
            throw new RuntimeException("Reverse transfer failed", e);
        }
    }
    /* ================= DISPUTES ================= */

    public String raiseDispute(String code, String evidenceHash)
            throws Exception {

        TransactionReceipt receipt = loadContract()
                .raiseDispute(code, evidenceHash)
                .send();

        return receipt.getTransactionHash();
    }

    public String approveDispute(String code)
            throws Exception {

        TransactionReceipt receipt = loadContract()
                .approveDispute(code)
                .send();

        return receipt.getTransactionHash();
    }

    public String resolveDispute(String code)
            throws Exception {

        TransactionReceipt receipt = loadContract()
                .resolveDispute(code)
                .send();

        return receipt.getTransactionHash();
    }

    /* ================= VERIFY ================= */

    public boolean verifyPropertyIntegrity(Property property) {

        try {

            Tuple4<String, String, BigInteger, BigInteger> data =
                    loadContract()
                            .getProperty(property.getPropertyCode())
                            .send();

            String chainHash = data.component2();
            BigInteger status = data.component4();

            return property.getDocumentHash().equalsIgnoreCase(chainHash)
                    && status.intValue() == 1;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Blockchain verification failed",
                    e
            );
        }
    }
}