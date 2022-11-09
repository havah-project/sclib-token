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

package io.havah.contract.token.hsp721.extensions;

import score.Address;

import java.math.BigInteger;

public interface HSP721Enumerable {
    /**
     * Returns the total amount of tokens stored by the contract.
     */
    int totalSupply();

    /**
     * Returns a token ID owned by `owner` at a given `index` of its token list.
     * Use along with {balanceOf} to enumerate all of ``owner``'s tokens.
     */
    BigInteger tokenOfOwnerByIndex(Address _owner, int _index);

    /**
     * Returns a token ID at a given `index` of all the tokens stored by the contract.
     * Use along with {totalSupply} to enumerate all tokens.
     */
    BigInteger tokenByIndex(int _index);
}
