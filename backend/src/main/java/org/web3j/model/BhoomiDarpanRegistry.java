package org.web3j.model;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.9.8.
 */
@SuppressWarnings("rawtypes")
public class BhoomiDarpanRegistry extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b50600380546001600160a01b031916339081179091555f908152600460205260409020805460ff191660011790556115d7806100495f395ff3fe608060405234801561000f575f5ffd5b50600436106100e5575f3560e01c80638f2238ba11610088578063a53328dc11610063578063a53328dc146101f4578063ab331a3414610217578063af92a6931461023a578063de2dfb821461024d575f5ffd5b80638f2238ba146101bb57806397493f2f146101ce578063a216bbfa146101e1575f5ffd5b806329575f6a116100c357806329575f6a14610124578063429b62e514610154578063704802751461018657806389aeca7614610199575f5ffd5b80630a1ba0b6146100e95780630d8b6fd9146100fe57806324eee4b914610111575b5f5ffd5b6100fc6100f73660046110b2565b610260565b005b6100fc61010c366004611126565b61042a565b6100fc61011f3660046110b2565b6104d7565b600354610137906001600160a01b031681565b6040516001600160a01b0390911681526020015b60405180910390f35b610176610162366004611160565b60046020525f908152604090205460ff1681565b604051901515815260200161014b565b6100fc610194366004611160565b6106e2565b6101766101a7366004611160565b60056020525f908152604090205460ff1681565b6100fc6101c9366004611126565b610752565b6100fc6101dc366004611180565b610886565b6100fc6101ef366004611126565b6109f9565b610207610202366004611126565b610c01565b60405161014b9493929190611213565b61022a610225366004611126565b610cc9565b60405161014b9493929190611262565b6100fc610248366004611160565b610e36565b6100fc61025b366004611126565b610ea6565b335f9081526004602052604090205460ff166102ba5760405162461bcd60e51b81526020600482015260146024820152732737ba1030baba3437b934bd32b21030b236b4b760611b60448201526064015b60405180910390fd5b5f836040516102c991906112b8565b9081526020016040518091039020600201545f1461031a5760405162461bcd60e51b815260206004820152600e60248201526d416c72656164792065786973747360901b60448201526064016102b1565b6040518060800160405280836001600160a01b03168152602001828152602001428152602001600160028111156103535761035361124e565b8152505f8460405161036591906112b8565b90815260405160209181900382019020825181546001600160a01b0319166001600160a01b039091161781559082015160018201906103a49082611352565b50604082015181600201556060820151816003015f6101000a81548160ff021916908360028111156103d8576103d861124e565b0217905550905050816001600160a01b03167fc0b7ccc2fa0763fc8d3dc57b48ce4a7096c158442002f5576fd34a2e6316316984834260405161041d93929190611410565b60405180910390a2505050565b335f9081526005602052604090205460ff166104585760405162461bcd60e51b81526004016102b190611445565b600360018260405161046a91906112b8565b908152604051908190036020019020600201805460ff191660018360038111156104965761049661124e565b02179055507fcf75242a0f6076da7c5f49aeb6e620d1dd6300b820cef5313d3692f504ad538c81426040516104cc92919061147c565b60405180910390a150565b825f816040516104e791906112b8565b9081526020016040518091039020600201545f036105175760405162461bcd60e51b81526004016102b19061149d565b5f5f8560405161052791906112b8565b90815260405190819003602001902080549091506001600160a01b031633146105875760405162461bcd60e51b815260206004820152601260248201527127b7363c9031bab93932b73a1037bbb732b960711b60448201526064016102b1565b6001600382015460ff1660028111156105a2576105a261124e565b146105e85760405162461bcd60e51b815260206004820152601660248201527550726f706572747920756e646572206469737075746560501b60448201526064016102b1565b604080516080810182526001600160a01b038616815260208101859052908101600181526020014281525060018660405161062391906112b8565b90815260405160209181900382019020825181546001600160a01b0319166001600160a01b039091161781559082015160018201906106629082611352565b50604082015160028201805460ff191660018360038111156106865761068661124e565b021790555060608201518160030155905050836001600160a01b03167f0ac61f0624634fe5a60a7ba95832be48b26126a6106ca7d55f0dd29f3eb4e01f86426040516106d392919061147c565b60405180910390a25050505050565b6003546001600160a01b0316331461072f5760405162461bcd60e51b815260206004820152601060248201526f27b7363c9039bab832b91030b236b4b760811b60448201526064016102b1565b6001600160a01b03165f908152600460205260409020805460ff19166001179055565b335f9081526005602052604090205460ff166107805760405162461bcd60e51b81526004016102b190611445565b60028160405161079091906112b8565b9081526040519081900360200190206003015460ff166107df5760405162461bcd60e51b815260206004820152600a6024820152694e6f206469737075746560b01b60448201526064016102b1565b5f6002826040516107f091906112b8565b908152604051908190036020018120600301805492151560ff19909316929092179091556001905f906108249084906112b8565b908152604051908190036020019020600301805460ff191660018360028111156108505761085061124e565b02179055507f248dc8c11185b055140b2df53a83de08372b5a0c15b07619000d1ef283de64f081426040516104cc92919061147c565b815f8160405161089691906112b8565b9081526020016040518091039020600201545f036108c65760405162461bcd60e51b81526004016102b19061149d565b6002836040516108d691906112b8565b9081526040519081900360200190206003015460ff16156109325760405162461bcd60e51b81526020600482015260166024820152754469737075746520616c72656164792061637469766560501b60448201526064016102b1565b604080516080810182523381526020810184905242818301526001606082015290516002906109629086906112b8565b90815260405160209181900382019020825181546001600160a01b0319166001600160a01b039091161781559082015160018201906109a19082611352565b5060408281015160028301556060909201516003909101805460ff19169115159190911790555133907f27fa90971b66777e07575f648a5fa9d7b1b4b0a755ec7646f0307165edd5f53f9061041d908690429061147c565b335f9081526005602052604090205460ff16610a275760405162461bcd60e51b81526004016102b190611445565b805f81604051610a3791906112b8565b9081526020016040518091039020600201545f03610a675760405162461bcd60e51b81526004016102b19061149d565b5f600183604051610a7891906112b8565b90815260405190819003602001902090506001600282015460ff166003811115610aa457610aa461124e565b14610ae65760405162461bcd60e51b8152602060048201526012602482015271139bc81c195b991a5b99c81c995c5d595cdd60721b60448201526064016102b1565b5f5f84604051610af691906112b8565b90815260405190819003602001902090506001600382015460ff166002811115610b2257610b2261124e565b14610b685760405162461bcd60e51b815260206004820152601660248201527550726f706572747920756e646572206469737075746560501b60448201526064016102b1565b805482546001600160a01b031982166001600160a01b0391821617835516600180830190610b98908501826114d4565b50426002838101829055848101805460ff1916909117905583546040516001600160a01b0391821692918416917f2b7d5d7aced5166e593302dd9727416db7a0660b3608ab35df53d621a7d5545b91610bf2918a9161147c565b60405180910390a35050505050565b8051602081830181018051600282529282019190930120915280546001820180546001600160a01b039092169291610c38906112ce565b80601f0160208091040260200160405190810160405280929190818152602001828054610c64906112ce565b8015610caf5780601f10610c8657610100808354040283529160200191610caf565b820191905f5260205f20905b815481529060010190602001808311610c9257829003601f168201915b50505050600283015460039093015491929160ff16905084565b5f60605f5f845f81604051610cde91906112b8565b9081526020016040518091039020600201545f03610d0e5760405162461bcd60e51b81526004016102b19061149d565b5f5f87604051610d1e91906112b8565b9081526040805191829003602090810183206080840190925281546001600160a01b0316835260018201805491840191610d57906112ce565b80601f0160208091040260200160405190810160405280929190818152602001828054610d83906112ce565b8015610dce5780601f10610da557610100808354040283529160200191610dce565b820191905f5260205f20905b815481529060010190602001808311610db157829003601f168201915b50505091835250506002828101546020830152600383015460409092019160ff1690811115610dff57610dff61124e565b6002811115610e1057610e1061124e565b905250805160208201516040830151606090930151919a90995091975095509350505050565b6003546001600160a01b03163314610e835760405162461bcd60e51b815260206004820152601060248201526f27b7363c9039bab832b91030b236b4b760811b60448201526064016102b1565b6001600160a01b03165f908152600560205260409020805460ff19166001179055565b335f9081526005602052604090205460ff16610ed45760405162461bcd60e51b81526004016102b190611445565b805f81604051610ee491906112b8565b9081526020016040518091039020600201545f03610f145760405162461bcd60e51b81526004016102b19061149d565b600282604051610f2491906112b8565b9081526040519081900360200190206003015460ff16610f795760405162461bcd60e51b815260206004820152601060248201526f139bc8191a5cdc1d5d1948199bdd5b9960821b60448201526064016102b1565b60025f83604051610f8a91906112b8565b908152604051908190036020019020600301805460ff19166001836002811115610fb657610fb661124e565b02179055507fbcb59ea8c75acb64142d7c2739f249424fa7c5b2c69b74f91bb385f617141c528242604051610fec92919061147c565b60405180910390a15050565b634e487b7160e01b5f52604160045260245ffd5b5f82601f83011261101b575f5ffd5b813567ffffffffffffffff81111561103557611035610ff8565b604051601f8201601f19908116603f0116810167ffffffffffffffff8111828210171561106457611064610ff8565b60405281815283820160200185101561107b575f5ffd5b816020850160208301375f918101602001919091529392505050565b80356001600160a01b03811681146110ad575f5ffd5b919050565b5f5f5f606084860312156110c4575f5ffd5b833567ffffffffffffffff8111156110da575f5ffd5b6110e68682870161100c565b9350506110f560208501611097565b9150604084013567ffffffffffffffff811115611110575f5ffd5b61111c8682870161100c565b9150509250925092565b5f60208284031215611136575f5ffd5b813567ffffffffffffffff81111561114c575f5ffd5b6111588482850161100c565b949350505050565b5f60208284031215611170575f5ffd5b61117982611097565b9392505050565b5f5f60408385031215611191575f5ffd5b823567ffffffffffffffff8111156111a7575f5ffd5b6111b38582860161100c565b925050602083013567ffffffffffffffff8111156111cf575f5ffd5b6111db8582860161100c565b9150509250929050565b5f81518084528060208401602086015e5f602082860101526020601f19601f83011685010191505092915050565b6001600160a01b03851681526080602082018190525f90611236908301866111e5565b60408301949094525090151560609091015292915050565b634e487b7160e01b5f52602160045260245ffd5b6001600160a01b03851681526080602082018190525f90611285908301866111e5565b9050836040830152600383106112a957634e487b7160e01b5f52602160045260245ffd5b82606083015295945050505050565b5f82518060208501845e5f920191825250919050565b600181811c908216806112e257607f821691505b60208210810361130057634e487b7160e01b5f52602260045260245ffd5b50919050565b601f82111561134d57805f5260205f20601f840160051c8101602085101561132b5750805b601f840160051c820191505b8181101561134a575f8155600101611337565b50505b505050565b815167ffffffffffffffff81111561136c5761136c610ff8565b6113808161137a84546112ce565b84611306565b6020601f8211600181146113b5575f831561139b5750848201515b600184901b5f19600386901b1c198216175b85555061134a565b5f84815260208120601f198516915b828110156113e457878501518255602094850194600190920191016113c4565b508482101561140157868401515f19600387901b60f8161c191681555b50505050600190811b01905550565b606081525f61142260608301866111e5565b828103602084015261143481866111e5565b915050826040830152949350505050565b60208082526018908201527f4e6f7420617574686f72697a6564207265676973747261720000000000000000604082015260600190565b604081525f61148e60408301856111e5565b90508260208301529392505050565b60208082526017908201527f50726f706572747920646f6573206e6f74206578697374000000000000000000604082015260600190565b8181036114df575050565b6114e982546112ce565b67ffffffffffffffff81111561150157611501610ff8565b61150f8161137a84546112ce565b5f601f82116001811461153e575f831561139b575081850154600184901b5f19600386901b1c198216176113ad565b5f8581526020808220868352908220601f198616925b838110156115745782860154825560019586019590910190602001611554565b508583101561159157818501545f19600388901b60f8161c191681555b5050505050600190811b0190555056fea264697066735822122025139eb9c121948cf1e8e26b03459510c438d951e6c5bdeb6c5213be99efe89964736f6c634300081e0033";

    public static final String FUNC_ADDADMIN = "addAdmin";

    public static final String FUNC_ADDREGISTRAR = "addRegistrar";

    public static final String FUNC_ADMINS = "admins";

    public static final String FUNC_APPROVEDISPUTE = "approveDispute";

    public static final String FUNC_APPROVETRANSFER = "approveTransfer";

    public static final String FUNC_DISPUTES = "disputes";

    public static final String FUNC_GETPROPERTY = "getProperty";

    public static final String FUNC_RAISEDISPUTE = "raiseDispute";

    public static final String FUNC_REGISTERPROPERTY = "registerProperty";

    public static final String FUNC_REGISTRARS = "registrars";

    public static final String FUNC_REJECTTRANSFER = "rejectTransfer";

    public static final String FUNC_REQUESTTRANSFER = "requestTransfer";

    public static final String FUNC_RESOLVEDISPUTE = "resolveDispute";

    public static final String FUNC_SUPERADMIN = "superAdmin";

    public static final Event DISPUTERAISED_EVENT = new Event("DisputeRaised", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event DISPUTERESOLVED_EVENT = new Event("DisputeResolved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event PROPERTYDISPUTED_EVENT = new Event("PropertyDisputed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event PROPERTYREGISTERED_EVENT = new Event("PropertyRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFERAPPROVED_EVENT = new Event("TransferApproved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFERREJECTED_EVENT = new Event("TransferRejected", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event TRANSFERREQUESTED_EVENT = new Event("TransferRequested", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected BhoomiDarpanRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected BhoomiDarpanRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected BhoomiDarpanRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected BhoomiDarpanRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<DisputeRaisedEventResponse> getDisputeRaisedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DISPUTERAISED_EVENT, transactionReceipt);
        ArrayList<DisputeRaisedEventResponse> responses = new ArrayList<DisputeRaisedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DisputeRaisedEventResponse typedResponse = new DisputeRaisedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.raisedBy = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DisputeRaisedEventResponse getDisputeRaisedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DISPUTERAISED_EVENT, log);
        DisputeRaisedEventResponse typedResponse = new DisputeRaisedEventResponse();
        typedResponse.log = log;
        typedResponse.raisedBy = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<DisputeRaisedEventResponse> disputeRaisedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDisputeRaisedEventFromLog(log));
    }

    public Flowable<DisputeRaisedEventResponse> disputeRaisedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DISPUTERAISED_EVENT));
        return disputeRaisedEventFlowable(filter);
    }

    public static List<DisputeResolvedEventResponse> getDisputeResolvedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(DISPUTERESOLVED_EVENT, transactionReceipt);
        ArrayList<DisputeResolvedEventResponse> responses = new ArrayList<DisputeResolvedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DisputeResolvedEventResponse typedResponse = new DisputeResolvedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static DisputeResolvedEventResponse getDisputeResolvedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(DISPUTERESOLVED_EVENT, log);
        DisputeResolvedEventResponse typedResponse = new DisputeResolvedEventResponse();
        typedResponse.log = log;
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<DisputeResolvedEventResponse> disputeResolvedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getDisputeResolvedEventFromLog(log));
    }

    public Flowable<DisputeResolvedEventResponse> disputeResolvedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DISPUTERESOLVED_EVENT));
        return disputeResolvedEventFlowable(filter);
    }

    public static List<PropertyDisputedEventResponse> getPropertyDisputedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROPERTYDISPUTED_EVENT, transactionReceipt);
        ArrayList<PropertyDisputedEventResponse> responses = new ArrayList<PropertyDisputedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PropertyDisputedEventResponse typedResponse = new PropertyDisputedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PropertyDisputedEventResponse getPropertyDisputedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROPERTYDISPUTED_EVENT, log);
        PropertyDisputedEventResponse typedResponse = new PropertyDisputedEventResponse();
        typedResponse.log = log;
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<PropertyDisputedEventResponse> propertyDisputedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPropertyDisputedEventFromLog(log));
    }

    public Flowable<PropertyDisputedEventResponse> propertyDisputedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROPERTYDISPUTED_EVENT));
        return propertyDisputedEventFlowable(filter);
    }

    public static List<PropertyRegisteredEventResponse> getPropertyRegisteredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROPERTYREGISTERED_EVENT, transactionReceipt);
        ArrayList<PropertyRegisteredEventResponse> responses = new ArrayList<PropertyRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PropertyRegisteredEventResponse typedResponse = new PropertyRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.documentHash = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PropertyRegisteredEventResponse getPropertyRegisteredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROPERTYREGISTERED_EVENT, log);
        PropertyRegisteredEventResponse typedResponse = new PropertyRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.documentHash = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<PropertyRegisteredEventResponse> propertyRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPropertyRegisteredEventFromLog(log));
    }

    public Flowable<PropertyRegisteredEventResponse> propertyRegisteredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROPERTYREGISTERED_EVENT));
        return propertyRegisteredEventFlowable(filter);
    }

    public static List<TransferApprovedEventResponse> getTransferApprovedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFERAPPROVED_EVENT, transactionReceipt);
        ArrayList<TransferApprovedEventResponse> responses = new ArrayList<TransferApprovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferApprovedEventResponse typedResponse = new TransferApprovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.oldOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferApprovedEventResponse getTransferApprovedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFERAPPROVED_EVENT, log);
        TransferApprovedEventResponse typedResponse = new TransferApprovedEventResponse();
        typedResponse.log = log;
        typedResponse.oldOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<TransferApprovedEventResponse> transferApprovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferApprovedEventFromLog(log));
    }

    public Flowable<TransferApprovedEventResponse> transferApprovedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERAPPROVED_EVENT));
        return transferApprovedEventFlowable(filter);
    }

    public static List<TransferRejectedEventResponse> getTransferRejectedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFERREJECTED_EVENT, transactionReceipt);
        ArrayList<TransferRejectedEventResponse> responses = new ArrayList<TransferRejectedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferRejectedEventResponse typedResponse = new TransferRejectedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferRejectedEventResponse getTransferRejectedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFERREJECTED_EVENT, log);
        TransferRejectedEventResponse typedResponse = new TransferRejectedEventResponse();
        typedResponse.log = log;
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<TransferRejectedEventResponse> transferRejectedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferRejectedEventFromLog(log));
    }

    public Flowable<TransferRejectedEventResponse> transferRejectedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERREJECTED_EVENT));
        return transferRejectedEventFlowable(filter);
    }

    public static List<TransferRequestedEventResponse> getTransferRequestedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFERREQUESTED_EVENT, transactionReceipt);
        ArrayList<TransferRequestedEventResponse> responses = new ArrayList<TransferRequestedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferRequestedEventResponse typedResponse = new TransferRequestedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static TransferRequestedEventResponse getTransferRequestedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(TRANSFERREQUESTED_EVENT, log);
        TransferRequestedEventResponse typedResponse = new TransferRequestedEventResponse();
        typedResponse.log = log;
        typedResponse.newOwner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.propertyCode = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<TransferRequestedEventResponse> transferRequestedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getTransferRequestedEventFromLog(log));
    }

    public Flowable<TransferRequestedEventResponse> transferRequestedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFERREQUESTED_EVENT));
        return transferRequestedEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addAdmin(String _admin) {
        final Function function = new Function(
                FUNC_ADDADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _admin)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addRegistrar(String _registrar) {
        final Function function = new Function(
                FUNC_ADDREGISTRAR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _registrar)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> admins(String param0) {
        final Function function = new Function(FUNC_ADMINS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approveDispute(String _propertyCode) {
        final Function function = new Function(
                FUNC_APPROVEDISPUTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approveTransfer(String _propertyCode) {
        final Function function = new Function(
                FUNC_APPROVETRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<String, String, BigInteger, Boolean>> disputes(String param0) {
        final Function function = new Function(FUNC_DISPUTES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}));
        return new RemoteFunctionCall<Tuple4<String, String, BigInteger, Boolean>>(function,
                new Callable<Tuple4<String, String, BigInteger, Boolean>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, Boolean> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, BigInteger, Boolean>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Tuple4<String, String, BigInteger, BigInteger>> getProperty(String _propertyCode) {
        final Function function = new Function(FUNC_GETPROPERTY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint8>() {}));
        return new RemoteFunctionCall<Tuple4<String, String, BigInteger, BigInteger>>(function,
                new Callable<Tuple4<String, String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> raiseDispute(String _propertyCode, String _evidenceHash) {
        final Function function = new Function(
                FUNC_RAISEDISPUTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode), 
                new org.web3j.abi.datatypes.Utf8String(_evidenceHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> registerProperty(String _propertyCode, String _owner, String _documentHash) {
        final Function function = new Function(
                FUNC_REGISTERPROPERTY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode), 
                new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Utf8String(_documentHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> registrars(String param0) {
        final Function function = new Function(FUNC_REGISTRARS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> rejectTransfer(String _propertyCode) {
        final Function function = new Function(
                FUNC_REJECTTRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> requestTransfer(String _propertyCode, String _newOwner, String _newDocumentHash) {
        final Function function = new Function(
                FUNC_REQUESTTRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode), 
                new org.web3j.abi.datatypes.Address(160, _newOwner), 
                new org.web3j.abi.datatypes.Utf8String(_newDocumentHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> resolveDispute(String _propertyCode) {
        final Function function = new Function(
                FUNC_RESOLVEDISPUTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_propertyCode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> superAdmin() {
        final Function function = new Function(FUNC_SUPERADMIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    @Deprecated
    public static BhoomiDarpanRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new BhoomiDarpanRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static BhoomiDarpanRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new BhoomiDarpanRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static BhoomiDarpanRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new BhoomiDarpanRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static BhoomiDarpanRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new BhoomiDarpanRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<BhoomiDarpanRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(BhoomiDarpanRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<BhoomiDarpanRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(BhoomiDarpanRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<BhoomiDarpanRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(BhoomiDarpanRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<BhoomiDarpanRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(BhoomiDarpanRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class DisputeRaisedEventResponse extends BaseEventResponse {
        public String raisedBy;

        public String propertyCode;

        public BigInteger timestamp;
    }

    public static class DisputeResolvedEventResponse extends BaseEventResponse {
        public String propertyCode;

        public BigInteger timestamp;
    }

    public static class PropertyDisputedEventResponse extends BaseEventResponse {
        public String propertyCode;

        public BigInteger timestamp;
    }

    public static class PropertyRegisteredEventResponse extends BaseEventResponse {
        public String owner;

        public String propertyCode;

        public String documentHash;

        public BigInteger timestamp;
    }

    public static class TransferApprovedEventResponse extends BaseEventResponse {
        public String oldOwner;

        public String newOwner;

        public String propertyCode;

        public BigInteger timestamp;
    }

    public static class TransferRejectedEventResponse extends BaseEventResponse {
        public String propertyCode;

        public BigInteger timestamp;
    }

    public static class TransferRequestedEventResponse extends BaseEventResponse {
        public String newOwner;

        public String propertyCode;

        public BigInteger timestamp;
    }
}
