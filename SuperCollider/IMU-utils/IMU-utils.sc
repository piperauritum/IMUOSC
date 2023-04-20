/*
Place this file into either:
Platform.userExtensionDir
Platform.systemExtensionDir
*/

IMUReceiver {
	/*
	a = IMUReceiver(2, 10);		// Start with 2 sensors, write to 6 busses per sensor from bus number 10
	a.showMsg = true;			// Show OSC messages
	a.stop;						// Stop the receiver
	*/

	var numSensors, bus, >showMsg, view, graph, aryVal, imuVal;
	var getAhrsData, getAcclData, plotVal, lowBatt;

	*new { | numSensors = 1, bus = 0, showMsg = false |
		if (Server.default.serverRunning.not) { Error("IMUReceiver - Server not running.").throw };
		^super.newCopyArgs(numSensors, bus, showMsg).init;
	}

	init {
		view = Window.new("IMUReceiver", Rect(100, 100, 600, 600));
		view.front;
		view.onClose = { this.stop };
		// view.alwaysOnTop = true;

		imuVal = Array.fill(3 * numSensors, { 0 });
		aryVal = Array2D(3 * numSensors, 100);
		graph = Array.fill(3 * numSensors, {|n|
			var w = 600 / (3 * numSensors);
			Plotter("", Rect(0, w * n, 600, w - 2), view);
		});

		this.start;
	}

	start {
		// Get and map IMU values, send to busses
		getAhrsData = OSCFunc({|msg, time|
			var id = msg[1];
			var val = msg[2..4];
			var j;
			val.do{|e,i|
				switch(i,
					0, { e = e.linlin(-86, 86, 0, 1)},
					1, { e = e.linlin(-180, 180, 0, 1)},
					2, { e = e.linlin(-189, 172, 0, 1)},
				);
				Server.default.sendMsg(\c_set, id * 6 + i + bus, e);
				j = id * 3 + i;
				if (j < imuVal.size) { imuVal[j] = e };
			};
			if (showMsg) { msg.postln };
		}, '/ahrsdata');

		getAcclData = OSCFunc({|msg, time|
			var id = msg[1];
			var val = msg[2..4];
			val.do{|e,i|
				Server.default.sendMsg(\c_set, id * 6 + i + 3 + bus, e);
			};
			if (showMsg) { msg.postln };
		}, '/accldata');

		lowBatt =  OSCFunc({|msg, time|
			var bd, wd, tx;
			var id = msg[1];
			defer {
				bd = Window.availableBounds;
				wd = Window.new("Low Battery", Rect(bd.width/2-200, bd.height/2-50, 400, 100));
				tx = StaticText(wd, Rect(0, 0, 400, 100));
				tx.align = \center;
				tx.font = Font("Arial", 16, true);
				tx.string = "Sensor" + id.asString + "is low battery.";
				wd.alwaysOnTop = true;
				wd.front;
			};
		}, '/low_batt');


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
		getAhrsData.free;
		getAcclData.free;
		lowBatt.free;
		plotVal.stop;
		view.close;
	}
}

MixABS {
	*kr {|ary|
		^Mix.krFill(ary.size, {|n| ary[n].abs})
	}
}

MixHPZ {
	*ar {|in|
		var accl, num;
		if (in.isArray == false) { in = [in] };
		num = in.size;
		accl = Mix.fill(num, {|n|
			var out = in[n];
			out = sin(out * 2pi);
			out = HPZ1.kr(out).abs;
			out = K2A.ar(out);
			Lag2.ar(out, 0.5);
		});
		^accl;
	}

	*kr {|in|
		var accl, num;
		if (in.isArray == false) { in = [in] };
		num = in.size;
		accl = Mix.fill(num, {|n|
			var out = in[n];
			out = sin(out * 2pi);
			out = HPZ1.kr(out).abs;
			Lag2.kr(out, 0.05);
		});
		^accl;
	}

}