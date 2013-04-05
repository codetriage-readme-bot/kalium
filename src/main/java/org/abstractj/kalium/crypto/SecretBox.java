/**
 * Copyright 2013 Bruno Oliveira, and individual contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.abstractj.kalium.crypto;

import org.abstractj.kalium.encoders.Encoder;

import static org.abstractj.kalium.NaCl.Sodium.BOXZERO_BYTES;
import static org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_NONCEBYTES;
import static org.abstractj.kalium.NaCl.Sodium.XSALSA20_POLY1305_SECRETBOX_KEYBYTES;
import static org.abstractj.kalium.NaCl.Sodium.ZERO_BYTES;
import static org.abstractj.kalium.NaCl.sodium;
import static org.abstractj.kalium.crypto.Util.checkLength;
import static org.abstractj.kalium.crypto.Util.isValid;
import static org.abstractj.kalium.crypto.Util.removeZeros;

public class SecretBox {

    private byte[] key;

    public SecretBox(byte[] key) {
        this.key = key;
        checkLength(key, XSALSA20_POLY1305_SECRETBOX_KEYBYTES);
    }

    public SecretBox(String key, Encoder encoder) {
        this(encoder.decode(key));
    }

    public byte[] encrypt(byte[] nonce, byte[] message) {
        checkLength(nonce, XSALSA20_POLY1305_SECRETBOX_NONCEBYTES);
        byte[] msg = Util.prependZeros(ZERO_BYTES, message);
        byte[] ct = Util.zeros(msg.length);
        isValid(sodium().crypto_secretbox_xsalsa20poly1305_ref(ct, msg, msg.length,
                nonce, key), "Encryption failed");
        return removeZeros(BOXZERO_BYTES, ct);
    }

    public byte[] decrypt(byte[] nonce, byte[] ciphertext) {
        checkLength(nonce, XSALSA20_POLY1305_SECRETBOX_NONCEBYTES);
        byte[] ct = Util.prependZeros(BOXZERO_BYTES, ciphertext);
        byte[] message = Util.zeros(ct.length);
        isValid(sodium().crypto_secretbox_xsalsa20poly1305_ref_open(message, ct,
                ct.length, nonce, key), "Decryption failed. Ciphertext failed verification");
        return removeZeros(ZERO_BYTES, message);
    }
}
