/*
    BeepBeep, an event stream processor
    Copyright (C) 2008-2017 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

import java.util.Scanner;

/**
 * A few methods repeatedly used throughout the examples of this
 * repository.
 * @author Sylvain Hallé
 */
public abstract class UtilityMethods
{
	/**
	 * Reads an integer form the standard input
	 * @return An integer
	 */
	public static int readInt()
	{
		Scanner s = new Scanner(System.in);
		int value = s.nextInt();
		s.close();
		return value;
	}

	/**
	 * Reads a line form the standard input
	 * @return A string with the contents of the line
	 */
	public static String readLine()
	{
		Scanner s = new Scanner(System.in);
		String value = s.nextLine();
		s.close();
		return value;
	}

	/**
	 * Pauses the execution of the current thread for some time
	 * @param milliseconds The number of milliseconds to wait before
	 *   resuming the execution
	 */
	public static void pause(long milliseconds)
	{
		try 
		{
			Thread.sleep(milliseconds);
		}
		catch (InterruptedException e) 
		{
			// Do nothing about this exception
			e.printStackTrace();
		}
	}

	/**
	 * Prints a "BeepBeep 3" greeting on the standard output
	 */
	public static void printGreeting()
	{
		System.out.println(" ___               ___                 ____");
		System.out.println("| _ ) ___ ___ _ __| _ ) ___ ___ _ __  |__ /");
		System.out.println("| _ \\/ -_) -_) '_ \\ _ \\/ -_) -_) '_ \\  |_ \\");
		System.out.println("|___/\\___\\___| .__/___/\\___\\___| .__/ |___/");
		System.out.println("             |_|               |_|");         
	}
}
