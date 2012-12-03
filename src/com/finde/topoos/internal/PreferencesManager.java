package com.finde.topoos.internal;

import java.util.Calendar;

import com.finde.topoos.api.AccessObject;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

	public static void SavePreferences_Access(AccessObject access, Context context) {
		// Guardar la informaci—n
		SharedPreferences settings = context.getSharedPreferences("PREFERENCIAS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("TOPOOS_ACCESS_TOKEN", access.AccessToken);
		editor.putString("TOPOOS_EXPIRATION", access.Expiration == null ? null : access.Expiration.toString());
		editor.commit();
	}
	
	
	
	public static AccessObject LoadPreferences_Access(Context context){
		SharedPreferences settings = context.getSharedPreferences("PREFERENCIAS", Context.MODE_PRIVATE);
		
		String AccessToken = settings.getString("TOPOOS_ACCESS_TOKEN","");
		String Expiration = settings.getString("TOPOOS_EXPIRATION", "");
				
		Calendar exp = Utils.StringToCalendar(Expiration);
		AccessObject Access = new AccessObject(AccessToken, exp);

		return Access;
	}
	
	public static void DeletePreferences_Access(Context context)
	{
		SharedPreferences settings = context.getSharedPreferences("PREFERENCIAS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}
}
