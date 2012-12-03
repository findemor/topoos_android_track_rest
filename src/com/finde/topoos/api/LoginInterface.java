package com.finde.topoos.api;

import java.net.URLDecoder;
import com.finde.topoos.internal.AppConfiguration;
import com.finde.topoos.internal.Utils;

/*
 * Clase que actua como interfaz a la parte nativa de la aplicaci—n, y viceversa
 */
public class LoginInterface {
	
	public static final String DialogURL = "https://login.topoos.com/oauth/authtoken?response_type=token&client_id="+AppConfiguration.TOPOOS_CLIENT_ID+"&redirect_uri=https%3A%2F%2Flogin.topoos.com%2Foauth%2Fdummy&scope=offline_access&agent=mobile"; 
	public static final String RedirectURI = "https://login.topoos.com/oauth/dummy";
	
	private static final String ParamKey_AccessToken = "access_token";
	private static final String ParamKey_ExpiresIn = "expires_in";
	

	
	public static AccessObject GetAccessToken (String url)
	{
		AccessObject Access = null;
		
    	//Capturamos el access token si procede
    	if (url.startsWith(LoginInterface.RedirectURI))
    	{
    		String AuxAccessToken = "";
    		String AuxExpiresIn = "";
    		
    		String[] urlFragment = url.split("#");
    		
    		if (urlFragment.length > 1) //#key1=value1&key2=value2...
    		{   			
    			String[] Params = urlFragment[1].split("&"); //key1=value1
    			for (String param : Params)
    			{
    				String[] pair = param.split("=");
    				
    				String key = URLDecoder.decode(pair[0]);
    				String value = URLDecoder.decode(pair[1]);
    				
    				if (key.equalsIgnoreCase(ParamKey_AccessToken))
    				{
    					AuxAccessToken = value;
    				}
    				else if (key.equalsIgnoreCase(ParamKey_ExpiresIn))
    				{
    					AuxExpiresIn = value;
    				}
    			}
    		}
    		
    		
    		//Crea el objeto AccessObject
    		if (!Utils.IsStringNullOrEmpty(AuxAccessToken))
    		{
    			Access = new AccessObject(AuxAccessToken, AuxExpiresIn);
    		}
    	}
    	
    	return Access;
    	
	}

}
