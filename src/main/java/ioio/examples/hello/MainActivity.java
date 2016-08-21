package ioio.examples.hello;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PulseInput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/*
 * TRIG = PIN 34
 * ECHO = PIN 35
 * VCC = PIN +5V
 * GND = PIN GND
 */

/**
 * This is the main activity of the HelloIOIO example application.
 *
 * It displays a toggle button on the screen, which enables control of the
 * on-board LED. This example shows a very simple usage of the IOIO, by using
 * the {@link IOIOActivity} class. For a more advanced use case, see the
 * HelloIOIOPower example.
 */
public class MainActivity extends IOIOActivity {
	private Button results_button;
	private Button rec_button;
	private Button nxt_button;
	private Button back_button;
	private Button reset_button;
	private Button redo_button;
	private ImageView imageA;

	private double DistanceOutput;
	private TextView distance_view ;
	private TextView distance_final;
	private double [] distance_values;
	private double [] recorded_distances;
	private int wptr ;
	private final int FILTER_LENGTH = 20;
	private final int NUM_IMAGES = 20;
	private final double OFFSET = 2.34;
	private int [] drawable_ids ;
	private int image_ptr;
	private final double optotype_size = 0.0087;
	private final double MAX_DISTANCE_THRESHOLD_WARN = 70.0;
	private int index_count = 0;
	private final String TAG="MainActivity";

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_actual_eye_test);
		results_button = (Button) findViewById(R.id.results);
		redo_button = (Button) findViewById(R.id.redo);
		reset_button = (Button) findViewById(R.id.reset);
		rec_button = (Button) findViewById(R.id.rec_button);
		nxt_button = (Button) findViewById(R.id.nextbutton);
		imageA = (ImageView) findViewById(R.id.image_view1);
		distance_view = (TextView) findViewById(R.id.distance_view);
		distance_final = (TextView) findViewById(R.id.distance_final);
		distance_values = new double[FILTER_LENGTH];
		recorded_distances = new double[NUM_IMAGES];
		drawable_ids = new int[NUM_IMAGES];
		back_button = (Button) findViewById(R.id.backbutton);

		results_button.setVisibility(View.INVISIBLE);
		//Initialize array of drawable ids
		/* drawable_ids[0] = R.drawable.etwohundo;
		drawable_ids[1] = R.drawable.fonehundo;
		drawable_ids[2] = R.drawable.ponehundo;
		drawable_ids[3] = R.drawable.oseventy;
		drawable_ids[4] = R.drawable.zseventy;
		drawable_ids[5] = R.drawable.tseventy;
		drawable_ids[6] = R.drawable.dfifty;
		drawable_ids[7] = R.drawable.efifty;
		drawable_ids[8] = R.drawable.lfifty;
		drawable_ids[9] = R.drawable.pfifty;
		drawable_ids[10] = R.drawable.cforty;
		drawable_ids[11] = R.drawable.dforty;
		drawable_ids[12] = R.drawable.eforty;
		drawable_ids[13] = R.drawable.fforty;
		drawable_ids[14] = R.drawable.pforty;
		drawable_ids[15] = R.drawable.cthirty;
		drawable_ids[16] = R.drawable.dthirty;
		drawable_ids[17] = R.drawable.ethirty;
		drawable_ids[18] = R.drawable.fthirty;
		drawable_ids[19] = R.drawable.pthirty;
		drawable_ids[20] = R.drawable.zthirty;
		drawable_ids[21] = R.drawable.dtwentyfive;
		drawable_ids[22] = R.drawable.etwentyfive; */
		drawable_ids[0] = R.drawable.ftwentyfive;
		drawable_ids[2] = R.drawable.ltwentyfive;
		drawable_ids[4] = R.drawable.otwentyfive;
		drawable_ids[6] = R.drawable.ptwentyfive;
		drawable_ids[8] = R.drawable.ztwentyfive;
		drawable_ids[10] = R.drawable.ctwenty;
		drawable_ids[12] = R.drawable.dtwenty;
		drawable_ids[14] = R.drawable.etwenty;
		drawable_ids[16] = R.drawable.ftwenty;
		drawable_ids[18] = R.drawable.ptwenty;

		//	drawable_ids[33] = R.drawable.ttwenty;
		//	drawable_ids[34] = R.drawable.otwenty;
		drawable_ids[1] = R.drawable.afp;
		drawable_ids[3] = R.drawable.e45_1_medium;
		drawable_ids[5] = R.drawable.e45_2_medium;
		drawable_ids[7] = R.drawable.vector;
		drawable_ids[9] = R.drawable.seamless_geometry;
		drawable_ids[11] = R.drawable.greenimage2;
		drawable_ids[13] = R.drawable.monocircles;
		drawable_ids[15] = R.drawable.blackandwhite;
		drawable_ids[17] = R.drawable.greenpattern;
		drawable_ids[19] = R.drawable.brown;


//		drawable_ids[6] = R.drawable.lettero;
//		drawable_ids[7] = R.drawable.letterp;
//		drawable_ids[8] = R.drawable.lettert;
//		drawable_ids[9] = R.drawable.letterz;

		imageA.setImageResource(drawable_ids[0]);
		rec_button.setBackgroundColor(Color.GREEN);
		results_button.setBackgroundColor(Color.MAGENTA);

		rec_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nxt_button.setVisibility(View.VISIBLE);
				rec_button.setVisibility(View.INVISIBLE);
				distance_final.setVisibility(View.VISIBLE);
				double total = 0;
				double distance_average;
				for (int i = 0; i < distance_values.length; i++) {
					total = total + distance_values[i] - OFFSET;
				}
				distance_average = total / FILTER_LENGTH;
				recorded_distances[index_count] = distance_average;
				double visual_angle = 2 * Math.atan(optotype_size / (2 * distance_average / 100));
				distance_final.setText(String.format("%.02f", distance_average) + " cm");
				distance_final.setTextColor(Color.BLACK);
				if (index_count == NUM_IMAGES-1) {
					results_button.setVisibility(View.VISIBLE);
				}
			}
		});

		nxt_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageA.setImageResource(drawable_ids[getNextImageId()]);
				if(index_count >= NUM_IMAGES - 1) {
					index_count = 0;
					results_button.setVisibility(View.INVISIBLE);
				}
				else if(index_count == NUM_IMAGES - 2) {
					results_button.setVisibility(View.INVISIBLE);
					index_count = NUM_IMAGES - 1;
				}
				else {
					index_count++;
					results_button.setVisibility(View.INVISIBLE);
				}
				rec_button.setVisibility(View.VISIBLE);
				distance_final.setVisibility(View.INVISIBLE);
			}
		});

		results_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				double letter_total = 0;
				double patterns_total = 0;
				Log.d(TAG,"Result button clicked");
				for(int i = 0; i < recorded_distances.length; i+=2)
				{
					letter_total = letter_total + recorded_distances[i];
				}
				for(int i = 1; i < recorded_distances.length; i+=2)
				{
					patterns_total = patterns_total + recorded_distances[i];
				}
				double recorded_letters_distance_average = letter_total/10;
				double recorded_patterns_distance_average = patterns_total/10;
				double overall_distance_average = (recorded_letters_distance_average + recorded_patterns_distance_average)/2;
				double overall_distance_average_in_meters = overall_distance_average/100;
				double prescription = 1/overall_distance_average_in_meters;
				distance_final.setVisibility(View.VISIBLE);
				distance_final.setText("Average distance for Letters: " + String.format("%.02f cm ", recorded_letters_distance_average) + " and Patterns: " + String.format("%.02f cm", recorded_patterns_distance_average) + "," + " Prescription: " + String.format("%.02f", prescription));
			}
		});

		back_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageA.setImageResource(drawable_ids[goBackOneImage()]);
				if(index_count <= 0) {
					index_count = NUM_IMAGES - 1;
					results_button.setVisibility(View.VISIBLE);
				}
				else {
					index_count--;
					results_button.setVisibility(View.INVISIBLE);
				}
				rec_button.setVisibility(View.VISIBLE);
				distance_final.setVisibility(View.INVISIBLE);
				//nxt_button.setVisibility(View.INVISIBLE);
			}
		});

		reset_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageA.setImageResource(drawable_ids[getFirstImage()]);
				index_count = 0;
				rec_button.setVisibility(View.VISIBLE);
				distance_final.setVisibility(View.INVISIBLE);
				//nxt_button.setVisibility(View.INVISIBLE);
			}
		});

		redo_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				distance_values[index_count] = 0;
				imageA.setImageResource(drawable_ids[image_ptr]);
				rec_button.setVisibility(View.VISIBLE);
				distance_final.setVisibility(View.INVISIBLE);
				//nxt_button.setVisibility(View.INVISIBLE);
			}
		});
	}

	int getFirstImage() {
		image_ptr = 0;
		return image_ptr;
	}

	int getNextImageId() {
		if (image_ptr >= NUM_IMAGES-1)
			image_ptr = 0;
		else
			image_ptr++;
		return image_ptr;
	}
	int goBackOneImage() {
		if(image_ptr <= 0)
			image_ptr = NUM_IMAGES - 1;
		else
			image_ptr--;
		return image_ptr;
	}
	/**
	 * This is the thread on which all the IOIO activity happens. It will be run
	 * every time the application is resumed and aborted when it is paused. The
	 * method setup() will be called right after a connection with the IOIO has
	 * been established (which might happen several times!). Then, loop() will
	 * be called repetitively until the IOIO gets disconnected.
	 */
	class Looper extends BaseIOIOLooper {
		/** The on-board LED. */
		private DigitalOutput led_;
		private DigitalOutput digital_led1;

		private DigitalOutput UltraSonicTrigger;
		private PulseInput UltraSonicEcho;

		/**
		 * Called every time a connection with IOIO has been established.
		 * Typically used to open pins.
		 *
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 *
		 *
		 */
		@Override
		protected void setup() throws ConnectionLostException {
			//showVersions(ioio_, "IOIO connected!");
			led_ = ioio_.openDigitalOutput(0, true);
			UltraSonicTrigger = ioio_.openDigitalOutput(34,false);
			UltraSonicEcho = ioio_.openPulseInput(35, PulseInput.PulseMode.POSITIVE);
			//enableUi(true);
		}

		/**
		 * Called repetitively while the IOIO is connected.
		 *
		 * @throws ConnectionLostException
		 *             When IOIO connection is lost.
		 * @throws InterruptedException
		 * 				When the IOIO thread has been interrupted.
		 *
		 * @see ioio.lib.util.IOIOLooper#loop()
		 */

		@Override
		public void loop() throws ConnectionLostException, InterruptedException {

			try{
				//led_.write(!button_.isChecked());


				UltraSonicTrigger.write(false);

				TimeUnit.MICROSECONDS.sleep(2);

				UltraSonicTrigger.write(true);

				TimeUnit.MICROSECONDS.sleep(10);

				UltraSonicTrigger.write(false);

				// For Centimeters
				DistanceOutput = (UltraSonicEcho.getDuration() * 1000000) / 58;

				distance_values[wptr] = DistanceOutput;

				if (wptr >= FILTER_LENGTH-1)
					wptr = 0;
				else
					wptr++;

				//For Inches
				//DistanceOutput = (UltraSonicEcho.getDuration() * 1000000) / 148;
				runOnUiThread(new Runnable() {
				@Override
				public void run() {
					distance_view.setText(String.format("I#%2d :: %.02f",index_count,DistanceOutput));

					if (DistanceOutput > MAX_DISTANCE_THRESHOLD_WARN){
						distance_view.setTextColor(Color.RED);
					}
					else{
						distance_view.setTextColor(Color.GREEN);
					}
				}
				});

				Thread.sleep(50);
			}catch(InterruptedException e){
				e.printStackTrace();
			}

		}

		/**
		 * Called when the IOIO is disconnected.
		 *
		 * @see ioio.lib.util.IOIOLooper#disconnected()
		 */
		@Override
		public void disconnected() {
			//enableUi(false);
//			toast("IOIO disconnected");
		}

		/**
		 * Called when the IOIO is connected, but has an incompatible firmware version.
		 *
		 * @see ioio.lib.util.IOIOLooper#incompatible(IOIO)
		 */
//		@Override
//		//public void incompatible() {
//			showVersions(ioio_, "Incompatible firmware version!");
//		}
}

	/**
	 * A method to create our IOIO thread.
	 *
	 * @see ioio.lib.util.AbstractIOIOActivity#createIOIOThread()
	 */
	protected IOIOLooper createIOIOLooper() {
		return new Looper();
	}

//	private void showVersions(IOIO ioio, String title) {
//		toast(String.format("%s\n" +
//				"IOIOLib: %s\n" +
//				"Application firmware: %s\n" +
//				"Bootloader firmware: %s\n" +
//				"Hardware: %s",
//				title,
//				ioio.getImplVersion(VersionType.IOIOLIB_VER),
//				ioio.getImplVersion(VersionType.APP_FIRMWARE_VER),
//				ioio.getImplVersion(VersionType.BOOTLOADER_VER),
//				ioio.getImplVersion(VersionType.HARDWARE_VER)));
//	}

//	private void toast(final String message) {
//		final Context context = this;
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
//			}
//		});
//	}

	private int numConnected_ = 0;

//	private void enableUi(final boolean enable) {
//		// This is slightly trickier than expected to support a multi-IOIO use-case.
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if (enable) {
//					if (numConnected_++ == 0) {
//						//button_.setEnabled(true);
//					}
//				} else {
//					if (--numConnected_ == 0) {
//						//button_.setEnabled(false);
//					}
//				}
//			}
//		});
//	}
}