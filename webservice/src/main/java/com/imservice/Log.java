package com.imservice;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// TODO: write in log file
/**
 * Simplifies the output of errors
 * 
 * @author Florian Nachtigall
 */

public class Log {
	public static void sErr(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.err.println("[Server@" + date + "]\t" + message);
	}

	public static void sOut(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.out.println("[Server@" + date + "]\t" + message);
	}

	public static void dErr(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.err.println("[Database@" + date + "]\t" + message);
	}

	public static void dOut(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.out.println("[Database@" + date + "]\t" + message);
	}

	public static void hErr(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.err.println("[Handler@" + date + "]\t" + message);
	}

	public static void hOut(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.out.println("[Handler@" + date + "]\t" + message);
	}

	public static void iErr(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.err.println("[Input@" + date + "]\t" + message);
	}

	public static void iOut(String message) {
		String date = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		System.out.println("[Input@" + date + "]\t" + message);
	}
}
