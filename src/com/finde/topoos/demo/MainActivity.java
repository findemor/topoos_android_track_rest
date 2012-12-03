package com.finde.topoos.demo;

import com.finde.topoos.api.AccessObject;
import com.finde.topoos.api.LoginInterface;
import com.finde.topoos.internal.PreferencesManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/*
 * Actividad principal de la aplicacion
 * Es una aplicacion mixta con un componente web
 */
public class MainActivity extends Activity {
	private WebView browser;
	private AccessObject Access = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//GoToPanel();

		Access = PreferencesManager.LoadPreferences_Access(this.getApplicationContext());
		
		if (!Access.IsValid()) //No tenemos un token valido, lo necesitamos
		{
			browser = (WebView) findViewById(R.id.webview);

			browser.getSettings().setJavaScriptEnabled(true);
			browser.addJavascriptInterface(this, "jsNativeInterface");
			browser.loadUrl(LoginInterface.DialogURL);
			browser.clearCache(true);
			browser.setWebViewClient(new LoginViewClient()); // Evita que se abra un navegador cuando se produce la carga de otra url
		}
		else
		{
			GoToPanel();
		}
	}

	
	private void GoToPanel() {
		Intent PanelIntent = new Intent(this.getApplicationContext(), PanelActivity.class);
		startActivityForResult(PanelIntent, 0);
		finish();
	}
	
	private void ProcessURLCallback(String url)
	{
		AccessObject AccessAux = LoginInterface.GetAccessToken(url);// Captura el access Token si esta en la url

		if (AccessAux != null) {
			PreferencesManager.SavePreferences_Access(AccessAux, this.getApplicationContext());
			Access = AccessAux;
			GoToPanel();
		}
	}
	

	private class LoginViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			ProcessURLCallback(url);
			view.loadUrl(url);
			return true;
		}

	}

	
	

}