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

import java.util.ArrayList;
import java.util.List;
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
	 * Enters an infinite loop. This is used in some example programs
	 * where a thread is started in the background, and the main program
	 * has to remain idle. The only way to stop this method is for the
	 * program to be interrupted by some other external means (e.g. pressing
	 * Ctrl+C at the console, closing a window, etc.).
	 */
	public static void waitForever()
	{
		while (true)
		{
			try 
			{
				Thread.sleep(10000);
			}
			catch (InterruptedException e) 
			{
				// Do nothing
			}
		}
	}
	
	/**
	 * Creates a list out of a variable number of elements
	 * @param elements The elements to put in the list
	 * @return The list
	 */
	public static List<Object> createList(Object ... elements)
	{
		List<Object> list = new ArrayList<Object>();
		for (Object o : elements)
		{
			list.add(o);
		}
		return list;
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
