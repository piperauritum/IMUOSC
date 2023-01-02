s.boot;

~imu = IMUReceiver(1);		// start with 1 sensor

~imu.showMsg = true;		// show OSC messages

~imu.stop;					// stop the receiver


/** Test sound **/
(
// pitch
play{
	var in = In.kr(0, 3).lag(0.5);
	b = Slope.kr(in[0]).lag(0.5);
	a = WhiteNoise.ar(b)!2
};

// roll
play{
	var in = In.kr(0, 3).lag(0.5);
	a = midicps(in[2]*120);
	b = Slope.kr(in[2]).lag(0.5);
	a = SinOsc.ar(a, 0, b)!2;
};

// yaw
play{
	var in = In.kr(0, 3).lag(0.5);
	b = Slope.kr(in[1]).lag(0.5);
	a = LFSaw.ar(b*50)!2;
};
)