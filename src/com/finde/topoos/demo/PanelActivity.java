package com.finde.topoos.demo;

import android.app.Activity;
import com.finde.topoos.api.AccessObject;
import com.finde.topoos.api.Operations;
import com.finde.topoos.api.Position;
import com.finde.topoos.internal.PreferencesManager;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PanelActivity extends Activity {
	
	private static final int WORKER_MSG_REGISTER_POSITION_OK = 2;
	private static final int WORKER_MSG_REGISTER_POSITION_ERROR = -2;
	private static final int WORKER_MSG_REGISTER_TRACK_OK = 1;
	private static final int WORKER_MSG_REGISTER_TRACK_ERROR = -1;
	
	public static final int EXIT_ID = 2;
	public static final int LOGOUT_ID = 1;
	
	private static final Integer MIN_GPS_TIME = 10000; //milliseconds
	private static final Integer MIN_GPS_DISTANCE = 5; //meters
	
	private AccessObject Access = null;
	private Location m_CurrentLocation = null;
	private int m_CurrentTrack = -1;
	private Handler m_Handler = new Handler(new ResultMessageCallback());
	
	private boolean m_WaitingTrackId = false;
	
	MapView MiMapa = null;
	MapController MiMapaController = null;
	
	LocationListener milocListener = null;
	LocationManager milocManager = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel); // establecemos el Layout de esta Actividad
		
		//Obtenemos las preferencias de esta ventana, asumimos que si hemos llegado aqui es porque es valido
		Access = PreferencesManager.LoadPreferences_Access(this.getApplicationContext());
		
		
		//El boton de terminar no debe estar habilitado si lo esta el boton de inicio
		Button EndButton = (Button) findViewById(R.id.ButtonEnd);
		EndButton.setEnabled(false);

	}

	/**
	 * Maneja el evento Init click
	 * @param v
	 */
	public void onClickInit(View v)
	{
		StartGPSListener();
		
		Button InitButton = (Button) findViewById(R.id.ButtonInit);
		//Button EndButton = (Button) findViewById(R.id.ButtonEnd);

		InitButton.setEnabled(false);
		//EndButton.setEnabled(true);
	}
	
	
	/**
	 * Maneja el evento Init end
	 * @param v
	 */
	public void onClickEnd(View v)
	{
		StopGPSListener();
		
		Button InitButton = (Button) findViewById(R.id.ButtonInit);
		Button EndButton = (Button) findViewById(R.id.ButtonEnd);

		InitButton.setEnabled(true);
		EndButton.setEnabled(false);
		
		if (m_CurrentLocation != null && m_CurrentTrack >= 0)
		{
			Runnable worker = new RegisterPositionWorker(m_CurrentLocation, Operations.POSITION_TYPE_TRACK_END, m_CurrentTrack, Access.AccessToken);
			Thread thread = new Thread(worker);
			thread.start();
			
			m_CurrentLocation = null;
			m_CurrentTrack = -1;
			m_WaitingTrackId = false;
		}
	}
	
	
	/**
	 * Comienza a capturar posiciones
	 */
	private void StartGPSListener()
	{	
		AppendLog("Iniciando Chip GPS...");
		AppendLog("Quiza deberias moverte...");
		// Usamos la LocationManager para acceder a la localizaci—n o uso del GPS
		milocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		milocListener = new MiLocationListener();

		milocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_GPS_TIME, MIN_GPS_DISTANCE,	milocListener);
	}
	
	
	/**
	 * Detiene la captura de posiciones
	 */
	private void StopGPSListener()
	{

		AppendLog("Deteniendo Chip GPS...");
		
		try{
			milocManager.removeUpdates(milocListener);
		}catch(Exception ex)
		{ }
		

		AppendLog("Chip GPS detenido");
	}
	
	
	/**
	 * Agrega una linea al log en la pantalla
	 * @param line
	 */
	private void AppendLog(String line)
	{
		TextView t = (TextView)findViewById(R.id.LogText);
		if (t != null)
		{
			t.setText(line + "\r\n" + t.getText().toString());
		}
	}
	
	

	private class ResultMessageCallback implements Callback {

		public boolean handleMessage(Message arg0) {
			
			switch(arg0.what)
			{
			case WORKER_MSG_REGISTER_POSITION_ERROR:
				Toast.makeText(PanelActivity.this, "Error en el registro de la posicion", Toast.LENGTH_LONG).show();
				AppendLog("Pos. Registrada: ERROR");
				break;
			case WORKER_MSG_REGISTER_POSITION_OK:
				AppendLog("Pos. Registrada: "+ (Integer)arg0.obj);
				break;
			case WORKER_MSG_REGISTER_TRACK_ERROR:
				Toast.makeText(PanelActivity.this, "Error en el registro de la posicion", Toast.LENGTH_LONG).show();
				StopGPSListener();
				
				Button InitButton = (Button) findViewById(R.id.ButtonInit);
				InitButton.setEnabled(true);
				
				AppendLog("Track Registrado: ERROR");
				break;
			case WORKER_MSG_REGISTER_TRACK_OK:
				m_CurrentTrack = (Integer)arg0.obj;
				m_WaitingTrackId = false;
				
				Button EndButton = (Button) findViewById(R.id.ButtonEnd);
				EndButton.setEnabled(true);
				
				AppendLog("Track Registrado: " + m_CurrentTrack);
				break;
			}
			
			return true; //lo marcamos como procesado
		}
	}
	
	/***********************************************************************************************
	 * REGISTRO DE POSICIONES
	 ***********************************************************************************************/
	
	/**
	 * Registra una nueva posicion en topoos
	 * @author findemor
	 *
	 */
	private class RegisterPositionWorker implements Runnable {
		
		Location location = null;
		int positionType = -1;
		int trackID= -1;
		String access_token = null;
		
		public RegisterPositionWorker(Location loc, int posType, int trackId, String accessToken)
		{
			location = loc;
			positionType = posType;
			trackID = trackId;
			access_token = accessToken;
		}
		
		public void run(){
			
			Message msg = new Message();
			
			try {
				
				Position pos = Operations.RegisterPosition(location.getLatitude(), location.getLongitude(), positionType, trackID, access_token);
				
				if (pos == null)
				{
					msg.what = WORKER_MSG_REGISTER_POSITION_ERROR;
					msg.obj = -1;
				}else 
				{
					msg.what = WORKER_MSG_REGISTER_POSITION_OK;
					msg.obj = pos.Id;
				}
				
				
			} catch (Exception e) {
				
				msg.what = WORKER_MSG_REGISTER_TRACK_ERROR;
				msg.obj = -1;
				Log.e("Register position", e.getMessage());
			}
			
			m_Handler.sendMessage(msg);
		}
	}
	
	

	/**
	 * Crea un nuevo track en topoos
	 * @author findemor
	 *
	 */
	private class RegisterTrackWorker implements Runnable {
		
		String access_token = null;
		
		public RegisterTrackWorker(String accessToken)
		{
			access_token = accessToken;
		}
		
		public void run(){
			
			Message msg = new Message();
			
			try {
				
				int trackId = Operations.RegisterTrack(access_token);
				
				msg.what = WORKER_MSG_REGISTER_TRACK_OK;
				msg.obj = trackId;
				
			} catch (Exception e) {

				msg.what = WORKER_MSG_REGISTER_TRACK_ERROR;
				Log.e("Track", e.getMessage());
			}
			
			m_Handler.sendMessage(msg);
		}
	}

	 /**
	  * Listener que se encarga de capturar nuevas posiciones del GPS
	  * @author findemor
	  *
	  */
	public class MiLocationListener implements LocationListener
	{
		
		public void onLocationChanged(Location loc)
		{
			m_CurrentLocation = loc;
			
			Runnable worker = null;
			
			if (m_CurrentTrack >= 0 && !m_WaitingTrackId)
			{
				//Ya tenemos un identificador de track, asociamos la posicion
				worker = new RegisterPositionWorker(loc, Operations.POSITION_TYPE_NORMAL, m_CurrentTrack, Access.AccessToken);
			}
			else if (!m_WaitingTrackId)
			{
				//No tenemos track id y aun no hemos solicitado uno
				m_WaitingTrackId = true;
				worker = new RegisterTrackWorker(Access.AccessToken);
			}
			
			if (worker != null)
			{
				Thread thread = new Thread(worker);
				thread.start();
			}
		}

		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(),"Gps Desactivado",Toast.LENGTH_SHORT ).show();
		}

		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(),"Gps Activo",Toast.LENGTH_SHORT ).show();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) { }
		
	}
	
	
	
	
	
	
	
	
	
	/***********************************************************************************************
	 * GESTION DE MENU CONTEXTUAL
	 ***********************************************************************************************/
	
	//MENU CREACION
	/**
	 * Cuando se crea el menu, se insertan los botones
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    boolean result = super.onCreateOptionsMenu(menu);
	    menu.add(0, LOGOUT_ID, 0, "LogOut");
	    menu.add(0, EXIT_ID, 1, "Close");
	    return result;
	}
	
	//MENU PULSACION
	/**
	 * Se determina la funcionalidad de cada boton del menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case EXIT_ID:
	        	StopGPSListener();
	            this.finish();
	            return true;
	        case LOGOUT_ID:
	        	ReLogin();
	        	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	private void ReLogin() {
		StopGPSListener();
		//Borramos preferencias
		PreferencesManager.DeletePreferences_Access(getApplicationContext());
		//Close this activity
		Intent LogInIntent = new Intent(this.getApplicationContext(), MainActivity.class);
		startActivityForResult(LogInIntent, 0);
		finish();
	}
	
		
}
