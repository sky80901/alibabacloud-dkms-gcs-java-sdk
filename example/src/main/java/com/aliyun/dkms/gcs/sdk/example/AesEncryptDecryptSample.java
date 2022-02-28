package com.aliyun.dkms.gcs.sdk.example;

import com.aliyun.dkms.gcs.openapi.models.Config;
import com.aliyun.dkms.gcs.openapi.util.models.RuntimeOptions;
import com.aliyun.dkms.gcs.sdk.Client;
import com.aliyun.dkms.gcs.sdk.models.DecryptRequest;
import com.aliyun.dkms.gcs.sdk.models.DecryptResponse;
import com.aliyun.dkms.gcs.sdk.models.EncryptRequest;
import com.aliyun.dkms.gcs.sdk.models.EncryptResponse;
import com.aliyun.tea.TeaException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * ClientKey传参支持以下三种方式：
 * 1、通过指定ClientKey.json文件路径方式
 * 示例：
 * String clientKeyFile = "<your client key file path>";
 * String password = "<your client key password>";
 * Config cfg = new Config();
 * cfg.setClientKeyFile(clientKeyFile);
 * cfg.setPassword(password);
 * <p>
 * 2、通过指定ClientKey内容方式
 * 示例：
 * String clientKeyContent = "<your client key content>";
 * String password = "<your client key password>";
 * Config cfg = new Config();
 * cfg.setClientKeyContent(clientKeyContent);
 * cfg.setPassword(password);
 * <p>
 * 3、通过指定私钥和AccessKeyId
 * 示例：
 * String accessKeyId = "<your client key KeyId>";
 * String privateKey = "<parse from your client key PrivateKeyData>";
 * Config cfg = new Config();
 * cfg.setAccessKeyId(accessKeyId);
 * cfg.setPrivateKey(privateKey);
 */
public class AesEncryptDecryptSample {

    // 加密服务实例Client对象
    private static Client client = null;

    public static void main(String[] args) {
        try {
            // 构建加密服务实例Client对象
            initClient();

            // 使用加密服务实例进行加解密示例
            encryptDecryptSample();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initClient() throws Exception {
        // 构建加密服务实例Client配置
        Config config = new Config();
        config.setProtocol("https");
        config.setClientKeyFile("<your-client-key-file>");
        config.setPassword("<your-password>");
        config.setEndpoint("<your-endpoint>");
        client = new Client(config);
    }

    // 加解密示例
    private static void encryptDecryptSample() {
        String keyId = "<your-key-id>";
        String plaintext = "<your-plaintext>";
        final EncryptResponse encryptResponse = encryptSample(keyId, plaintext);
        String decryptResult = decryptSample(encryptResponse.getKeyId(), encryptResponse.getAlgorithm(), encryptResponse.getIv(), encryptResponse.getCiphertextBlob());
        if (!plaintext.equals(decryptResult)) {
            System.out.println("Decrypt data not match the plaintext");
        }
    }

    // 加密示例
    private static EncryptResponse encryptSample(String keyId, String plaintext) {
        // 构建加密请求
        EncryptRequest encryptRequest = new EncryptRequest();
        encryptRequest.setKeyId(keyId);
        encryptRequest.setPlaintext(plaintext.getBytes(StandardCharsets.UTF_8));
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.ignoreSSL = true;

        try {
            // 调用加密接口进行加密
            EncryptResponse encryptResponse = client.encryptWithOptions(encryptRequest, runtimeOptions);
            System.out.printf("KeyId: %s%n", encryptResponse.getKeyId());
            System.out.printf("CiphertextBlob: %s%n", Arrays.toString(encryptResponse.getCiphertextBlob()));
            System.out.printf("Iv: %s%n", Arrays.toString(encryptResponse.getIv()));
            System.out.printf("RequestId: %s%n", encryptResponse.getRequestId());
            return encryptResponse;
        } catch (Exception e) {
            if (e instanceof TeaException) {
                System.out.printf("code: %s%n", ((TeaException) e).getCode());
                System.out.printf("message: %s%n", ((TeaException) e).getMessage());
                System.out.printf("requestId: %s%n", ((TeaException) e).getData().get("requestId"));
            } else {
                System.out.printf("encrypt err: %s%n", e.getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // 解密示例
    private static String decryptSample(String keyId, String algorithm, byte[] iv, byte[] ciphertextBlob) {
        // 构建解密请求对象
        DecryptRequest decryptRequest = new DecryptRequest();
        decryptRequest.setKeyId(keyId);
        decryptRequest.setCiphertextBlob(ciphertextBlob);
        decryptRequest.setAlgorithm(algorithm);
        decryptRequest.setIv(iv);
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        runtimeOptions.ignoreSSL = true;

        try {
            // 调用解密接口进行解密
            DecryptResponse decryptResponse = client.decryptWithOptions(decryptRequest, runtimeOptions);
            System.out.printf("KeyId: %s%n", decryptResponse.getKeyId());
            System.out.printf("Plaintext: %s%n", new String(decryptResponse.getPlaintext()));
            System.out.printf("RequestId: %s%n", decryptResponse.getRequestId());
            return new String(decryptResponse.getPlaintext());
        } catch (Exception e) {
            if (e instanceof TeaException) {
                System.out.printf("code: %s%n", ((TeaException) e).getCode());
                System.out.printf("message: %s%n", ((TeaException) e).getMessage());
                System.out.printf("requestId: %s%n", ((TeaException) e).getData().get("requestId"));
            } else {
                System.out.printf("decrypt err: %s%n", e.getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
