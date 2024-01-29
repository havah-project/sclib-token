package io.havah.contract.token.hsp1363;

import score.Address;
import score.Context;
import score.annotation.External;

import java.math.BigInteger;

public class HSP1363SampleReceiver implements HSP1363Receiver, HSP1363Spender {

    @External(readonly = true)
    public String name() {
        return "SampleReceiverSpender";
    }

    @Override
    public boolean onTransferReceived(Address _operator, Address _from, BigInteger _value, byte[] _data) {
        return true;
    }

    @Override
    public boolean onApprovalReceived(Address _owner, BigInteger _value, byte[] _data) {
        return true;
    }
}
