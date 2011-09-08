package com.kurento.kas.mscontrol.join;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import android.util.Log;

import com.kurento.commons.mscontrol.MsControlException;
import com.kurento.commons.mscontrol.join.Joinable;
import com.kurento.commons.mscontrol.join.JoinableContainer;
import com.kurento.kas.media.profiles.VideoProfile;
import com.kurento.kas.media.rx.VideoRx;
import com.kurento.kas.media.tx.MediaTx;
import com.kurento.kas.mscontrol.mediacomponent.VideoSink;

public class VideoJoinableStreamImpl extends JoinableStreamBase implements
		VideoSink, VideoRx {

	public final static String LOG_TAG = "VideoJoinableStream";

	private VideoProfile videoProfile;

	private long t_suma = 0;
	private long n = 1;

	private long t_suma50 = 0;
	private long n50 = 1;

	private long t_rx = 0;
	private long t_tx = 0;
	private long t_tx_suma = 0;

	private long t_tx_total = 0;
	private long t_tx_total_suma = 0;

	private long t_tx_total_medio = 0;

	private class Frame {
		private byte[] data;
		private int width;
		private int height;

		public Frame(byte[] data, int width, int height) {
			this.data = data;
			this.width = width;
			this.height = height;
		}
	}
	
	private ArrayBlockingQueue<Frame> framesQueue= new ArrayBlockingQueue<Frame>(1);
	
	public VideoProfile getVideoProfile() {
		return videoProfile;
	}

	public VideoJoinableStreamImpl(JoinableContainer container,
			StreamType type, VideoProfile videoProfile) {
		super(container, type);
		this.videoProfile = videoProfile;
		(new VideoTxThread()).start();
	}

	@Override
	public void putVideoFrame(byte[] data, int width, int height) {
		Log.e(LOG_TAG, "RECEIVE FRAME FROM CAMERA");
		framesQueue.clear();
		framesQueue.offer( new Frame(data, width, height) );
	}

	private class VideoTxThread extends Thread {
		@Override
		public void run() {
			for (;;) {
				Frame frameProcessed;
				try {
					frameProcessed = framesQueue.take();
				} catch (InterruptedException e) {
					break;
				}
				
				Log.e(LOG_TAG, "\t\tPROCESS");

				long t = System.currentTimeMillis();
				long t_diff = t - t_tx;
				Log.d(LOG_TAG, "Diff TX frame times: " + t_diff
						+ "\t\tFrame rate: " + videoProfile.getFrameRate());

				if (n > 50) {
					t_tx_suma += t_diff;
					long t_medio50 = t_tx_suma / n;
					Log.d(LOG_TAG, "Diff TX frame times: " + t_diff
							+ "\t\tDiff TX frame times MEDIO: " + t_medio50
							+ "\t\tFrame rate: " + videoProfile.getFrameRate());

					if (videoProfile != null) {
						if (t_tx_total_medio != 0
								&& t_tx_total_medio < 1.1 * 1000 / videoProfile
										.getFrameRate()) {
							Log.e(LOG_TAG, "return");

							t_tx_total_suma += t_diff;
							t_tx_total_medio = t_tx_total_suma / n50;
							Log.d(LOG_TAG, "t TX total: " + t_diff
									+ "\t\t t TX total medio: "
									+ t_tx_total_medio);

							n50++;
							continue;
						}
					}
				}

				t_tx = t;

				long t_init = System.currentTimeMillis();

				MediaTx.putVideoFrame(frameProcessed.data,
						frameProcessed.width, frameProcessed.height);

				long t_fin = System.currentTimeMillis();
				long tiempo = t_fin - t_init;
				t_suma += tiempo;
				long t_medio = t_suma / n;
				if (n > 50) {
					t_suma50 += tiempo;
					long t_medio50 = t_suma50 / n50;
					Log.d(LOG_TAG, "n: " + n + "\t\tTiempo: " + tiempo
							+ "\t\tTiempo medio: " + t_medio
							+ "\t\tTiempo medio50: " + t_medio50);

					t_diff = t_fin - t_tx_total;
					t_tx_total_suma += t_diff;
					t_tx_total_medio = t_tx_total_suma / n50;
					Log.d(LOG_TAG, "t TX total: " + t_diff
							+ "\t\t t TX total medio: " + t_tx_total_medio);

					n50++;
				} else {
					Log.d(LOG_TAG, "n: " + n + "\t\tTiempo: " + tiempo
							+ "\t\tTiempo medio: " + t_medio);
				}

				t_tx_total = t_fin;

				n++;
			}

		}
	}

	@Override
	public void putVideoFrameRx(int[] rgb, int width, int height) {

		long t = System.currentTimeMillis();
		long t_diff = t - t_rx;
		Log.d(LOG_TAG, "Diff RX frame times: " + t_diff);
		t_rx = t;

		try {
			for (Joinable j : getJoinees(Direction.SEND))
				if (j instanceof VideoRx)
					((VideoRx) j).putVideoFrameRx(rgb, width, height);
		} catch (MsControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
