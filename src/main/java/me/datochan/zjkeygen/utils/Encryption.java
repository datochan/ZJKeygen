package me.datochan.zjkeygen.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

public class Encryption {
    private PublicKey publicKey = null;
    private PrivateKey privateKey = null;

    public Encryption() {
    }

    public void generateKeys() throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom());
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public void generateKeys(String publicURI, String privateURI) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        this.generateKeys();
        this.writeKeys(publicURI, privateURI);
    }

    public PublicKey getPublic() {
        return this.publicKey;
    }

    public PrivateKey getPrivate() {
        return this.privateKey;
    }

    public void writeKeys(String publicURI, String privateURI) throws IOException, FileNotFoundException {
        this.writePublicKey(publicURI);
        this.writePrivateKey(privateURI);
    }

    public void writePublicKey(String URI) throws IOException, FileNotFoundException {
        byte[] enckey = this.publicKey.getEncoded();

        String strPubKey = new String(Base64Coder.encode(enckey));

        FileOutputStream keyfos = new FileOutputStream(URI);
        keyfos.write(strPubKey.getBytes());
        keyfos.close();
    }

    public void writePrivateKey(String URI) throws IOException, FileNotFoundException {
        byte[] enckey = this.privateKey.getEncoded();
        String strPrivKey = new String(Base64Coder.encode(enckey));
        FileOutputStream keyfos = new FileOutputStream(URI);
        keyfos.write(strPrivKey.getBytes());
        keyfos.close();
    }

    public void readKeys(String publicKeyString, String privateKeyString) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        this.readPublicKey(publicKeyString);
        this.readPrivateKey(privateKeyString);
    }

    public PublicKey readPublicKey(String URI) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        FileInputStream keyfis = new FileInputStream(URI);
        byte[] encKey = new byte[keyfis.available()];
        keyfis.read(encKey);
        keyfis.close();
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        this.publicKey = keyFactory.generatePublic(pubKeySpec);
        return this.publicKey;
    }

    public PublicKey readPublicKeyFromBytes(byte[] publicKeyByteArry) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyByteArry);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        this.publicKey = keyFactory.generatePublic(pubKeySpec);
        return this.publicKey;
    }

    public PrivateKey readPrivateKey(String URI) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        FileInputStream keyfis = new FileInputStream(URI);
        byte[] encKey = new byte[keyfis.available()];
        keyfis.read(encKey);
        keyfis.close();
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        this.privateKey = keyFactory.generatePrivate(privKeySpec);
        return this.privateKey;
    }

    public PrivateKey readPrivateKeyFromBytes(byte[] privateKeyByteArry) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(privateKeyByteArry);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        this.privateKey = keyFactory.generatePrivate(privKeySpec);
        return this.privateKey;
    }

    public String sign(String message) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        return this.sign(message, this.privateKey);
    }

    public String sign(String message, String privateKeyURI) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException, IOException {
        PrivateKey pk = this.readPrivateKey(privateKeyURI);
        return this.sign(message, pk);
    }

    public String sign(String message, PrivateKey privateKey) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance("SHA/DSA");
        dsa.initSign(privateKey);
        dsa.update(message.getBytes());
        byte[] m1 = dsa.sign();
        return new String(Base64Coder.encode(m1));
    }

    public boolean verify(String message, String signature, String publicKeyURI) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        PublicKey pk = this.readPublicKey(publicKeyURI);
        return this.verify(message, signature, pk);
    }

    public boolean verify(String message, String signature) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        if (this.publicKey == null) {
            throw new InvalidKeyException("Public Key not provided.");
        } else {
            return this.verify(message, signature, this.publicKey);
        }
    }

    public boolean verify(String message, String signature, PublicKey publicKey) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        Signature dsa = Signature.getInstance("SHA/DSA");
        dsa.initVerify(publicKey);
        dsa.update(message.getBytes());
        byte[] sigDec = Base64Coder.decode(signature.toCharArray());
        return dsa.verify(sigDec);
    }
}
