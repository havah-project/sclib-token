package io.havah.contract.token.hsp1363;

import io.havah.contract.token.hsp20.HSP20;
import score.Address;
import score.annotation.Optional;

import java.math.BigInteger;

public interface HSP1363 extends HSP20 {

    /**
     * @notice Transfer tokens from `Context.getCaller()` to another address and then call `onTransferReceived` on receiver
     * @param _to address The address which you want to transfer to
     * @param _value BigInteger The amount of tokens to be transferred
     * @param _data bytes Additional data with no specified format, sent in call to `_to`
     * @return true unless throwing
     */
    boolean transferAndCall(Address _to, BigInteger _value, @Optional byte[] _data);


    /**
     * @notice Transfer tokens from one address to another and then call `onTransferReceived` on receiver
     * @param _from address The address which you want to send tokens from
     * @param _to address The address which you want to transfer to
     * @param _value BigInteger The amount of tokens to be transferred
     * @param _data bytes Additional data with no specified format, sent in call to `_to`
     * @return true unless throwing
     */
    boolean transferFromAndCall(Address _from, Address _to, BigInteger _value, @Optional byte[] _data);

    /**
     * @notice Approve the passed address to spend the specified amount of tokens on behalf of caller
     * and then call `onApprovalReceived` on spender.
     * @param _spender address The address which will spend the funds
     * @param _value BigInteger The amount of tokens to be spent
     * @param _data bytes Additional data with no specified format, sent in call to `_spender`
     * @return true unless throwing
     */
    boolean approveAndCall(Address _spender, BigInteger _value, @Optional byte[] _data);
}

