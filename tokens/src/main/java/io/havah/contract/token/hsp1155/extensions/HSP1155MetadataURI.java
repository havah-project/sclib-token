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
package io.havah.contract.token.hsp1155.extensions;

import java.math.BigInteger;

public interface HSP1155MetadataURI {
    /**
     *
     * This implementation returns the same URI for *all* token types. It relies
     * on the token type ID substitution mechanism
     *
     * Clients calling this function must replace the `\{_id\}` substring with the
     * actual token type ID.
     */
    String uri(BigInteger _id);
}
