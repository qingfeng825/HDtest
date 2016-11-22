package com.example.lijinming.hdtest.bleManage;

public class DataValid {
	/**
	 * ����ģʽ
	 */
	private DataValid() {
	}

	private final static DataValid dv = new DataValid();

	/**
	 * �ж������Ƿ���Ч
	 * 
	 * @param data
	 * @param checksum
	 * @return
	 */
	public static boolean isValid(String data, String checksum) {
		byte[] dataBytes = hexStringToBytes(data);
		byte dataSum = addData(dataBytes);
		byte[] sumBytes = hexStringToBytes(checksum);
		byte checkSum = addData(sumBytes);
		checkSum = (byte) ~checkSum;

		if (dataSum == checkSum) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * �ַ���ת��Ϊ16��������
	 * 
	 * @param hexString
	 * @return
	 */
	private static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * char ת��Ϊ Byte
	 * 
	 * @param c
	 * @return
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * int ������ͣ��������� 8 λ
	 * 
	 * @param byteData
	 * @return
	 */
	private static byte addData(byte[] byteData) {
		int dataSum = 0;
		for (int b : byteData) {
			dataSum += b;
		}
		return (byte) dataSum;
	}
}
