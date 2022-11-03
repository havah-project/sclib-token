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

import io.havah.score.token.hsp20.extensions.HSP20Metadata;
import score.*;
import score.annotation.EventLog;
import score.annotation.External;

import java.math.BigInteger;

public abstract class HSP20Basic implements HSP20, HSP20Metadata {
    protected static final Address ZERO_ADDRESS = new Address(new byte[Address.LENGTH]);
    private final VarDB<String> name = Context.newVarDB("token_name", String.class);
    private final VarDB<String> symbol = Context.newVarDB("token_symbol", String.class);
    private final VarDB<BigInteger> decimals = Context.newVarDB("decimals", BigInteger.class);
    private final VarDB<BigInteger> totalSupply = Context.newVarDB("total_supply", BigInteger.class);
    private final DictDB<Address, BigInteger> balances = Context.newDictDB("balances", BigInteger.class);
    private final BranchDB<Address, DictDB<Address, BigInteger>> allowances = Context.newBranchDB("allowances", BigInteger.class);

    public HSP20Basic(String _name, String _symbol, int _decimals) {
        // initialize values only at first deployment
        if (this.name.get() == null) {
            this.name.set(ensureNotEmpty(_name));
            this.symbol.set(ensureNotEmpty(_symbol));

            // decimals must be larger than 0 and less than 21
            Context.require(_decimals >= 0, "decimals needs to be positive");
            Context.require(_decimals <= 21, "decimals needs to be equal or lower than 21");
            this.decimals.set(BigInteger.valueOf(_decimals));
        }
    }

    private String ensureNotEmpty(String str) {
        Context.require(str != null && !str.trim().isEmpty(), "str is null or empty");
        assert str != null;
        return str.trim();
    }

    @External(readonly=true)
    public String name() {
        return name.get();
    }

    @External(readonly=true)
    public String symbol() {
        return symbol.get();
    }

    @External(readonly=true)
    public BigInteger decimals() {
        return decimals.get();
    }

    @External(readonly=true)
    public BigInteger totalSupply() {
        return totalSupply.getOrDefault(BigInteger.ZERO);
    }

    @External(readonly=true)
    public BigInteger balanceOf(Address _account) {
        return balances.getOrDefault(_account, BigInteger.ZERO);
    }

    private void safeSetBalance(Address owner, BigInteger amount) {
        balances.set(owner, amount);
    }

    private void transfer(Address from, Address to, BigInteger amount) {
        // check some basic requirements
        Context.require(amount.compareTo(BigInteger.ZERO) >= 0, "_value needs to be positive");
        Context.require(balanceOf(from).compareTo(amount) >= 0, "Insufficient balance");

        // adjust the balances
        safeSetBalance(from, balanceOf(from).subtract(amount));
        safeSetBalance(to, balanceOf(to).add(amount));

        // emit Transfer event first
        Transfer(from, to, amount);
    }

    @External
    public boolean transfer(Address _to, BigInteger _amount) {
        Address _from = Context.getCaller();
        transfer(_from, _to, _amount);
        return true;
    }

    /**
     * Creates `amount` tokens and assigns them to `owner`, increasing the total supply.
     */
    protected void _mint(Address owner, BigInteger amount) {
        Context.require(!ZERO_ADDRESS.equals(owner), "Owner address cannot be zero address");
        Context.require(amount.compareTo(BigInteger.ZERO) >= 0, "amount needs to be positive");

        totalSupply.set(totalSupply().add(amount));
        safeSetBalance(owner, balanceOf(owner).add(amount));
        Transfer(ZERO_ADDRESS, owner, amount);
    }

    /**
     * Destroys `amount` tokens from `owner`, reducing the total supply.
     */
    protected void _burn(Address owner, BigInteger amount) {
        Context.require(!ZERO_ADDRESS.equals(owner), "Owner address cannot be zero address");
        Context.require(amount.compareTo(BigInteger.ZERO) >= 0, "amount needs to be positive");
        Context.require(balanceOf(owner).compareTo(amount) >= 0, "Insufficient balance");

        safeSetBalance(owner, balanceOf(owner).subtract(amount));
        totalSupply.set(totalSupply().subtract(amount));
        Transfer(owner, ZERO_ADDRESS, amount);
    }

    private void _approve(Address owner, Address spender, BigInteger value) {
        Context.require(!ZERO_ADDRESS.equals(owner), "approve from the zero address");
        Context.require(!ZERO_ADDRESS.equals(spender), "approve to the zero address");

        allowances.at(owner).set(spender, value);
        Approval(owner, spender, value);
    }

    @External(readonly = true)
    public boolean approve(Address _spender, BigInteger _amount) {
        Address owner = Context.getCaller();
        _approve(owner, _spender, _amount);
        return true;
    }

    @External(readonly = true)
    public BigInteger allowance(Address _owner, Address _spender) {
        return allowances.at(_owner).getOrDefault(_spender, BigInteger.ZERO);
    }

    void _spendAllowance(Address owner, Address spender, BigInteger amount) {
        BigInteger currentAllowance = allowance(owner, spender);
        BigInteger reminder = currentAllowance.subtract(amount);
        Context.require(reminder.signum() >= 0, "insufficient allowance");
        _approve(owner, spender,  reminder);
    }

    @External
    public boolean transferFrom(Address _from, Address _to, BigInteger _amount) {
        Address spender = Context.getCaller();
        _spendAllowance(_from, spender, _amount);
        transfer(_from, _to, _amount);
        return true;
    }

    @EventLog(indexed=3)
    public void Transfer(Address _from, Address _to, BigInteger _value) {}

    @EventLog(indexed=3)
    public void Approval(Address _owner, Address _spender, BigInteger _value) {}
}
