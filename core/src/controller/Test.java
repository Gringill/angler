package controller;

public class Test {
	public static String testGetBytes() {
		
		String s = "";
		try {
			byte[] ba = ("ř").getBytes("UTF16");
			for (byte b : ba) {
				System.out.format("%02x ", b);
			}
			s = s + " || ";
			ba = ("ř").getBytes("UTF8");
			for (byte b : ba) {
				System.out.format("%02x ", b);
			}
			s = s + " || ";
			ba = ("ř").getBytes("Latin1");
			for (byte b : ba) {
				System.out.format("%02x ", b);
			}
		} catch (Exception e) {

		}
		return s;
	}

	public static void main(String[] args) {
		System.out.println(testGetBytes());
		System.out.println(System.getProperty("file.encoding"));
	}
}
