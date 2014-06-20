package com.wutong.taxiapp.activity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.wutong.taxiapp.IA.IApplication;
import com.wutong.taxiapp.Itreface.ImBaseSocketNet;
import com.wutong.taxiapp.base.BaseActivity;
import com.wutong.taxiapp.domain.MyLocation;
import com.wutong.taxiapp.domain.RequestCallTaxi;
import com.wutong.taxiapp.util.ActivityUtils;
import com.wutong.taxiapp.util.AudioRecorder;
import com.wutong.taxiapp.util.ToastUtils;
import com.wutong.taxiapp_passenger.R;

public class TaxiRecordActivity extends BaseActivity implements ImBaseSocketNet {

	private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
	private static final int MAX_RECORD_TIME = 10; // 最长录制时间，单位秒，0为无时间限制

	private static final int RECORD_OFF = 0; // 不在录音
	private static final int RECORD_ON = 1; // 正在录音
	private static final String RECORD_FILENAME = "record"; // 录音文件名

	private int recordState = 0; // 录音状态
	private float recodeTime = 0.0f; // 录音时长
	private double voiceValue = 0.0; // 录音的音量值
	private int playState = 0; // 录音的播放状态
	public static final int STOP = 0;
	public static final int START = 1;
	public static final int PAUSED = 2;

	private boolean moveState = false; // 手指是否移动
	private float downY;
	private Dialog mRecordDialog;
	private AudioRecorder mAudioRecorder;
	private MediaPlayer mMediaPlayer;
	private Thread mRecordThread;

	private ImageView record_image;
	private boolean isSend;
	private View record_but;
	private RadioGroup sexy_rd;
	private ImageView mIvRecVolume;
	private TextView mTvRecordDialogTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_record);
		initTitle(true, "语音叫车", false, null);
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

		record_image = (ImageView) findViewById(R.id.record_image);

		record_but = findViewById(R.id.record_but);

		sexy_rd = (RadioGroup) findViewById(R.id.sexy_rd);
		RadioButton sexy_all = (RadioButton) findViewById(R.id.sexy_all);
		sexy_all.setChecked(true);

		record_tv1 = (TextView) findViewById(R.id.record_tv1);
		record_tv2 = (TextView) findViewById(R.id.record_tv2);

		record_but_tv = (TextView) findViewById(R.id.record_but_tv);

	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();

		mMyLocation = (MyLocation) intent.getSerializableExtra("LOCATION");

	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

		record_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playState == STOP) {
					try {
						mMediaPlayer = new MediaPlayer();
						mMediaPlayer.setDataSource(getAmrPath());
						mMediaPlayer.prepare();
						record_tv2.setText("点击暂停");
						record_image.setImageResource(R.drawable.record_stop);
						playState = START;
						mMediaPlayer.start();
						// 设置播放结束时监听
						mMediaPlayer
								.setOnCompletionListener(new OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										if (playState == START) {
											record_tv2.setText("点击播放");
											record_image
													.setImageResource(R.drawable.record_play);
											playState = STOP;
										}
									}
								});
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else if (playState == START) {
					if (mMediaPlayer.isPlaying()) {
						mMediaPlayer.pause();
						playState = PAUSED;
					} else {
						playState = PAUSED;
					}
					record_tv2.setText("点击播放");
					record_image.setImageResource(R.drawable.record_play);
				} else if (playState == PAUSED) {
					mMediaPlayer.start();
					playState = START;
					record_image.setImageResource(R.drawable.record_stop);
					record_tv2.setText("点击暂停");
				}
			}
		});

		record_but.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: // 按下按钮
					if (isSend) {
						sendSound();
					} else {
						if (recordState != RECORD_ON) {
							downY = event.getY();
							deleteOldFile();
							mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
							recordState = RECORD_ON;
							try {
								mAudioRecorder.start();
								recordTimethread();
								showVoiceDialog(0);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					break;
				case MotionEvent.ACTION_MOVE: // 滑动手指
					if (isSend) {

					} else {
						float moveY = event.getY();
						if (moveY - downY > 50) {
							moveState = true;
							showVoiceDialog(1);
						}
						if (moveY - downY < 20) {
							moveState = false;
							showVoiceDialog(0);
						}
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP: // 松开手指
					if (isSend) {
					} else {
						if (recordState == RECORD_ON) {
							recordState = RECORD_OFF;
							if (mRecordDialog.isShowing()) {
								mRecordDialog.dismiss();
							}
							try {

								try {
									Thread.sleep(150);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								mAudioRecorder.stop();
								mRecordThread.interrupt();
								voiceValue = 0.0;
							} catch (IOException e) {
								e.printStackTrace();
							}
							if (!moveState) {
								if (recodeTime < MIN_RECORD_TIME) {
									ToastUtils.toast(mContext, "时间太短  录音失败");
								} else if (recodeTime > MAX_RECORD_TIME) {
									ToastUtils.toast(mContext, "时间太长  录音失败");
								} else {
									// 录音成功
									record_tv1.setVisibility(View.GONE);
									record_tv2.setVisibility(View.VISIBLE);
									record_but_tv.setText("确认发送");
									isSend = true;
									record_image
											.setImageResource(R.drawable.record_play);
								}
							}
							moveState = false;
						}
					}
					break;
				}
				return false;
			}
		});
	}

	// 录音时显示Dialog
	void showVoiceDialog(int flag) {
		if (mRecordDialog == null) {
			mRecordDialog = new Dialog(TaxiRecordActivity.this,
					R.style.DialogStyle);
			mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mRecordDialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			mRecordDialog.setContentView(R.layout.record_dialog);
			mIvRecVolume = (ImageView) mRecordDialog
					.findViewById(R.id.record_dialog_img);
			mTvRecordDialogTxt = (TextView) mRecordDialog
					.findViewById(R.id.record_dialog_txt);
		}
		switch (flag) {
		case 1:
			mIvRecVolume.setImageResource(R.drawable.record_cancel);
			mTvRecordDialogTxt.setText("松开手指可取消录音");
			break;

		default:
			mIvRecVolume.setImageResource(R.drawable.record_animate_01);
			mTvRecordDialogTxt.setText("向下滑动可取消录音");
			break;
		}
		mTvRecordDialogTxt.setTextSize(14);
		mRecordDialog.show();
	}

	// 删除老文件
	void deleteOldFile() {
		File file = new File(Environment.getExternalStorageDirectory(),
				"WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
		if (file.exists()) {
			file.delete();
		}
	}

	// 获取文件手机路径
	private String getAmrPath() {
		File file = new File(Environment.getExternalStorageDirectory(),
				"WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
		return file.getAbsolutePath();
	}

	// 录音计时线程
	void recordTimethread() {
		mRecordThread = new Thread(recordThread);
		mRecordThread.start();
	}

	// 录音Dialog图片随声音大小切换
	void setDialogImage() {
		// System.out.println(voiceValue);
		if (voiceValue == 1) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue == 2) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue == 3) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue == 4) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue == 5) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue == 6) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue == 7) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue == 8) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue == 9) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue == 10) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue == 11) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue == 12) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue == 13) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue == 14) {
			mIvRecVolume.setImageResource(R.drawable.record_animate_14);
		}
	}

	// 录音线程
	private Runnable recordThread = new Runnable() {

		@Override
		public void run() {
			recodeTime = 0.0f;
			while (recordState == RECORD_ON) {
				// 限制录音时长
				// if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
				// imgHandle.sendEmptyMessage(0);
				// } else
				{
					try {
						Thread.sleep(150);
						recodeTime += 0.15;
						// 获取音量，更新dialog
						if (!moveState) {
							voiceValue = mAudioRecorder.getAmplitude();
							recordHandler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};

	public Handler recordHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setDialogImage();
		}
	};
	private TextView record_tv1;
	private TextView record_tv2;
	private TextView record_but_tv;
	private MyLocation mMyLocation;

	@Override
	public void acceptResult(JSONObject jsonObject) {
		// TODO Auto-generated method stub

	}

	// 开线程分割发送语音
	private void sendSound() {
		new Thread() {
			@Override
			public void run() {
				byte[] date = mAudioRecorder.getWAVDate();

				ByteArrayInputStream bis = new ByteArrayInputStream(date);

				byte[] buff = new byte[1024 * 4];

				int i = (int) Math.ceil(date.length / ((double) 1024.00 * 4));
				for (int j = 0; j < i; j++) {

					RequestCallTaxi callTaxi = new RequestCallTaxi();
					int checkedRadioButtonId = sexy_rd
							.getCheckedRadioButtonId();
					String sex = null;
					if (checkedRadioButtonId == R.id.sexy_male) {
						sex = "男";
					} else if (checkedRadioButtonId == R.id.sexy_female) {
						sex = "女";
					} else {
						sex = "";
					}
					callTaxi.setType("1");
					callTaxi.setDesAddress("");
					callTaxi.setDesLat("");
					callTaxi.setDesLong("");
					callTaxi.setSourceAddress(mMyLocation.getAddress_address());
					callTaxi.setSourceLat(mMyLocation.getLatitude() + "");
					callTaxi.setSourceLong(mMyLocation.getLongitude() + "");
					callTaxi.setSex(sex);
					callTaxi.setPhone(IApplication.getInstance().sharedConfig
							.getMobile());
					if (j == i - 1) {
						callTaxi.setFlag("END");
					} else if (j == 0) {
						callTaxi.setFlag("START");
					} else {
						callTaxi.setFlag("" + j);
					}
					try {
						bis.read(buff);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					callTaxi.setSourceSound(Base64.encodeToString(buff,
							Base64.DEFAULT));

					try {
						lib.requestCallTaxi(callTaxi);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				handler.sendEmptyMessage(SENDSOUNDEND);

			}
		}.start();
	}

	public static final int SENDSOUNDEND = 0;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			int i = msg.what;

			switch (i) {
			case SENDSOUNDEND:
				// 发送数据,跳转到等待界面

				ActivityUtils.startActivityAndFinish(mContext,
						WaitOrderActivity.class);

				break;
			}

		};

	};

}
