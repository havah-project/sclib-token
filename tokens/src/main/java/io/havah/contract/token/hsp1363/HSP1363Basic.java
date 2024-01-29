package io.havah.contract.token.hsp1363;

import io.havah.contract.token.hsp20.HSP20Basic;
import score.Address;
import score.Context;
import score.annotation.External;
import score.annotation.Optional;

import java.math.BigInteger;

public class HSP1363Basic extends HSP20Basic implements HSP1363 {
    public HSP1363Basic(String _name, String _symbol, int _decimals) {
        // initialize values only at first deployment
        super(_name, _symbol, _decimals);
    }

    private boolean _checkOnTransferReceived(Address sender, Address recipient, BigInteger amount, @Optional byte[] _data) {
        return Context.call(Boolean.class, recipient, "onTransferReceived", Context.getCaller(), sender, amount, _data == null ? new byte[]{} : _data);
    }

    private boolean _checkOnApprovalReceived(Address spender, BigInteger amount, @Optional byte[] _data) {
        return Context.call(Boolean.class, spender, "onApprovalReceived", Context.getCaller(), amount, _data == null ? new byte[]{} : _data);
    }

    @External
    public boolean transferAndCall(Address _to, BigInteger _value, @Optional byte[] _data) {
        Context.require(_to.isContract(), "transfer to non contract address");
        transfer(_to, _value);
        Context.require(_checkOnTransferReceived(Context.getCaller(), _to, _value, _data), "receiver returned false");
        return true;
    }

    @External
    public boolean transferFromAndCall(Address _from, Address _to, BigInteger _value, @Optional byte[] _data) {
        Context.require(_to.isContract(), "transfer to non contract address");
        transferFrom(_from, _to, _value);
        Context.require(_checkOnTransferReceived(_from, _to, _value, _data), "receiver returned false");
        return true;
    }

    @External
    public boolean approveAndCall(Address _spender, BigInteger _value, @Optional byte[] _data) {
        Context.require(_spender.isContract(), "approval to non contract address");
        approve(_spender, _value);
        Context.require(_checkOnApprovalReceived(_spender, _value, _data), "receiver returned false");
        return true;
    }
}
