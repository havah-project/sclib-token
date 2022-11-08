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

package io.havah.score.token.hsp1155;

import score.Address;
import score.annotation.Optional;

import java.math.BigInteger;

public interface HSP1155 {
    // ================================================
    // Event Logs
    // ================================================

    /**
     * Must trigger on any successful token transfers, including zero value transfers as well as minting or burning.
     * When minting/creating tokens, the {@code _from} must be set to zero address.
     * When burning/destroying tokens, the {@code _to} must be set to zero address.
     *
     * @param _operator The address of an account/contract that is approved to make the transfer
     * @param _from     The address of the token holder whose balance is decreased
     * @param _to       The address of the recipient whose balance is increased
     * @param _id       ID of the token
     * @param _value    The amount of transfer
     */
    void TransferSingle(Address _operator, Address _from, Address _to, BigInteger _id, BigInteger _value);

    /**
     * Must trigger on any successful token transfers, including zero value transfers as well as minting or burning.
     * When minting/creating tokens, the {@code _from} must be set to zero address.
     * When burning/destroying tokens, the {@code _to} must be set to zero address.
     * <p>
     * NOTE: RLP (Recursive Length Prefix) would be used for the serialized bytes to represent list type.
     *
     * @param _operator The address of an account/contract that is approved to make the transfer
     * @param _from     The address of the token holder whose balance is decreased
     * @param _to       The address of the recipient whose balance is increased
     * @param _ids      Serialized bytes of list for token IDs (order and length must match {@code _values})
     * @param _values   Serialized bytes of list for transfer amounts per token (order and length must match {@code _ids})
     */
    void TransferBatch(Address _operator, Address _from, Address _to, byte[] _ids, byte[] _values);

    /**
     * Must trigger on any successful approval (either enabled or disabled) for a third party/operator address
     * to manage all tokens for the {@code _owner} address.
     *
     * @param _account  The address of the token holder
     * @param _operator The address of authorized operator
     * @param _approved True if the operator is approved, false to revoke approval
     */
    void ApprovalForAll(Address _account, Address _operator, boolean _approved);

    /**
     * Must trigger on any successful URI updates for a token ID.
     * URIs are defined in RFC 3986.
     * The URI must point to a JSON file that conforms to the "HSP-1155 Metadata URI JSON Schema".
     *
     * @param _value The updated URI string
     * @param _id    ID of the token
     */
    void URI(String _value, BigInteger _id);

    // ================================================
    // External methods
    // ================================================

    /**
     * Returns the balance of the owner's tokens.
     *
     * @param _account The address of the token holder
     * @param _id      ID of the token
     * @return The _account's balance of the token type requested
     */
    BigInteger balanceOf(Address _account, BigInteger _id);

    /**
     * Returns the balance of multiple owner/id pairs.
     *
     * @param _accounts The addresses of the token holders
     * @param _ids      IDs of the tokens
     * @return The list of balance (i.e. balance for each owner/id pair)
     */
    BigInteger[] balanceOfBatch(Address[] _accounts, BigInteger[] _ids);

    /**
     * Enables or disables approval for a third party ("operator") to manage all of the caller's tokens,
     * and must emit {@code ApprovalForAll} event on success.
     *
     * @param _operator Address to add to the set of authorized operators
     * @param _approved True if the operator is approved, false to revoke approval
     */
    void setApprovalForAll(Address _operator, boolean _approved);

    /**
     * Returns the approval status of an operator for a given owner.
     *
     * @param _account  The owner of the tokens
     * @param _operator The address of authorized operator
     * @return True if the operator is approved, false otherwise
     */
    boolean isApprovedForAll(Address _account, Address _operator);

    /**
     * Transfers `amount` tokens of token type `id` from `from` to `to`.
     * <p>
     * Emits a {TransferSingle} event.
     * <p>
     * Requirements:
     * <p>
     * - `to` cannot be the zero address.
     * - If the caller is not `from`, it must have been approved to spend ``from``'s tokens via {setApprovalForAll}.
     * - `from` must have a balance of tokens of type `id` of at least `amount`.
     * - If `to` refers to a smart contract, it must implement {HSP1155Receiver-onHSP1155Received} and return the
     * acceptance magic value.
     */
    void safeTransferFrom(Address _from, Address _to, BigInteger _id, BigInteger _amount, @Optional byte[] _data);

    /**
     * xref:ROOT:hsp1155.adoc#batch-operations[Batched] version of {safeTransferFrom}.
     * <p>
     * Emits a {TransferBatch} event.
     * <p>
     * Requirements:
     * <p>
     * - `ids` and `amounts` must have the same length.
     * - If `to` refers to a smart contract, it must implement {HSP1155Receiver-onHSP1155BatchReceived} and return the
     * acceptance magic value.
     */
    void safeBatchTransferFrom(Address _from, Address _to, BigInteger[] _ids, BigInteger[] _amounts, @Optional byte[] _data);
}