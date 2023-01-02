/*
Place this file into either:
Platform.userExtensionDir
Platform.systemExtensionDir
*/

IMUReceiver {
	/*
	a = IMUReceiver(2);		// start with 2 sensors
	a.showMsg = true;		// show OSC messages
	a.stop;					// stop the receiver
	*/

	var <>numSensor, <>showMsg, view, graph, aryVal, imuVal;
	var recvData, plotVal;

	*new { | numSensor = 1, showMsg = false |
		if(Server.default.serverRunning.not) { Error("IMUReceiver - Server not running.").throw };
		^super.newCopyArgs(numSensor, showMsg).init;
	}

	init {
		view = Window.new("M5StickC-Plus IMU", Rect(100, 100, 600, 600));
		view.alwaysOnTop = true;
		view.onClose = {
			this.stop;
		};
		view.front;

		imuVal = Array.fill(3 * numSensor, { 0 });
		aryVal = Array2D(3 * numSensor, 100);
		graph = Array.fill(3 * numSensor, {|n|
			var w = 600 / (3 * numSensor);
			Plotter("", Rect(0, w * n, 600, w - 2), view);
		});

		this.start;
	}

	start {
		// Get and map IMU values, send to busses
		recvData = OSCFunc({|msg, time|
			var id = msg[1] * 3;
			var val = msg[2..4];
			var v;
			val.do{|e,i|
				switch(i,
					0, { v = e.linlin(-90, 90, 0, 1)},
					1, { v = e.linlin(-180, 180, 0, 1)},
					2, { v = e.linlin(-189, 170, 0, 1)},
				);
				Server.default.sendMsg(\c_set, id + i, v);
				imuVal[id + i] = v;
			};
			if(showMsg){ msg.postln };
		}, '/senddata');


		// Plot the IMU values
		plotVal = Routine({
			inf.do{|tick|
				0.1.wait;
				imuVal.do{|e,i|

					var b = aryVal.rowAt(i).asArray;
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
		}).play;
	}

	stop {
		recvData.free;
		plotVal.stop;
		view.close;
	}
}


IMUAccelMix {
	*ar {|in|
		var accl = Mix.fill(3, {|n|
			var out = in[n];
			out = sin(out * 2pi);
			out = HPZ1.kr(out).abs;
			out = K2A.ar(out);
			Lag2.ar(out, 0.5);
		});
		^accl;
	}
}