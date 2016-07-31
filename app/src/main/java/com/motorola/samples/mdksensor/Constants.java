/**
 * Copyright (c) 2016 Motorola Mobility, LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.motorola.samples.mdksensor;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * A class to represent constant values.
 */
public class Constants {
    public static final String TAG = "MDKSensor";

    public static String URL_PRIVACY_POLICY = "https://motorola.com/device-privacy";
    public static String URL_DEV_PORTAL = "http://developer.motorola.com";
    public static String URL_MOD_STORE = "http://developer.motorola.com/buy/";

    public static final int INVALID_ID = -1;
    public static final int MAX_SAMPLING_SUM = 15;

    public static final int VID_MDK = 0x00000312;
    public static final int VID_DEVELOPER = 0x00000042;

    public static final int PID_DEVELOPER = 0x00000001;
    public static final int PID_TEMPERATURE = 0x00010503;

    /**
     * Command  is [cmd ID(1 byte)] [size of payload(1byte)] [payload]
     * Response is [cmd ID(1 byte)] [size of payload(1byte)] [payload]
     * <p/>
     * payload of info:
     * struct sensor_attr {
     * uint8_t     version;
     * uint8_t     reserved;
     * uint16_t    max_latency;
     * uint8_t     name[48];
     * };
     * <p/>
     * payload of data: byte[4]
     */
    public static final int CMD_OFFSET = 0;
    public static final int CMD_LENGTH = 1;
    public static final int SIZE_OFFSET = CMD_OFFSET + CMD_LENGTH;
    public static final int SIZE_LENGTH = 1;
    public static final int PAYLOAD_OFFSET = SIZE_OFFSET + SIZE_LENGTH;

    public static int CMD_INFO_VERSION_OFFSET = 0;
    public static int CMD_INFO_VERSION_SIZE = 1;
    public static int CMD_INFO_RESERVED_OFFSET = CMD_INFO_VERSION_OFFSET + CMD_INFO_VERSION_SIZE;
    public static int CMD_INFO_RESERVED_SIZE = 1;
    public static int CMD_INFO_LATENCYLOW_OFFSET = CMD_INFO_RESERVED_OFFSET + CMD_INFO_RESERVED_SIZE;
    public static int CMD_INFO_LATENCYLOW_SIZE = 1;
    public static int CMD_INFO_LATENCYHIGH_OFFSET = CMD_INFO_LATENCYLOW_OFFSET + CMD_INFO_LATENCYLOW_SIZE;
    public static int CMD_INFO_LATENCYHIGH_SIZE = 1;
    public static int CMD_INFO_NAME_OFFSET = CMD_INFO_LATENCYHIGH_OFFSET + CMD_INFO_LATENCYHIGH_SIZE;
    public static int CMD_INFO_HEAD_SIZE = CMD_INFO_VERSION_SIZE + CMD_INFO_RESERVED_SIZE
            + CMD_INFO_LATENCYLOW_SIZE + CMD_INFO_LATENCYHIGH_SIZE;

    public static int CMD_DATA_LOWDATA_OFFSET = 0;
    public static int CMD_DATA_LOWDATA_SIZE = 1;
    public static int CMD_DATA_HIGHDATA_OFFSET = CMD_DATA_LOWDATA_OFFSET + CMD_DATA_LOWDATA_SIZE;
    public static int CMD_DATA_HIGHDATA_SIZE = 1;
    public static int CMD_DATA_RESERVED_SIZE = 2;
    public static int CMD_DATA_SIZE = CMD_DATA_LOWDATA_SIZE + CMD_DATA_HIGHDATA_SIZE + CMD_DATA_RESERVED_SIZE;

    public static  int CMD_CHALLENGE_SIZE = 16;

    public static int CMD_CHLGE_RESP_OFFSET = 0;
    public static int CMD_CHLGE_RESP_SIZE = 4;

    public static final int TEMP_RAW_COMMAND_RESP_MASK = 0x80;
    public static final int TEMP_RAW_COMMAND_INVALID = 0x00;
    public static final int TEMP_RAW_COMMAND_INFO = 0x01;
    public static final int TEMP_RAW_COMMAND_ON = 0x02;
    public static final int TEMP_RAW_COMMAND_OFF = 0x03;
    public static final int TEMP_RAW_COMMAND_DATA = 0x04;
    public static final int TEMP_RAW_COMMAND_CHALLENGE = 0x05;
    public static final int TEMP_RAW_COMMAND_CHLGE_RESP = 0x06;

    /** Challenge code for MDK Temperature Sensor protocol */
    public static final int CHALLENGE_ADDATION = 777;

    /**  Samples for RAW command format */
    public static final int SENSOR_COMMAND_SIZE = 0x02;
    public static byte[] RAW_CMD_INFO = {TEMP_RAW_COMMAND_INFO, 0x00};
    public static byte[] RAW_CMD_CHALLENGE = {TEMP_RAW_COMMAND_CHALLENGE,
            0x00, /* should use actually size of encrypted AES */
            0x00, /* should be payload of encrypted AES */};
    public static byte[] RAW_CMD_START = {TEMP_RAW_COMMAND_ON, SENSOR_COMMAND_SIZE,
            0x00, /* interval(low) */
            0x20, /* interval(high) */};
    public static byte[] RAW_CMD_STOP = {TEMP_RAW_COMMAND_OFF, 0x00}; /* stop cmd not need payload */

    /**
     * RAW data encrypt / decrypt via AES.
     *
     * AES-ECB is used for illustrative example only. We do not recommend using AES-ECB in a real
     * application as it is not semantically secure.
     */
    public static final String AES_ECB_KEY = "moto-temp-apk";

    public static byte[] getAESECBEncryptor(String key, byte[] text) {
        byte[] result = null;
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] keyBytes16 = new byte[16];
            System.arraycopy(keyBytes, 0, keyBytes16, 0, Math.min(keyBytes.length, 16));

            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes16, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            result = cipher.doFinal(text);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * RAW data encrypt / decrypt via AES.
     *
     * AES-ECB is used for illustrative example only. We do not recommend using AES-ECB in a real
     * application as it is not semantically secure.
     */
    public static byte[] getAESECBDecryptor(String key, byte[] encrypt) {
        byte[] result = null;
        try {
            byte[] keyBytes = key.getBytes("UTF-8");
            byte[] keyBytes16 = new byte[16];
            System.arraycopy(keyBytes, 0, keyBytes16, 0, Math.min(keyBytes.length, 16));

            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes16, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            result = cipher.doFinal(encrypt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }
}
