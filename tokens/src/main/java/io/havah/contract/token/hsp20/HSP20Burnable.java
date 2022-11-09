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

package io.havah.contract.token.hsp20;

import score.Context;
import score.annotation.External;

import java.math.BigInteger;

public abstract class HSP20Burnable extends HSP20Basic {
    public HSP20Burnable(String _name, String _symbol, int _decimals) {
        super(_name, _symbol, _decimals);
    }

    /**
     * Destroys `_value` tokens from the caller.
     */
    @External
    public void burn(BigInteger _value) {
        _burn(Context.getCaller(), _value);
    }
}
