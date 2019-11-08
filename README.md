[![Maven Central](https://img.shields.io/maven-central/v/im.mak/waves-crypto-java.svg?label=Maven%20Central)](https://search.maven.org/artifact/im.mak/waves-crypto-java)

# Waves Crypto for Java

The Waves Crypto gives convenient way to create Waves accounts and work with all cryptographic primitives, hashes and other base things of the Waves blockchain.

## Account creation

Seed + nonce -> private key -> public key -> + chainId -> address

```java
Seed specificSeed  = Seed.from("some specific seed phrase");
Seed seedWithNonce = Seed.from("some specific seed phrase", 42);
Seed randomSeed    = Seed.random();
```

## Signing and signature validation

## Base encodings

## Hash algorithms

## RSA

## Merkle tree

