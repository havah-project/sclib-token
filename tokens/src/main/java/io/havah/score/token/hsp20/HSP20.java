/*
 * Copyright 2022 HAVAH Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.havah.score.token.hsp20;

import score.Address;
import score.annotation.Optional;

import java.math.BigInteger;

public interface HSP20 {
    // ================================================
    // Event Logs
    // ================================================

    /**
     * (EventLog) Must trigger on any successful token transfers.
     */
    void Transfer(Address _from, Address _to, BigInteger _value);

    /**
     * (EventLog) Must trigger on any successful call to {@code approve(Address, int)}.
     */
    void Approval(Address _owner, Address _spender, BigInteger _value);

    // ================================================
    // External methods
    // ================================================

    /**
     * Returns the total token supply.
     */
    BigInteger totalSupply();

    /**
     * Returns the account balance of another account with address {@code _owner}.
     */
    BigInteger balanceOf(Address _account);

    /**
     * Transfers {@code _amount} amount of tokens to address {@code _to}, and MUST fire the {@code Transfer} event.
     * This function SHOULD throw if the caller account balance does not have enough tokens to spend.
     * If {@code _to} is a contract, this function MUST invoke the function {@code tokenFallback(Address, int, bytes)}
     * in {@code _to}. If the {@code tokenFallback} function is not implemented in {@code _to} (receiver contract),
     * then the transaction must fail and the transfer of tokens should not occur.
     * If {@code _to} is an externally owned address, then the transaction must be sent without trying to execute
     * {@code tokenFallback} in {@code _to}.
     */
    boolean transfer(Address _to, BigInteger _amount);

    /**
     * Returns the amount which {@code _spender} is still allowed to withdraw from {@code _owner}.
     */
    BigInteger allowance(Address _owner, Address _spender);

    /**
     * Allows {@code _spender} to withdraw from your account  multiple times, up to the {@code _value} amount.
     * If this function is called again it overwrites the current allowance with _value.
     */
    boolean approve(Address _spender, BigInteger _amount);

    /**
     * Transfers the ownership of an NFT from one address to another address, and MUST fire the {@code Transfer} event.
     * Throws unless the caller is the current owner or the approved address for the NFT.
     * Throws if {@code _from} is not the current owner.
     * Throws if {@code _to} is the zero address.
     */
    boolean transferFrom(Address _from, Address _to, BigInteger _amount);
}
