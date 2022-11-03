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

import java.math.BigInteger;

public interface HSP721TokenReceiver {
    /**
     * @param _operator
     * @param _from
     * @param _tokenId
     * @param _data
     * @return true if transfer is allowed
     */
    boolean onHSP721Received(Address _operator, Address _from, BigInteger _tokenId, byte[] _data);
}
