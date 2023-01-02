s.boot;

(
var numSensor, view, graph, aryVal, imuVal;
var recvData, batt_chk, low_batt, plotVal;

numSensor = 1;

view = Window.new("M5StickC-Plus IMU", Rect(100, 100, 600, 600));
view.alwaysOnTop = true;
view.onClose = {
	CmdPeriod.run;
};
view.front;

imuVal = Array.fill(3 * numSensor, { 0 });
aryVal = Array2D(3 * numSensor, 100);
graph = Array.fill(3 * numSensor, {|n|
	w = 600 / (3 * numSensor);
	Plotter("", Rect(0, w*n, 600, w-2), view);
});


// Get and map IMU values, send to busses
recvData.free;
recvData = OSCFunc({|msg, time|
	var id = msg[1] * 3;
	var val = msg[2..4];
	val.do{|e,i|
		if (i == 0, {
			v = e.linlin(-90, 90, 0, 1);
		},{
			v = e.linlin(-180, 180, 0, 1);
		});
		s.sendMsg(\c_set, id + i, v);
		imuVal[id + i] = v;
	};
	// msg.postln;
}, '/senddata');


// Plot the IMU values
plotVal.stop;
plotVal = Routine({
	inf.do{|tick|
		0.1.wait;
		imuVal.do{|e,i|

			b = aryVal.rowAt(i).asArray;
			b.removeAt(0);
			b.add(e);
			b = b.collect{|f| if(((f==inf)||(f==nil)),{0},{f})};
			b.do{|f,j| aryVal.put(i, j, f)};

			defer{
				graph[i].value = aryVal.rowAt(i);
				graph[i].minval = 0;
				graph[i].maxval = 1;
			};
		};
	};
}).play


// Sounds for battery check
/*batt_chk.free;
batt_chk = OSCFunc({
	play{
		a = Line.ar(1, 0, 1, doneAction:2);
		SinOsc.ar(440, 0, a);
	}
}, '/batt_chk');

low_batt.free;
low_batt = OSCFunc({
	play{
		a = Line.ar(dur:1, doneAction:2);
		b = LFPulse.ar(4);
		SinOsc.ar(440, 0, b);
	}
}, '/low_batt');*/
)

// Test sounds
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