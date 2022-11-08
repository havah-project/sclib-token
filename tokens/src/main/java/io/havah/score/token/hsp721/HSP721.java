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

package io.havah.score.token.hsp721;

import score.Address;
import score.annotation.Optional;

import java.math.BigInteger;

public interface HSP721 {
    // ================================================
    // Event Logs
    // ================================================

    /**
     * (EventLog) Must trigger on any successful token transfers.
     */
    void Transfer(Address _from, Address _to, BigInteger _tokenId);

    /**
     * (EventLog) Must trigger on any successful call to {@code approve(Address, int)}.
     */
    void Approval(Address _owner, Address _approved, BigInteger _tokenId);

    /**
     * (EventLog) Must trigger on any successful call to {@code setApprovalForAll(Address, boolean)}.
     */
    void ApprovalForAll(Address _owner, Address _operator, boolean _approved);

    // ================================================
    // External methods
    // ================================================

    /**
     * Returns the number of NFTs owned by {@code _owner}.
     * NFTs assigned to the zero address are considered invalid,
     * so this function SHOULD throw for queries about the zero address.
     */
    int balanceOf(Address _owner);

    /**
     * Returns the owner of an NFT.
     * Throws if {@code _tokenId} is not a valid NFT.
     */
    Address ownerOf(BigInteger _tokenId);

    /**
     * Transfers the ownership of an NFT from one address to another address
     * Throws unless caller is the current owner, an authorized operator,
     * or the approved address for this NFT. Throws if `_from` is
     * not the current owner. Throws if `_to` is the zero address. Throws if
     * `_tokenId` is not a valid NFT. When transfer is complete, this function
     * checks if `_to` is a smart contract. If so, it calls
     * `onHSP721Received` on `_to` and throws if the return value is not
     * `bytes4(keccak256("onHSP721Received(address,address,uint256,bytes)"))`.
     */
    void safeTransferFrom(Address _from, Address _to, BigInteger _tokenId, @Optional byte[] data);

    /**
     * Transfers the ownership of an NFT from one address to another address, and MUST fire the {@code Transfer} event.
     * Throws unless the caller is the current owner or the approved address for the NFT.
     * Throws if {@code _from} is not the current owner.
     * Throws if {@code _to} is the zero address.
     * Throws if {@code _tokenId} is not a valid NFT.
     */
    void transferFrom(Address _from, Address _to, BigInteger _tokenId);

    /**
     * Allows {@code _to} to change the ownership of {@code _tokenId} from your account.
     * The zero address indicates there is no approved address.
     * Throws unless the caller is the current NFT owner.
     */
    void approve(Address _to, BigInteger _tokenId);

    /**
     * Approve or remove `operator` as an operator for the caller.
     * Operators can call {transferFrom} or {safeTransferFrom} for any token owned by the caller.
     *
     * Requirements:
     *
     * - The `operator` cannot be the caller.
     *
     * Emits an {ApprovalForAll} event.
     */
    void setApprovalForAll(Address _operator, boolean _approved);

    /**
     * Returns the approved address for a single NFT.
     * If there is none, returns the zero address.
     * Throws if {@code _tokenId} is not a valid NFT.
     */
    Address getApproved(BigInteger _tokenId);

    /**
     * Returns if the `operator` is allowed to manage all of the assets of `owner`.
     *
     * See {setApprovalForAll}
     */
    boolean isApprovedForAll(Address _owner, Address _operator);
}
