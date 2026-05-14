package com.bhoomidarpan.service;


import com.bhoomidarpan.dto.TransactionResponse;
import com.bhoomidarpan.entity.*;
import com.bhoomidarpan.entity.enums.MutationStatus;
import com.bhoomidarpan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BuyRequestRepository buyRequestRepository;
    private final MutationRepository mutationRepository;
    private final DisputeRepository disputeRepository;


    public List<TransactionResponse> getUserTransactions(User user) {

        List<TransactionResponse> result = new ArrayList<>();

        List<BuyRequest> buyRequests =
                buyRequestRepository.findAllByBuyerIdOrOwnerId(user.getId());

        for (BuyRequest br : buyRequests) {

            TransactionResponse tr = new TransactionResponse();

            tr.setTransactionId(br.getId());
            tr.setPropertyCode(br.getProperty().getPropertyCode());
            tr.setPropertyLocation(br.getProperty().getLocation());

            tr.setBuyerName(br.getBuyer().getName());
            tr.setSellerName(
                    br.getOwnerConsents()
                            .stream()
                            .findFirst()
                            .map(c -> c.getOwner().getName())
                            .orElse("—")
            );

            // 🔁 Determine current stage
            if (disputeRepository.existsActiveDispute(br.getProperty().getId())) {
                tr.setCurrentStage("Dispute");
                tr.setStatus("ON_HOLD");
            }
            else if (mutationRepository.existsByPropertyIdAndStatus(
                    br.getProperty().getId(), MutationStatus.PENDING)) {

                tr.setCurrentStage("Mutation (Tehsil)");
                tr.setStatus("IN_PROGRESS");
            }
            else if (br.getStatus().name().equals("COMPLETED")) {
                tr.setCurrentStage("Completed");
                tr.setStatus("FINALIZED");
            }
            else {
                tr.setCurrentStage("Registration");
                tr.setStatus("IN_PROGRESS");
            }

            result.add(tr);
        }

        return result;
    }
}

