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
	private Button rec_button;
	private Button nxt_button;
	private ImageView imageB;
	private ImageView imageA;
	//private ToggleButton button_;
	//private TextView textView1;
	private double DistanceOutput;
	private TextView distance_view ;
	private TextView distance_final;
	private double [] distance_values;
	private int wptr ;
	private final int FILTER_LENGTH = 20;
	private final int NUM_IMAGES = 7;
	private int [] drawable_ids ;
	private int image_ptr ;

	/**
	 * Called when the activity is first created. Here we normally initialize
	 * our GUI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		//button_ = (ToggleButton) findViewById(R.id.button);
		//textView1 = (TextView) findViewById(R.id.textView1);

		setContentView(R.layout.activity_actual_eye_test);
		rec_button = (Button) findViewById(R.id.rec_button);
		nxt_button = (Button) findViewById(R.id.nxt_button);
		imageB = (ImageView) findViewById(R.id.image_view2);
		imageA = (ImageView) findViewById(R.id.image_view1);
		distance_view = (TextView)findViewById(R.id.distance_view);
		distance_final = (TextView)findViewById(R.id.distance_final);
		distance_values = new double[FILTER_LENGTH];
		drawable_ids = new int[NUM_IMAGES];

		//Initialize array of drawable ids
		drawable_ids[0] = R.drawable.lettera;
		drawable_ids[1] = R.drawable.letterb;
		drawable_ids[2] = R.drawable.afp;
		drawable_ids[3] = R.drawable.e45_1_medium;
		drawable_ids[4] = R.drawable.e45_2_medium;
		drawable_ids[5] = R.drawable.focuspat;
		drawable_ids[6] = R.drawable.icon;

		imageA.setImageResource(drawable_ids[0]);

		rec_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nxt_button.setVisibility(View.VISIBLE);
				rec_button.setVisibility(View.INVISIBLE);
				distance_final.setVisibility(View.VISIBLE);
				double total = 0;
				double distance_average;
				for(int i = 0; i < distance_values.length; i++)
				{
					total = total + distance_values[i];
				}

				distance_average = total/FILTER_LENGTH;
				distance_final.setText(String.format("%.02f",distance_average));
			}
		});

		nxt_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageA.setImageResource(drawable_ids[getNextImageId()]);
				//imageA.setVisibility(View.INVISIBLE);
				//imageB.setVisibility(View.VISIBLE);
				rec_button.setVisibility(View.VISIBLE);
				distance_final.setVisibility(View.INVISIBLE);
				nxt_button.setVisibility(View.INVISIBLE);
			}
		});
	}

	int getNextImageId() {
		if (image_ptr >= NUM_IMAGES-1)
			image_ptr = 0;
		else
			image_ptr++;
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
					distance_view.setText(String.format("%.02f",DistanceOutput));
					if (DistanceOutput > 60){
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