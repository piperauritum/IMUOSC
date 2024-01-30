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


/** Test sounds **/
(
Ndef(\pitch, {
	var in = In.kr(0).lag(0.5);
	a = in.linlin(0, 1, 24, 120).midicps;
	b = Slope.kr(in).lag(0.5);
	c = WhiteNoise.ar(b)!2;
	c = BPF.ar(c, a, 0.1);
}).play;
)
Ndef(\pitch).stop;

(
Ndef(\roll, {
	var in = In.kr(2).lag(0.5);
	a = in.linlin(0, 1, 24, 120).midicps;
	b = Slope.kr(in).lag(0.5);
	a = SinOsc.ar(a, 0, b/4)!2;
}).play;
)
Ndef(\roll).stop;

(
Ndef(\yaw, {
	var in = In.kr(1).lag(0.5);
	b = Slope.kr(in).lag(0.5);
	a = LFSaw.ar(b*50)/4!2;
}).play;
)
Ndef(\yaw).stop;