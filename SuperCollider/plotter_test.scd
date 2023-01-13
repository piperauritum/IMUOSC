s.boot;

~imu = IMUReceiver(2);		// Start with 2 sensors
~mon = KrMonitor.new(0, 20);	// Control bus monitor

/*
The receiver sends six values (ahrs_x, ahrs_y, ahrs_z, accl_x, accl_y, accl_z) per sensor to the control bus.
The index of the control bus is a multiple of 6.
*/

~imu.showMsg = true;		// Show OSC messages
~imu.stop;					// Stop the receiver
~mon.stop;					// Stop the monitor


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