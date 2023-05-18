package com.abazy.otbasym;


public class s {

	public static void l(Object... objects) {
		StringBuilder str = new StringBuilder();
		if( objects != null ) {
			for( Object obj : objects )
				str.append(obj).append(" ");
		} else
			str.append((Object) null);
		System.out.println(".\t" + str);

	}

}
