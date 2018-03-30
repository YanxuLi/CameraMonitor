package com.media.dingping.cameramonitor.utils;

/**
 * 工具类 ,整型与长度为4的字节数组的互换
 * 
 * @author zsc
 * 
 */

public final class ByteUtil {
	
	
	/**
	 * 注释：short到字节数组的转换！
	 * 
	 * @param number
	 * @return
	 */
	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = Integer.valueOf(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}
	
	/**
	 * 注释：short到字节数组的转换！
	 * 
	 * @param number
	 * @return
	 */
	public static byte[] shortToByte_c(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = Integer.valueOf(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		byte[] b_c = new byte[2];
		b_c[0]=b[1];
		b_c[1]=b[0];
		return b_c;
	}
	
	
	
	
	
	
	/**
	 * 整型转换为4位字节数组
	 * 
	 * @param intValue
	 *            要转换的整数
	 * @return 转换后的结果
	 */
	public static byte[] int2Byte(int intValue) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
			// System.out.print(Integer.toBinaryString(b[i])+" ");
			// System.out.print((b[i] & 0xFF) + " ");
		}
		return b;
	}

	/**
	 * 整型转换为4位字节数组  C 与 java 字节序相反
	 * 
	 * @param intValue
	 *            要转换的整数
	 * @return 转换后的结果
	 */
	public static byte[] int2Byte_c(int intValue) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (intValue >> 8 * (3 - i) & 0xFF);
			// System.out.print(Integer.toBinaryString(b[i])+" ");
			// System.out.print((b[i] & 0xFF) + " ");
		}
		byte[] data_c = new byte[4];
		data_c[0] = b[3];
		data_c[1] = b[2];
		data_c[2] = b[1];
		data_c[3] = b[0];
		return data_c;
	}

	/**
	 * 4位字节数组转换为整型
	 * 
	 * @param b
	 *            要转换的byte[] 数组
	 * @return 转换后的结果
	 */
	public static int byte2Int(byte[] b) {
		int intValue = 0;
		// int tempValue = 0xFF;
		for (int i = 0; i < b.length; i++) {
			intValue += (b[i] & 0xFF) << (8 * (3 - i));
			// System.out.print(Integer.toBinaryString(intValue)+" ");
		}
		return intValue;
	}

	/**
	 * 4位字节数组转换为整型 C 与 java 字节序相反
	 * 
	 * @param b
	 *            要转换的byte[] 数组
	 * @return 转换后的结果
	 */
	public static int byte2Int_C(byte[] b) {
		byte[] data4c = new byte[4];
		data4c[0] = b[3];
		data4c[1] = b[2];
		data4c[2] = b[1];
		data4c[3] = b[0];
		return byte2Int(data4c);
	}

	/**
	 * long转换成byte[]
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] long2Bytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}

	/**
	 * long转换成byte[] C 序
	 * 
	 * @param num
	 * @return
	 */
	public static byte[] long2Bytes_c(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		byte[] byte_c = new byte[8];
		for (int i = 0; i < byte_c.length; i++) {
			byte_c[i] = byteNum[7 - i];
		}
		return byte_c;
	}
	
	/**
	 * byte[]转换成long
	 * 
	 * @param byteArray
	 * @return
	 */
	public static long bytes2Long(byte[] byteArray) {
		long num = 0;
		for (int ix = 0; ix < 8; ++ix) {
			num <<= 8;
			num |= (byteArray[ix] & 0xff);
		}
		return num;
	}


	/**
	 * byte[]转换成long c 序
	 * 
	 * @param byteArray
	 * @return
	 */
	public static long bytes2Long_c(byte[] byteArray) {
		byte[] byte_c = new byte[8];
		for (int i = 0; i < byte_c.length; i++) {
			byte_c[i] = byteArray[7 - i];
		}
		long num = 0;
		for (int ix = 0; ix < 8; ++ix) {
			num <<= 8;
			num |= (byte_c[ix] & 0xff);
		}
		return num;
	}
	
	/**
	 * 将data字节型数据转换为0~255 (0xFF 即BYTE)。
	 * 
	 * @param data
	 * @return
	 */
	public int getUnsignedByte(byte data) {
		return data & 0x0FF;
	}

	/**
	 * 将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
	 * 
	 * @param data
	 * @return
	 */
	public int getUnsignedByte(short data) {
		return data & 0x0FFFF;
	}

	/**
	 * 将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
	 * 
	 * @param data
	 * @return
	 */
	public long getUnsignedIntt(int data) {
		return data & 0x0FFFFFFFFl;
	}

}
