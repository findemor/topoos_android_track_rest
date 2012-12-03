package com.finde.topoos.api;

import java.util.Calendar;
import com.finde.topoos.internal.Utils;

public class AccessObject {
	
	public String AccessToken;
	public Calendar Expiration; 
	
	public AccessObject()
	{
		AccessToken = "";
		Expiration = null;
	}
	
	public AccessObject(String token, String expiresIn)
	{
		Load(token, expiresIn);
	}
	
	public AccessObject(String token, Calendar expiresIn)
	{
		AccessToken = token;
		Expiration = expiresIn;
	}
	
	public boolean IsValid()
	{
		if (Utils.IsStringNullOrEmpty(AccessToken))
			return false;
		
		if (Expiration != null && Expiration.after(Calendar.getInstance()))
			return false;
		
		return true;
	}
	
	private void Load(String token, String expiresIn)
	{
		if (!Utils.IsStringNullOrEmpty(token))
		{
			AccessToken = token;
			if (!Utils.IsStringNullOrEmpty(expiresIn))
			{
				Calendar cal = Calendar.getInstance();
				cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, Integer.parseInt(expiresIn));
				
				Expiration = cal;
			}
		}
	}
	
}
