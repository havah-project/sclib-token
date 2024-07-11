package io.havah.contract.token.hsp1363;

import score.Address;

import java.math.BigInteger;

public interface HSP1363Receiver {
    /**
     * Handle the receipt of HSP1363 tokens
     * Any HSP1363 smart contract calls this function on the recipient
     * after a `transfer` or a `transferFrom`. This function MAY throw to revert and reject the
     * transfer. Return of other than true MUST result in the
     * transaction being reverted.
     * Note: the token contract address is always the message sender.
     * @param _operator address The address which called `transferAndCall` or `transferFromAndCall` function
     * @param _from address The address which are token transferred from
     * @param _value BigInteger The amount of tokens transferred
     * @param _data bytes Additional data with no specified format
     * @return true unless throwing
     */
    boolean onTransferReceived(Address _operator, Address _from, BigInteger _value, byte[] _data);
}
