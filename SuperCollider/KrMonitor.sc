KrMonitor {
	var bus, numChannels, wb, view, mtr, trigOn;
	var plotVal, trig;

	*new { | bus = 0, numChannels = 4 |
		if(Server.default.serverRunning.not) { Error("KrMonitor - Server not running.").throw };
		^super.newCopyArgs(bus, numChannels).init;
	}

	init {
		wb = Window.availableBounds;
		view = Window("KrMonitor", Rect(100, wb.height-240, 20*numChannels+10, 240));
		view.front;
		view.onClose = { this.stop };
		// view.alwaysOnTop = true;

		mtr = Array.fill(numChannels, {|i|
			var lb, mt;
			lb = StaticText(view, Rect(i*20, 0, 15, 15));
			lb.font = Font("Arial", 10, true);
			lb.align = \center;
			lb.string = (bus + i).asString;

			mt = LevelIndicator(view, Rect(i*20+5, 15, 15, 220));
			mt.drawsPeak = true;
			mt.numTicks = 9;
			mt.numMajorTicks = 3;
		});

		this.start;
	}

	start {
		plotVal = OSCFunc({|msg|
			msg.do{|e,i|
				if(i > 2, {
					defer { mtr[i-3].value = e };
				});
			}
		}, '/cbmon');

		trig = {
			var sig = In.kr(bus, numChannels);
			SendReply.kr(Impulse.kr(30), '/cbmon', sig);
		}.play;

		trigOn = true;
	}

	stop {
		plotVal.free;
		if(trigOn, {
			trig.free;
			trigOn = false;
		});
		view.close;
	}
}