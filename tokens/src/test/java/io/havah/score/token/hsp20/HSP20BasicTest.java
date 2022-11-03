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

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import score.Address;
import score.Context;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HSP20BasicTest extends TestBase {
    private static final String name = "MyHSP20Token";
    private static final String symbol = "MIT";
    private static final int decimals = 18;
    private static final BigInteger initialSupply = BigInteger.valueOf(1000);

    private static final BigInteger totalSupply = initialSupply.multiply(TEN.pow(decimals));
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private static Score tokenScore;

    public static class HSP20BasicToken extends HSP20Basic {
        public HSP20BasicToken(String _name, String _symbol, int _decimals, BigInteger _totalSupply) {
            super(_name, _symbol, _decimals);
            _mint(Context.getCaller(), _totalSupply);
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        tokenScore = sm.deploy(owner, HSP20BasicToken.class,
                name, symbol, decimals, totalSupply);
        owner.addBalance(symbol, totalSupply);
    }

    @Test
    void name() {
        assertEquals(name, tokenScore.call("name"));
    }

    @Test
    void symbol() {
        assertEquals(symbol, tokenScore.call("symbol"));
    }

    @Test
    void decimals() {
        assertEquals(BigInteger.valueOf(decimals), tokenScore.call("decimals"));
    }

    @Test
    void totalSupply() {
        assertEquals(totalSupply, tokenScore.call("totalSupply"));
    }

    @Test
    void balanceOf() {
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", tokenScore.getOwner().getAddress()));
    }

    @Test
    void transfer() {
        Account alice = sm.createAccount();
        BigInteger value = TEN.pow(decimals);
        _transfer(owner, alice.getAddress(), value);

        // transfer self
        tokenScore.invoke(alice, "transfer", alice.getAddress(), value);
        assertEquals(value, tokenScore.call("balanceOf", alice.getAddress()));
    }

    void _transfer(Account owner, Address to, BigInteger value) {
        tokenScore.invoke(owner, "transfer", to, value);
        owner.subtractBalance(symbol, value);
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", tokenScore.getOwner().getAddress()));
        assertEquals(value,
                tokenScore.call("balanceOf", to));
    }

    @Test
    void approve() {
        Account alice = sm.createAccount();
        BigInteger value = TEN.pow(decimals);
        _transfer(owner, alice.getAddress(), value);
        _approve(alice, owner.getAddress(), value);
    }

    void _approve(Account owner, Address to, BigInteger value) {
        tokenScore.invoke(owner, "approve", to, value);
        BigInteger allowance = (BigInteger)tokenScore.call("allowance", owner.getAddress(), to);
        assertEquals(value, allowance);
    }

    @Test
    void transferFrom() {
        Account alice = sm.createAccount();
        BigInteger value = TEN.pow(decimals);
        _transfer(owner, alice.getAddress(), value);

        // before approve
        Account bob = sm.createAccount();
        BigInteger allowance = ONE.pow(decimals);
        assertThrows(AssertionError.class, () ->
                tokenScore.invoke(owner, "transferFrom", alice.getAddress(), bob.getAddress(), allowance));
        assertEquals(value,
                tokenScore.call("balanceOf", alice.getAddress()));
        assertEquals(BigInteger.ZERO,
                tokenScore.call("balanceOf", bob.getAddress()));

        // approve
        _approve(alice, owner.getAddress(), allowance);

        // after approve
        tokenScore.invoke(owner, "transferFrom", alice.getAddress(), bob.getAddress(), allowance);
        assertEquals(value.subtract(allowance),
                tokenScore.call("balanceOf", alice.getAddress()));
        assertEquals(allowance,
                tokenScore.call("balanceOf", bob.getAddress()));
    }
}
