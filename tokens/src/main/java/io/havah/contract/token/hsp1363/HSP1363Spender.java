package io.havah.contract.token.hsp1363;

import score.Address;

import java.math.BigInteger;

public interface HSP1363Spender {
    /**
     * @notice Handle the approval of HSP1363 tokens
     * @dev Any HSP1363 smart contract calls this function on the recipient
     * after an `approve`. This function MAY throw to revert and reject the
     * approval. Return of other than true MUST result in the
     * transaction being reverted.
     * Note: the token contract address is always the message sender.
     * @param _owner address The address which called `approveAndCall` function
     * @param _value BigInteger The amount of tokens to be spent
     * @param _data bytes Additional data with no specified format
     * @return true unless throwing
     */
    boolean onApprovalReceived(Address _owner, BigInteger _value, byte[] _data);
}
