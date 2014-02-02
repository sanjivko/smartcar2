package com.example.smartcar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

public class SmartCarActivity extends Activity {
	private static final String TAG = "SMARTCAR";
	private static final Boolean DEBUG = true;
	private static final int RESULT_SETTINGS = 1;
	Button connectButton;
	Button fwdButton;
	Button bwButton;
	Button stopButton;

	Button rightButton;
	Button leftButton;

	private MjpegView mv = null;
	private boolean suspending = false;
	WebView web;

	EditText serverIP;
	private DatagramSocket cliSock = null;
	public static final int SERVERPORT = 5000;

	final Handler handler = new Handler();

	AsyncTask<String, Void, MjpegInputStream> asynTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smart_car);

		connectButton = (Button) findViewById(R.id.buttonConnect);
		fwdButton = (Button) findViewById(R.id.buttonFwd);
		bwButton = (Button) findViewById(R.id.buttonBw);
		rightButton = (Button) findViewById(R.id.buttonRight);
		leftButton = (Button) findViewById(R.id.buttonLeft);

		serverIP = (EditText) findViewById(R.id.editText1);
		web = (WebView) findViewById(R.id.webView1);

		mv = (MjpegView) findViewById(R.id.mv);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		connectButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mv.setSource(MjpegInputStream.read("http://"
						+ serverIP.getText().toString() + ":8081"));
				mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
				mv.showFps(false);
			}
		});

		rightButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "RIGHT";

									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Right Command");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				case MotionEvent.ACTION_UP: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "STRAIGHT";
									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "RIght Button Released");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				}

				return false;
			}
		});

		leftButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "LEFT";

									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Left Command");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				case MotionEvent.ACTION_UP: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "STRAIGHT";
									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Left Button Released");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				}

				return false;
			}
		});

		fwdButton.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "FWD";

									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Forward Command");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				case MotionEvent.ACTION_UP: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "STOP";
									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Forward Button Released");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}

				}
				return false;
			}
		});

		bwButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "BACK";

									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Back Command");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				case MotionEvent.ACTION_UP: {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							{
								try {
									cliSock = new DatagramSocket();
									// OutputStream out =
									// cliSock.getOutputStream();
									// out.write("FWD".getBytes());
									InetAddress receiverAddress = null;
									try {
										receiverAddress = InetAddress
												.getByName(serverIP.getText()
														.toString());
									} catch (UnknownHostException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}

									String cmd = "STOP";
									DatagramPacket packet = new DatagramPacket(
											cmd.getBytes(),
											cmd.getBytes().length,
											receiverAddress, SERVERPORT);
									cliSock = new DatagramSocket();
									cliSock.send(packet);
									Log.d("SmartCar", "Back Button Released");
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					});

					thread.run();
					break;
				}
				}
				return false;
			}
		});
	}

	public void onStart() {
		if (DEBUG)
			Log.d(TAG, "onStart()");
		super.onStart();
	}

	/*
	 * public void onPause() { if(DEBUG) Log.d(TAG,"onPause()");
	 * super.onPause(); if(mv!=null){ if(mv.isStreaming()){ mv.stopPlayback();
	 * suspending = true; } } } public void onStop() { if(DEBUG)
	 * Log.d(TAG,"onStop()"); super.onStop(); }
	 * 
	 * public void onDestroy() { if(DEBUG) Log.d(TAG,"onDestroy()");
	 * 
	 * if(mv!=null){ mv.freeCameraMemory(); }
	 * 
	 * super.onDestroy(); }
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_smart_car, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.menu_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;

		}

		return true;
	}

	public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
		protected MjpegInputStream doInBackground(String... url) {
			// TODO: if camera has authentication deal with it and don't just
			// not work
			HttpResponse res = null;
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpParams httpParams = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
			Log.d(TAG, "1. Sending http request");
			try {
				res = httpclient.execute(new HttpGet(URI.create(url[0])));
				Log.d(TAG, "2. Request finished, status = "
						+ res.getStatusLine().getStatusCode());
				if (res.getStatusLine().getStatusCode() == 401) {
					// You must turn off camera User Access Control before this
					// will work
					return null;
				}
				return new MjpegInputStream(res.getEntity().getContent());
			} catch (ClientProtocolException e) {
				if (true) {
					e.printStackTrace();
					Log.d(TAG, "Request failed-ClientProtocolException", e);
				}
				// Error connecting to camera
			} catch (IOException e) {
				if (true) {
					e.printStackTrace();
					Log.d(TAG, "Request failed-IOException", e);
				}
				// Error connecting to camera
			}
			return null;
		}

		protected void onPostExecute(MjpegInputStream result) {
			mv.setSource(result);
			if (result != null) {
				// result.setSkip(1);
				setTitle(R.string.app_name);
			} else {
				setTitle("Disconnected");
			}
			mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
			mv.showFps(false);
		}
	}

	public void setImageError() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				setTitle("Image Error");
				return;
			}
		});
	}

}
