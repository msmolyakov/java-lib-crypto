package com.wavesplatform.crypto.account;

import com.wavesplatform.crypto.Bytes;
import com.wavesplatform.crypto.Hash;
import com.wavesplatform.crypto.base.Base58;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Address is used as recipient of transactions.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Address {

    /**
     * Generate an address from the public key.
     * Depends on the Id of a particular blockchain network.
     * @param chainId blockchain network Id.
     * @return address
     * @see com.wavesplatform.crypto.ChainId
     */
    public static Address from(PublicKey publicKey, byte chainId) {
        return new Address(publicKey, chainId);
    }

    /**
     * Create address instance from its base58 representation
     * @param base58Encoded address bytes as base58-encoded string
     * @return address instance
     * @throws IllegalArgumentException if base58 string is null
     */
    public static Address as(Base58 base58Encoded) throws IllegalArgumentException {
        return new Address(base58Encoded);
    }

    /**
     * Create address instance from its bytes
     * @param bytes address bytes
     * @return address instance
     * @throws IllegalArgumentException if the address is wrong
     */
    public static Address as(byte[] bytes) throws IllegalArgumentException {
        return new Address(bytes);
    }

    /**
     * Check if the address is correct for specified Waves network
     * @param encodedAddress address as base58-encoded string
     * @param chainId blockchain network Id
     * @return true if the address is correct
     */
    public static boolean isCorrect(Base58 encodedAddress, byte chainId) {
        return isCorrect(encodedAddress.decoded(), chainId);
    }

    /**
     * Check if the address is correct for any Waves network
     * @param encodedAddress address as base58-encoded string
     * @return true if the address is correct
     */
    public static boolean isCorrect(Base58 encodedAddress) {
        return isCorrect(encodedAddress.decoded());
    }

    /**
     * Check if the address is correct for specified Waves network
     * @param addressBytes address bytes
     * @param chainId blockchain network Id
     * @return true if the address is correct
     */
    public static boolean isCorrect(byte[] addressBytes, byte chainId) {
        return isCorrect(addressBytes) && addressBytes[1] == chainId;
    }

    /**
     * Check if the address is correct for any Waves network
     * @param addressBytes address bytes
     * @return true if the address is correct
     */
    public static boolean isCorrect(byte[] addressBytes) {
        try {
            new Address(addressBytes);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private byte[] bytes;
    private String encoded;

    /**
     * Generate an address from the public key.
     * Depends on the Id of a particular blockchain network.
     * @param chainId blockchain network Id.
     * @see com.wavesplatform.crypto.ChainId
     */
    public Address(PublicKey publicKey, byte chainId) {
        ByteBuffer buf = ByteBuffer.allocate(26);
        byte[] hash = Hash.secureHash(publicKey.bytes());
        buf.put((byte) 1).put(chainId).put(hash, 0, 20);
        byte[] checksum = Hash.secureHash(Arrays.copyOfRange(buf.array(), 0 , 22));
        buf.put(checksum, 0, 4);

        this.bytes = buf.array();
        this.encoded = Base58.encode(this.bytes);
    }

    /**
     * Create address instance from its base58 representation
     * @param encodedAddress address bytes as base58-encoded string
     * @throws IllegalArgumentException if base58 string is null
     */
    public Address(Base58 encodedAddress) throws IllegalArgumentException {
        this(encodedAddress.decoded());
    }

    /**
     * Create address instance from its bytes
     * @param addressBytes address bytes
     * @throws IllegalArgumentException if the address is wrong
     */
    public Address(byte[] addressBytes) throws IllegalArgumentException {
        if (addressBytes.length != 26)
            throw new IllegalArgumentException("Address has wrong length. " +
                    "Expected: " + 26 + " bytes, actual: " + addressBytes.length + " bytes");
        if (addressBytes[0] != 1)
            throw new IllegalArgumentException("Address has unknown address version " + addressBytes[0]);

        byte[][] parts = Bytes.chunk(addressBytes, 22, 4);
        byte[] checkSum = Hash.secureHash(parts[0]);
        if (!Bytes.equal(parts[1], checkSum))
            throw new IllegalArgumentException("Address has wrong checksum");

        this.bytes = addressBytes;
        this.encoded = Base58.encode(this.bytes);
    }

    /**
     * Get blockchain network id of the address
     * @return network id
     */
    public byte chainId() {
        return this.bytes[1];
    }

    /**
     * Get bytes of the address
     * @return bytes of the address
     */
    public byte[] bytes() {
        return this.bytes;
    }

    /**
     * Get the address as base58-encoded string
     * @return the address as base58-encoded string
     */
    public String base58() {
        return this.encoded;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Arrays.equals(bytes, address.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

    @Override
    public String toString() {
        return this.base58();
    }

}
