package test;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.omg.CosTransactions.FORBIDS;

public class Test implements Runnable
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// String text = "849892 123122 ; 12312,32131.32131";
		// String[] arrayOfText = text.split("[,.;\\s]");
		//
		// for (String s : arrayOfText)
		// {
		// System.out.println(s);
		// }

		for (int i = 0; i < 100; i++)
		{
			Test t = new Test();
			Thread tr = new Thread(t);
			tr.start();
		}

	}

	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Socket socket = null;
			try
			{
				socket = new Socket("10.8.13.31", 5000);
				String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
						+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:osaxsd=\"http://www.csapi.org/osa/schema\" xmlns:osa=\"http://www.csapi.org/osa/wsdl\" xmlns:csxsd=\"http://www.csapi.org/cs/schema\" xmlns:cs=\"http://www.csapi.org/cs/wsdl\">"
						+ "<SOAP-ENV:Header><osa:header>10.32.62.60:5000</osa:header></SOAP-ENV:Header>"
						+ "<SOAP-ENV:Body SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><cs:directDebitAmountRes><sessionID>7589325</sessionID><requestNumber>1</requestNumber><debitedAmount><Currency>VND</Currency><Amount><Number>840000</Number><Exponent>0</Exponent></Amount></debitedAmount><requestNumberNextRequest>2</requestNumberNextRequest></cs:directDebitAmountRes></SOAP-ENV:Body></SOAP-ENV:Envelope>";
				byte[] contentbytes = content.getBytes();
				String req = "POST / HTTP/1.0\r\n"
						+ "Host: 10.32.62.60\r\n"
						+ "Connection: close\r\n"
						+ "Content-Length: " + contentbytes.length + "\r\n"
						+ "SOAPAction: http://www.csapi.org/cs/IpAppChargingSession#directDebitAmountRes\r\n"
						+ "\r\n" + content;
				System.out.println(req);
				byte[] bytes = req.getBytes();
				socket.getOutputStream().write(bytes, 0, bytes.length);
				socket.getOutputStream().flush();
			}
			catch (UnknownHostException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally
			{
				try
				{
					socket.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static void printBinaryShort(short s)
	{
		System.out.println("Short: " + s + ", binary: ");
		System.out.print("   ");
		for (int j = 15; j >= 0; j--)
			if (((1 << j) & s) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}

	static void printBinaryInt(int i)
	{
		System.out.println("Int: " + i + ", binary: ");
		System.out.print("   ");
		for (int j = 31; j >= 0; j--)
			if (((1 << j) & i) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}

	static void printBinaryByte(byte b)
	{
		System.out.println("Byte: " + b + ", binary: ");
		System.out.print("   ");
		for (int j = 7; j >= 0; j--)
			if (((1 << j) & b) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}

	static void printBinaryLong(long l)
	{
		System.out.println("Long: " + l + ", binary: ");
		System.out.print("   ");
		for (int i = 63; i >= 0; i--)
			if (((1L << i) & l) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		System.out.println();
	}

}
