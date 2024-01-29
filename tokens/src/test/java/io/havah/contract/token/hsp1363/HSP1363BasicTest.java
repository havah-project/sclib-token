package io.havah.contract.token.hsp1363;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import score.Address;
import score.Context;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HSP1363BasicTest extends TestBase {
    private static final String name = "MyHSP1363Token";
    private static final String symbol = "MYT";
    private static final int decimals = 18;
    private static final BigInteger initialSupply = BigInteger.valueOf(1000);

    private static final BigInteger totalSupply = initialSupply.multiply(BigInteger.TEN.pow(decimals));
    private static final ServiceManager sm = getServiceManager();
    private static final Account owner = sm.createAccount();
    private static Score tokenScore;
    private static Score receiverScore;

    public static class HSP1363BasicToken extends HSP1363Basic {
        public HSP1363BasicToken(String _name, String _symbol, int _decimals, BigInteger _totalSupply) {
            super(_name, _symbol, _decimals);
            _mint(Context.getCaller(), _totalSupply);
        }
    }

    @BeforeAll
    public static void setup() throws Exception {
        tokenScore = sm.deploy(owner, HSP1363BasicToken.class,
                name, symbol, decimals, totalSupply);
        owner.addBalance(symbol, totalSupply);

        receiverScore = sm.deploy(owner, HSP1363SampleReceiver.class);
    }

    void _transfer(Account owner, Address to, BigInteger value) {
        BigInteger balance = (BigInteger) tokenScore.call("balanceOf", to);
        tokenScore.invoke(owner, "transfer", to, value);
        owner.subtractBalance(symbol, value);
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", owner.getAddress()));
        assertEquals(balance.add(value),
                tokenScore.call("balanceOf", to));
    }

    void _transferAndCall(Account owner, Address to, BigInteger value, byte[] data) {
        BigInteger balance = (BigInteger) tokenScore.call("balanceOf", to);
        tokenScore.invoke(owner, "transferAndCall", to, value, data);
        owner.subtractBalance(symbol, value);
        assertEquals(owner.getBalance(symbol),
                tokenScore.call("balanceOf", owner.getAddress()));
        assertEquals(balance.add(value),
                tokenScore.call("balanceOf", to));
    }

    void _transferFromAndCall(Account owner, Account from, Address to, BigInteger value, byte[] data) {
        BigInteger balance = (BigInteger) tokenScore.call("balanceOf", to);
        tokenScore.invoke(owner, "transferFromAndCall", from.getAddress(), to, value, data);
        from.subtractBalance(symbol, value);
        assertEquals(from.getBalance(symbol),
                tokenScore.call("balanceOf", from.getAddress()));
        assertEquals(balance.add(value),
                tokenScore.call("balanceOf", to));
    }

    void _approve(Account owner, Address to, BigInteger value) {
        tokenScore.invoke(owner, "approve", to, value);
    }

    void _approveAndCall(Account owner, Address to, BigInteger value, byte[] data) {
        tokenScore.invoke(owner, "approveAndCall", to, value, data);
    }

    @Test
    void transferAndCall() {
        Account alice = sm.createAccount();
        BigInteger value = BigInteger.TEN.pow(decimals);
        _transfer(owner, alice.getAddress(), value);
        alice.addBalance(symbol, value);

        byte[] data = "Hello".getBytes();

        Account bob = sm.createAccount();
        assertThrows(AssertionError.class, () ->
                tokenScore.invoke(alice, "transferAndCall", bob.getAddress(), value, data));
        assertEquals(value, tokenScore.call("balanceOf", alice.getAddress()));
        assertEquals(BigInteger.ZERO, tokenScore.call("balanceOf", bob.getAddress()));

        // transferAndCall
        _transferAndCall(alice, receiverScore.getAddress(), value, data);
        assertEquals(BigInteger.ZERO, tokenScore.call("balanceOf", alice.getAddress()));
    }

    @Test
    void transferFromAndCall() {
        Account alice = sm.createAccount();
        Account bob = sm.createAccount();
        Account carry = sm.createAccount();
        BigInteger value = BigInteger.TEN.pow(decimals);
        _transfer(owner, alice.getAddress(), value);
        alice.addBalance(symbol, value);

        // approve
        _approve(alice, bob.getAddress(), value);
        assertEquals(value, tokenScore.call("allowance", alice.getAddress(), bob.getAddress()));

        byte[] data = "Hello".getBytes();
        assertThrows(AssertionError.class, () ->
                tokenScore.invoke(bob, "transferFromAndCall", alice.getAddress(), carry.getAddress(), value, data));
        assertEquals(value, tokenScore.call("balanceOf", alice.getAddress()));
        assertEquals(BigInteger.ZERO, tokenScore.call("balanceOf", carry.getAddress()));

        // transferFromAndCall
        _transferFromAndCall(bob, alice, receiverScore.getAddress(), value, data);
        assertEquals(BigInteger.ZERO, tokenScore.call("balanceOf", alice.getAddress()));
    }

    @Test
    void approveAndCallTest() {
        Account alice = sm.createAccount();
        BigInteger value = BigInteger.TEN.pow(decimals);
        _transfer(owner, alice.getAddress(), value);
        alice.addBalance(symbol, value);

        byte[] data = "Hello".getBytes();

        Account bob = sm.createAccount();
        assertThrows(AssertionError.class, () ->
                tokenScore.invoke(alice, "approveAndCall", bob.getAddress(), value, data));

        // approveAndCall
        _approveAndCall(alice, receiverScore.getAddress(), value, data);
    }
}
