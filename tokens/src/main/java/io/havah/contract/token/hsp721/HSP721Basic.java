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

 package io.havah.contract.token.hsp721;

 import io.havah.contract.token.hsp721.extensions.HSP721Enumerable;
 import io.havah.contract.token.hsp721.extensions.HSP721Metadata;
 import io.havah.contract.util.EnumerableMap;
 import io.havah.contract.util.IntSet;
 import score.*;
 import score.annotation.EventLog;
 import score.annotation.External;
 import score.annotation.Optional;

 import java.math.BigInteger;

 public abstract class HSP721Basic implements HSP721, HSP721Metadata, HSP721Enumerable {
     protected static final Address ZERO_ADDRESS = new Address(new byte[Address.LENGTH]);
     private final VarDB<String> name = Context.newVarDB("name", String.class);
     private final VarDB<String> symbol = Context.newVarDB("symbol", String.class);
     protected final DictDB<Address, IntSet> holderTokens = Context.newDictDB("holders", IntSet.class);
     protected final EnumerableMap<BigInteger, Address> tokenOwners = new EnumerableMap<>("owners", BigInteger.class, Address.class);
     protected final DictDB<BigInteger, Address> tokenApprovals = Context.newDictDB("token_approvals", Address.class);
     protected final BranchDB<Address, DictDB<Address, Boolean>> operatorApprovals = Context.newBranchDB("operator_approvals", Boolean.class);
     // id => token URI
     protected final DictDB<BigInteger, String> tokenURIs = Context.newDictDB("token_uri", String.class);

     public HSP721Basic(String _name, String _symbol) {
         // initialize values only at first deployment
         if (this.name.get() == null) {
             this.name.set(_name);
             this.symbol.set(_symbol);
         }
     }

     @External(readonly = true)
     public String name() {
         return name.get();
     }

     @External(readonly = true)
     public String symbol() {
         return symbol.get();
     }

     @External(readonly = true)
     public String tokenURI(BigInteger _tokenId) {
         return tokenURIs.get(_tokenId);
     }

     @External(readonly = true)
     public int balanceOf(Address _owner) {
         Context.require(!ZERO_ADDRESS.equals(_owner), "Owner address cannot be zero address");
         var tokens = holderTokens.get(_owner);
         return (tokens != null) ? tokens.length() : 0;
     }

     @External(readonly = true)
     public Address ownerOf(BigInteger _tokenId) {
         return tokenOwners.getOrThrow(_tokenId, "Non-existent token");
     }

     private boolean isApprovedOrOwner(Address spender, BigInteger tokenId) {
         Address owner = ownerOf(tokenId);
         return (spender.equals(owner) || isApprovedForAll(owner, spender) || getApproved(tokenId).equals(spender));
     }

     protected void _setTokenURI(BigInteger _id, String _uri) {
         Context.require(_uri.length() > 0, "Uri should be set");
         tokenURIs.set(_id, _uri);
     }

     private boolean checkOnHSP721Received(Address from, Address to,
                                           BigInteger tokenId, byte[] data) {
         boolean result = true;
         if (to.isContract()) {
             result = (boolean) Context.call(to, "onHSP721Received", Context.getCaller(), from, tokenId, data);
         }
         return result;
     }

     private void safeTransfer(Address from, Address to, BigInteger tokenId, byte[] data) {
         _transfer(from, to, tokenId);
         Context.require(checkOnHSP721Received(from, to, tokenId, data == null ? new byte[]{} : data));
     }

     @External
     public void safeTransferFrom(Address _from, Address _to, BigInteger _tokenId, @Optional byte[] _data) {
         Context.require(isApprovedOrOwner(Context.getCaller(), _tokenId), "caller is not token owner or approved");
         safeTransfer(_from, _to, _tokenId, _data);
     }

     @External(readonly = true)
     public Address getApproved(BigInteger _tokenId) {
         return tokenApprovals.getOrDefault(_tokenId, ZERO_ADDRESS);
     }

     @External(readonly = true)
     public boolean isApprovedForAll(Address _owner, Address _operator) {
         return operatorApprovals.at(_owner).getOrDefault(_operator, false);
     }

     @External
     public void approve(Address _approved, BigInteger _tokenId) {
         Address owner = ownerOf(_tokenId);
         Context.require(!owner.equals(_approved), "Cannot approve owner");
         Context.require(owner.equals(Context.getCaller()), "Only owner can call this method");
         _approve(_approved, _tokenId);
     }

     @External
     public void setApprovalForAll(Address _operator, boolean _approved) {
         Address owner = Context.getCaller();
         Context.require(!owner.equals(_operator), "approve to caller");
         operatorApprovals.at(owner).set(_operator, _approved);
         ApprovalForAll(owner, _operator, _approved);
     }

     private void _approve(Address approved, BigInteger tokenId) {
         tokenApprovals.set(tokenId, approved);
         Approval(ownerOf(tokenId), approved, tokenId);
     }

     @External
     public void transferFrom(Address _from, Address _to, BigInteger _tokenId) {
         Context.require(isApprovedOrOwner(Context.getCaller(), _tokenId), "caller is not token owner or approved");
         _transfer(_from, _to, _tokenId);
     }

     private void _transfer(Address from, Address to, BigInteger tokenId) {
         Context.require(ownerOf(tokenId).equals(from), "from address is not owner");
         Context.require(!to.equals(ZERO_ADDRESS), "destination address cannot be zero address");
         // clear approvals from the previous owner
         _approve(ZERO_ADDRESS, tokenId);

         _removeTokenFrom(tokenId, from);
         _addTokenTo(tokenId, to);
         tokenOwners.set(tokenId, to);
         Transfer(from, to, tokenId);
     }

     /**
      * (Extension) Returns the total amount of tokens stored by the contract.
      */
     @External(readonly = true)
     public int totalSupply() {
         return tokenOwners.length();
     }

     /**
      * (Extension) Returns a token ID at a given index of all the tokens stored by the contract.
      * Use along with {@code _totalSupply} to enumerate all tokens.
      */
     @External(readonly = true)
     public BigInteger tokenByIndex(int _index) {
         return tokenOwners.getKey(_index);
     }

     /**
      * (Extension) Returns a token ID owned by owner at a given index of its token list.
      * Use along with {@code balanceOf} to enumerate all of owner's tokens.
      */
     @External(readonly = true)
     public BigInteger tokenOfOwnerByIndex(Address _owner, int _index) {
         var tokens = holderTokens.get(_owner);
         return (tokens != null) ? tokens.at(_index) : BigInteger.ZERO;
     }

     /**
      * Mints `tokenId` and transfers it to `to`.
      */
     protected void _mint(Address to, BigInteger tokenId) {
         Context.require(!ZERO_ADDRESS.equals(to), "Destination address cannot be zero address");
         Context.require(!_tokenExists(tokenId), "Token already exists");

         _addTokenTo(tokenId, to);
         tokenOwners.set(tokenId, to);
         Transfer(ZERO_ADDRESS, to, tokenId);
     }

     /**
      * Destroys `tokenId`.
      */
     protected void _burn(BigInteger tokenId) {
         Address owner = ownerOf(tokenId);
         // clear approvals
         _approve(ZERO_ADDRESS, tokenId);

         _removeTokenFrom(tokenId, owner);
         tokenOwners.remove(tokenId);
         Transfer(owner, ZERO_ADDRESS, tokenId);
     }

     protected boolean _tokenExists(BigInteger tokenId) {
         return tokenOwners.contains(tokenId);
     }

     private void _addTokenTo(BigInteger tokenId, Address to) {
         var tokens = holderTokens.get(to);
         if (tokens == null) {
             tokens = new IntSet(to.toString());
             holderTokens.set(to, tokens);
         }
         tokens.add(tokenId);
     }

     private void _removeTokenFrom(BigInteger tokenId, Address from) {
         var tokens = holderTokens.get(from);
         Context.require(tokens != null, "tokens don't exist for this address");
         tokens.remove(tokenId);
         if (tokens.length() == 0) {
             holderTokens.set(from, null);
         }
     }

     @EventLog(indexed = 3)
     public void Transfer(Address _from, Address _to, BigInteger _tokenId) {
     }

     @EventLog(indexed = 3)
     public void Approval(Address _owner, Address _approved, BigInteger _tokenId) {
     }

     @EventLog(indexed = 2)
     public void ApprovalForAll(Address _owner, Address _operator, boolean _approved) {
     }
 }
